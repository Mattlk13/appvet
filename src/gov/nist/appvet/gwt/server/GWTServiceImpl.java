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
package gov.nist.appvet.gwt.server;

import gov.nist.appvet.gwt.client.GWTService;
import gov.nist.appvet.gwt.shared.AppInfoGwt;
import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.gwt.shared.ToolInfoGwt;
import gov.nist.appvet.gwt.shared.ToolStatusGwt;
import gov.nist.appvet.gwt.shared.UserInfoGwt;
import gov.nist.appvet.gwt.shared.UserToolCredentialsGwt;
import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.shared.Authenticate;
import gov.nist.appvet.shared.Database;
import gov.nist.appvet.shared.FileUtil;
import gov.nist.appvet.shared.Logger;
import gov.nist.appvet.shared.analysis.AnalysisType;
import gov.nist.appvet.shared.appvetparameters.AppVetParameter;
import gov.nist.appvet.shared.os.DeviceOS;
import gov.nist.appvet.shared.role.Role;
import gov.nist.appvet.shared.servletcommands.AppVetServletCommand;
import gov.nist.appvet.shared.status.ToolStatus;
import gov.nist.appvet.shared.status.ToolStatusManager;
import gov.nist.appvet.toolmgr.ToolAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author steveq@nist.gov
 */
public class GWTServiceImpl extends RemoteServiceServlet implements GWTService {
	private static final long serialVersionUID = 1L;
	private static final Logger log = AppVetProperties.log;
	static {
		log.info("*** Starting GWT Service " + AppVetProperties.APPVET_VERSION
				+ " on " + AppVetProperties.URL);
	}

	@Override
	public List<UserInfoGwt> adminSetUser(UserInfoGwt userInfo)
			throws IllegalArgumentException {
		if (userInfo.isNewUser()) {
			if (Database.adminAddNewUser(userInfo)) {
				log.debug("Added user " + userInfo.getFullName());
			} else {
				log.error("Could not add user " + userInfo.getFullName());
			}
		} else {
			if (Database.updateUser(userInfo)) {
				log.debug("Updated user " + userInfo.getFullName());
			} else {
				log.error("Could not update user " + userInfo.getFullName());
			}
		}
		return getUsersList();
	}

	@Override
	public ConfigInfoGwt authenticate(String username, String password, boolean sso)
			throws IllegalArgumentException {
		String sql = "SELECT * FROM users " + "where username='" + username
				+ "'";
		if (!Database.exists(sql)) {
			// Check if user is the AppVet default admin defined in
			// AppVetProperties.xml
			if (username.equals(AppVetProperties.ADMIN_USERNAME)
					&& password.equals(AppVetProperties.ADMIN_PASSWORD)) {
				log.debug("Adding new AppVet admin '" + username);
				Database.adminAddNewUser(username, password, "NIST",
						"appvet@nist.gov", Role.ADMIN.name(), "Administrator",
						"AppVet");
			} else {
				log.warn("User " + username + " not defined as default admin");
				return null;
			}
		}
		final String clientIpAddress = getThreadLocalRequest().getRemoteAddr();
		boolean userExists = 
				Database.exists("SELECT * FROM users WHERE username='" + username + "'");
		if (!userExists) {
			log.error("No such user: " + username);
			return null;
		}
		if (Authenticate.isAuthenticated(username, password)) {
			Database.updateClientHost(username, clientIpAddress);
			Database.updateUserLogonTime(username);
			log.debug(username + " logged into GWT from: " + clientIpAddress);
			final String sessionId = Database.setSession(username,
					clientIpAddress);
			final long sessionExpiration = Database.getSessionExpiration(
					sessionId, clientIpAddress);
			// If SSO login, randomly change current password so it cannot 
			// be copied by another user and used as a query parameter to 
			// directly access AppVet.
			if (sso) {
				UUID newPassword = UUID.randomUUID();
				Database.setPBKDF2Password(username, newPassword.toString());
			}
			return getConfigInfo(username, sessionId, sessionExpiration);
		} else {
			AppVetProperties.log.warn("Could not authenticate user: "
					+ username);
			return null;
		}
	}

	private static ConfigInfoGwt getConfigInfo(String username,
			String sessionId, long sessionExpiration) {
		final ConfigInfoGwt configInfo = new ConfigInfoGwt();
		configInfo.setAppVetHostUrl(AppVetProperties.HOST_URL);
		configInfo.setAppVetUrl(AppVetProperties.URL);
		configInfo.setAppVetServletUrl(AppVetProperties.SERVLET_URL);
		configInfo.setAppVetVersion(AppVetProperties.APPVET_VERSION);
		configInfo.setSessionId(sessionId);
		configInfo.setMaxIdleTime(AppVetProperties.MAX_SESSION_IDLE_DURATION);
		configInfo.setGetUpdatesDelay(AppVetProperties.GET_UPDATES_DELAY);
		configInfo.setSessionExpirationLong(sessionExpiration);
		configInfo.setSystemMessage(AppVetProperties.STATUS_MESSAGE);
		final ArrayList<ToolInfoGwt> tools = new ArrayList<ToolInfoGwt>();
		// Get Android tools
		final ArrayList<ToolAdapter> androidTools = AppVetProperties.androidTools;
		if ((androidTools != null) && !androidTools.isEmpty()) {
			for (int i = 0; i < androidTools.size(); i++) {
				final ToolAdapter androidTool = androidTools.get(i);
				if (androidTool != null
						&& (androidTool.analysisType == AnalysisType.TESTTOOL || 
						androidTool.analysisType == AnalysisType.REPORT ||
						androidTool.analysisType == AnalysisType.AUDIT)) {
					ToolInfoGwt toolInfo = new ToolInfoGwt();
					toolInfo.setOs(DeviceOS.ANDROID.name());
					toolInfo.setName(androidTool.name);
					toolInfo.setId(androidTool.id);
					toolInfo.setAuthenticationRequired(androidTool.authenticationRequired);
					if (toolInfo.requiresAuthentication()) {
						toolInfo.setAuthenticationParameterNames(androidTool.authenticationParams);
					}
					toolInfo.setReportFileType(androidTool.reportFileType
							.name());
					tools.add(toolInfo);
				}
			}
		} else {
			log.error("GWT DataProvider could not read "
					+ "Android tool info from AppVetProperties. Tools must include "
					+ " at least 'appinfo'" + " and 'registration' tools.");
			return null;
		}

		// Get iOS tools
		final ArrayList<ToolAdapter> iosTools = AppVetProperties.iosTools;
		if ((iosTools != null) && !iosTools.isEmpty()) {
			for (int i = 0; i < iosTools.size(); i++) {
				final ToolAdapter iosTool = iosTools.get(i);
				if (iosTool != null
						&& (iosTool.analysisType == AnalysisType.TESTTOOL ||
						iosTool.analysisType == AnalysisType.REPORT ||
						iosTool.analysisType == AnalysisType.AUDIT)) {
					ToolInfoGwt toolInfo = new ToolInfoGwt();
					toolInfo.setOs(DeviceOS.IOS.name());
					toolInfo.setName(iosTool.name);
					toolInfo.setId(iosTool.id);
					toolInfo.setAuthenticationRequired(iosTool.authenticationRequired);
					if (toolInfo.requiresAuthentication()) {
						toolInfo.setAuthenticationParameterNames(iosTool.authenticationParams);
					}
					toolInfo.setReportFileType(iosTool.reportFileType.name());
					tools.add(toolInfo);
				}
			}
		} else {
			log.error("GWT DataProvider could not read "
					+ "iOS tool info from AppVetProperties. Tools must include "
					+ " at least 'appinfo'" + " and 'registration' tools.");
			return null;
		}
		configInfo.setTools(tools);
		// Get user's information
		final UserInfoGwt userInfo = Database.getUserInfo(username, tools);
		if (userInfo == null) {
			log.error("GWT DataProvider could not get user information");
			return null;
		} else {
			configInfo.setUserInfo(userInfo);
		}
		ArrayList<UserToolCredentialsGwt> toolCredentials = configInfo
				.getUserInfo().getToolCredentials();
		if (toolCredentials == null)
			log.error("toolCredentials is null in GWTServiceImpl");

		return configInfo;
	}

	@Override
	public Boolean deleteApp(DeviceOS os, String appid, String username)
			throws IllegalArgumentException {
		// TODO: Deleting an app will not be immediately reflected to other
		// users until a new AppVet session is started. A better approach is to
		// simply update the app's status to "DELETED" and update users'
		// display.
		final boolean deletedDbEntries = Database.deleteApp(os, appid);
		if (deletedDbEntries) {
			final String appIdPath = AppVetProperties.APPS_ROOT + "/" + appid;
			final File appDirectory = new File(appIdPath);
			FileUtil.deleteDirectory(appDirectory);
			final String iconPath = AppVetProperties.APP_IMAGES + "/" + appid
					+ ".png";
			File iconFile = new File(iconPath);
			if (iconFile.exists()) {
				iconFile.delete();
			}
			iconFile = null;
			log.debug(username + " deleted app " + appid);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Boolean deleteUser(String username) throws IllegalArgumentException {
		final boolean deletedUser = Database.deleteUser(username);
		if (deletedUser) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<AppInfoGwt> getAllApps(String username)
			throws IllegalArgumentException {
		return Database.getAllApps(username);
	}

	@Override
	public List<ToolStatusGwt> getToolsResults(DeviceOS os, String sessionId,
			String appId) throws IllegalArgumentException {
		return getToolsStatuses(os, sessionId, appId);
	}

	@Override
	public List<AppInfoGwt> getUpdatedApps(long lastClientUpdate,
			String username) throws IllegalArgumentException {
		return Database.getUpdatedApps(username, lastClientUpdate);
	}

	@Override
	public List<UserInfoGwt> getUsersList() throws IllegalArgumentException {
		return Database.getUsers();
	}

	@Override
	public Boolean removeSession(String sessionId)
			throws IllegalArgumentException {
		final String clientIpAddress = getThreadLocalRequest().getRemoteAddr();
		final boolean removedSession = Database.removeSession(sessionId,
				clientIpAddress);
		Database.clearExpiredSessions();
		if (removedSession) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Long updateSessionExpiration(String sessionId, long sessionTimeout)
			throws IllegalArgumentException {
		final String clientIpAddress = getThreadLocalRequest().getRemoteAddr();
		final long sessionValid = Database.isSessionExpired(sessionId,
				clientIpAddress);
		if (sessionValid == -1) {
			Database.clearExpiredSessions();
			return new Long(-1);
		} else {
			Database.updateSessionExpiration(sessionId, clientIpAddress,
					sessionTimeout);
			return new Long(sessionTimeout);
		}
	}

	public static List<ToolStatusGwt> getToolsStatuses(DeviceOS os,
			String sessionId, String appId) {
		final ArrayList<ToolStatusGwt> toolStatusList = 
				new ArrayList<ToolStatusGwt>();
		// Registration status
		ToolAdapter tool = ToolAdapter.getByToolId(os, "registration");
		ToolStatusGwt toolStatus = getToolStatusHtml(os, sessionId, appId, tool);
		if (toolStatus != null) {
			toolStatusList.add(toolStatus);
		}
		// App metadata status
		tool = ToolAdapter.getByToolId(os, "appinfo");
		toolStatus = getToolStatusHtml(os, sessionId, appId, tool);
		if (toolStatus != null) {
			toolStatusList.add(toolStatus);
		}
		ArrayList<ToolAdapter> tools = null;
		if (os == DeviceOS.ANDROID) {
			tools = AppVetProperties.androidTools;
		} else if (os == DeviceOS.IOS) {
			tools = AppVetProperties.iosTools;
		}
		// Tool and manual report uploaded statuses
		for (int i = 0; i < tools.size(); i++) {
			tool = tools.get(i);
			if (tool.analysisType == AnalysisType.TESTTOOL
					|| tool.analysisType == AnalysisType.REPORT ||
					tool.analysisType == AnalysisType.AUDIT) {
				toolStatus = getToolStatusHtml(os, sessionId, appId, tool);
				if (toolStatus != null) {
					toolStatusList.add(toolStatus);
				} else {
					log.error("Status for tool " + tool.id + " is null");
				}
			}
		}
		return toolStatusList;
	}

	private static ToolStatusGwt getToolStatusHtml(DeviceOS os,
			String sessionId, String appId, ToolAdapter tool) {
		if (tool == null) {
			log.error("Tool is null");
			return null;
		}
		final ToolStatusGwt toolStatusGwt = new ToolStatusGwt();
		toolStatusGwt.setAppId(appId);
		String websiteHrefTag = "";
		if (tool.webSite != null) {
			websiteHrefTag = "<a href=\"" + tool.webSite
					+ "\" target=\"_blank\">" + tool.name + "</a>";
		} else {
			websiteHrefTag = tool.name;
		}
		toolStatusGwt.setAnalysisType(tool.analysisType);
		toolStatusGwt.setToolDisplayName(websiteHrefTag);
		boolean toolCompleted = false;
		final ToolStatus toolStatus = ToolStatusManager.getToolStatus(os,
				appId, tool.id);
		if (toolStatus == null) {
			log.warn(appId + ", " + tool.id + "-status: null!");
		}
		// ---------------------- Compute Tool Status
		// ---------------------------
		if (toolStatus == null) {
			// Status for a tool may be null if it was recently installed
			// but not run for previously submitted apps. In such cases,
			// we return an NA status.
			ToolStatusManager.setToolStatus(os, appId, tool.id, ToolStatus.NA);
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tabledim\" style='color: gray'>N/A</div>");
		} else if (toolStatus == ToolStatus.NA) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tabledim\" style='color: gray'>N/A</div>");
		} else if (toolStatus == ToolStatus.HIGH) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitembad\" style='color: red'>HIGH</div>");
		} else if (toolStatus == ToolStatus.ERROR) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitembad\" style='color: black'>ERROR</div>");
		} else if (toolStatus == ToolStatus.MODERATE) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitembad\" style='color: orange'>MODERATE</div>");
		} else if (toolStatus == ToolStatus.LOW) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitemendorsed\" style='color: green'>LOW</div>");
		} else if (toolStatus == ToolStatus.SUBMITTED) {
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitem\" style='color: black'>SUBMITTED</div>");
		} else {
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitem\" style='color: black'>"
							+ toolStatus.name() + "</div>");
		}
		// ------------------------ Attach security report
		// ----------------------
		final String reportsPath = AppVetProperties.APPS_ROOT + "/" + appId
				+ "/reports";
		File reportFile = new File(reportsPath + "/" + tool.reportName);
		if (toolStatus == null || toolStatus == ToolStatus.NA
				|| !reportFile.exists()) {
			toolStatusGwt
					.setReport("<div id=\"tabledim\" style='color: gray'>N/A</div>");
		} else if (toolCompleted) {
			// Make sure we do not cache this report since any new reports will
			// override this URL with the same filename. Make sure to set
			// cachingAllowed="false" in Tomcat context.xml.
			final String dateString = "?nocache" + new Date().getTime();
			if (reportFile.exists()) {
				toolStatusGwt.setReport("<a href=\""
						+ AppVetProperties.SERVLET_URL + dateString + "&"
						+ AppVetParameter.COMMAND.value + "="
						+ AppVetServletCommand.GET_TOOL_REPORT.name() + "&"
						+ AppVetParameter.APPID.value + "=" + appId + "&"
						+ AppVetParameter.SESSIONID.value + "=" + sessionId
						+ "&" + AppVetParameter.TOOLID.value + "=" + tool.id
						+ "\" target=\"_blank\">Report</a>");
			}
		} else {
			toolStatusGwt
					.setReport("<div id=\"tabledim\" style='color: gray'>Unavailable</div>");
		}
		return toolStatusGwt;
	}

	@Override
	public Boolean updateSelf(UserInfoGwt userInfo)
			throws IllegalArgumentException {
		return Database.updateUser(userInfo);
	}

	@Override
	public Boolean updateUserToolCredentials(String username,
			ArrayList<UserToolCredentialsGwt> credentialsList)
			throws IllegalArgumentException {
		// Update user's tool credentials in user table
		Database.saveUserToolCredentials(username, credentialsList);
		return true;
	}
}
