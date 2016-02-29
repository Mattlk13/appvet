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
package gov.nist.appvet.gwt.server;

import gov.nist.appvet.gwt.client.GWTService;
import gov.nist.appvet.gwt.shared.AppsListGwt;
import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.gwt.shared.SystemAlert;
import gov.nist.appvet.gwt.shared.ToolInfoGwt;
import gov.nist.appvet.gwt.shared.ToolStatusGwt;
import gov.nist.appvet.shared.all.AppVetParameter;
import gov.nist.appvet.shared.all.AppVetServletCommand;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.OrgDepts;
import gov.nist.appvet.shared.all.ToolType;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.all.UserToolCredentials;
import gov.nist.appvet.shared.backend.AppVetProperties;
import gov.nist.appvet.shared.backend.Authenticate;
import gov.nist.appvet.shared.backend.Database;
import gov.nist.appvet.shared.backend.FileUtil;
import gov.nist.appvet.shared.backend.Logger;
import gov.nist.appvet.shared.backend.ToolAdapter;
import gov.nist.appvet.shared.backend.ToolStatus;
import gov.nist.appvet.shared.backend.ToolStatusManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author steveq@nist.gov
 */
public class GWTServiceImpl extends RemoteServiceServlet implements GWTService {

	private static final long serialVersionUID = 1L;
	private static final Logger log = AppVetProperties.log;
	
	/** This method is called by AppVet.java. */
	public ConfigInfoGwt handleServletRequest() {
		HttpServletRequest request = getThreadLocalRequest();
		// Check if AppVet Single-Sign On (SSO) is active.
		if (!AppVetProperties.SSO_ACTIVE) {
			// SSO is not active. Returning null to GWT client will send
			// user to main login page for password authentication.
			ConfigInfoGwt configInfo = new ConfigInfoGwt();
			configInfo.setSSOActive(false);
			return configInfo;
		} else {
			// SSO is active. Note that SSO should only be used in a
			// secured environment where the user has already authenticated
			// into the system because AppVet SSO does not support user password
			// authentication. Password authentication is only supported for
			// non-SSO Appvet.
			String ssoUnauthorizedURL = AppVetProperties.SSO_UNAUTHORIZED_URL;
			// Check for incoming HTTP parameters for AppVet SSO
			String ssoUsername = getSSOUserName(request);
			if (ssoUsername == null) {
				log.debug("User 'null' not authenticated for SSO. Directing to "
						+ ssoUnauthorizedURL);
				// Return SSOActive=true to GWT client so it can invoke the
				// redirect.
				ConfigInfoGwt configInfo = new ConfigInfoGwt();
				configInfo.setSSOActive(true);
				configInfo.setUnauthorizedURL(ssoUnauthorizedURL);
				return configInfo;
			}
			// Check if ssoUsername exists in database
			if (!Database.userExists(ssoUsername)) {
				log.debug("User " + ssoUsername
						+ " not authenticated for SSO. Directing to "
						+ ssoUnauthorizedURL);
				// Return SSOActive=true to GWT client so it can allow the
				// redirect.
				ConfigInfoGwt configInfo = new ConfigInfoGwt();
				configInfo.setSSOActive(true);
				configInfo.setUnauthorizedURL(ssoUnauthorizedURL);
				return configInfo;
			}
			log.debug("User " + ssoUsername + " authenticated for SSO.");
			// Generate session
			String clientIpAddress = request.getRemoteAddr();
			if (clientIpAddress.equals("0:0:0:0:0:0:0:1")) {
				clientIpAddress = "127.0.0.1";
			}
			if (Database.updateClientHost(ssoUsername, clientIpAddress)) {
				log.debug("Updated client IP '" + clientIpAddress
						+ "' for user '" + ssoUsername + "' updated.");
			} else {
				log.warn("Updated client IP '" + clientIpAddress
						+ "' for user '" + ssoUsername
						+ "' could not be updated.");
				return null;
			}
			if (Database.updateUserLogonTime(ssoUsername)) {
				log.debug("Updated logon time for user '" + ssoUsername + "'.");
			} else {
				log.debug("Could not update logon time for user '" + ssoUsername + "'.");
				return null;
			}
			// Clear all expired sessions
			Database.clearExpiredSessions();
			// Update current session
			Date newSessionTimeout = new Date(System.currentTimeMillis()
					+ AppVetProperties.MAX_SESSION_IDLE_DURATION);
			String sessionId = Database.createNewSession(ssoUsername,
					clientIpAddress);
			return getConfigInfo(ssoUsername, sessionId, newSessionTimeout);
		}
	}

	private String getSSOUserName(HttpServletRequest request) {
		if (AppVetProperties.SSO_USERNAME_PARAMNAME != null) {
			return request.getHeader(AppVetProperties.SSO_USERNAME_PARAMNAME);
		} else {
			// Use email if SSO username is not available
			if (AppVetProperties.SSO_EMAIL_PARAMNAME != null) {
				return request.getHeader(AppVetProperties.SSO_EMAIL_PARAMNAME);
			} else {
				log.error("SSO username (and email) is null");
				return null;
			}
		}
	}

	@Override
	public Boolean setAlertMessage(String username, SystemAlert alert) {
		if (Database.setAlertMessage(username, alert)) {
			return new Boolean(true);
		} else {
			return new Boolean(false);
		}
	}

	@Override
	public Boolean clearAlertMessage(String username) {
		if (Database.update("DELETE FROM alerts")) {
			return new Boolean(true);
		} else {
			return new Boolean(false);
		}
	}

	@Override
	public SystemAlert getAlertMessage() {
		return Database.getAlertMessage();
	}

	@Override
	public List<UserInfo> adminSetUser(UserInfo userInfo)
			throws IllegalArgumentException {
		if (userInfo.isNewUser()) {
			if (Database.adminAddNewUser(userInfo)) {
				log.debug("Added user " + userInfo.getFullName());
			} else {
				log.error("Could not add user " + userInfo.getFullName());
			}
		} else {
			if (Database.updateUser(userInfo)) {
				log.debug("Updated user " + userInfo.getFullName());
			} else {
				log.error("Could not update user " + userInfo.getFullName());
			}
		}
		return getUsersList();
	}

	/**
	 * This method is used only with the main AppVet login page. It is not used
	 * for AppVet SSO which uses the organization's secured environment to
	 * provide user authentication.
	 */
	@Override
	public ConfigInfoGwt authenticateNonSSO(String username, String password)
			throws IllegalArgumentException {
		String sql = "SELECT * FROM users " + "where username='" + username
				+ "'";
		if (!Database.exists(sql)) {
			// Check if user is the AppVet default Admin defined in
			// AppVetProperties.xml. This user will always be able to access
			// AppVet, even if its database info has been corrupted.
			if (username.equals(AppVetProperties.DEFAULT_ADMIN_USERNAME)
					&& password.equals(AppVetProperties.DEFAULT_ADMIN_PASSWORD)) {
				log.debug("Adding user-defined default admin '" + username
						+ "'");
				String org = AppVetProperties.DEFAULT_ADMIN_ORG;
				String dept = AppVetProperties.DEFAULT_ADMIN_DEPT;
				String email = AppVetProperties.DEFAULT_ADMIN_EMAIL;
				String role = AppVetProperties.DEFAULT_ADMIN_ROLE.name();
				String lastName = AppVetProperties.DEFAULT_ADMIN_LASTNAME;
				String firstName = AppVetProperties.DEFAULT_ADMIN_FIRSTNAME;
				if (Database.adminAddNewUser(username, password, org, dept, 
						email, role, lastName, firstName)) {
					log.debug("Added new admin user '" + username + "'");
				} else {
					log.debug("Could not add new admin user '" + username + "'");
					return null;
				}
			} else {
				log.debug("User '" + username
						+ "' does not exist in database. Cannot authenticate.");
				return null;
			}
		}
		log.debug("User '" + username + "' exists in database.");
		if (Authenticate.isAuthenticated(username, password)) {
			log.debug("User '" + username + "' authenticated");
			String clientIpAddress = getThreadLocalRequest().getRemoteAddr();
			if (clientIpAddress.equals("0:0:0:0:0:0:0:1")) {
				clientIpAddress = "127.0.0.1";
			}
			if (Database.updateClientHost(username, clientIpAddress)) {
				log.debug("Updated client IP '" + clientIpAddress
						+ "' for user '" + username + "' updated.");
			} else {
				log.warn("Updated client IP '" + clientIpAddress
						+ "' for user '" + username + "' could not be updated.");
				return null;
			}
			if (Database.updateUserLogonTime(username)) {
				log.debug("Updated logon time for user '" + username + "'.");
			} else {
				log.debug("Could not update logon time for user '" + username
						+ "'.");
				return null;
			}
			log.debug(username + " logged into GWT from: " + clientIpAddress);
			// Clear all expired sessions
			Database.clearExpiredSessions();
			final String sessionId = Database.createNewSession(username,
					clientIpAddress);
			final Date sessionExpiration = Database.getSessionExpiration(
					sessionId, clientIpAddress);
			if (sessionExpiration == null) {
				// Session already expired
				log.warn("Session expiration for " + sessionId
						+ " could not be retrieved.");
				return null;
			}
			return getConfigInfo(username, sessionId, sessionExpiration);
		} else {
			log.debug("Could not authenticate user: " + username);
			return null;
		}
	}

	public Boolean clearLog() throws IllegalArgumentException {
		String appVetLogPath = AppVetProperties.APPVET_LOG_PATH;
		PrintWriter pw;
		try {
			pw = new PrintWriter(appVetLogPath);
			pw.close();
			return true;
		} catch (FileNotFoundException e) {
			log.debug(e.toString());
			return false;
		}
	}

	private static ConfigInfoGwt getConfigInfo(String username,
			String sessionId, Date sessionExpiration) {
		final ConfigInfoGwt configInfo = new ConfigInfoGwt();
		configInfo.setAppVetHostUrl(AppVetProperties.HOST_URL);
		configInfo.setAppVetUrl(AppVetProperties.APPVET_URL);
		configInfo.setAppVetServletUrl(AppVetProperties.SERVLET_URL);
		configInfo.setAppVetVersion(AppVetProperties.APPVET_VERSION);
		configInfo.setSessionId(sessionId);
		configInfo.setOrgLogoAltText(AppVetProperties.ORG_LOGO_ALT_TEXT);
		configInfo.setMaxIdleTime(AppVetProperties.MAX_SESSION_IDLE_DURATION);
		configInfo.setGetUpdatesDelay(AppVetProperties.GET_UPDATES_DELAY);
		configInfo.setSessionExpiration(sessionExpiration);
		configInfo.setSystemMessage(AppVetProperties.STATUS_MESSAGE);
		configInfo.setNumRowsAppsList(AppVetProperties.NUM_ROWS_APPS_LIST);
		configInfo.setNumRowsUsersList(AppVetProperties.NUM_ROWS_USERS_LIST);
		configInfo.setDocumentationURL(AppVetProperties.DOCUMENTATION_URL);
		configInfo.setSSOActive(AppVetProperties.SSO_ACTIVE);
		configInfo.setSSOLogoutURL(AppVetProperties.SSO_LOGOUT_URL);
		final ArrayList<ToolInfoGwt> tools = new ArrayList<ToolInfoGwt>();
		// Get Android tools
		final ArrayList<ToolAdapter> androidTools = AppVetProperties.androidTools;
		if ((androidTools != null) && !androidTools.isEmpty()) {
			for (int i = 0; i < androidTools.size(); i++) {
				final ToolAdapter androidTool = androidTools.get(i);
				if (androidTool != null
						&& (androidTool.toolType != ToolType.PREPROCESSOR)) {
					ToolInfoGwt toolInfo = new ToolInfoGwt();
					toolInfo.setOs(DeviceOS.ANDROID.name());
					toolInfo.setName(androidTool.name);
					toolInfo.setId(androidTool.toolId);
					toolInfo.setType(androidTool.toolType);
					toolInfo.setRestrictionType(androidTool.restrictionType.name());
					toolInfo.setAuthenticationRequired(androidTool.authenticationRequired);
					if (toolInfo.requiresAuthentication()) {
						toolInfo.setAuthenticationParameterNames(androidTool.authenticationParams);
					}
					toolInfo.setReportTemplateURL(androidTool.reportTemplateURL);
					toolInfo.setIconURL(androidTool.iconURL);
					toolInfo.setIconAltText(androidTool.iconAltText);
					toolInfo.setReportFileType(androidTool.reportFileType
							.name());
					tools.add(toolInfo);
				}
			}
		} else {
			log.error("GWT DataProvider could not read "
					+ "Android tool info from AppVetProperties. Tools must include "
					+ " at least 'appinfo'" + " and 'registration' tools.");
			return null;
		}
		// Get iOS tools
		final ArrayList<ToolAdapter> iosTools = AppVetProperties.iosTools;
		if ((iosTools != null) && !iosTools.isEmpty()) {
			for (int i = 0; i < iosTools.size(); i++) {
				final ToolAdapter iosTool = iosTools.get(i);
				if (iosTool != null
						&& (iosTool.toolType != ToolType.PREPROCESSOR)) {
					ToolInfoGwt toolInfo = new ToolInfoGwt();
					toolInfo.setOs(DeviceOS.IOS.name());
					toolInfo.setName(iosTool.name);
					toolInfo.setId(iosTool.toolId);
					toolInfo.setType(iosTool.toolType);
					toolInfo.setAuthenticationRequired(iosTool.authenticationRequired);
					if (toolInfo.requiresAuthentication()) {
						toolInfo.setAuthenticationParameterNames(iosTool.authenticationParams);
					}
					toolInfo.setReportFileType(iosTool.reportFileType.name());
					tools.add(toolInfo);
				}
			}
		} else {
			log.error("GWT DataProvider could not read "
					+ "iOS tool info from AppVetProperties. Tools must include "
					+ " at least 'appinfo'" + " and 'registration' tools.");
			return null;
		}
		// Add tools to configuration info
		configInfo.setTools(tools);
		// Get user's information
		final UserInfo userInfo = Database.getUserInfo(username, tools);
		if (userInfo == null) {
			log.error("GWT DataProvider could not get user information");
			return null;
		} else {
			configInfo.setUserInfo(userInfo);
		}
		ArrayList<UserToolCredentials> toolCredentials = configInfo
				.getUserInfo().getToolCredentials();
		if (toolCredentials == null) {
			log.error("toolCredentials is null in GWTServiceImpl");
		}
		return configInfo;
	}

	@Override
	public Boolean deleteApp(DeviceOS os, String appid, String username)
			throws IllegalArgumentException {
		// Delete database entries
		final boolean deletedDbEntries = Database.deleteApp(os, appid);
		// Delete app files
		if (deletedDbEntries) {
			final String appIdPath = AppVetProperties.APPS_ROOT + "/" + appid;
			final File appDirectory = new File(appIdPath);
			FileUtil.deleteDirectory(appDirectory);
			final String iconPath = AppVetProperties.APP_IMAGES_PATH + "/" + appid
					+ ".png";
			File iconFile = new File(iconPath);
			if (iconFile.exists()) {
				iconFile.delete();
			}
			iconFile = null;
			log.debug(username + " deleted app " + appid);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Boolean deleteUser(String username) throws IllegalArgumentException {
		return Database.deleteUser(username);
	}

	@Override
	public AppsListGwt getAllApps(String username) throws IllegalArgumentException {
		return Database.getAllApps(username);
	}

	@Override
	public List<ToolStatusGwt> getToolsResults(DeviceOS os, String sessionId,
			String appId) throws IllegalArgumentException {
		return getToolsStatuses(os, sessionId, appId);
	}

	@Override
	public AppsListGwt getUpdatedApps(Date lastClientUpdate, String username)
			throws IllegalArgumentException {
		return Database.getUpdatedApps(username, lastClientUpdate);
	}

	@Override
	public List<UserInfo> getUsersList() throws IllegalArgumentException {
		return Database.getUsers(null);
	}

	@Override
	public Boolean removeSession(String sessionId)
			throws IllegalArgumentException {
		final String clientIpAddress = getThreadLocalRequest().getRemoteAddr();
		final boolean removedSession = Database.removeSession(sessionId,
				clientIpAddress);
		Database.clearExpiredSessions();
		if (removedSession) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Date updateSessionExpiration(String sessionId, Date newSessionTimeout)
			throws IllegalArgumentException {
		final String clientIpAddress = getThreadLocalRequest().getRemoteAddr();
		// First check if session has already expired
		if (!Database.sessionIsGood(sessionId, clientIpAddress)) {
			return null;
		} else {
			if (Database.updateSessionExpiration(sessionId, clientIpAddress,
					newSessionTimeout)) {
				return newSessionTimeout;
			} else {
				log.error("Could not update session expiration");
				return null;
			}
		}
	}

	public static List<ToolStatusGwt> getToolsStatuses(DeviceOS os,
			String sessionId, String appId) {
		final ArrayList<ToolStatusGwt> toolStatusList = new ArrayList<ToolStatusGwt>();
		// Registration status
		ToolAdapter tool = ToolAdapter.getByToolId(os, "registration");
		ToolStatusGwt toolStatus = getToolStatusHtml(os, sessionId, appId, tool);
		if (toolStatus != null) {
			toolStatusList.add(toolStatus);
		}
		// App metadata status
		tool = ToolAdapter.getByToolId(os, "appinfo");
		toolStatus = getToolStatusHtml(os, sessionId, appId, tool);
		if (toolStatus != null) {
			toolStatusList.add(toolStatus);
		}
		// Rest of the tools
		ArrayList<ToolAdapter> tools = null;
		if (os == DeviceOS.ANDROID) {
			tools = AppVetProperties.androidTools;
		} else if (os == DeviceOS.IOS) {
			tools = AppVetProperties.iosTools;
		}
		// Tool and manual report uploaded statuses
		for (int i = 0; i < tools.size(); i++) {
			tool = tools.get(i);
			if (tool.toolType != ToolType.PREPROCESSOR) {
				toolStatus = getToolStatusHtml(os, sessionId, appId, tool);
				if (toolStatus != null) {
					toolStatusList.add(toolStatus);
				} else {
					log.error("Status for tool " + tool.toolId + " is null");
				}
			}
		}
		return toolStatusList;
	}

	private static ToolStatusGwt getToolStatusHtml(DeviceOS os,
			String sessionId, String appId, ToolAdapter tool) {
		if (tool == null) {
			log.error("Tool is null");
			return null;
		}
		final ToolStatusGwt toolStatusGwt = new ToolStatusGwt();
		toolStatusGwt.setAppId(appId);
		String websiteHrefTag = "";
		if (tool.webSite != null) {
			websiteHrefTag = "<a href=\"" + tool.webSite
					+ "\" target=\"_blank\">" + tool.name + "</a>";
		} else {
			websiteHrefTag = tool.name;
		}
		toolStatusGwt.setToolType(tool.toolType);
		toolStatusGwt.setRestrictionType(tool.restrictionType);
		toolStatusGwt.setIconURL(tool.iconURL);
		toolStatusGwt.setIconAltText(tool.iconAltText);
		toolStatusGwt.setToolDisplayName(websiteHrefTag);
		boolean toolCompleted = false;
		final ToolStatus toolStatus = ToolStatusManager.getToolStatus(os,
				appId, tool.toolId);
		if (toolStatus == null) {
			// Status for a tool may be null if it was recently installed
			// but not run for previously submitted apps. In such cases,
			// we return an NA status.
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tabledim\" style='color: gray'>N/A</div>");
		} else if (toolStatus == ToolStatus.NA) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tabledim\" style='color: gray'>N/A</div>");

		} else if (toolStatus == ToolStatus.AVAILABLE) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tabledim\" style='color: black'>AVAILABLE</div>");

		} else if (toolStatus == ToolStatus.HIGH) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitembad\" style='color: red'>HIGH</div>");
		} else if (toolStatus == ToolStatus.ERROR) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitembad\" style='color: black'>ERROR</div>");
		} else if (toolStatus == ToolStatus.MODERATE) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitembad\" style='color: orange'>MODERATE</div>");
		} else if (toolStatus == ToolStatus.LOW) {
			toolCompleted = true;
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitemendorsed\" style='color: green'>LOW</div>");
		} else if (toolStatus == ToolStatus.SUBMITTED) {
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitem\" style='color: black'>SUBMITTED</div>");
		} else {
			toolStatusGwt
					.setStatusHtml("<div id=\"tableitem\" style='color: black'>"
							+ toolStatus.name() + "</div>");
		}
		final String reportsPath = AppVetProperties.APPS_ROOT + "/" + appId
				+ "/reports";
		File reportFile = new File(reportsPath + "/" + tool.reportName);
		if (toolStatus == null || toolStatus == ToolStatus.NA
				|| !reportFile.exists()) {
			toolStatusGwt
					.setReport("<div id=\"tabledim\" style='color: gray'>N/A</div>");
		} else if (toolCompleted) {
			// Make sure we do not cache this report since any new reports will
			// override this URL with the same filename. Make sure to set
			// cachingAllowed="false" in Tomcat context.xml.
			final String dateString = "?nocache" + new Date().getTime();
			if (reportFile.exists()) {
				toolStatusGwt.setReport("<a href=\""
						+ AppVetProperties.SERVLET_URL + dateString + "&"
						+ AppVetParameter.COMMAND.value + "="
						+ AppVetServletCommand.GET_TOOL_REPORT.name() + "&"
						+ AppVetParameter.APPID.value + "=" + appId + "&"
						+ AppVetParameter.SESSIONID.value + "=" + sessionId
						+ "&" + AppVetParameter.TOOLID.value + "="
						+ tool.toolId + "\" target=\"_blank\">Report</a>");
			}
		} else {
			toolStatusGwt
					.setReport("<div id=\"tabledim\" style='color: gray'>N/A</div>");
		}
		return toolStatusGwt;
	}

	@Override
	public Boolean updateSelf(UserInfo userInfo)
			throws IllegalArgumentException {
		return Database.updateUser(userInfo);
	}

	@Override
	public Boolean updateUserToolCredentials(String username,
			ArrayList<UserToolCredentials> credentialsList)
			throws IllegalArgumentException {
		Database.saveUserToolCredentials(username, credentialsList);
		return true;
	}

	/**
	 * This method gets the organizations and departments for users. Note that
	 * this method is inefficient and should be optimized.
	 */
	@Override
	public List<OrgDepts> getOrgDeptsList() throws IllegalArgumentException {
		Hashtable<String, List<String>> orgDeptHashtable = new Hashtable<String, List<String>>();
		List<UserInfo> users = Database.getUsers(null);
		for (int i = 0; i < users.size(); i++) {
			UserInfo user = users.get(i);
			// log.debug("Adding org/dept for user: " + user.getUserName());
			String userOrg = user.getOrganization();
			String userDept = user.getDepartment();
			if (!orgDeptHashtable.containsKey(userOrg)) {
				// log.debug("Adding new org " + userOrg);
				// Add this org to the hashtable
				List<String> deptList = new ArrayList<String>();
				deptList.add(userDept);
				// log.debug("Adding new dept " + userDept + " for new org " +
				// userOrg);
				orgDeptHashtable.put(userOrg, deptList);
			} else {
				// Org is already in hashtable so check if dept is in its list
				List<String> deptList = orgDeptHashtable.get(userOrg);
				if (!listContains(deptList, userDept)) {
					// Add it to the list
					deptList.add(userDept);
					// log.debug("Adding new dept " + userDept +
					// " to existing org " + userOrg);
				}
			}
		}
		List<OrgDepts> orgDeptsList = new ArrayList<OrgDepts>();
		Set<String> keys = orgDeptHashtable.keySet();
		for (String key : keys) {
			List<String> deptList = orgDeptHashtable.get(key);
			// log.debug("key: " + key);
			for (int i = 0; i < deptList.size(); i++) {
				// log.debug("depts: " + deptList.get(i));
			}
			OrgDepts orgDepts = new OrgDepts();
			orgDepts.orgName = key;
			orgDepts.deptNames = deptList.toArray(new String[deptList.size()]);
			orgDeptsList.add(orgDepts);
		}
		return orgDeptsList;
	}

	private boolean listContains(List<String> list, String item) {
		for (int i = 0; i < list.size(); i++) {
			if (item.equals(list.get(i))) {
				return true;
			}
		}
		return false;
	}
}
