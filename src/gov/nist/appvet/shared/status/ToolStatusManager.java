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
package gov.nist.appvet.shared.status;

import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.shared.Database;
import gov.nist.appvet.shared.Logger;
import gov.nist.appvet.shared.analysis.AnalysisType;
import gov.nist.appvet.shared.os.DeviceOS;
import gov.nist.appvet.toolmgr.ToolAdapter;

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
		String toolStatusName = getToolStatusName(os, appid, toolId);
		return ToolStatus.getStatus(toolStatusName);
	}

	private static String getToolStatusName(DeviceOS os,
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

	public synchronized static void setToolStatus(DeviceOS os, String appId,
			String toolId, ToolStatus toolStatus) {
		if (os == DeviceOS.ANDROID) {
			Database.update("UPDATE androidtoolstatus SET " + toolId + "='"
					+ toolStatus.name() + "' where appid='" + appId + "'");
		} else if (os == DeviceOS.IOS) {
			Database.update("UPDATE iostoolstatus SET " + toolId + "='"
					+ toolStatus.name() + "' where appid='" + appId + "'");
		}
		Database.setAppIsUpdated(appId, true);
		computeAppStatus(os, appId);
	}

	/**
	 * @return true if app status was computed successfully, false otherwise.
	 */
	private synchronized static void computeAppStatus(DeviceOS os, String appId) {
		// Get current app status
		final AppStatus appStatus = AppStatusManager.getAppStatus(appId);

		// If audit report exists, use it to override the app's status. Note 
		// that an audit can only have a status of LOW, MODERATE, or HIGH.
		ToolAdapter auditTool = ToolAdapter.getByToolId(os, "audit");
		ToolStatus auditStatus = getToolStatus(os, appId, auditTool.toolId);
		if (auditStatus != null && auditStatus != ToolStatus.NA) {
			if (auditStatus == ToolStatus.LOW) {
				AppStatusManager.setAppStatus(appId, AppStatus.LOW);
				return;
			} else if (auditStatus == ToolStatus.MODERATE) {
				AppStatusManager.setAppStatus(appId, AppStatus.MODERATE);
				return;
			} else if (auditStatus == ToolStatus.HIGH) {
				AppStatusManager.setAppStatus(appId, AppStatus.HIGH);
				return;
			}
		}
		// Audit report has not been set, so compute app status based on
		// preprocessing (registration and metadata) and tool results.
        // Registration. Note that registrations can only have a status of LOW
		// or ERROR.
		ToolAdapter registrationTool = ToolAdapter.getByToolId(os,
				"registration");
		ToolStatus registrationStatus = getToolStatus(os, appId,
				registrationTool.toolId);
		if (registrationStatus == ToolStatus.ERROR) {
			AppStatusManager.setAppStatus(appId, AppStatus.ERROR);
			return;
		} else if (registrationStatus == ToolStatus.LOW) {
			if (appStatus == AppStatus.REGISTERING) {
				AppStatusManager.setAppStatus(appId, AppStatus.PENDING);
				return;
			}
		}
		// App metadata. Note that we only change app status if metadata
		// ERROR or if metadata LOW while app status is REGISTERING
		ToolAdapter metadataTool = ToolAdapter.getByToolId(os, "appinfo");
		ToolStatus metadataStatus = getToolStatus(os, appId, metadataTool.toolId);
		if (metadataStatus == ToolStatus.ERROR) {
			AppStatusManager.setAppStatus(appId, AppStatus.ERROR);
			return;
		} else if (metadataStatus == ToolStatus.SUBMITTED) {
			if (appStatus == AppStatus.PENDING){
				AppStatusManager.setAppStatus(appId, AppStatus.PROCESSING);
			}
			return;
		}
		
		int numTools = 0;
		if (os == DeviceOS.ANDROID) {
			numTools = AppVetProperties.androidTools.size();
		} else if (os == DeviceOS.IOS) {
			numTools = AppVetProperties.iosTools.size();
		}
		// Compute the number of each TESTTOOL or REPORTS status type. 
		int numToolErrors = 0;
		int numToolHighs = 0;
		int numToolModerates = 0;
		int numToolLows = 0;
		int numToolSubmitted = 0;
		int numToolNAs = 0;
		for (int i = 0; i < numTools; i++) {
			ToolAdapter tool = null;
			if (os == DeviceOS.ANDROID) {
				tool = AppVetProperties.androidTools.get(i);
			} else if (os == DeviceOS.IOS) {
				tool = AppVetProperties.iosTools.get(i);
			}
			if (tool.analysisType == AnalysisType.TESTTOOL ||
					tool.analysisType == AnalysisType.REPORT) {
				ToolStatus toolStatus = getToolStatus(os, appId,
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
		// Set app status based on TESTTOOL and REPORT statuses
	    if (numToolSubmitted > 0) {
	    	AppStatusManager.setAppStatus(appId, AppStatus.PROCESSING);
	    } else if (numToolErrors > 0) {
			AppStatusManager.setAppStatus(appId, AppStatus.ERROR);
		} else if (numToolHighs > 0) {
			AppStatusManager.setAppStatus(appId, AppStatus.HIGH);
		} else if (numToolModerates > 0) {
			AppStatusManager.setAppStatus(appId, AppStatus.MODERATE);
		} else if (numToolLows > 0) {
			AppStatusManager.setAppStatus(appId, AppStatus.LOW);	
		} else {
			AppStatusManager.setAppStatus(appId, AppStatus.NA);
		}
	}
}
