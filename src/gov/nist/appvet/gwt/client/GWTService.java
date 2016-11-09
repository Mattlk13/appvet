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

import gov.nist.appvet.gwt.shared.AppsListGwt;
import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.gwt.shared.ServerPacket;
import gov.nist.appvet.gwt.shared.SystemAlert;
import gov.nist.appvet.gwt.shared.ToolInfoGwt;
import gov.nist.appvet.gwt.shared.ToolStatusGwt;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.all.UserToolCredentials;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author steveq@nist.gov
 */
@RemoteServiceRelativePath("greet")
public interface GWTService extends RemoteService {
		
	Boolean setTestToolsEnabledDisabled(ArrayList<ToolInfoGwt> newToolInfo)
			throws IllegalArgumentException;
	
	List<ToolInfoGwt> getTestToolsEnabledDisabled() throws IllegalArgumentException;
	
	String getAppLog(String appId) throws IllegalArgumentException;
	
	String getAppVetLog() throws IllegalArgumentException;

	ConfigInfoGwt handleServletRequest() throws IllegalArgumentException;

	AppsListGwt getAllApps(String username) throws IllegalArgumentException;

	ConfigInfoGwt authenticateNonSSO(String username, String password)
			throws IllegalArgumentException;

	List<ToolStatusGwt> getToolsResults(DeviceOS os, String sessionId,
			String appId) throws IllegalArgumentException;
	
	List<String> getOrgHierarchies() throws IllegalArgumentException;

	Boolean selfUpdatePassword(UserInfo userInfo) throws IllegalArgumentException;

	Boolean updateUserToolCredentials(String username,
			ArrayList<UserToolCredentials> credentialsList)
			throws IllegalArgumentException;

	Boolean setAlertMessage(String username, SystemAlert alert)
			throws IllegalArgumentException;

	Boolean clearAlertMessage(String username) throws IllegalArgumentException;

	List<UserInfo> adminSetUser(UserInfo userInfo)
			throws IllegalArgumentException;

	Boolean clearLog() throws IllegalArgumentException;

	Boolean deleteApp(DeviceOS os, String appId, String username)
			throws IllegalArgumentException;

	Boolean deleteUser(String username) throws IllegalArgumentException;

	List<UserInfo> getAllUsers() throws IllegalArgumentException;

	Boolean removeSession(String sessionId) throws IllegalArgumentException;
	
	ServerPacket getServerUpdates(String username, String sessionId, 
			Date sessionExpiration, Date lastAppsListUpdate) throws IllegalArgumentException;

}
