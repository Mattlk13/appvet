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

import java.util.ArrayList;
import java.util.Date;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author steveq@nist.gov
 */
public class UserInfo implements IsSerializable {
	
	private boolean newUser = false;
	// -------------- Updated by user/admin -------------
	private String userName = null;
	private String password = null;
	private String passwordAgain = null;
	private boolean changePassword = false;
	private String lastName = null;
	private String firstName = null;
	private String email = null;
	private ArrayList<UserToolCredentials> toolCredentials = null;
	private boolean defaultAdmin = false;
	/** The string representation of the user's role and associated organizational membership hierarchy.*/
	private String roleAndOrgMembership = null;
	
	// --------------- Updated only by AppVet --------------
	private Date lastLogon = null;
	private String fromHost = null;

	public UserInfo() {
		
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getFromHost() {
		return fromHost;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public Date getLastLogon() {
		return lastLogon;
	}

	public String getLastName() {
		return lastName;
	}

	public String getNameWithLastNameInitial() {
		return firstName + " " + lastName.substring(0, 1) + ".";
	}
	
	public String getUserName() {
		return userName;
	}

	public boolean isChangePassword() {
		return changePassword;
	}

	public boolean isNewUser() {
		return newUser;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setFromHost(String fromHost) {
		this.fromHost = fromHost;
	}

	public void setLastLogon(Date lastLogon) {
		this.lastLogon = lastLogon;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswords(String password, String passwordAgain) {
		this.password = password;
		this.passwordAgain = passwordAgain;
		
		if (password != null && !password.isEmpty() 
				&& passwordAgain != null && !passwordAgain.isEmpty() 
				&& password.equals(passwordAgain)) {
			this.changePassword = true;
		} else {
			this.changePassword = false;
		}
	}

	public void setUserName(String username) {
		userName = username;
	}

	public String getPasswordAgain() {
		return passwordAgain;
	}

	public void setPasswordAgain(String passwordAgain) {
		this.passwordAgain = passwordAgain;
	}

	public boolean tokenMatch(String token) {
		String lowerCaseToken = token.toLowerCase();

		if (userName.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}

		if (email.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}

		if (lastLogon.toString().toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}

		if (fromHost.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}

		if (lastName.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}

		if (firstName.toLowerCase().indexOf(lowerCaseToken) > -1) {
			return true;
		}

		return false;
	}

	public ArrayList<UserToolCredentials> getToolCredentials() {
		return toolCredentials;
	}

	public void setToolCredentials(
			ArrayList<UserToolCredentials> toolCredentials) {
		this.toolCredentials = toolCredentials;
	}

	public boolean isDefaultAdmin() {
		return defaultAdmin;
	}

	public void setDefaultAdmin(boolean defaultAdmin) {
		this.defaultAdmin = defaultAdmin;
	}

	public String getRoleAndOrgMembership() {
		return roleAndOrgMembership;
	}

	public void setRoleAndOrgMembership(String roleAndOrgMembership) {
		this.roleAndOrgMembership = roleAndOrgMembership;
	}
}
