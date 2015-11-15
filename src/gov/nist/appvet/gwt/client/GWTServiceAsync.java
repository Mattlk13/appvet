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
package gov.nist.appvet.gwt.client;

import gov.nist.appvet.gwt.shared.AppInfoGwt;
import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.gwt.shared.ToolStatusGwt;
import gov.nist.appvet.gwt.shared.UserInfoGwt;
import gov.nist.appvet.gwt.shared.UserToolCredentialsGwt;
import gov.nist.appvet.shared.os.DeviceOS;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author steveq@nist.gov
 */
public interface GWTServiceAsync {
	
	void adminSetUser(UserInfoGwt userInfo,
			AsyncCallback<List<UserInfoGwt>> callback)
			throws IllegalArgumentException;

	void authenticate(String username, String password,
			AsyncCallback<ConfigInfoGwt> callback)
			throws IllegalArgumentException;

	void deleteApp(DeviceOS os, String appId, String username,
			AsyncCallback<Boolean> callback) throws IllegalArgumentException;

	void deleteUser(String username, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;

	void getAllApps(String username, AsyncCallback<List<AppInfoGwt>> callback)
			throws IllegalArgumentException;

	void getToolsResults(DeviceOS os, String sessionId, String appId,
			AsyncCallback<List<ToolStatusGwt>> callback)
			throws IllegalArgumentException;

	void getUpdatedApps(long lastClientUpdate, String username,
			AsyncCallback<List<AppInfoGwt>> callback)
			throws IllegalArgumentException;

	void getUsersList(AsyncCallback<List<UserInfoGwt>> callback)
			throws IllegalArgumentException;

	void removeSession(String sessionId, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;

	void updateSessionTimeout(String sessionId, long sessionTimeout,
			AsyncCallback<Boolean> callback) throws IllegalArgumentException;

	void updateSelf(UserInfoGwt userInfo, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;

	void updateUserToolCredentials(String username,
			ArrayList<UserToolCredentialsGwt> credentialsList,
			AsyncCallback<Boolean> callback) throws IllegalArgumentException;
	
}
