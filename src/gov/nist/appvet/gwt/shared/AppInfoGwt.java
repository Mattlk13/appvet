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
package gov.nist.appvet.gwt.shared;

import gov.nist.appvet.shared.app.AppInfoBase;

/**
 * This class defines app metadata that may be used by a GWT client.
 * 
 * @author steveq@nist.gov
 */
public class AppInfoGwt extends AppInfoBase {
	private static final long serialVersionUID = 1L;

	public AppInfoGwt() {
		super();
	}

	/** This method matches keyword tokens for searching. */
	public boolean tokenMatch(String token) {
		final String lowerCaseToken = token.toLowerCase();
		if (appId.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}
		if (appName.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}
		if (packageName.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}
		if (versionCode.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}
		if (versionName.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}
		if (os.name().toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}
		if (appFileName.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}
		if (appStatus.name().toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}
		if (ownerName.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}
		return false;
	}
}
