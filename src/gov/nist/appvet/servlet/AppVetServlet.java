/* This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 United States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 * 
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS".  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof including, but
 * not limited to, the correctness, accuracy, reliability or usefulness of
 * the software.
 * 
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement.
 */
package gov.nist.appvet.servlet;

import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.servlet.preprocessor.Registration;
import gov.nist.appvet.servlet.shared.Emailer;
import gov.nist.appvet.servlet.shared.ErrorMessage;
import gov.nist.appvet.servlet.shared.Zip;
import gov.nist.appvet.shared.all.AppStatus;
import gov.nist.appvet.shared.all.AppVetParameter;
import gov.nist.appvet.shared.all.AppVetServletCommand;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.HttpBasicAuthentication;
import gov.nist.appvet.shared.all.ReportFileType;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.ToolType;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.all.Validate;
import gov.nist.appvet.shared.backend.AppInfo;
import gov.nist.appvet.shared.backend.AppStatusManager;
import gov.nist.appvet.shared.backend.Authenticate;
import gov.nist.appvet.shared.backend.Database;
import gov.nist.appvet.shared.backend.FileUtil;
import gov.nist.appvet.shared.backend.Logger;
import gov.nist.appvet.shared.backend.ToolAdapter;
import gov.nist.appvet.shared.backend.ToolStatus;
import gov.nist.appvet.shared.backend.ToolStatusManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * This class defines the AppVet HTTP GET and POST server.
 * @author steveq@nist.gov
 */
public class AppVetServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	/** Server logger. */
	private static final Logger log = AppVetProperties.log;

	@Override
	/** Handler for HTTP GET messages.*/
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
				
		String authHeaderValue = request.getHeader("Authorization");
		if (authHeaderValue != null) {
			// Requester is attempting to authenticate
			String[] usernameAndPassword = HttpBasicAuthentication.getUsernameAndPassword(authHeaderValue);
			String username = usernameAndPassword[0];
			String password = usernameAndPassword[1];
			if (!authenticateUserNameAndPassword(username, password)) {
				// Authentication error
				sendHttpResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
						ErrorMessage.AUTHENTICATION_ERROR.getDescription(),
						true);
				return;
			} else {
				// Username and password authenticated so return session ID

				// Client IP address of the request.
				String clientIpAddress = request.getRemoteAddr();

				// On CentOS, clientIpAddress will be '0:0:0:0:0:0:0:1' if on
				// localhost, so change to '127.0.0.1'
				if (clientIpAddress.equals("0:0:0:0:0:0:0:1")) {
					clientIpAddress = "127.0.0.1";
				}
				// Clear all expired sessions
				Database.clearExpiredSessions();
				final String sessionId = Database.createNewSession(username,
						clientIpAddress);
				log.debug("AUTHENTICATION SESSIONID: " + sessionId);
				sendHttpResponse(response, HttpServletResponse.SC_OK,
						sessionId, false);
				return;
			}
		}		
		
		// AppVet-generated session ID.
		String sessionId = request
				.getParameter(AppVetParameter.SESSIONID.value);
		// Client IP address of the request.
		String clientIpAddress = request.getRemoteAddr();
		// On CentOS, clientIpAddress will be '0:0:0:0:0:0:0:1' if on
		// localhost, so change to '127.0.0.1'
		if (clientIpAddress.equals("0:0:0:0:0:0:0:1")) {
			clientIpAddress = "127.0.0.1";
		}
		// Requesting username.
		String requesterUserName = request
				.getParameter(AppVetParameter.USERNAME.value);
		// Requesting user's password.
		String requesterPassword = request
				.getParameter(AppVetParameter.PASSWORD.value);
		
		// Authenticate session
		if (!authenticateSession(sessionId, clientIpAddress)) {
			
			// Authentication error
			sendHttpResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
					ErrorMessage.AUTHENTICATION_ERROR.getDescription(),
					true);
			return;
		} else {
			// Session authenticated
			requesterUserName = Database.getSessionUser(sessionId);
		}
		
		// Validate AppVet command
		String commandStr = request.getParameter(AppVetParameter.COMMAND.value);
		
		log.debug("COMMAND: " + commandStr);
		AppVetServletCommand command = AppVetServletCommand
				.getCommand(commandStr);
		if (command == null) {
			sendHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST,
					ErrorMessage.INVALID_COMMAND.getDescription(), true);
			return;
		}
		
		// Validate AppVet app ID. Required for all GET commands except
		// GET_APPVET_LOG and DOWNLOAD_LOG.
		String appId = request.getParameter(AppVetParameter.APPID.value);
		if (command != AppVetServletCommand.GET_APPVET_LOG
				&& command != AppVetServletCommand.DOWNLOAD_LOG) {
			if (!isValidAppId(appId, command)) {
				sendHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST,
						ErrorMessage.INVALID_APPID.getDescription(), true);
				return;
			}
		}
		
		// Ensure user is authorized to access the app ID. Required for all GET commands except
		// GET_APPVET_LOG and DOWNLOAD_LOG.
		if (command != AppVetServletCommand.GET_APPVET_LOG
				&& command != AppVetServletCommand.DOWNLOAD_LOG) {
			boolean authorizedForAppId = 
					requesterAuthorizedToAccessAppId(appId, requesterUserName);
			if (!authorizedForAppId) {
				sendHttpResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
						ErrorMessage.AUTHORIZATION_ERROR.getDescription(), true);
				return;
			}
		}
		
		// Validate tool ID. Required only for GET_TOOL_REPORT command.
		String toolId = request.getParameter(AppVetParameter.TOOLID.value);
		if (command == AppVetServletCommand.GET_TOOL_REPORT) {
			if (!ToolAdapter.isValidToolId(toolId)) {
				sendHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST,
						ErrorMessage.INVALID_TOOLID.getDescription(), true);
				return;
			}
		}
		
/*		log.debug("Incoming GET message:\n" + "sessionId: " + sessionId + "\n"
				+ "clientIpAddress: " + clientIpAddress + "\n"
				+ "requesterUserName: " + requesterUserName + "\n"
				+ "commandStr: " + commandStr + "\n" + "appId: " + appId + "\n"
				+ "toolId: " + toolId);
*/
		
		// Handle AppVet command
		try {
			switch (command) {
			case GET_APP_STATUS:
				// Get the current processing status of the app. Used only by
				// non-GUI clients. GUI clients get app status via GWT RPC.
				final AppStatus currentStatus = AppStatusManager
						.getAppStatus(appId);
				sendHttpResponse(response, HttpServletResponse.SC_OK,
						currentStatus.name(), false);
				break;
			case GET_APP_TOOLS_STATUS:
				// Get the current tool statuses for an app. Used only by
				// non-GUI clients. GUI clients get tool statuses via GWT RPC.
				String toolStatuses = getAppToolStatuses(appId);
				sendHttpResponse(response, HttpServletResponse.SC_OK,
						toolStatuses, false);
				break;
			case GET_ALL_TOOL_IDS:
				// Get a list of all tool IDs used by this instance
				// of AppVet.
				String allToolIDs = getAllToolIDs(response, clientIpAddress);
				sendHttpResponse(response, HttpServletResponse.SC_OK,
						allToolIDs, false);
				break;
			case GET_APP_TOOL_IDS:
				// Get a list of tools used to process a specific app.
				String appToolIDs = getAppToolIDs(response, appId,
						clientIpAddress);
				sendHttpResponse(response, HttpServletResponse.SC_OK,
						appToolIDs, true);
				break;
			case GET_TOOL_REPORT:
				// Get a tool report for the selected app. Used by GUI and
				// non-GUI clients.
				DeviceOS os = Database.getAppOS(appId);
				ToolStatus toolStatus = ToolStatusManager.getToolStatus(os,
						appId, toolId);
				if (toolStatus != null) {
					if (toolStatus == ToolStatus.ERROR
							|| toolStatus == ToolStatus.LOW
							|| toolStatus == ToolStatus.MODERATE
							|| toolStatus == ToolStatus.HIGH
							|| toolStatus == ToolStatus.AVAILABLE) {
						returnReport(response, appId, toolId, clientIpAddress);
					} else if (toolStatus != ToolStatus.NA) {
						sendHttpResponse(response,
								HttpServletResponse.SC_BAD_REQUEST,
								"Tool report not available.", true);
					}
				} else {
					sendHttpResponse(response,
							HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.UNKNOWN_TOOL_STATUS.getDescription(),
							true);
					break;
				}
				break;
			case GET_APP_LOG:
				// Get the log for the selected app. Used by GUI and non-GUI
				// clients.
				returnAppLog(response, appId, clientIpAddress);
				break;
			case GET_APPVET_LOG:
				// Get the main AppVet log. Used by GUI and non-GUI clients.
				returnAppVetLog(requesterUserName, response, clientIpAddress);
				break;
			case DOWNLOAD_REPORTS:
				// Download reports for the selected app. Reports can only be
				// downloaded if the app has completed processing. Used by GUI
				// and non-GUI clients.
				final AppStatus appStatus = AppStatusManager
						.getAppStatus(appId);
				if (appStatus != null) {
					downloadReports(response, appId, sessionId, clientIpAddress);
				} else {
					sendHttpResponse(response,
							HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.UNKNOWN_APP_STATUS.getDescription(),
							true);
				}
				break;
			case DOWNLOAD_LOG:
				downloadAppVetLog(response);
				break;
			case DOWNLOAD_APP:
				// Download AppVet log. Used by GUI and non-GUI clients.
				downloadApp(response, appId);
				break;
			default:
				log.warn("Received unknown GET command: " + commandStr
						+ " from IP: " + clientIpAddress + ". Ignoring...");
			}
		} finally {
			requesterUserName = null;
			requesterPassword = null;
			sessionId = null;
			commandStr = null;
			appId = null;
			clientIpAddress = null;
			System.gc();
		}
	}

	@Override
	/** Handler for HTTP POST messages.*/
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		AppVetServletCommand command = null;
		String commandStr = null;
		String requesterUserName = null;
		String requesterPassword = null;
		String sessionId = null;
		String toolId = null;
		String toolRisk = null;
		String appId = null;
		FileItem fileItem = null;
		String clientIpAddress = request.getRemoteAddr();
		
		// On CentOS, clientIpAddress will be '0:0:0:0:0:0:0:1' if on
		// localhost, so change to '127.0.0.1'
		if (clientIpAddress.equals("0:0:0:0:0:0:0:1")) {
			clientIpAddress = "127.0.0.1";
		}
		FileItemFactory factory = null;
		ServletFileUpload upload = null;
		List<FileItem> items = null;
		Iterator<FileItem> iter = null;
		FileItem item = null;
		try {
			factory = new DiskFileItemFactory();
			upload = new ServletFileUpload(factory);
			items = upload.parseRequest(request);
			iter = items.iterator();
			String incomingParameter = null;
			while (iter.hasNext()) {
				item = iter.next();
				if (item.isFormField()) {
					incomingParameter = item.getFieldName();
					if (incomingParameter.equals(AppVetParameter.COMMAND.value)) {
						// Used for all POST commands.
						commandStr = item.getString();
					} else if (incomingParameter
							.equals(AppVetParameter.USERNAME.value)) {
						// Used for all POST commands.
						requesterUserName = item.getString();
					} else if (incomingParameter
							.equals(AppVetParameter.PASSWORD.value)) {
						// Used for all POST commands.
						requesterPassword = item.getString();
					} else if (incomingParameter
							.equals(AppVetParameter.SESSIONID.value)) {
						// Used for all POST commands.
						sessionId = item.getString();
					} else if (incomingParameter
							.equals(AppVetParameter.TOOLID.value)) {
						// Used only for submit report command.
						toolId = item.getString();
					} else if (incomingParameter
							.equals(AppVetParameter.TOOLRISK.value)) {
						// Used only for submit report command.
						toolRisk = item.getString();
					} else if (incomingParameter
							.equals(AppVetParameter.APPID.value)) {
						// Used only for submit report command.
						appId = item.getString();
					} else {
						// Received unknown parameter. Ignoring.
					}
				} else {
					// item should now hold the received file.
					fileItem = item;
					//log.debug("Received file " + fileItem.getName());
				}
			}
/*
			log.debug("Incoming POST message:\n" + "sessionId: " + sessionId
					+ "\n" + "clientIpAddress: " + clientIpAddress + "\n"
					+ "requesterUserName: " + requesterUserName + "\n"
					+ "commandStr: " + commandStr + "\n" + "appId: " + appId
					+ "\n" + "toolId: " + toolId + "\ntoolRisk: " + toolRisk);
*/

			// Authenticate
			if (!authenticateSession(sessionId, clientIpAddress)) {
				if (!authenticateUserNameAndPassword(requesterUserName,
						requesterPassword)) {
					log.debug("Authentication error for user '"
							+ requesterUserName + "'");
					sendHttpResponse(response,
							HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.AUTHENTICATION_ERROR.getDescription(),
							true);
					return;
				}
			} else {
				requesterUserName = Database.getSessionUser(sessionId);
			}

			// Validate AppVet command.
			command = AppVetServletCommand.getCommand(commandStr);
			if (command == null) {
				log.error("Unknown command received. Aborting...");
				sendHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST,
						ErrorMessage.INVALID_COMMAND.getDescription(), true);
				return;
			}

			// TODO: Note that any user can submit a report for any tool. 
			// Need to determine if only the owner of the tool should be able to submit
			// a report for that tool, or filter based on type (TEST_TOOL vs REPORT)
			if (command == AppVetServletCommand.SUBMIT_REPORT) {
				
				// Validate AppVet app ID. Required only for SUBMIT_APP_REPORT.
				if (!isValidAppId(appId, command)) {
					log.error("Invalid app ID: " + appId);
					sendHttpResponse(response,
							HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.INVALID_APPID.getDescription(), true);
					return;
				}
				
				// Validate tool ID. Required only for SUBMIT_REPORT command.
				if (!ToolAdapter.isValidToolId(toolId)) {
					log.error("Invalid tool ID: " + toolId);
					sendHttpResponse(response,
							HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.INVALID_TOOLID.getDescription(), true);
					return;
				}
			}
			
			// Verify file attachment (or file encoded in an HTML form param
			// "report")
			if (command == AppVetServletCommand.SUBMIT_APP
					|| command == AppVetServletCommand.SUBMIT_REPORT) {
				if (fileItem == null) {
					log.error("File attachment is missing.");
					sendHttpResponse(response,
							HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.MISSING_FILE.getDescription(), true);
					return;
				} else if (!Validate.isPrintable(fileItem.getName())) {
					log.error("Filename contains illegal character");
					sendHttpResponse(
							response,
							HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.ILLEGAL_CHAR_IN_UPLOADED_FILENAME_ERROR
									.getDescription(), true);
					return;
				} else {
					//log.debug("File attachment is valid");
				}
			}

			// Handle AppVet command
			AppInfo appInfo = null;
			switch (command) {
			case SUBMIT_APP:
				// Verify app file extension
				DeviceOS deviceOS = Validate.hasValidAppFileExtension(fileItem
						.getName());
				if (deviceOS == null) {
					sendHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.INVALID_APP_FILE_EXTENSION
									.getDescription(), true);
					return;	
				}
				
				// Create app info object
				appInfo = createAppInfo(requesterUserName, fileItem,
						clientIpAddress, request, deviceOS);
				
				// Send a response before completing the registration
				sendHttpResponse(response, HttpServletResponse.SC_ACCEPTED,
						appInfo.appId, false);
				
				// Register the app
				Registration appFileRegistration = new Registration(appInfo);
				appFileRegistration.registerApp();
				break;
			case SUBMIT_REPORT:
				// Check if app exists
				if (!Database.appExists(appId)) {
					sendHttpResponse(response,
							HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.INVALID_APPID.getDescription(), true);
					return;
				}

				// Verify report file extension
				ReportFileType reportFileType = Validate.hasValidReportFileExtension(fileItem
						.getName());
				if (reportFileType == null) {
					sendHttpResponse(response,
							HttpServletResponse.SC_BAD_REQUEST,
							ErrorMessage.INVALID_REPORT_FILE_EXTENSION
									.getDescription(), true);
					return;	
				}
				
				// Create app info object
				appInfo = createAppInfo(appId, toolId, toolRisk, fileItem);
				
				// Send a response before completing the submission
				sendHttpResponse(response, HttpServletResponse.SC_ACCEPTED,
						"Report received successfully", false);
				
				// Submit the report
				submitReport(requesterUserName, appInfo, response);
				break;
			default:
				// Received unknown command. Ignoring.
			}
		} catch (final FileUploadException e) {
			sendHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST,
					ErrorMessage.FILE_UPLOAD_ERROR.getDescription(), true);
			return;
		} finally {
			command = null;
			commandStr = null;
			requesterUserName = null;
			requesterPassword = null;
			sessionId = null;
			toolId = null;
			toolRisk = null;
			appId = null;
			fileItem = null;
			clientIpAddress = null;
			factory = null;
			upload = null;
			items = null;
			iter = null;
			item = null;
			System.gc();
		}
	}
	
	public boolean requesterAuthorizedToAccessAppId(String appId, String requesterUsername) {
		// Check if the owner is the requester
		String ownerName = Database.getOwner(appId);
		
		if (ownerName.equals(requesterUsername)) {
			// Owner is the requester
			return true;
		}
		
		// Check if requester is an ADMIN or ANALYST
		String requesterRoleStr = Database.getRoleStr(requesterUsername);
		Role requesterRole = null;
		try {
			requesterRole = Role.getRole(requesterRoleStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (requesterRole == Role.ADMIN){
			// Requester is an admin
			return true;
		} else if (requesterRole == Role.ANALYST) {
			AppInfo appInfo = new AppInfo(appId);
			if (Database.appIsAccessibleToUser(requesterUsername, appInfo)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}  
	}

	public boolean isValidAppId(String appId, AppVetServletCommand command) {
		if (appId != null) {
			if (Database.appExists(appId)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * This method creates an app information object for a tool report
	 * submission.
	 */
	private AppInfo createAppInfo(String appId, String toolId, String toolRisk,
			FileItem fileItem) {
		AppInfo appInfo = new AppInfo(appId);
		appInfo.toolId = toolId;
		appInfo.toolRisk = toolRisk;
		appInfo.fileItem = fileItem;
		return appInfo;
	}

	
	/**
	 * This method creates an app information object for an app submission.
	 */
	private AppInfo createAppInfo(String userName, FileItem fileItem,
			String clientIpAddress, HttpServletRequest request, DeviceOS deviceOS) {
		String appId = generateAppid();
		AppInfo appInfo = new AppInfo(appId, true);
		appInfo.ownerName = userName;
		String newAppFileName = null;
		
		// App file was received.
		appInfo.fileItem = fileItem;
		String origFileName = FileUtil.getNormalizedFileName(fileItem
				.getName());
		// Note that spaces in the original fileItem name are replaced
		// with underscores.
		newAppFileName = origFileName.replaceAll(" ", "_");
		String fileNameUpperCase = newAppFileName.toUpperCase();
		if (fileNameUpperCase.endsWith(".APK")) {
			appInfo.os = DeviceOS.ANDROID;
		} else if (fileNameUpperCase.endsWith(".IPA")) {
			appInfo.os = DeviceOS.IOS;
		}
		// Set app file and project name.
		appInfo.setAppFileAndProjectName(newAppFileName, appInfo.os);
		log.debug("Got project name: " + appInfo.getAppProjectName());
		
		// Get client IP address to set session.
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(request.getRemoteHost());
		} catch (UnknownHostException e) {
			log.error(e.toString());
			return null;
		}
		
		appInfo.clientHost = addr.getCanonicalHostName();
		return appInfo;
	}

	
	private void downloadReports(HttpServletResponse response, String appid,
			String sessionId, String clientIpAddress) {
		boolean zipped = false;
		String destinationZipPath = null;
		String contentDisposition = "";
		String sourceDirPath = AppVetProperties.APPS_ROOT + "/" + appid
				+ "/reports";
		destinationZipPath = AppVetProperties.APPS_ROOT + "/" + appid
				+ "/AppVet_Reports_App-" + appid + "_SID-" + sessionId + ".zip";
		Zip zip = new Zip();
		zipped = zip.zipDir(sourceDirPath, destinationZipPath);
		if (zipped) {
			try {
				File file = new File(destinationZipPath);
				if (!file.exists()) {
					sendHttpResponse(response,
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Could not locate zipped report", true);
					return;
				}
				contentDisposition = "attachment;filename=AppVet_Reports_App-"
						+ appid + ".zip";
				response.setContentType("application/zip");
				response.setHeader("Content-Disposition", contentDisposition);
				response.setHeader("Cache-Control", "max-age=0");
				response.setContentLength((int) file.length());
				returnFile(response, file);
				file = null;
				// Remove zipped file on the local machine.
				FileUtil.deleteFile(destinationZipPath, appid);
			} catch (final Exception e) {
				log.error(e.toString());
			}
		}
	}

	private void downloadApp(HttpServletResponse response, String appid) {
		AppInfo appInfo = new AppInfo(appid);
		try {
			log.debug("Returning app at " + appInfo.getAppFilePath());
			File file = new File(appInfo.getAppFilePath());
			if (!file.exists()) {
				sendHttpResponse(response,
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Could not locate app file. Make sure AppVet KEEP_APPS property is true.", true);
				return;
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + appInfo.getAppFileName());
			response.setHeader("Cache-Control", "max-age=0");
			response.setContentLength((int) file.length());
			returnFile(response, file);
			file = null;
		} catch (final Exception e) {
			log.error(e.toString());
		}
	}

	private void downloadAppVetLog(HttpServletResponse response) {
		try {
			File file = new File(AppVetProperties.APPVET_LOG_PATH);
			if (!file.exists()) {
				sendHttpResponse(response,
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Could not locate AppVet log", true);
				return;
			}
			response.setContentType("text/plain");
			response.setHeader("Content-Disposition",
					"attachment;filename=appvet_log.txt");
			response.setHeader("Cache-Control", "max-age=0");
			response.setContentLength((int) file.length());
			returnFile(response, file);
			file = null;
		} catch (final Exception e) {
			log.error(e.toString());
		}
	}

	private String generateAppid() {
		for (;;) {
			Random r = new Random();
			final int randInt = r.nextInt(99999999);
			String appId = new Integer(randInt).toString();
			if (!Database.appExists(appId)) {
				return appId;
			}
		}
	}
	

	/** This method authenticates a user based on session information. */
	private boolean authenticateSession(String sessionId, String clientIpAddress) {
		// Clear all expired sessions
		Database.clearExpiredSessions();
		
		// Verify session ID. 
		if (Database.sessionIsGood(sessionId, clientIpAddress)) {
			// If session ID is valid, get user based on session ID only. Don't
			// use userName since this might not correlate to the session ID.
			String user = Database.getSessionUser(sessionId);
			Database.updateUserLogonTime(user);
			return true;
		} else {
			//log.debug("Session " + sessionId + " expired");
			return false;
		}
	}
	

	/** This method authenticates a user based on username and password. */
	private boolean authenticateUserNameAndPassword(String userName,
			String password) {
		// Make sure user is not DEACTIVATED (i.e., deleted)
		boolean userExists = Database
				.exists("SELECT * FROM users WHERE username = '" 
						+ userName + "' AND fromhost <> 'DEACTIVATED' or fromhost is null");
		if (!userExists) {
			log.error("User " + userName + " does not exist or has been de-activated. ");
			return false;
		} else {
			log.debug("User " + userName + " exists. ");

		}
		if (Authenticate.isAuthenticated(userName, password)) {
			Database.updateUserLogonTime(userName);
			return true;
		} else {
			return false;
		}
	}

	/** Get the app's log. */
	private void returnAppLog(HttpServletResponse response, String appid,
			String clientIpAddress) {
		if (appid == null) {
			sendHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST,
					"Invalid app ID", true);
			return;
		}
		try {
			String filePath = AppVetProperties.APPS_ROOT + "/" + appid
					+ "/reports/" + AppVetProperties.APP_LOG_NAME;
			File file = new File(filePath);
			try {
				if (!file.exists()) {
					sendHttpResponse(response,
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Could not locate app log at: " + filePath, true);
					return;
				}
				response.setContentType("text/plain");
				response.setHeader("Content-Disposition",
						"inline;filename=appvet_log.txt");
				response.setHeader("Cache-Control", "max-age=0");
				response.setContentLength((int) file.length());
				returnFile(response, file);
			} finally {
				file = null;
				filePath = null;
			}
		} catch (final Exception e) {
			log.error(e.toString());
		}
	}

	private void returnAppVetLog(String userName, HttpServletResponse response,
			String clientIpAddress) {
		try {
			String userRoleStr = Database.getRoleStr(userName);
			Role userRole = Role.getRole(userRoleStr);
			if (userRole != Role.ADMIN) {
				sendHttpResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
						"Unauthorized access to AppVet log", true);
				return;
			}
			String appVetLogPath = AppVetProperties.APPVET_LOG_PATH;
			File logFile = new File(appVetLogPath);
			try {
				if (!logFile.exists()) {
					sendHttpResponse(response,
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Could not locate AppVet log", true);
					return;
				}
				response.setContentType("text/plain");
				response.setHeader("Content-Disposition",
						"inline;filename=appvet_log.txt");
				response.setHeader("Cache-Control", "max-age=0");
				response.setContentLength((int) logFile.length());
				returnFile(response, logFile);
			} finally {
				logFile = null;
				appVetLogPath = null;
			}
		} catch (final Exception e) {
			log.error(e.toString());
		}
	}

	/** Return a file to the client. */
	public boolean returnFile(HttpServletResponse response, File file) {
		FileInputStream fis = null;
		OutputStream os = null;
		try {
			fis = new FileInputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			os = response.getOutputStream();
			while ((read = fis.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			os.close();
			fis.close();
			return true;
		} catch (final IOException e) {
			log.error(e.toString());
			return false;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				os = null;
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fis = null;
			}
		}
	}

	/** Return all tool IDs used by this instance of AppVet. */
	public String getAllToolIDs(HttpServletResponse response,
			String clientIpAddress) {
		StringBuffer payload = new StringBuffer("");
		payload.append("* Android tool IDs:\n");
		ArrayList<ToolAdapter> androidTools = AppVetProperties.androidTools;
		for (int i = 0; i < androidTools.size(); i++) {
			ToolAdapter androidTool = androidTools.get(i);
			payload.append(androidTool.toolId + "\n");
		}
		payload.append("* iOS tool IDs:\n");
		ArrayList<ToolAdapter> iosTools = AppVetProperties.iosTools;
		for (int i = 0; i < iosTools.size(); i++) {
			ToolAdapter iosTool = iosTools.get(i);
			payload.append(iosTool.toolId + "\n");
		}
		return payload.toString();
	}

	public String getAppToolStatuses(String appId) {
		DeviceOS appOS = Database.getAppOS(appId);
		String toolStatuses = "";
		if (appOS == DeviceOS.ANDROID) {
			for (int i = 0; i < AppVetProperties.androidTools.size(); i++) {
				ToolAdapter toolAdapter = AppVetProperties.androidTools.get(i);
				String androidToolId = toolAdapter.toolId;
				ToolStatus toolStatus = ToolStatusManager.getToolStatus(appOS,
						appId, androidToolId);
				toolStatuses += androidToolId + "=" + toolStatus + ",";
			}
		} else if (appOS == DeviceOS.IOS) {
			for (int i = 0; i < AppVetProperties.iosTools.size(); i++) {
				ToolAdapter toolAdapter = AppVetProperties.iosTools.get(i);
				String iosToolId = toolAdapter.toolId;
				ToolStatus toolStatus = ToolStatusManager.getToolStatus(appOS,
						appId, iosToolId);
				toolStatuses += iosToolId + "=" + toolStatus + ",";
			}
		}
		return toolStatuses;
	}

	/** Return tool IDs used to process a specific app. */
	public String getAppToolIDs(HttpServletResponse response, String appid,
			String clientIpAddress) {
		StringBuffer payload = new StringBuffer();
		DeviceOS appOS = Database.getAppOS(appid);
		if (appOS == DeviceOS.ANDROID) {
			ArrayList<ToolAdapter> androidTools = AppVetProperties.androidTools;
			for (int i = 0; i < androidTools.size(); i++) {
				ToolAdapter androidTool = androidTools.get(i);
				payload.append(androidTool.toolId + ",");
			}
		} else if (appOS == DeviceOS.IOS) {
			ArrayList<ToolAdapter> iosTools = AppVetProperties.iosTools;
			for (int i = 0; i < iosTools.size(); i++) {
				ToolAdapter iosTool = iosTools.get(i);
				payload.append(iosTool.toolId + ",");
			}
		} else {
			log.error("Unknown device OS");
		}
		return payload.toString();
	}

	public void returnReport(HttpServletResponse response, String appid,
			String toolId, String clientIpAddress) {
		DeviceOS appOS = Database.getAppOS(appid);
		ToolAdapter tool = ToolAdapter.getByToolId(appOS, toolId);
		if (tool.reportName == null) {
			sendHttpResponse(response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error generating tool report", true);
			return;
		} else {
			String filePath = AppVetProperties.APPS_ROOT + "/" + appid
					+ "/reports/" + tool.reportName;
			File file = new File(filePath);
			if (!file.exists()) {
				sendHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST,
						"Report not available", true);
				return;
			}
			if (tool.reportFileType == ReportFileType.PDF) {
				response.setContentType("application/pdf");
			} else if (tool.reportFileType == ReportFileType.HTML) {
				response.setContentType("text/html");
			} else if (tool.reportFileType == ReportFileType.TXT) {
				response.setContentType("text/plain");
			} else if (tool.reportFileType == ReportFileType.RTF) {
				response.setContentType("application/rtf");
			}
			response.setHeader("Content-Disposition", "inline;filename="
					+ tool.reportName);
			response.setHeader("Cache-Control", "max-age=0");
			response.setContentLength((int) file.length());
			returnFile(response, file);
		}
	}

	/** Send HTTP Response to client. */
	private boolean sendHttpResponse(HttpServletResponse response,
			int httpResponseCode, String message, boolean errorMessage) {
		PrintWriter out = null;
		try {
			response.setStatus(httpResponseCode);
			response.setContentType("text/html");
			out = response.getWriter();
			out.print(message);
			out.flush();
			if (errorMessage) {
				log.debug("Returned HTTP " + httpResponseCode + "\n"
				 + "message = " + message);
			}
			return true;
		} catch (final IOException e) {
			log.error(e.toString().toString());
			return false;
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
		}
	}

	private void submitReport(String submitterUserName, AppInfo appInfo,
			HttpServletResponse response) {
		final ToolAdapter tool = ToolAdapter.getByToolId(appInfo.os,
				appInfo.toolId);
		
		appInfo.log.info("Received SUBMIT_REPORT from user '" + submitterUserName + "' for '"
				+ tool.toolId + "' on app " + appInfo.appId + " with report '"
				+ appInfo.fileItem.getName() + "' setting '" + tool.toolId + "' status to "
				+ appInfo.toolRisk + ".");
		
		String submitterRoleStr = Database.getRoleStr(submitterUserName);
		Role submitterRole = null;
		try {
			submitterRole = Role.getRole(submitterRoleStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		/*
		 * TODO: Make the following configurable via tool adapter properties
		 * The following should match the policies defined in ReportUploadDialogBox!
		 * The following can be modified to support specific use-case policies.
		 */
		
		/* PUT YOUR SPECIFIC POLICY HERE FOR PERMITTING
		 *  TOOL REPORT TO BE UPLOADED */
		
		/*
		 * THE FOLLOWING SHOULD MATCH THE POLICIES DEFINED IN YOUR
		 * ReportUploadDialogBox constructor!
		 */
		
		// TODO: For AV3, SUMMARY was removed (now uses only REPORT)
		if (tool.toolType == ToolType.SUMMARY) {
			if (tool.toolId.equals("androidsummary") || tool.toolId.equals("iossummary")) {
				if (submitterRole == Role.ADMIN){
					// Only ADMINs are permitted to submit summary reports
				} else {
					// No other users are permitted to submit summary reports
					appInfo.log.debug("Submitter " + submitterUserName + " is not authorized to submit " + tool.toolType.name() + " reports");
					return;
				}
			} else if (tool.toolId.equals("golive")) {
				if (submitterRole == Role.ADMIN){
					// ADMINs are permitted to submit GoLive reports
				} else if (submitterRole == Role.ANALYST) {
					// Check if ANALYST can access related app
					if (Database.appIsAccessibleToUser(submitterUserName, appInfo)) {
						// Approved ANALYSTs are permitted to submit GoLive reports
					} else {
						// Unapproved ANALYSTs are not permitted to submit GoLive reports
						appInfo.log.debug("Submitter " + submitterUserName + " is not authorized to submit " + tool.toolType.name() + " reports");
						return;
					}
				} else {
					// No other users can submit GoLive reports
					appInfo.log.debug("Submitter " + submitterUserName + " is not authorized to submit " + tool.toolType.name() + " reports");
					return;
				}
			} else if (tool.toolId.equals("approval")) {
				// Third-party approval -- all users permitted
			}			
		} else if (tool.toolType == ToolType.AUDIT) {  
			if (submitterRole == Role.ADMIN) {
				// ADMINs are permitted to submit AUDIT reports
			} else if (submitterRole == Role.ANALYST) {
				// Check if ANALYST can access related app
				if (Database.appIsAccessibleToUser(submitterUserName, appInfo)) {
					// Approved ANALYSTs are permitted to submit AUDIT reports
				} else {
					// Unapproved ANALYSTs are not permitted to submit AUDIT reports
					appInfo.log.error("Submitter " + submitterUserName + " is not authorized to submit " + tool.toolType.name() + " reports");
					return;
				}
			} else {
				// No other users can submit AUDIT reports
				appInfo.log.error("Submitter " + submitterUserName + " is not authorized to submit " + tool.toolType.name() + " reports");
				return;
			}
		} else if (tool.toolType == ToolType.TESTTOOL || tool.toolType == ToolType.REPORT) {
			// All users permitted
		} 

		// Save report
		final boolean reportSaved = FileUtil.saveReportUpload(appInfo.appId,
				tool.reportName, appInfo.fileItem);
		
		// Update report time
		Database.setReportTime(appInfo.appId, appInfo.os, tool.toolId);
		
		if (reportSaved) {
			// Override reports with final LOW/MODERATE/HIGH risk decision.
			if (appInfo.toolRisk == null) {
				appInfo.log.error("appInfo.toolRisk is null");
			}
			if (appInfo.toolRisk.equals("HIGH")) {
				ToolStatusManager.setToolStatus(appInfo,
						tool.toolId, ToolStatus.HIGH);
			} else if (appInfo.toolRisk.equals("MODERATE")) {
				ToolStatusManager.setToolStatus(appInfo,
						tool.toolId, ToolStatus.MODERATE);
			} else if (appInfo.toolRisk.equals("LOW")) {
				ToolStatusManager.setToolStatus(appInfo,
						tool.toolId, ToolStatus.LOW);
			} else if (appInfo.toolRisk.equals("ERROR")) {
				ToolStatusManager.setToolStatus(appInfo,
						tool.toolId, ToolStatus.ERROR);
			} else if (appInfo.toolRisk.equals("AVAILABLE")) {
				ToolStatusManager.setToolStatus(appInfo,
						tool.toolId, ToolStatus.AVAILABLE);	
			} else {
				appInfo.log.warn("Unknown risk type '" + appInfo.toolRisk
						+ "' received from " + appInfo.ownerName);
			}
			
			UserInfo userInfo = Database.getUserInfo(appInfo.ownerName, null);

			// TODO: For AV3, AUDIT and SUMMARY was removed (now uses only REPORT)
			if (tool.toolType == ToolType.AUDIT || tool.toolType == ToolType.SUMMARY || 
					tool.toolType == ToolType.REPORT){
				String emailSubject = tool.name + " report for app " + appInfo.appId + " '" + appInfo.appName + "' submitted by '" + submitterUserName + "'";
				String emailContent = tool.name + " report for app " + appInfo.appId + " '" + appInfo.appName + "' was submitted by '" + submitterUserName + "'.";
				Emailer.sendEmail(userInfo.getEmail(), emailSubject, emailContent);
			}
			
		} else {
			appInfo.log.error("Error saving report!");
		}
	}
}
