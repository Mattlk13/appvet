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

import org.eclipse.jetty.util.log.Log;

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

	public static synchronized boolean setToolStatus(DeviceOS os, String appId,
			String toolId, ToolStatus toolStatus) {
		log.info("trace a - " + appId);

		if (toolStatus == ToolStatus.ERROR){
			log.warn("Setting " + toolId + " to ERROR for " + appId);
		}
		log.info("trace b - " + appId);

		if (os == DeviceOS.ANDROID) {
			if (!Database.update("UPDATE androidtoolstatus SET " + toolId + "='"
					+ toolStatus.name() + "' where appid='" + appId + "'")) {
				log.error("Could not update Android tool " + toolId + " to " + toolStatus.name());
				return false;
			} else {
				log.debug("Updated Android tool " + toolId + " to " + toolStatus.name());
			}
		} else if (os == DeviceOS.IOS) {
			if (!Database.update("UPDATE iostoolstatus SET " + toolId + "='"
					+ toolStatus.name() + "' where appid='" + appId + "'")) {
				log.error("Could not update iOS tool " + toolId + " to " + toolStatus.name());
				return false;
			} else {
				log.debug("Updating iOS tool " + toolId + " to " + toolStatus.name());
			}
		} else {
			log.error("Unknown operating system encountered. Returning.");
			return false;
		}
		
//		if (updatedApp) {
//			log.debug("Set updated flag for app " + appId);
//			return computeAppStatus(os, appId);
//		} else {
//			log.error("Could not set updated flag for app " + appId);
//			return false;
//		}
		log.info("trace c - " + appId);

		if (computeAppStatus(os, appId)) {
			return Database.setLastUpdated(appId);

		} else {
			return false;
		}

	}
	

	/**
	 * @return true if app status was computed successfully, false otherwise.
	 */
	private synchronized static boolean computeAppStatus(DeviceOS os, String appId) {
		log.info("trace aa - " + appId);

		log.debug("Updating status for app " + appId + "...");
		
		// Get current app status
		final AppStatus appStatus = AppStatusManager.getAppStatus(appId);
		log.debug("Current app status for app " + appId + ": " + appStatus.name());
		
		// If audit report exists, use it to override the app's status. Note 
		// that an audit can only have a status of LOW, MODERATE, or HIGH.
		ToolAdapter auditTool = ToolAdapter.getByToolId(os, "audit");
		if (auditTool == null) {
			log.debug("Audit tool not found for app " + appId);
		} else {
			ToolStatus auditStatus = getToolStatus(os, appId, auditTool.toolId);
			if (auditStatus == null || auditStatus == ToolStatus.NA) {
				log.debug("Audit tool exists but status is not available");
			} else if (auditStatus != null && auditStatus != ToolStatus.NA){
				if (auditStatus == ToolStatus.LOW) {
					AppStatusManager.setAppStatus(appId, AppStatus.LOW);
					return true;
				} else if (auditStatus == ToolStatus.MODERATE) {
					AppStatusManager.setAppStatus(appId, AppStatus.MODERATE);
					return true;
				} else if (auditStatus == ToolStatus.HIGH) {
					AppStatusManager.setAppStatus(appId, AppStatus.HIGH);
					return true;
				}
			}
		}

		// Audit report has not been set, so compute app status based on
		// preprocessing (registration and metadata) and tool results.
        // Registration. Note that registrations can only have a status of LOW
		// or ERROR.
		ToolAdapter registrationTool = ToolAdapter.getByToolId(os,
				"registration");
		log.info("trace bb - " + appId);

		if (registrationTool == null) {
			Log.info("trace cc - " + appId);

			log.error("Registration tool is null");
			return false;
		}
		log.info("trace dd - " + appId);

		ToolStatus registrationStatus = getToolStatus(os, appId,
				registrationTool.toolId);
		if (registrationStatus == null) {
			log.info("trace ee - " + appId);

			log.error("Registration status is null");
			return false;
		} 
		if (registrationStatus == ToolStatus.ERROR) {
			log.info("trace ff - " + appId);

			AppStatusManager.setAppStatus(appId, AppStatus.ERROR);
			return true;
		} else if (registrationStatus == ToolStatus.LOW) {
			log.info("trace gg - " + appId);

			if (appStatus == AppStatus.REGISTERING) {
				log.info("trace hh - " + appId);

				AppStatusManager.setAppStatus(appId, AppStatus.PENDING);
				return true;
			}
		}
		
		log.info("trace ii - " + appId);

		// App metadata. Note that we only change app status if metadata
		// ERROR or if metadata LOW while app status is REGISTERING
		ToolAdapter metadataTool = ToolAdapter.getByToolId(os, "appinfo");
		if (metadataTool == null) {
			log.info("trace jj - " + appId);

			log.error("Metadata tool is null");
			return false;
		}
		ToolStatus metadataStatus = getToolStatus(os, appId, metadataTool.toolId);
		log.info("appinfo status - " + appId + ": " + metadataStatus.name());
		if (metadataStatus == null) {
			log.info("trace kk - " + appId);

			log.error("Metadata status is null");
			return false;
		}
		if (metadataStatus == ToolStatus.NA){
			log.debug("Reg status while metadata null: " + registrationStatus.name());
			log.debug("App status is: " + Database.getAppStatus(appId));
			return false; // since no cchange made
		}
		if (metadataStatus == ToolStatus.ERROR) {
			log.info("trace ll - " + appId);

			AppStatusManager.setAppStatus(appId, AppStatus.ERROR);
			return true;
		} else if (metadataStatus == ToolStatus.SUBMITTED) {
			log.info("trace mm - " + appId);

			if (appStatus == AppStatus.PENDING){
				AppStatusManager.setAppStatus(appId, AppStatus.PROCESSING);
			}
			return true;
		}
		
		log.info("trace nn - " + appId);

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
			if (tool.toolType == ToolType.TESTTOOL ||
					tool.toolType == ToolType.REPORT) {
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
		log.info("trace oo - " + appId);

		// Set app status based on TESTTOOL and REPORT statuses
	    if (numToolSubmitted > 0) {
			log.info("trace pp - " + appId);

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
			log.info("trace qq - " + appId);

			AppStatusManager.setAppStatus(appId, AppStatus.NA);
		}
	    
	    return true;
	}
}
