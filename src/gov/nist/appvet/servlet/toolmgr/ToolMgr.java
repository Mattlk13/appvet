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
package gov.nist.appvet.servlet.toolmgr;

import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.servlet.preprocessor.AndroidMetadata;
import gov.nist.appvet.servlet.preprocessor.IOSMetadata;
import gov.nist.appvet.servlet.shared.Emailer;
import gov.nist.appvet.shared.all.AppStatus;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.ToolType;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.backend.AppInfo;
import gov.nist.appvet.shared.backend.Database;
import gov.nist.appvet.shared.backend.FileUtil;
import gov.nist.appvet.shared.backend.Logger;
import gov.nist.appvet.shared.backend.ToolAdapter;
import gov.nist.appvet.shared.backend.ToolStatus;
import gov.nist.appvet.shared.backend.ToolStatusManager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author steveq@nist.gov
 */
public class ToolMgr implements Runnable {
	private static final Logger log = AppVetProperties.log;
	//private static final long TOOL_ADAPTER_SHUTDOWN_TIMEOUT = 10000;

	public ToolMgr() {
	}
	
	@Override
	public void run() {
		// Set the time that the app processing timeout will occur
		Date currentAppTimeout = null;
		
		for (;;) {
			// See AppVetProperties.xml (/AppVet/ToolServices/PollingInterval)
			delay(AppVetProperties.TOOL_MGR_POLLING_INTERVAL);
			// Get the currently processing app ID
			String currentProcessingAppId = Database.getCurrentProcessingAppId();
			if (currentProcessingAppId != null) {
				// An app is currently PROCESSING. Check if current app
				// has exceeded timeout.
				Date currentTime = new Date(System.currentTimeMillis());
				if (currentTime.after(currentAppTimeout)) {
					// Timeout occurred waiting for reports to be received
					// from one or more tools, so properly kill the tools.
					handleToolAdapterTimeout();
				}				
			} else {
				// Get the next PENDING app
				final String appid = Database.getNextApp(AppStatus.PENDING);
				if (appid != null) {
					// Set timeout for this app. See AppVetProperties.xml
					// (/AppVet/ToolServices/Timeout).
					currentAppTimeout = new Date(System.currentTimeMillis() + 
							AppVetProperties.ToolServiceProcessingTimeout);
					// Get app info
					AppInfo appInfo = new AppInfo(appid);
					final long startTime = new Date().getTime();
					appInfo.log.debug(MemoryUtil.getFreeHeap("ToolMgr.run()"));
					// Get app metadata.
					if (!getAppMetaData(appInfo)) {
						log.error("Could not retrieve metadata for app " + appid);
						cleanUpFiles(appInfo);
					} else {
						// Get all available tools. Note that if AppVet has
						// previously timed-out waiting for a report from a tool,
						// that tool will remain in availableTools but will have
						// its internal "serviceSuspended" flag set to true. In such
						// cases, AppVet will not send apps to those tools and
						// set the tool's status to N/A. To re-activate a 
						// tool adapter, you must ensure that no apps are 
						// currently in a SUBMITTED state and 
						// restart the AppVet web server.
						ArrayList<ToolAdapter> availableTools = null;
						if (appInfo.os == DeviceOS.ANDROID) {
							availableTools = AppVetProperties.androidTools;
						} else if (appInfo.os == DeviceOS.IOS) {
							availableTools = AppVetProperties.iosTools;
						} else {
							availableTools = new ArrayList<ToolAdapter>();
						}
						appInfo.log.debug("availableTools.size: "
								+ availableTools.size());
						boolean appFileAvailable = appInfo.getAppFileName() != null;
						for (int i = 0; i < availableTools.size(); i++) {
							//staggerStart(AppVetProperties.TOOL_MGR_STAGGER_INTERVAL);
							final ToolAdapter toolAdapter = availableTools.get(i);
							log.info("TOOL " + toolAdapter.toolId + " is suspended: " + toolAdapter.isServiceSuspended());
							
							// Only process test tools (not preprocessors,
							// audit, or
							// manual reports).
							if (toolAdapter.toolType == ToolType.TESTTOOL) {
								if (!appFileAvailable
										&& toolAdapter.appSubmitType == AppSubmitType.APP_FILE) {
									// Do not execute tool if app file is
									// required but
									// not available
									appInfo.log.warn("Skipping tool "
											+ toolAdapter.name
											+ " since app file is not available.");
								} else {
									// If app file available, we run through
									// tools that
									// require either app file or metadata
									if (toolAdapter.isServiceSuspended()) {
										// Do not start the tool adapter if it was suspended
										log.warn("Not starting tool '" + toolAdapter.toolId + "' as it has been suspended.");
									} else {
										// If the tool adapter was not disabled.
										toolAdapter.setApp(appInfo);
										final Thread thread = toolAdapter.getThread();
										appInfo.log.debug("App " + appInfo.appId
												+ " starting " + toolAdapter.name);
										thread.start();
										// Delay to keep processes from blocking
										delay(AppVetProperties.TOOL_MGR_STAGGER_INTERVAL);
									}
								}
							}
						}
						// Wait for tools to complete
						for (int i = 0; i < availableTools.size(); i++) {
							final ToolAdapter toolAdapter = availableTools.get(i);
							if (toolAdapter.toolType == ToolType.TESTTOOL) {
								if (!appFileAvailable
										&& toolAdapter.appSubmitType == AppSubmitType.APP_FILE) {
									// Do not wait for tool if app file was
									// required but
									// not available
								} else {
									if (toolAdapter.isServiceSuspended()) {
										log.warn("Not waiting for '" + toolAdapter.toolId + "' as it has been suspended.");
									} else {
										// If the tool adapter was not disabled.
										wait(appInfo, toolAdapter);
									}
								}
							}
						}
						// Stop tools if they are still running
						for (int i = 0; i < availableTools.size(); i++) {
							final ToolAdapter toolAdapter = availableTools.get(i);
							if (toolAdapter.toolType == ToolType.TESTTOOL) {
								if (!appFileAvailable
										&& toolAdapter.appSubmitType == AppSubmitType.APP_FILE) {
									// Do not clean up tool if app file was
									// required but
									// not available
								} else {
									if (toolAdapter.isServiceSuspended()) {
										log.warn("Not checking tool '" + toolAdapter.toolId + "' as it has been suspended.");
									} else {
										// If the tool adapter was not disabled.
										toolAdapter.shutdown(appInfo, true);
									}
								}
							}
						}
						
						// The following does not account for tools where reports
						// have not yet come in before the timeout
						final long endTime = new Date().getTime();
						final long elapsedTime = endTime - startTime;
						//appInfo.log.debug("Total elapsed: "
						//		+ Logger.formatElapsed(elapsedTime));
						
						appInfo.log.debug(MemoryUtil
								.getFreeHeap("End ToolMgr.run()"));
						availableTools = null;
						
						cleanUpFiles(appInfo);
					}
				}
			}
		}
	}
	
	public void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (final InterruptedException e) {
			log.error(e.toString());
		}
	}

	public void wait(AppInfo appInfo, ToolAdapter tool) {
		try {
			// Wait for tool adapter thread to shut down.
			tool.thread.join(AppVetProperties.ToolServiceProcessingTimeout);
			//appInfo.log.info(tool.toolId + " shutting down.");
		} catch (final InterruptedException e) {
			appInfo.log.error(tool.toolId + " shut down prematurely after " + 
					"AppVetProperties.ToolServiceProcessingTimeout = " + 
					+ AppVetProperties.ToolServiceProcessingTimeout + "ms");
			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId, tool.toolId,
					ToolStatus.ERROR);
/*			
			// Next, set the suspendedService flag for the tool to prevent
			// further apps from being sent to the tool
			appInfo.log.warn("Tool '" + tool.name + "' has experienced an error. Disabling '" + tool.name + "' adapter and suspending subsequent app submissions.");
			tool.serviceSuspended = true;
			
			// Email admins about the problem with the tool
			UserInfo userInfo = Database.getUserInfo(appInfo.ownerName, null);
			String emailSubject = "Tool '" + tool.name + "' has experienced an error. Disabling '" + tool.name + "' adapter and suspending subsequent app submissions.";
			String emailContent = "Tool '" + tool.name + "' has experienced an error. Disabling '" + tool.name + "' adapter and suspending subsequent app submissions.";
			Emailer.sendEmail(userInfo.getEmail(), emailSubject, emailContent);*/
		}
	}
	
	public void cleanUpFiles(AppInfo appInfo) {
		if (!AppVetProperties.KEEP_APPS) {
			// Remove app file
			final String appFilePath = appInfo.getAppFilePath();
			final File appFile = new File(appFilePath);
			if (appFile.exists()) {
				FileUtil.deleteFile(appFilePath, appInfo.appId);
				appInfo.log.debug("Removed " + appInfo.appName + " app file.");
			}
		}
		// Remove expanded (unzipped) app project directory		
		if (appInfo.os == DeviceOS.IOS) {
			// Remove zip file
			String zipPath = appInfo.getIdPath() + "/Received.zip";
			final File zipFile = new File(zipPath);
			if (zipFile.exists()) {
				FileUtil.deleteFile(zipPath, appInfo.appId);
				appInfo.log.debug("Removed " + zipPath);
			}
			// Remove expanded zip directory
			String expandedZipPath = appInfo.getIdPath() + "/Received";
			final File expandedZipFile = new File(expandedZipPath);
			if (expandedZipFile.exists()) {
				FileUtil.deleteDirectory(expandedZipFile);
				appInfo.log.debug("Removed " + expandedZipPath);
			}
		} else {
			// Removed expanded apk directory
			String projectPath = appInfo.getProjectPath();
			appInfo.log.debug("Project Path: " + projectPath);
			final File projectDirectory = new File(projectPath);
			if (projectDirectory.exists()) {
				FileUtil.deleteDirectory(projectDirectory);
				appInfo.log.debug("Removed " + projectPath);
			}
		}
		appInfo.log.close();
		System.gc();
	}
	
	private boolean getAppMetaData(AppInfo appInfo) {
		if (appInfo.os == DeviceOS.ANDROID) {
			AndroidMetadata androidMetadata = new AndroidMetadata();
			return androidMetadata.getMetadata(appInfo);
		} else if (appInfo.os == DeviceOS.IOS) {
			IOSMetadata iosMetadata = new IOSMetadata();
			return iosMetadata.getMetadata(appInfo);
		} else {
			appInfo.log.error("Unknown OS when getting app metadata");
			return false;
		}
	}
	
	/**
	 * If a tool is stuck in a SUBMITTED state, set the tool to ERROR and 
	 * set the app status from PROCESSING or ERROR to LOW*, MODERATE*, or HIGH*.
	 */
	public synchronized static void handleToolAdapterTimeout() {
		Connection connection = null;
		Statement statement = null;
		Statement statement2 = null;
		ResultSet appIDsResultSet = null;
		ResultSet appToolsResultSet = null;
		String sql = null;
		try {
			connection = Database.getConnection();
			// Select apps where app status is PROCESSING or ERROR
			sql = "SELECT * FROM apps WHERE appstatus='"
					+ AppStatus.PROCESSING.name() 
					//+ "' OR appstatus='" + AppStatus.ERROR.name() 
					+ "'";
			
			statement = connection.createStatement();
			appIDsResultSet = statement.executeQuery(sql);
			AppInfo appInfo = null;
			while (appIDsResultSet.next()) {
				String appId = appIDsResultSet.getString(1);
				appInfo = new AppInfo(appId);
				String deviceOs = Database.getOs(appId);
				String sql2 = null;
				ArrayList<String> tableColumnNames = new ArrayList<String>();
				if (deviceOs.equals(DeviceOS.ANDROID.name())) {
					sql2 = "SELECT * FROM androidtoolstatus WHERE appid='"
							+ appId + "'";
					tableColumnNames = Database.getTableColumnNames("androidtoolstatus");
				} else if (deviceOs.equals(DeviceOS.IOS.name())) {
					sql2 = "SELECT * FROM iostoolstatus WHERE appid='"
							+ appId + "'";
					tableColumnNames = Database.getTableColumnNames("iostoolstatus");
				}
				
				statement2 = connection.createStatement();
				appToolsResultSet = statement2.executeQuery(sql2);
				if (appToolsResultSet == null) {
					log.error("appToolsResultSet IS NULL");
				} else {
					while (appToolsResultSet.next()) {
						// Table column index starts at 1 (not 0). Skip appid at column 1.
						for (int i = 1; i < tableColumnNames.size(); i++) {
							String toolId = tableColumnNames.get(i);
							String toolStatus = appToolsResultSet.getString(i+1); // Add 1.
							
							if ((toolStatus != null) && (toolStatus.equals(ToolStatus.SUBMITTED.name()))) {
								if (appInfo.log == null) {
									log.error("AppInfo log is null");
								} else {
									appInfo.log.error("The '" + toolId + "' tool adapter has experienced an error for app " + appInfo.appId + 
											"'. Changing tool status from " + toolStatus + " to ERROR.");
								}
								
								// Next, set the suspendedService flag for the tool to prevent
								// further apps from being sent to the tool
								appInfo.log.warn("Disabling '" + toolId + "' adapter and suspending subsequent app submissions.");
								DeviceOS os = DeviceOS.valueOf(deviceOs);
								ToolAdapter toolAdapter = ToolAdapter.getByToolId(os, toolId);
								toolAdapter.setServiceSuspended(true);
								
								// Email admins about the problem with the tool
								UserInfo userInfo = Database.getUserInfo(appInfo.ownerName, null);
								String emailSubject = "Tool '" + toolId + "' has experienced an error. Disabling '" + toolId + "' adapter and suspending subsequent app submissions.";
								String emailContent = "Tool '" + toolId + "' has experienced an error. Disabling '" + toolId + "' adapter and suspending subsequent app submissions.";
								Emailer.sendEmail(userInfo.getEmail(), emailSubject, emailContent);
								
								ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId, toolId,
										ToolStatus.ERROR);
							} else {
								//appInfo.log.debug("NOT SETTING TOOL STATUS FOR " + toolId);
							}
						}
					}
				}
			}
		} catch (final SQLException e) {
			log.error(e.toString());
		} finally {
			sql = null;
			appToolsResultSet = null;
			appIDsResultSet = null;
			statement2 = null;
			statement = null;
			connection = null;
		}
	}
}