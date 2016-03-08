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
 * This enumeration defines the possible AppVet user roles.
 * @author steveq@nist.gov
 */
public enum Role {
	// An ADMIN is an administrator of the AppVet system. An ADMIN
	// has complete access and control of all accounts and apps.
	ADMIN,
	// An ANALYST is a user that can view all apps and add
	// tool reports, but does not have access to other user accounts.
	ANALYST, 
    // An ORG_ANALYST is a user of the AppVet system that can access
    // all apps submitted by other organizational users. An ORG_ANALYST
    // can submit tool reports for apps belonging to organizational users
    // but cannot access other organizational user accounts.
//    ORG_ANALYST,
    // An DEPT_ANALYST is a user of the AppVet system that can access
    // all apps submitted by other departmental users of the same
    // organization. An DEPT_ANALYST
    // can submit tool reports for apps belonging to departmental users
    // but cannot access other departmental user accounts.
//    DEPT_ANALYST,
	// A TOOL_PROVIDER is a user that can submit tool reports
	// for any app and access their own account (e.g., for testing their tool).
	TOOL_PROVIDER, 
	// A USER is a user of the AppVet system. A USER has access to
	// only the apps that they submit and cannot submit tool reports.
	USER;
	private Role() {
	}

	public static Role getRole(String roleName) {
		if (roleName != null) {
			for (final Role r : Role.values()) {
				if (roleName.equalsIgnoreCase(r.name())) {
					return r;
				}
			}
		}
		return null;
	}
}
