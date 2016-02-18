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

import gov.nist.appvet.shared.all.AppStatus;

/**
 * This class supports the setting and getting of an app's status. The app
 * status lifecycle for an app is as follows:
 * 
 * - When app is uploaded and processed by Registration.java, the app moves
 *   from a null state to the REGISTERING state.
 * - When an app has completed registration, it moves REGISTERING state
 *   to the PENDING state.
 * - The ToolMgr looks for apps in a PENDING state to process. When the ToolMgr
 *   selects an app, that app moves from the PENDING state to the 
 *   PROCESSING state and the app then performs its metadata analysis.
 * - After the apps performs its metadata processing, it moves from the
 *   PROCESSING state to the NA state. This is so because the app state at
 *   this point is based on the state of the available tools which are NA 
 *   due to the fact that the tools have not yet been launched (i.e., the app 
 *   has not yet been SUBMITTED to the tools). If no tools are available to 
 *   launch (i.e., to put into a SUBMITTED state), then the app remains in the 
 *   NA state.
 * - If tools are available to process the app, the ToolMgr sets each tool 
 *   into a SUBMITTED state which changes the state of the app from NA back
 *   to PROCESSING.
 * - After a tool has completed processing, the tool will move from the
 *   SUBMITTING state to an HIGH, MODERATE, LOW, or ERROR state. This will 
 *   change the state of the app depending on the policy encoded in 
 *   ToolStatusManager.computeAppStatus().  
 * 
 * @author steveq@nist.gov
 */
public class AppStatusManager {
	private static final Logger log = AppVetProperties.log;
	
	private AppStatusManager() {
	}

	public synchronized static boolean setAppStatus(String appId,
			AppStatus appStatus) {

		if (appId == null || appId.isEmpty()) {
			log.error("App ID is null or empty");
			return false;
		} else {
			if (appStatus == null) {
				log.error("App status is null");
				return false;
			}
		}

		// Only update the status if the new status is different from the
		// current status (reduces GUI refresh).
		final String currentAppStatusString = getAppStatusName(appId);

		if (currentAppStatusString == null) {
			log.error("Current app " + appId + " status string is null");
			return false;
		} else {
			log.debug("Current app " + appId + " status string is: " + currentAppStatusString);
		}
		

		final AppStatus currentAppStatus = AppStatus
				.getStatus(currentAppStatusString);
		if (currentAppStatus == null) {
			log.error("Current app " + appId + " status is null");
			return false;
		} else {
			log.debug("Current app " + appId + " status is " + currentAppStatus.name());
		}
		
		log.debug("For " + appId + ": appstatus: " + appStatus.name() + ", currentStatus: " + currentAppStatus.name());

		if (appStatus != currentAppStatus) {

			log.debug("New status is different than current status for app " + appId);
			final String sql = "UPDATE apps SET appstatus='" + appStatus.name()
					+ "' where appid='" + appId + "'";
			log.debug("SQL: " + sql);
			if (Database.update(sql)) {

				if (Database.setLastUpdatedTime(appId)) {

					log.debug("App " + appId + " updated mod time.");
					return true;
				} else {
					log.error("App " + appId + " update flag not set successfully.");
					return false;
				}
			} else {

				log.error("Could not update status for app " + appId);
				return false;
			}
		} else {

			log.debug("New status is the same as current status for app " + appId + ". Not updating status.");
			return true;
		}
	}

	private static String getAppStatusName(String appid) {
		return Database.getString("SELECT appstatus FROM apps "
				+ "where appid='" + appid + "'");
	}

	public static AppStatus getAppStatus(String appid) {
		String appStatusStr = getAppStatusName(appid);
		return AppStatus.getStatus(appStatusStr);
	}
}
