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
package gov.nist.appvet.toolmgr;

import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.shared.AppSubmitType;
import gov.nist.appvet.shared.Database;
import gov.nist.appvet.shared.FileUtil;
import gov.nist.appvet.shared.Logger;
import gov.nist.appvet.shared.MemoryUtil;
import gov.nist.appvet.shared.analysis.AnalysisType;
import gov.nist.appvet.shared.app.AppInfo;
import gov.nist.appvet.shared.os.DeviceOS;
import gov.nist.appvet.shared.status.AppStatus;
import gov.nist.appvet.shared.status.ToolStatus;
import gov.nist.appvet.shared.status.ToolStatusManager;
import gov.nist.appvet.tools.preprocessor.AndroidMetadata;
import gov.nist.appvet.tools.preprocessor.IOSMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author steveq@nist.gov
 */
public class ToolMgr implements Runnable {
	private static final Logger log = AppVetProperties.log;
	private static final long TOOL_ADAPTER_SHUTDOWN_TIMEOUT = 10000;

	public ToolMgr() {
		log.info("*** Starting AppVet Tool Manager "
				+ AppVetProperties.APPVET_VERSION + " on "
				+ AppVetProperties.URL);
	}

	public void removeAppFiles(AppInfo appInfo) {
		log.debug("Removing app files from: " + appInfo.getIdPath());
		if (appInfo.os == DeviceOS.IOS) {
			// Newly received iOS apps use "Receiving...zip" as the expanded
			// name.
			String zipPath = appInfo.getIdPath() + "/Receiving....zip";
			final File zipFile = new File(zipPath);
			// Remove zip directory (i.e., decompressed app file)
			if (zipFile.exists()) {
				// This directory will only exist if an IPA file was uploaded.
				FileUtil.deleteFile(zipPath);
				appInfo.log.debug("Removed " + appInfo.appName + " project.");
			} else {
				appInfo.log.error("App file does not exist at: " + zipPath);
			}
		} else {
			String projectPath = appInfo.getProjectPath();
			log.debug("Project Path: " + projectPath);
			final File projectDirectory = new File(projectPath);
			// Remove project directory (i.e., decompressed app file)
			if (projectDirectory.exists()) {
				// This directory will only exist if an APK file was uploaded.
				FileUtil.deleteDirectory(projectDirectory);
				appInfo.log.debug("Removed " + appInfo.appName
						+ " project directory.");
			}
		}
		// Remove app file
		final String appFilePath = appInfo.getAppFilePath();
		final File appFile = new File(appFilePath);
		if (appFile.exists()) {
			FileUtil.deleteFile(appFilePath);
			appInfo.log.debug("Removed " + appInfo.appName + " app file.");
		}
		appInfo.log.close();
		System.gc();
	}

	private boolean getAppMetaData(AppInfo appInfo) {
		if (appInfo.os == DeviceOS.ANDROID) {
			if (appInfo.getAppFileName() != null) {
				return AndroidMetadata.getFromFile(appInfo);
			} else {
				log.error("No file provided to acquire metadata.");
				return false;
			}
		} else if (appInfo.os == DeviceOS.IOS) {
			if (appInfo.getAppFileName() != null) {
				return IOSMetadata.getFromFile(appInfo);
			} else {
				log.error("No file provided to acquire metadata.");
				return false;
			}
		} else {
			log.error("Unknown OS");
			return false;
		}
	}

	@Override
	public void run() {
		// Set the time that the app processing timeout will occur
		Date currentAppTimeout = new Date(System.currentTimeMillis() + AppVetProperties.APP_PROCESSING_TIMEOUT);
		for (;;) {
			delay(AppVetProperties.TOOL_MGR_POLLING_INTERVAL); // Wait to see if another app is processing
			String currentProcessingAppId = Database.getCurrentProcessingAppId();
			if (currentProcessingAppId != null) {
				// An app already has a status of PROCESSING so no other apps
				// will be processed. 
				//log.debug("An app is already processing. Waiting...");
				
				// Check if current app has exceeded processing timeout
				Date currentTime = new Date(System.currentTimeMillis());
				if (currentTime.after(currentAppTimeout)) {
					// Timeout occurred waiting for one or more reports to be received
					// Get app info to set the status of these tools to ERROR
					String appOS = Database.getOs(currentProcessingAppId);
					Database.killProcessingTools(currentProcessingAppId, DeviceOS.getOS(appOS));
				}				
			} else {
				AppInfo appInfo = null;
				final String appid = Database.getNextApp(AppStatus.PENDING);
				if (appid != null) {
					log.debug("App " + appid + " now processing.");
					final long startTime = new Date().getTime();
					log.debug(MemoryUtil.getFreeHeap("ToolMgr.run()"));
					appInfo = new AppInfo(appid);
					// Get app metadata.
					if (!getAppMetaData(appInfo) && !AppVetProperties.KEEP_APPS) {
						// If we can't get metadata info for the app, this is an
						// error and we should set an error this app and remove
						// all related files unless KEEP_APPS is true.
						removeAppFiles(appInfo);
					} else {
						// Get all available tools
						ArrayList<ToolAdapter> availableTools = null;
						if (appInfo.os == DeviceOS.ANDROID) {
							availableTools = AppVetProperties.androidTools;
						} else if (appInfo.os == DeviceOS.IOS) {
							availableTools = AppVetProperties.iosTools;
						}
						log.debug("availableTools.size: "
								+ availableTools.size());
						boolean appFileAvailable = appInfo.getAppFileName() != null;
						for (int i = 0; i < availableTools.size(); i++) {
							//staggerStart(AppVetProperties.TOOL_MGR_STAGGER_INTERVAL);
							final ToolAdapter tool = availableTools.get(i);
							// Only process test tools (not preprocessors,
							// audit, or
							// manual reports).
							if (tool.analysisType == AnalysisType.TESTTOOL) {
								if (!appFileAvailable
										&& tool.appSubmitType == AppSubmitType.APP_FILE) {
									// Do not execute tool if app file is
									// required but
									// not available
									log.debug("Skipping tool "
											+ tool.name
											+ " since app file is not available.");
								} else {
									// If app file available, we run through
									// tools that
									// require either app file or metadata
									tool.setApp(appInfo);
									final Thread thread = tool.getThread();
									log.debug("App " + appInfo.appId
											+ " starting " + tool.name);
									thread.start();
									// Delay to keep processes from blocking
									delay(AppVetProperties.TOOL_MGR_STAGGER_INTERVAL);
								}
							}
						}
						// Wait for tools to complete
						for (int i = 0; i < availableTools.size(); i++) {
							final ToolAdapter tool = availableTools.get(i);
							if (tool.analysisType == AnalysisType.TESTTOOL) {
								if (!appFileAvailable
										&& tool.appSubmitType == AppSubmitType.APP_FILE) {
									// Do not wait for tool if app file was
									// required but
									// not available
								} else {
									wait(appInfo, tool);
								}
							}
						}
						// Stop tools if they are still running
						for (int i = 0; i < availableTools.size(); i++) {
							final ToolAdapter tool = availableTools.get(i);
							if (tool.analysisType == AnalysisType.TESTTOOL) {
								if (!appFileAvailable
										&& tool.appSubmitType == AppSubmitType.APP_FILE) {
									// Do not clean up tool if app file was
									// required but
									// not available
								} else {
									tool.shutdown(appInfo, true);
								}
							}
						}
						final long endTime = new Date().getTime();
						final long elapsedTime = endTime - startTime;
						appInfo.log.info("Total elapsed: "
								+ Logger.formatElapsed(elapsedTime));
						appInfo.log.debug(MemoryUtil
								.getFreeHeap("End ToolMgr.run()"));
						availableTools = null;
						if (!AppVetProperties.KEEP_APPS) {
							removeAppFiles(appInfo);
						}

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
			tool.thread.join(TOOL_ADAPTER_SHUTDOWN_TIMEOUT);
			log.debug("Tool " + tool.toolId + " shut down.");
		} catch (final InterruptedException e) {
			appInfo.log.warn("Tool " + tool.toolId + " shut down after "
					+ AppVetProperties.APP_PROCESSING_TIMEOUT + "ms timeout");
			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId, tool.toolId,
					ToolStatus.ERROR);
		}
	}
}