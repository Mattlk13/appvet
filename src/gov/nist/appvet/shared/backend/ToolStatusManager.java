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
package gov.nist.appvet.shared.backend;

import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.shared.all.AppStatus;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.ToolType;

/**
 * This class provides status information for tools and computes overall app
 * status.
 * 
 * @author steveq@nist.gov
 */
public class ToolStatusManager {
	
	private static final Logger log = AppVetProperties.log;

	
	private ToolStatusManager() {
	}

	
	public static ToolStatus getToolStatus(DeviceOS os,
			String appid, String toolId) {
		String toolStatusStr = getToolStatusStr(os, appid, toolId);
		return ToolStatus.getStatus(toolStatusStr);
	}

	
	// TODO: Change to getToolStatusStr
	private static String getToolStatusStr(DeviceOS os,
			String appid, String toolId) {
		if (os == DeviceOS.ANDROID) {
			return Database.getString("SELECT " + toolId
					+ " FROM androidtoolstatus where appid='" + appid + "'");
		} else if (os == DeviceOS.IOS) {
			return Database.getString("SELECT " + toolId
					+ " FROM iostoolstatus where appid='" + appid + "'");
		} else {
			// This should never occur. 
			log.error("Unknown OS for tool status");
			return null;
		}
	}

	
	public static synchronized void setToolStatus(AppInfo appInfo,
			String toolId, ToolStatus toolStatus) {
		String dbTableName = null;
		if (appInfo.os == DeviceOS.ANDROID) {
			dbTableName = "androidtoolstatus";			
		} else if (appInfo.os == DeviceOS.IOS) {
			dbTableName = "iostoolstatus";			
		}
		// Update tool status
		if (!Database.update("UPDATE " + dbTableName + " SET " + toolId + "='"
				+ toolStatus.name() + "' where appid='" + appInfo.appId + "'")) {
			appInfo.log.error("Could not set " + toolId + " status to " + toolStatus.name());
			return;
		} else {
			appInfo.log.info("Setting tool '" + toolId + "' status to " + toolStatus.name());
		}
		// Set last-updated time due to tool status change
		Database.setLastUpdatedTime(appInfo.appId);
		// Set app status due to possible change in tool status
		computeAppStatus(appInfo);
	}
	

	/**
	 * For more information on the app status lifecycle, see AppStateManager.java.
	 * @return true if app status was computed successfully, false otherwise.
	 */
	private synchronized static void computeAppStatus(AppInfo appInfo) {		
		// Get current app status
		final AppStatus appStatus = AppStatusManager.getAppStatus(appInfo.appId);
		
		// If audit report exists, use it to override the app's status. Note 
		// that an audit can only have a status of LOW, MODERATE, or HIGH.
		ToolAdapter auditTool = ToolAdapter.getByToolId(appInfo.os, "audit");
		if (auditTool == null) {
			// Audit tool is not being used. Continue.
		} else {
			ToolStatus auditStatus = getToolStatus(appInfo.os, appInfo.appId, auditTool.toolId);
			if (auditStatus == null || auditStatus == ToolStatus.NA) {
				// Audit tool exists but status is not available
			} else {
				if (auditStatus == ToolStatus.LOW) {
					AppStatusManager.setAppStatus(appInfo, AppStatus.LOW);
					return;
				} else if (auditStatus == ToolStatus.MODERATE) {
					AppStatusManager.setAppStatus(appInfo, AppStatus.MODERATE);
					return;
				} else if (auditStatus == ToolStatus.HIGH) {
					AppStatusManager.setAppStatus(appInfo, AppStatus.HIGH);
					return;
				}
			}
		}

		// Audit report has not been set, so compute app status based on
		// preprocessing (registration and metadata) and tool results.
        // Registration. Note that registrations can only have a status of LOW
		// or ERROR.
		ToolAdapter registrationTool = ToolAdapter.getByToolId(appInfo.os,
				"registration");
		if (registrationTool == null) {
			return;
		}
		
		ToolStatus registrationStatus = getToolStatus(appInfo.os, appInfo.appId,
				registrationTool.toolId);
		if (registrationStatus == null) {
			return;
		} 
		
		if (registrationStatus == ToolStatus.ERROR) {
			AppStatusManager.setAppStatus(appInfo, AppStatus.ERROR);
			return;
		} else if (registrationStatus == ToolStatus.LOW) {
			if (appStatus == AppStatus.REGISTERING) {
				AppStatusManager.setAppStatus(appInfo, AppStatus.PENDING);
				return;
			}
		}

		// App metadata. Note that we only change app status if metadata is
		// ERROR or if metadata LOW (COMPLETED) while app status is REGISTERING
		ToolAdapter metadataTool = ToolAdapter.getByToolId(appInfo.os, "appinfo");
		if (metadataTool == null) {
			return;
		}
		
		ToolStatus metadataStatus = getToolStatus(appInfo.os, appInfo.appId, metadataTool.toolId);	
		if (metadataStatus == null) {
			return;
		} else if (metadataStatus == ToolStatus.NA){
			return; // since no change made to default tool status value
		} else if (metadataStatus == ToolStatus.ERROR) {
			AppStatusManager.setAppStatus(appInfo, AppStatus.ERROR);
			return;
		} else if (metadataStatus == ToolStatus.SUBMITTED) {
			// When metadata is in SUBMITTED state, app goes into PROCESSING state
			if (appStatus == AppStatus.PENDING){
				AppStatusManager.setAppStatus(appInfo, AppStatus.PROCESSING);
			}
			return;
		}
		
		int numTools = 0;
		if (appInfo.os == DeviceOS.ANDROID) {
			numTools = AppVetProperties.androidTools.size();
		} else if (appInfo.os == DeviceOS.IOS) {
			numTools = AppVetProperties.iosTools.size();
		}
		// Compute the number of each TESTTOOL or REPORT status type. 
		int numToolErrors = 0;
		int numToolHighs = 0;
		int numToolModerates = 0;
		int numToolLows = 0;
		int numToolSubmitted = 0;
		//int numToolNAs = 0;
		for (int i = 0; i < numTools; i++) {
			ToolAdapter tool = null;
			if (appInfo.os == DeviceOS.ANDROID) {
				tool = AppVetProperties.androidTools.get(i);
			} else if (appInfo.os == DeviceOS.IOS) {
				tool = AppVetProperties.iosTools.get(i);
			} 
			if (tool != null) {
				if (tool.toolType == ToolType.TESTTOOL ||
						tool.toolType == ToolType.REPORT) {
					ToolStatus toolStatus = getToolStatus(appInfo.os, appInfo.appId,
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
						//numToolNAs++; 
					}
				}
			}

		}
		
		// If a tool has an ERROR but other tools have HIGH, MODERATE, or
		// LOW statuses, display the highest of these statuses with an 
		// asterisk (e.g., LOW*).
		boolean toolErrorExists = false;
		if (numToolErrors > 0) {
			toolErrorExists = true;
		}

		// Set app status based on TESTTOOL and REPORT statuses
	    if (numToolSubmitted > 0) {
	    	AppStatusManager.setAppStatus(appInfo, AppStatus.PROCESSING);
		} else if (numToolHighs > 0) {
			if (toolErrorExists) {
				AppStatusManager.setAppStatus(appInfo, AppStatus.HIGH_WITH_ERROR);
			} else {
				AppStatusManager.setAppStatus(appInfo, AppStatus.HIGH);
			}
		} else if (numToolModerates > 0) {
			if (toolErrorExists) {
				AppStatusManager.setAppStatus(appInfo, AppStatus.MODERATE_WITH_ERROR);
			} else {
				AppStatusManager.setAppStatus(appInfo, AppStatus.MODERATE);
			}			
		} else if (numToolLows > 0) {
			if (toolErrorExists) {
				AppStatusManager.setAppStatus(appInfo, AppStatus.LOW_WITH_ERROR);
			} else {
				AppStatusManager.setAppStatus(appInfo, AppStatus.LOW);
			}			
	    } else if (numToolErrors > 0) {
			AppStatusManager.setAppStatus(appInfo, AppStatus.ERROR);
		} else {
			// Check for NAs has been moved to end of ToolMgr.
			//AppStatusManager.setAppStatus(appInfo, AppStatus.NA);
		}
	    
	    return;
	}
}
