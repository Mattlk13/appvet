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

import gov.nist.appvet.gwt.shared.AppInfoGwt;
import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.shared.all.DeviceOS;

import java.io.File;

import org.apache.commons.fileupload.FileItem;

/**
 * This class is used to define metadata of an app or other uploaded file
 * including report files. This class is not used by GWT clients as they cannot
 * invoke method invocations defined here (e.g., database access). This class,
 * however, may be used by all other AppVet components.
 * 
 * @author steveq@nist.gov
 */
public class AppInfo extends AppInfoGwt {
	public static final long serialVersionUID = 1L;
	/** Used to log all app-specific output to global AppVet log. */
	private static final Logger appvetLog = AppVetProperties.log;
	/** Logs all app-specific output to the app's log file. */
	public Logger log = null;
	/**
	 * Contains the file during file upload. Note that fileItem only contains
	 * the content of the file and is not associated with a filename.
	 */
	public FileItem fileItem = null;
	
	public String iconFilePath = null;
		
	/*---------------------- For Tool Reports/Risks Only ---------------------*/
	/**
	 * Tool ID for an uploaded report file. Used only for submitting tool
	 * reports.
	 */
	public String toolId = null;
	/**
	 * Tool risk for an uploaded report file. Used only for submitting tool risk
	 * values.
	 */
	public String toolRisk = null;

	/** Retrieve existing app metadata. */
	public AppInfo(String appId) {
		this.appId = appId;
		appName = Database.getAppName(appId);
		packageName = Database.getPackageName(appId);
		versionCode = Database.getVersionCode(appId);
		versionName = Database.getVersionName(appId);
		log = new Logger(getLogPath(appId), "APPVET");
		ownerName = Database.getOwner(appId);
		String osName = Database.getOs(appId);
		os = DeviceOS.getOS(osName);
		setAppFileAndProjectName(Database.getAppFileName(appId), os);
		clientHost = Database.getClientIPAddress(appId);
	}

	/** Register new app metadata. */
	public AppInfo(String appId, boolean createAppDirectories) {
		this.appId = appId;
		final String reportsDir = AppVetProperties.APPS_ROOT + "/" + appId
				+ "/reports";
		// Create report and global image directories.
		if (createAppDirectories) {
			if (!new File(reportsDir).mkdirs()) {
				appvetLog.error("Could not create reports directory "
						+ reportsDir);
				return;
			}
			final File imagesDir = new File(AppVetProperties.APP_IMAGES_PATH);
			if (!imagesDir.exists()) {
				if (!imagesDir.mkdirs()) {
					appvetLog.error("Could not create images directory "
							+ imagesDir);
				}
			}
		}
		log = new Logger(getLogPath(appId), "APP " + appId);
	}

	public String getLogPath(String appid) {
		return AppVetProperties.APPS_ROOT + "/" + appid + "/reports/"
				+ AppVetProperties.APP_LOG_NAME;
	}

	public String getIdPath() {
		return AppVetProperties.APPS_ROOT + "/" + appId;
	}

	public String getProjectPath() {
		return AppVetProperties.APPS_ROOT + "/" + appId + "/"
				+ getAppProjectName();
	}

	public String getReportsPath() {
		return AppVetProperties.APPS_ROOT + "/" + appId + "/reports";
	}

	public String getAppFilePath() {
		return AppVetProperties.APPS_ROOT + "/" + appId + "/"
				+ getAppFileName();
	}
}
