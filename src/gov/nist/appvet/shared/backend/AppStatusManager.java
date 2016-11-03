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
 * - When app is uploaded to AppVet, the app moves from a null state to
 *   a REGISTERING state.
 *   After registration of the app is complete, the app moves to a PENDING state.
 *   AppVet searches for the next available app in a PENDING state and launches
 *   the tool adapters for that app. When the tool adapters are launched, the
 *   tool adapters are set to a SUBMITTED state. If one or more tool adapters
 *   are in a SUBMITTED state, the app state will be set to a PROCESSING state.
 *   Note that AppVet processes apps sequentially (mostly to ensure that third-party
 *   tools do not experience processing issues). 
 * - After a tool has completed processing, the tool will move from the
 *   SUBMITTED state to a HIGH, MODERATE, or LOW state. If an error occurs
 *   with a tool, that tool will be set to an ERROR state and will force the
 *   app state to a LOW*, MODERATE*, or HIGH* state indicating the next-highest
 *   severity level among all of the other tools.
 * 
 * @author steveq@nist.gov
 */
public class AppStatusManager {
	
	private AppStatusManager() {
	}

	public synchronized static boolean setAppStatus(AppInfo appInfo,
			AppStatus appStatus) {
		// Only update the status if the new status is different from the
		// current status (reduces GUI refresh).
		final String currentAppStatusString = getAppStatusName(appInfo.appId);
		if (currentAppStatusString == null) {
			return false;
		}
		final AppStatus currentAppStatus = AppStatus
				.getStatus(currentAppStatusString);
		if (currentAppStatus == null) {
			return false;
		}
		
		if (appStatus != currentAppStatus) {
			// Update app status
			final String sql = "UPDATE apps SET appstatus='" + appStatus.name()
					+ "' where appid='" + appInfo.appId + "'";
			if (Database.update(sql)) {
				appInfo.log.info("Setting app " + appInfo.appId + " status to " + appStatus.name());
				// Set last-updated time due to app status change
				if (Database.setLastUpdatedTime(appInfo.appId)) {
					return true;
				} else {
					return false;
				}
			} else {
				appInfo.log.error("Could not set app status to " + appStatus.name());
				return false;
			}
		} else {
			return true;
		}
	}

	private static String getAppStatusName(String appid) {
		return Database.getString("SELECT appstatus FROM apps "
				+ "where appid='" + appid + "'");
	}

	public synchronized static AppStatus getAppStatus(String appid) {
		String appStatusStr = getAppStatusName(appid);
		return AppStatus.getStatus(appStatusStr);
	}
}
