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
package gov.nist.appvet.shared.all;

/**
 * This class defines the status of an app.
 * 
 * @author steveq@nist.gov
 */
public enum AppStatus {

	// App is currently being registered.
	REGISTERING,
	// App has been registered and is in the queue for processing.
	PENDING,
	// App is currently processing (i.e., being analyzed by tools).
	PROCESSING,
	// All tools assessed the app as low-risk.
	//PASS,
	LOW,
	// All tools assesed the app as low-risk but at least one app
	// experienced an error.
	LOW_WITH_ERROR,
	// At least one tool has assessed the app as moderate-risk but no tool
	// has assessed the app as high-risk.
	//WARNING,
	MODERATE,
	// At least one tool as assess the app as moderate but at least one tool
	// experienced an ERROR.
	MODERATE_WITH_ERROR,
	// At least one tool has assessed the app as high-risk.
	//FAIL,
	HIGH,
	// At least one tool as assessed the app as high-risk and at least one
	// app experienced an error.
	HIGH_WITH_ERROR,
	// App experienced an error or could not be processed by at least one
	// tool.
	ERROR,
	// No tools were available for analysis. Only registration and
	// preprocessing were performed.
	NA;
	
	private AppStatus() {
	}

	public static AppStatus getStatus(String name) {
		if (name != null) {
			for (final AppStatus s : AppStatus.values()) {
				if (name.equalsIgnoreCase(s.name())) {
					return s;
				}
			}
		}
		return null;
	}
}
