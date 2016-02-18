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

/**
 * This class supports the setting and getting of an app's status.
 * 
 * @author steveq@nist.gov
 */
public class AppStatusManager {
	private static final Logger log = AppVetProperties.log;
	
	private AppStatusManager() {
	}

	public synchronized static boolean setAppStatus(String appId,
			AppStatus appStatus) {
		log.info("trace 11 - " + appId);

		if (appId == null || appId.isEmpty()) {
			log.error("App ID is null or empty");
			return false;
		} else {
			if (appStatus == null) {
				log.error("App status is null");
				return false;
			}
		}
		log.info("trace 22 - " + appId);

		// Only update the status if the new status is different from the
		// current status (reduces GUI refresh).
		final String currentAppStatusString = getAppStatusName(appId);
		log.info("trace 33 - " + appId);

		if (currentAppStatusString == null) {
			log.error("Current app " + appId + " status string is null");
			return false;
		} else {
			log.debug("Current app " + appId + " status string is: " + currentAppStatusString);
		}
		
		log.info("trace 44 - " + appId);

		final AppStatus currentAppStatus = AppStatus
				.getStatus(currentAppStatusString);
		if (currentAppStatus == null) {
			log.error("Current app " + appId + " status is null");
			return false;
		} else {
			log.debug("Current app " + appId + " status is " + currentAppStatus.name());
		}
		
		log.info("trace 55 - " + appId);

		if (appStatus != currentAppStatus) {
			log.debug("appstatus: " + appStatus.name() + ", currentStatus: " + currentAppStatus.name());
			log.info("trace 66 - " + appId);

			log.debug("New status is different than current status for app " + appId);
			final String sql = "UPDATE apps SET appstatus='" + appStatus.name()
					+ "' where appid='" + appId + "'";
			log.debug("SQL: " + sql);
			if (Database.update(sql)) {
				log.info("trace 77 - " + appId);

				if (Database.setLastUpdated(appId)) {
					log.info("trace 88 - " + appId);

					log.debug("App " + appId + " updated mod time.");
					return true;
				} else {
					log.error("App " + appId + " update flag not set successfully.");
					return false;
				}
			} else {
				log.info("trace 99 - " + appId);

				log.error("Could not update status for app " + appId);
				return false;
			}
		} else {
			log.info("trace 100 - " + appId);

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
