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
import gov.nist.appvet.shared.backend.AppStatusManager;
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
import java.sql.ResultSetMetaData;

/**
 * @author steveq@nist.gov
 */
public class ToolMgr implements Runnable {
	private static final Logger log = AppVetProperties.log;

	public ToolMgr() {
	}

	@Override
	public void run() {

		for (;;) {
			// Delay
			delay(AppVetProperties.TOOL_MGR_POLLING_INTERVAL);

			// Get the next PENDING app
			final String appid = Database.getNextApp(AppStatus.PENDING);
			
			if (appid != null) {
				// Get app info
				AppInfo appInfo = new AppInfo(appid);
				appInfo.log.info("*** PROCESSING APP " + appid);
				
				// Get app metadata.
				if (!getAppMetaData(appInfo)) {
					appInfo.log.error("Could not retrieve metadata for app " + appid);
					cleanUpFiles(appInfo);
				} else {
					// Get available tools for OS
					ArrayList<ToolAdapter> availableTools = null;
					if (appInfo.os == DeviceOS.ANDROID) {
						availableTools = AppVetProperties.androidTools;
					} else if (appInfo.os == DeviceOS.IOS) {
						availableTools = AppVetProperties.iosTools;
					}
					appInfo.log.debug("availableTools.size: "
							+ availableTools.size());
					// Start tool adapters
					appInfo.log.info("*** STARTING TOOL ADAPTERS FOR APPID: " + appid);
					ArrayList<Thread> threads = new ArrayList<Thread>();
					for (int i = 0; i < availableTools.size(); i++) {
						ToolAdapter toolAdapter = availableTools.get(i);

						// Only process test tools (not preprocessors,
						// audit, or manual reports).
						if (toolAdapter.toolType == ToolType.TESTTOOL) {
							if (toolAdapter.serviceIsDisabled()) {
								// Do not start the tool adapter if it was disabled
								appInfo.log.warn("Tool adapter '" + toolAdapter.toolId + "' is disabled. Not starting.");
							} else {
								// If the tool adapter was not disabled.
								toolAdapter.setApp(appInfo);
								Thread thread = new Thread(toolAdapter);
								appInfo.log.info("App " + appInfo.appId
										+ " starting " + toolAdapter.toolId);
								thread.start();
								threads.add(thread);
							}
						}
					}
					
					appInfo.log.info("*** WAITING FOR TOOL ADAPTERS TO COMPLETE FOR APPID: " + appid);
					try {
						for (int j = 0; j < threads.size(); j++) {
							Thread thread = threads.get(j);
							thread.join();
						}
						appInfo.log.info("*** ALL TOOL ADAPTERS HAVE CLOSED");

					} catch (InterruptedException e) {
						log.error(e.getMessage());
					}
					
					// Verify app and tool statuses before moving to next app
					verifyToolsAndAppEndStates(appInfo, availableTools);

					// Clean up
					cleanUpFiles(appInfo);
					availableTools = null;
				}
			}
		}
	}
	
	public void verifyToolsAndAppEndStates(AppInfo appInfo, 
			ArrayList<ToolAdapter> availableTools) {
		
		// Check if all tools are NA. If so, set app status to NA and return.
		appInfo.log.info("*** CHECKING ALL-NA TOOLS FOR APPID: " + appInfo.appId);
		if (allToolsAreNA(appInfo, availableTools)) {
			return;
		}
		
		// Handle tools stuck in SUBMITTED state (change to ERROR state)
		appInfo.log.info("*** CHECKING TOOLS THATHAVE TIMED-OUT FOR APPID: " + appInfo.appId);
		checkToolsInSubmittedState(appInfo);
		
		// Handle tools in ERROR state
		appInfo.log.debug("*** HANDLING TOOL ERRORS FOR APPID: " + appInfo.appId);
		handleToolAdapterErrors(appInfo, availableTools);
		
		// Verify the app is not in a PROCESSING state (it shouldn't be at this point)
		AppStatus appStatus = AppStatusManager.getAppStatus(appInfo.appId);
		if (appStatus == AppStatus.PROCESSING) {
			appInfo.log.error("TEST: App " + appInfo.appId + " is still in a PROCESSING state");
		} else {
			//appInfo.log.info("TEST: App " + appInfo.appId + " is not in a PROCESSING state");
		}

/*		// Verify if tools in ERROR state have suspendSUbmission flag set
		for (int i = 0; i < availableTools.size(); i++) {
			final ToolAdapter toolAdapter = availableTools.get(i);
			if (toolAdapter.toolType == ToolType.TESTTOOL) {
				if (toolAdapter.serviceIsDisabled()) {
					appInfo.log.info("TEST: Tool adapter '" + toolAdapter.toolId + "' was disabled.");
				} else {
					// If the tool adapter was not disabled.
					appInfo.log.info("TEST: Tool adapter '" + toolAdapter.toolId + "' was NOT disabled.");

				}
			}
		}*/
	}
	
	public boolean allToolsAreNA(AppInfo appInfo, ArrayList<ToolAdapter> availableTools) {
		int numTools = availableTools.size();

		// Compute the number of each TESTTOOL or REPORTS status type. 
		int numToolErrors = 0;
		int numToolHighs = 0;
		int numToolModerates = 0;
		int numToolLows = 0;
		int numToolSubmitted = 0;
		int numToolNAs = 0;
		for (int i = 0; i < numTools; i++) {
			ToolAdapter tool = null;
			tool = availableTools.get(i);

			if (tool != null) {
				if (tool.toolType == ToolType.TESTTOOL ||
						tool.toolType == ToolType.REPORT) {
					ToolStatus toolStatus = ToolStatusManager.getToolStatus(appInfo.os, appInfo.appId,
							tool.toolId);
					if (toolStatus == ToolStatus.ERROR) {
						numToolErrors++;
					} else if (toolStatus == ToolStatus.HIGH) {
						numToolHighs++;
					} else if (toolStatus == ToolStatus.MODERATE) {
						numToolModerates++;
					} else if (toolStatus == ToolStatus.LOW) {
						numToolLows++;
					} else if (toolStatus == ToolStatus.SUBMITTED) {
						numToolSubmitted++;
					} else if (toolStatus == ToolStatus.NA) {
						numToolNAs++; 
					}
				}
			}

		}
		if (numToolErrors == 0 &&
				numToolHighs == 0 &&
				numToolModerates == 0 &&
				numToolLows == 0 &&
				numToolSubmitted == 0 &&
				numToolNAs > 0) {
			
			// Only tool statuses of NA exist so set app status to NA
			appInfo.log.debug("All tools for app " + appInfo.appId + " have a status of NA. Setting app " + appInfo.appId + " status to NA.");
			AppStatusManager.setAppStatus(appInfo, AppStatus.NA);
			return true;
		} else {
			// No change
			return false;
		}
	}
	
	/** If a tool is stuck in SUBMITTED state, set to ERROR state. */
	public static void checkToolsInSubmittedState(AppInfo appInfo) {
		Connection connection = null;
		Statement statement = null;
		ResultSet toolStatuses = null;
		String sql = null;
		try {
			connection = Database.getConnection();
			// Select tools in SUBMITTED state
			if (appInfo.os == DeviceOS.ANDROID) {
				sql = "SELECT * FROM androidtoolstatus WHERE appid='" + appInfo.appId + "'";
			} else if (appInfo.os == DeviceOS.IOS) {
				sql = "SELECT * FROM iostoolstatus WHERE appid='" + appInfo.appId + "'";
			} else {
				// Unknown OS
				return;
			}
			statement = connection.createStatement();
			toolStatuses = statement.executeQuery(sql);
			while (toolStatuses.next()) {
				ResultSetMetaData rsmd = toolStatuses.getMetaData();
				int columnsNumber = rsmd.getColumnCount();
				for (int i = 2; i <= columnsNumber; i++) { // Skip appid in 1st col
					String toolId = rsmd.getColumnName(i);
					String toolStatus = toolStatuses.getString(i);
					appInfo.log.debug("Tool '" + toolId + "' in " + toolStatus + " state at end.");
					if (toolStatus != null && toolStatus.equals(ToolStatus.SUBMITTED.name())) {
						appInfo.log.error("Tool '" + toolId + 
								"' adapter timed-out waiting for report. "
								+ "Setting '" + toolId + "' status from SUBMITTED to ERROR.");
						ToolStatusManager.setToolStatus(appInfo, toolId,
								ToolStatus.ERROR);
					}
				}
			}
		} catch (final SQLException e) {
			appInfo.log.error(e.toString());
		} finally {
			sql = null;
			statement = null;
			connection = null;
		}
	}
	
	/**
	 * If a tool experienced an error, we need to disable the adapter for that
	 * tool and alert administrators via email.
	 */
	public synchronized static void handleToolAdapterErrors(AppInfo appInfo, 
			ArrayList<ToolAdapter> availableTools) {
		Connection connection = null;
		Statement statement = null;
		ResultSet appToolStatuses = null;
		String sql = null;
		try {
			connection = Database.getConnection();
			// Select tools in SUBMITTED state
			if (appInfo.os == DeviceOS.ANDROID) {
				sql = "SELECT * FROM androidtoolstatus WHERE appid='" + appInfo.appId + "'";
			} else if (appInfo.os == DeviceOS.IOS) {
				sql = "SELECT * FROM iostoolstatus WHERE appid='" + appInfo.appId + "'";
			} else {
				// Unknown OS
				return;
			}

			statement = connection.createStatement();
			appToolStatuses = statement.executeQuery(sql);
			while (appToolStatuses.next()) {
				ResultSetMetaData rsmd = appToolStatuses.getMetaData();
				int columnsNumber = rsmd.getColumnCount();
				for (int i = 2; i <= columnsNumber; i++) { // Skip appid in 1st col
					String toolId = rsmd.getColumnName(i);
					String toolStatus = appToolStatuses.getString(i);
					
					if (toolStatus != null && toolStatus.equals(ToolStatus.ERROR.name())) {
						// Next, set the suspendedService flag for the tool to prevent
						// further apps from being sent to the tool
						appInfo.log.warn("Disabling '" + toolId + "' adapter and suspending subsequent app submissions.");
						ToolAdapter toolAdapter = getToolAdapter(toolId, availableTools);
						toolAdapter.disableService();

						// Email admins about the problem with the tool
						UserInfo userInfo = Database.getUserInfo(appInfo.ownerName, null);
						String errorMessage = "Tool '" + toolId + "' encountered an ERROR. Disabling '" + toolId + "' adapter and suspending subsequent app submissions.";						
						String emailSubject = errorMessage;
						String emailContent = errorMessage;
						Emailer.sendEmail(userInfo.getEmail(), emailSubject, emailContent);
					}
				}
			}
		} catch (final SQLException e) {
			appInfo.log.error(e.toString());
		} finally {
			sql = null;
			statement = null;
			connection = null;
		}
	}
	
	/** Handle app stuck in PROCESSING state. This method is called at launch
	 * of AppVet in case AppVet was previously shut down during the processing 
	 * of an app. Here, apps stuck in a PROCESSING state are due to one or 
	 * more tools stuck in a SUBMITTED state. To address this issue, this 
	 * method finds all apps stuck in a PROCESSING state and sets any of its
	 * tools stuck in a SUBMITTED state to an ERROR state. */
	public static void handleStuckAppProcessing() {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet appStatus = null;
		String sql = null;
		try {
			connection = Database.getConnection();
			// Select apps where app status is PROCESSING
			sql = "SELECT * FROM apps WHERE appstatus='PROCESSING'";

			statement = connection.createStatement();
			appStatus = statement.executeQuery(sql);
			while (appStatus.next()) {
				// Get app ID
				String appId = appStatus.getString(1);
				AppInfo appInfo = new AppInfo(appId);
				checkToolsInSubmittedState(appInfo);
			}
		} catch (final SQLException e) {
			log.error(e.toString());
		} finally {
			sql = null;
			statement = null;
			connection = null;
		}
	}

	public void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (final InterruptedException e) {
			log.error(e.toString());
		}
	}

	public void cleanUpFiles(AppInfo appInfo) {
		appInfo.log.info("CLEANING UP FOR APPID: " + appInfo.appId);

		if (!AppVetProperties.KEEP_APPS) {
			// Remove app file
			final String appFilePath = appInfo.getAppFilePath();
			final File appFile = new File(appFilePath);
			if (appFile.exists()) {
				FileUtil.deleteFile(appFilePath, appInfo.appId);
				appInfo.log.debug("Removed " + appInfo.appName + " app file.");
			}
		}
		if (appInfo.os == DeviceOS.IOS) {
			// Remove expanded (unzipped) app project directory		
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
		} else if (appInfo.os == DeviceOS.ANDROID){
			// Removed expanded apk directory
			String projectPath = appInfo.getProjectPath();
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
	
	public static ToolAdapter getToolAdapter(String toolId, ArrayList<ToolAdapter> availableTools) {
		if (availableTools != null) {
			for (int i = 0; i < availableTools.size(); i++) {
				ToolAdapter toolAdapter = availableTools.get(i);
				if (toolId.equals(toolAdapter.toolId)) {
					return toolAdapter;
				}
			}
		} 			
		return null;
	}
	
}