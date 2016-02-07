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
package gov.nist.appvet.shared.app;

import gov.nist.appvet.shared.os.DeviceOS;
import gov.nist.appvet.shared.status.AppStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * This class defines the core metadata of an app. This class is extended by
 * AppInfoGwt, for defining app metadata that may be used by GWT clients, and by
 * AppInfo, for defining app metadata used by all other components.
 * 
 * @author steveq@nist.gov
 */
public class AppInfoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	/** The AppVet ID of the app. */
	public String appId = null;
	/** Used by AppVet to determine if an app's status was recently updated 
	 * (but not yet consumed by AppVet). Rarely used within an AppInfoCore 
	 * object.
	 */
	public boolean updated = false;
	/** The display name of the app. */
	public String appName = null;
	/** The package of bundle name of the app. */
	public String packageName = null;
	/**
	 * The filename of the app. Note that whitespace in filenames are replaced
	 * with underscore. Further note that apps submitted as metadata only (name,
	 * version, etc.) will have a null file name.
	 */
	public String appFileName = null;
	/**
	 * The file name of the app minus the file extension (e.g., '.apk'). The app
	 * project name is typically used to store a decompressed binary app file.
	 */
	public String appProjectName = null;
	/** The version code of the app. */
	public String versionCode = null;
	/** The displayed version code of the app. */
	public String versionName = null;
	/** The target OS platform for the app. */
	public DeviceOS os = null;
	/** The submitter (owner) of an app during app submission. */
	public String ownerName = null;
	/** Submission date and time of the app. */
	public Date submitTime = null;
	/** The status of the app. */
	public AppStatus appStatus = null;
	/** The client host from which this app was submitted. */
	public String clientHost = null;
	/** The icon name (default or actual) */
	//public String iconFileName = null;

	/** Empty constructor. */
	public AppInfoBase() {
	}

	/** Set the app filename and project name. */
	public void setAppFileAndProjectName(String appFileName, DeviceOS os) {
		this.appFileName = appFileName;
		String extension = null;
		if (os == DeviceOS.ANDROID) {
			extension = ".APK";
		} else if (os == DeviceOS.IOS) {
			extension = ".IPA";
		}
		int extensionIndex = appFileName.toUpperCase().indexOf(extension);
		this.appProjectName = appFileName.substring(0, extensionIndex);
	}

	/** Get the app project name. */
	public String getAppProjectName() {
		return appProjectName;
	}

	/** Get the app filename. */
	public String getAppFileName() {
		return appFileName;
	}


}
