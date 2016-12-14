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
import gov.nist.appvet.gwt.shared.AppsListGwt;
import gov.nist.appvet.gwt.shared.SystemAlert;
import gov.nist.appvet.gwt.shared.SystemAlertType;
import gov.nist.appvet.gwt.shared.ToolInfoGwt;
import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.shared.all.AppStatus;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.all.UserToolCredentials;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author steveq@nist.gov
 */
public class Database {

	private static final Logger log = AppVetProperties.log;

	public static boolean userExists(String username) {
		String sql = "SELECT * FROM users WHERE username='" 
				+ username + "' AND fromhost <> 'DEACTIVATED' or fromhost is null";
		return exists(sql);
	}

	public static boolean updateClientHost(String username, String host) {
		return update("UPDATE users SET fromhost ='" + host
				+ "' WHERE username = '" + username + "'");
	}

	public static boolean updateUserLogonTime(String username) {
		return update("UPDATE users SET lastlogon = NOW() WHERE username = '"
				+ username + "'");
	}

	public static boolean clearExpiredSessions() {
		Timestamp currentTime = new Timestamp(new Date().getTime());
		String sql = "DELETE FROM sessions WHERE expiretime < '" + currentTime
				+ "'";
		return update(sql);
	}

	public synchronized static String createNewSession(String username,
			String clientIpAddress) {
		UUID uuid = UUID.randomUUID();
		String sessionId = uuid.toString().replaceAll("-", "");
		// Add session duration to the current time to get expiration time
		Date expirationTime = new Date(System.currentTimeMillis()
				+ AppVetProperties.MAX_SESSION_IDLE_DURATION);
		String sql = "INSERT INTO sessions (sessionid, username, expiretime, clientaddress) VALUES(?, ?, ?, ?)";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(sql);
			ps.setString(1, sessionId);
			ps.setString(2, username);
			ps.setTimestamp(3, new Timestamp(expirationTime.getTime()));
			ps.setString(4, clientIpAddress);
			ps.executeUpdate();
		} catch (final SQLException e) {
			log.error(e.toString());
			return null;
		} finally {
			sql = null;
			uuid = null;
			expirationTime = null;
			cleanUpPreparedStatement(ps);
			cleanUpConnection(connection);
		}
		return sessionId;
	}

	public static UserInfo getUserInfo(String username,
			ArrayList<ToolInfoGwt> tools) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		UserInfo userInfo = null;
		String sql = null;
		try {
			sql = "SELECT * FROM users WHERE username='" + username + "'";
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			userInfo = new UserInfo();
			resultSet.next();
			userInfo.setUserName(resultSet.getString(1));
			userInfo.setPassword(resultSet.getString(2));
			userInfo.setLastName(resultSet.getString(3));
			userInfo.setFirstName(resultSet.getString(4));
			userInfo.setEmail(resultSet.getString(5));
			userInfo.setRoleAndOrgMembership(resultSet.getString(6));
			userInfo.setLastLogon(resultSet.getTimestamp(7));
			userInfo.setFromHost(resultSet.getString(8));
			// Check if default admin
			if (userInfo.getFirstName().equals(
					AppVetProperties.DEFAULT_ADMIN_FIRSTNAME)
					&& userInfo.getLastName().equals(
							AppVetProperties.DEFAULT_ADMIN_LASTNAME)) {
				userInfo.setDefaultAdmin(true);
			}
			// Set tool authentication
			if (tools != null && tools.size() != 0) {
				String toolCredentialsStr = resultSet.getString(9);
				ArrayList<UserToolCredentials> toolCredentialsList = null;
				if (toolCredentialsStr == null) {
					// Create new tool credentials list
					toolCredentialsList = createToolCredentialsList(username,
							tools);
				} else {
					toolCredentialsList = getToolCredentials(toolCredentialsStr);
					// Check to make sure user credential objects exist for each
					// tool. This is important if a new tool was recently added.
					updateToolCredentials(username, toolCredentialsList, tools);
				}
				userInfo.setToolCredentials(toolCredentialsList);
			}
			return userInfo;
		} catch (final Exception e) {
			log.error(e.toString());
			return null;
		} finally {
			sql = null;
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
	}

	public static boolean setAlertMessage(String username, SystemAlert alert) {
		// Clear existing message first
		if (!update("DELETE FROM alerts")) {
			log.error("Could not clear alerts table");
			return false;
		}
		// Add alert message
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection
					.prepareStatement("INSERT INTO alerts (username, time, alerttype, message) "
							+ "values (?, ?, ?, ?)");
			preparedStatement.setString(1, username);
			preparedStatement.setTimestamp(2,
					new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(3, alert.type.name());
			preparedStatement.setString(4, alert.message);
			preparedStatement.executeUpdate();
			return true;
		} catch (final SQLException e) {
			log.error(e.toString());
			return false;
		} finally {
			cleanUpPreparedStatement(preparedStatement);
			cleanUpConnection(connection);
		}
	}

//	public static boolean deleteAlerts() {
//		return update("DELETE FROM alerts");
//	}

	public static SystemAlert getAlertMessage() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = null;
		SystemAlert alert = null;
		try {
			connection = getConnection();
			sql = "SELECT * FROM alerts";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				// Return first (and should be only) alert message
				alert = new SystemAlert();
				String alertTypeStr = resultSet.getString(3);
				alert.type = SystemAlertType.getType(alertTypeStr);
				alert.message = resultSet.getString(4);
				return alert;
			}
		} catch (final SQLException e) {
			log.error(e.toString());
		} finally {
			sql = null;
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
		return null;
	}

	public synchronized static boolean adminAddNewUser(UserInfo userInfo) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		final String username = userInfo.getUserName();
		try {
			connection = getConnection();
			preparedStatement = connection
					.prepareStatement(""
							+ "REPLACE INTO users (username, lastName, firstName, email, role) values "
							+ "(?, ?, ?, ?, ?)");
			log.debug("Admin Adding user: " + username);
			preparedStatement.setString(1, username);
			// log.debug("Admin Adding lastname: " + userInfo.getLastName());
			preparedStatement.setString(2, userInfo.getLastName());
			// log.debug("Admin Adding firstname: " + userInfo.getFirstName());
			preparedStatement.setString(3, userInfo.getFirstName());
			// log.debug("Admin Adding email: " + userInfo.getEmail());
			preparedStatement.setString(4, userInfo.getEmail());
			log.debug("Admin Adding role: " + userInfo.getRoleAndOrgMembership());
			preparedStatement.setString(5, userInfo.getRoleAndOrgMembership());
			preparedStatement.executeUpdate();
			final String password = userInfo.getPassword();
			final String passwordAgain = userInfo.getPasswordAgain();
			if (password != null && !password.isEmpty()
					&& passwordAgain != null && !passwordAgain.isEmpty()
					&& password.equals(passwordAgain)) {
				try {
					return setPBKDF2Password(username, password);
				} catch (Exception e) {
					log.error(e.toString());
					return false;
				}
			} else {
				return true;
			}
		} catch (final SQLException e) {
			log.error(e.toString());
			return false;
		} finally {
			cleanUpPreparedStatement(preparedStatement);
			cleanUpConnection(connection);
		}
	}

	/**
	 * This method updates user information but does not update a user's tool
	 * credentials.
	 */
	public synchronized static boolean updateUser(UserInfo userInfo) {
		Connection connection = null;
		Statement statement = null;
		try {
			log.debug("Admin Updating user: " + userInfo.getUserName());
			// log.debug("Admin Updating lastname: " + userInfo.getLastName());
			// log.debug("Admin Updating firstname: " +
			// userInfo.getFirstName());
			// log.debug("Admin Updating organization: " +
			// userInfo.getOrganization());
			// log.debug("Admin Updating dept: " + userInfo.getDepartment());
			// log.debug("Admin Updating email: " + userInfo.getEmail());
			log.debug("Admin Updating role: " + userInfo.getRoleAndOrgMembership());
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("UPDATE users SET "
					+ "username='" + userInfo.getUserName() + "', "
					+ "lastName='" + userInfo.getLastName() + "', "
					+ "firstName='" + userInfo.getFirstName() + "', "
					+ "email='" + userInfo.getEmail() + "', "
					+ "role='" + userInfo.getRoleAndOrgMembership() + "' "
					+ "WHERE username='" + userInfo.getUserName() + "'");
			if (userInfo.isChangePassword()) {
				final String userName = userInfo.getUserName();
				final String password = userInfo.getPassword();
				try {
					return setPBKDF2Password(userName, password);
				} catch (Exception e) {
					log.error(e.toString());
					return false;
				}
			} else {
				return true;
			}
		} catch (final SQLException e) {
			log.error(e.toString());
			return false;
		} finally {
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
	}

	public synchronized static boolean setPBKDF2Password(String username,
			String password) {
		try {
			// Generate salted PBKDF2 hash
			final String passwordHash = Authenticate.createHash(password);
			return update("UPDATE users SET password='" + passwordHash + "'"
					+ " WHERE username='" + username + "'");
		} catch (NoSuchAlgorithmException e) {
			log.error(e.toString());
		} catch (InvalidKeySpecException e) {
			log.error(e.toString());
		}
		return false;
	}

	/**
	 * @param role
	 *            If null, select all users.
	 * @return
	 */
	public static List<UserInfo> getAllUsers(Role role) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		UserInfo userInfo = null;
		String sql = null;
		ArrayList<UserInfo> arrayList = null;
		try {
			connection = getConnection();
			if (role == null) {
				sql = "SELECT * FROM users ORDER BY lastName ASC";
			} else {
				sql = "SELECT * FROM users WHERE role='" + role
						+ "' ORDER BY lastName ASC";
			}
			arrayList = new ArrayList<UserInfo>();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				userInfo = new UserInfo();
				userInfo.setUserName(resultSet.getString(1));
				userInfo.setPassword(resultSet.getString(2));
				userInfo.setLastName(resultSet.getString(3));
				userInfo.setFirstName(resultSet.getString(4));
				userInfo.setEmail(resultSet.getString(5));
				userInfo.setRoleAndOrgMembership(resultSet.getString(6));
				userInfo.setLastLogon(resultSet.getTimestamp(7));
				userInfo.setFromHost(resultSet.getString(8));
				// Check if default admin
				if (userInfo.getFirstName().equals(
						AppVetProperties.DEFAULT_ADMIN_FIRSTNAME)
						&& userInfo.getLastName().equals(
								AppVetProperties.DEFAULT_ADMIN_LASTNAME)) {
					userInfo.setDefaultAdmin(true);
				}
				arrayList.add(userInfo);
			}
		} catch (final Exception e) {
			log.error(e.toString());
			arrayList = null;
		} finally {
			sql = null;
			userInfo = null;
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
		return arrayList;
	}
	
	public static List<String> getUserOrgMemberships() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = null;
		ArrayList<String> arrayList = null;
		try {
			connection = getConnection();
			sql = "SELECT * FROM users ORDER BY lastName ASC";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			arrayList = new ArrayList<String>();
			while (resultSet.next()) {
				String userRoleAndOrgMembership = resultSet.getString(6);
				String[] roleAndMembership = userRoleAndOrgMembership.split(":");
				if (roleAndMembership != null && roleAndMembership[1] != null) {
					arrayList.add(roleAndMembership[1]);
				}
			}
		} catch (final Exception e) {
			log.error(e.toString());
			arrayList = null;
		} finally {
			sql = null;
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
		return arrayList;
	}

	public synchronized static boolean adminAddNewUser(String username, String password,
			String email, String role, String lastName, String firstName) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection
					.prepareStatement("REPLACE INTO users (username, password, "
							+ "email, role, lastName, firstName) "
							+ "values (?, ?, ?, ?, ?, ?)");
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, getPBKDF2Password(password));
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, role);
			preparedStatement.setString(5, lastName);
			preparedStatement.setString(6, firstName);
			preparedStatement.executeUpdate();
			return true;
		} catch (final SQLException e) {
			log.error(e.toString());
			return false;
		} finally {
			cleanUpPreparedStatement(preparedStatement);
			cleanUpConnection(connection);
		}
	}

	public synchronized static boolean deleteApp(DeviceOS os, String appId) {
		final boolean appDeleted = update("DELETE FROM apps " + "WHERE appid='"
				+ appId + "'");
		boolean statusDeleted = false;
		if (os == DeviceOS.ANDROID) {
			statusDeleted = update("DELETE FROM androidtoolstatus "
					+ "WHERE appid='" + appId + "'");
		} else {
			statusDeleted = update("DELETE FROM iostoolstatus "
					+ "WHERE appid='" + appId + "'");
		}
		if (appDeleted && statusDeleted) {
			return setLastUpdatedTime(appId);
		} else {
			return false;
		}
	}
	
	public synchronized static boolean setReportTime(String appId, DeviceOS os, 
			String toolId) {
		String reportTableName = null;
		if (os == DeviceOS.ANDROID) {
			reportTableName = "androidreporttimes";
		} else if (os == DeviceOS.IOS) {
			reportTableName = "iosreporttimes";
		}
		
		return update("UPDATE " + reportTableName + " SET " + toolId + " = NOW() WHERE appid='"
				+ appId + "'");	
	}

	public synchronized static boolean setLastUpdatedTime(String appId) {
		// To ensure update changes are acquired by UIs, wait n ms
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return update("UPDATE apps SET lastupdated = NOW() WHERE appid='"
				+ appId + "'");
	}

	/** As of 2.4.5, we do not delete the user but instead set the user
	 * as DEACTIVATED.
	 * @param username
	 * @return
	 */
	public synchronized static boolean deleteUser(String username) {
		return update("UPDATE users SET fromhost='DEACTIVATED' WHERE username = '" 
				+ username + "'");
//		return update("DELETE FROM users " + "WHERE username='" + username
//				+ "'");
	}
	
	public synchronized static boolean reactivateUser(String username) {
		return update("UPDATE users SET fromhost='' WHERE username = '" 
				+ username + "'");
	}

	public static AppsListGwt getApps(String username, 
			Date lastClientUpdateDate) {
		Connection connection = null;
		AppsListGwt appsListGwt = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = null;
		Timestamp lastClientUpdate = null;
		if (lastClientUpdateDate != null) {
			lastClientUpdate = new Timestamp(lastClientUpdateDate.getTime());
		}
		try {
			// Get apps based on user's role
			Role userRole = Role.getRole(getRoleStr(username));
			switch (userRole) {
			case ADMIN:
				// ADMINs can view all apps
				if (lastClientUpdate != null) {
					// Get only updated apps
					sql = "SELECT * FROM apps WHERE lastupdated > '"
							+ lastClientUpdate + "'";
				} else {
					// Get all apps
					sql = "SELECT * FROM apps ORDER BY submittime DESC";
				}
				break;
			case TOOL_PROVIDER:
				// TOOL_PROVIDERs can only view their own apps. Note, however,
				// that TOOL_PROVIDERs can submit reports for any app.
				if (lastClientUpdate != null) {
					sql = "SELECT * FROM apps WHERE username = '" + username
							+ "' and lastupdated > '" + lastClientUpdate + "'";
				} else {
					sql = "SELECT * FROM apps WHERE username='" + username
							+ "' ORDER BY submittime DESC";
				}
				break;
			case ANALYST:
				// ANALYSTs can view any app in their org unit and below.
				if (lastClientUpdate != null) {
					// Get only updated apps
					sql = "SELECT * FROM apps WHERE lastupdated > '"
							+ lastClientUpdate + "'";
				} else {
					// Get all apps
					sql = "SELECT * FROM apps ORDER BY submittime DESC";
				}
				break;
			case USER:
				// USERs can only view their own apps.
				if (lastClientUpdate != null) {
					sql = "SELECT * FROM apps WHERE username = '" + username
							+ "' and lastupdated > '" + lastClientUpdate + "'";
				} else {
					sql = "SELECT * FROM apps WHERE username='" + username
							+ "' ORDER BY submittime DESC";
				}
				break;
			case NEW:
				log.error("User " + username + " role is set to NEW. Administrator must set role to ADMIN, TOOL, ANALYST, or USER");
				return null;
			default:
				log.error("Unknown user role: " + userRole);
				return null;
			}
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			ArrayList<AppInfoGwt> appsList = new ArrayList<AppInfoGwt>();

			while (resultSet.next()) {
				AppInfoGwt resultSetAppInfo = getAppInfo(resultSet);
				if (userRole == Role.ADMIN) {
					// ADMINs can view all apps
					appsList.add(resultSetAppInfo);
				} else if (userRole == Role.ANALYST) {
					// ANALYSTs can view apps in their org unit and below
					if (appIsAccessibleToUser(username, resultSetAppInfo)) {
						appsList.add(resultSetAppInfo);
					}
				} else if (userRole == Role.USER ||
						userRole == Role.TOOL_PROVIDER) {
					// USERS and TOOL_PROVIDERS can view only their own apps
					appsList.add(resultSetAppInfo);
				}
			}
			// Return last-checked timestamp and apps
			appsListGwt = new AppsListGwt();
			appsListGwt.appsLastChecked = new Date(System.currentTimeMillis());
			appsListGwt.apps = appsList;
		} catch (final SQLException e) {
			log.error(username + ": " + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cleanUpConnection(connection);
			cleanUpStatement(statement);
			cleanUpResultSet(resultSet);
		}
		return appsListGwt;
	}

	public static boolean appIsAccessibleToUser(String userName,
			AppInfoGwt appInfo) {	
		// Check if the user owns the app
		if (userName.equals(appInfo.ownerName)) {
			return true;
		}

		// Check if user is an ADMIN
		String userRoleStr = Database.getRoleStr(userName);
		Role userRole = null;
		try {
			userRole = Role.getRole(userRoleStr);
			if (userRole == null) {
				log.warn("Role is null for user '" + userName + "'");
				return false;
			} else if (userRole == Role.ADMIN){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Check if app owner still exists in the database. If they do not, 
		// deny the request because we cannot verify the app owner's role
		// (which, for example, could be ADMIN).
		String ownerName = appInfo.ownerName;
		if (!Database.userExists(ownerName)) {
			log.warn("App owner " + ownerName + " no longer exists in database");
			return false;
		}
		
		// Check if app was uploaded by an owner that is a member of an org unit
		// accessible to the user if the user is an ANALYST.
		String appOwnerRoleStr = Database.getRoleStr(ownerName);
		
		// Check if app is owned by an ADMIN. If so, users cannot access
		// apps owned by ADMINs (unless the user is also an ADMIN).
		try {
			Role appOwnerRole = Role.getRole(appOwnerRoleStr);
			if (appOwnerRole == null) {
				log.debug("Role is null for app owner '" + ownerName + "'");
				return false;
			} else if (appOwnerRole == Role.ADMIN) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// Check if owner's hierarchy is null
		String appOwnerHierarchyStr = null;
		try {
			appOwnerHierarchyStr = Role.getOrgMembershipLevelsStr(appOwnerRoleStr);
			if (appOwnerHierarchyStr == null || appOwnerHierarchyStr.isEmpty()) {
				//log.debug("App owner's hierarchy string is null or empty");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// Check if user's hierarchy is null
		String userHierarchyStr = null;
		try {
			userHierarchyStr = Role.getOrgMembershipLevelsStr(userRoleStr);
			if (userHierarchyStr == null || userHierarchyStr.isEmpty()) {
				//log.debug("User's hierarchy string is null or empty");
				return false;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}

		//log.debug("ACCESSIBLE?: " + userHierarchyStr + " in " + appOwnerHierarchyStr);

		// Check if user's role is ANALYST and if user has access to the 
		// owner's org unit.
		if (userRole == Role.ANALYST) {
			if (appOwnerHierarchyStr.indexOf(userHierarchyStr, 0) > -1) {
				// Analyst has access to app owner's org unit
				return true;
			}
		}

		return false;
	}

	public synchronized static boolean removeSession(String sessionId, String clientIpAddress) {
		return update("DELETE FROM sessions WHERE (clientaddress='"
				+ clientIpAddress + "' OR clientaddress='127.0.0.1') "
				+ "AND sessionid='" + sessionId + "'");
	}

	/**
	 * Check if current session exists and has not expired. If sessionId,
	 * clientIpAddress, or any other data is invalid, this method will return
	 * false.
	 */
	public static boolean sessionIsGood(String sessionId, String clientIpAddress) {
		if (sessionId == null || sessionId.isEmpty()) {
			//log.debug("Session ID is null while authenticating session. "
			//		+ "Cannot authenticate session.");
			return false;
		}
		if (clientIpAddress == null || clientIpAddress.isEmpty()) {
			log.debug("Client IP is null -- cannot authenticate session.");
			return false;
		}
		final Date sessionExpiration = getSessionExpiration(sessionId,
				clientIpAddress);
		Date currentDate = new Date(System.currentTimeMillis());
		if (sessionExpiration == null) {
			log.debug("Session " + sessionId + " expired");
			return false;
		} else if (currentDate.after(sessionExpiration)) {
			// Session expired
			log.debug("Session " + sessionId + " has expired");
			update("DELETE FROM sessions WHERE sessionid='" + sessionId + "'");
			return false;
		} else {
			// Session has not expired yet so return false
			return true;
		}
	}

	/**
	 * @return Session expiration if it exists. If session does not exist (or no
	 *         longer exists), null is returned.
	 */
	public static Date getSessionExpiration(String sessionId,
			String clientIpAddress) {
		//log.debug("CLIENT IP IN SESS EXPIR: " + clientIpAddress);
		if (clientIpAddress.equals("0:0:0:0:0:0:0:1")) {
			clientIpAddress = "127.0.0.1";
		}
		String sql = "SELECT expiretime FROM sessions "
				+ "WHERE (clientaddress='" + clientIpAddress + "') "
				+ "AND sessionid='" + sessionId + "'";
		//log.debug("Getting session expiration using: " + sql);
		return getTimestamp(sql);
	}

	public synchronized static boolean updateSessionExpiration(String sessionId,
			String clientIpAddress, Date newSesionTimeout) {
		Timestamp sessionExpiration = new Timestamp(newSesionTimeout.getTime());
		return update("UPDATE sessions SET expiretime='" + sessionExpiration
				+ "' WHERE (clientaddress='" + clientIpAddress
				+ "' OR clientaddress='127.0.0.1') " + "AND sessionid='"
				+ sessionId + "'");
	}

	/**
	 * This method saves credentials as a string to the db with the following
	 * syntax: <code>
	 * [<toolId1>,<toolName1>,<OS1>,<credentialsRequired1>]:<parameter1_name>=<parameter1_value>,<parameter2_name>=<parameter2=value>,...;[<toolId2>,<toolName2>,<OS2>,<credentialsRequired2>]:<parameter1_name>=<parameter1_value>,...(<platform2>)<tool1_id>...
	 * <code>
	 * For example: <b><b>
	 * <code>
	 * [tool1,App Tool,ANDROID,true]:userid=me,password=mypassword,key=12345;[tool2,Other  Tool,IOS,false]:username=me,pwd=mypassword,pin=12345<code>
	 * <b><b>
	 * Note that parameter names and the number of parameters will vary 
	 * based on the tools used. Note that parameter names and values must not
	 * contain the following characters: '[',']',':',';',',' and '='. Whitespace
	 * should not be used except for tool names.
	 * 
	 * TODO: Look into possible XML schema for this.
	 */
	public synchronized static void saveUserToolCredentials(String username,
			ArrayList<UserToolCredentials> credentialsList) {
		StringBuffer credentialsStrBuf = new StringBuffer();
		for (int i = 0; i < credentialsList.size(); i++) {
			UserToolCredentials toolCredentials = credentialsList.get(i);
			credentialsStrBuf.append("[" + toolCredentials.toolId + ","
					+ toolCredentials.toolName + "," + toolCredentials.os + ","
					+ toolCredentials.authRequired + "]");
			if (toolCredentials.authRequired) {
				credentialsStrBuf.append(":"); // Colon only used if
												// authentication is required
				for (int j = 0; j < toolCredentials.authParamNames.length; j++) {
					String parameterName = toolCredentials.authParamNames[j];
					String parameterValue = toolCredentials.authParamValues[j];
					credentialsStrBuf.append(parameterName + "="
							+ parameterValue);
					if (j < toolCredentials.authParamNames.length - 1)
						credentialsStrBuf.append(","); // Only add comma if
														// there are more
														// parameters
				}
			}
			if (i < credentialsList.size() - 1)
				credentialsStrBuf.append(";"); // Only add semi-colon if there
												// are more tools
		}
		String credentials = credentialsStrBuf.toString();
		// log.debug("Saving " + username + " tool credentials to Database: \n"
		// +
		// credentials);
		// Save to database
		update("UPDATE users SET toolsAuth='" + credentials
				+ "' WHERE username='" + username + "'");
	}

	public static boolean appExists(String appid) {
		return exists("SELECT * FROM apps " + "WHERE appid='" + appid + "'");
	}



	private synchronized static ArrayList<UserToolCredentials> createToolCredentialsList(
			String username, ArrayList<ToolInfoGwt> tools) {
		ArrayList<UserToolCredentials> toolCredentialsList = new ArrayList<UserToolCredentials>();
		// Initialize credentials for each tool
		for (int i = 0; i < tools.size(); i++) {
			ToolInfoGwt toolInfo = tools.get(i);
			UserToolCredentials toolCredentials = new UserToolCredentials();
			toolCredentials.toolId = toolInfo.getId();
			toolCredentials.toolName = toolInfo.getName();
			toolCredentials.os = toolInfo.getOs();
			toolCredentials.authRequired = toolInfo.requiresAuthentication();
			// Initialize parameter names and values only if authentication is
			// required
			if (toolCredentials.authRequired) {
				toolCredentials.authParamNames = toolInfo
						.getAuthenticationParameterNames();
				String defaultParameterValue = "null";
				toolCredentials.authParamValues = new String[toolInfo
						.getAuthenticationParameterNames().length];
				// Set all initial parameter values to the String "null"
				Arrays.fill(toolCredentials.authParamValues,
						defaultParameterValue);
			}
			// Add each tool credentials to the credentials list.
			toolCredentialsList.add(toolCredentials);
		}
		// Save to database
		saveUserToolCredentials(username, toolCredentialsList);
		return toolCredentialsList;
	}



	public static ArrayList<UserToolCredentials> getUserToolCredentials(
			String username) {
		String sql = "Select toolsAuth FROM users WHERE username='" + username
				+ "'";
		String credentialsString = Database.getString(sql);
		if (credentialsString == null) {
			log.error("Tool credentials for user " + username + " is null.");
			return null;
		} else {
			return getToolCredentials(credentialsString);
		}
	}

	/**
	 * This method parses credentials retrieved as a string from the db with the
	 * following syntax: <code>
	 * [<toolId1>,<toolName1>,<OS1>,<credentialsRequired1>]:<parameter1_name>=<parameter1_value>,<parameter2_name>=<parameter2=value>,...;[<toolId2>,<toolName2>,<OS2>,<credentialsRequired2>]:<parameter1_name>=<parameter1_value>,...(<platform2>)<tool1_id>...
	 * <code>
	 * For example: <b><b>
	 * <code>
	 * [tool1,App Tool,ANDROID,true]:userid=me,password=mypassword,key=12345;[tool2,Other  Tool,IOS,false]:username=me,pwd=mypassword,pin=12345<code>
	 * <b><b>
	 * Note that parameter names and the number of parameters will vary 
	 * based on the tools used. Note that parameter names and values must not
	 * contain the following characters: '[',']',':',';',',' and '='. Whitespace
	 * should not be used except for tool names.
	 * 
	 * @param credentialsString
	 */
	private static ArrayList<UserToolCredentials> getToolCredentials(
			String credentialsString) {
		ArrayList<UserToolCredentials> toolsList = new ArrayList<UserToolCredentials>();
		// Parse credentials. Note that if the split regex is not matched, then
		// tools will only contain one string, namely, credentialsString.
		String[] tools = credentialsString.split(";");
		for (int i = 0; i < tools.length; i++) {
			UserToolCredentials toolCredentialsGwt = new UserToolCredentials();
			String tool = tools[i];
			// Parse tool info and credential parameters
			String[] toolAndCredentials = tool.split(":");
			String toolInfo = toolAndCredentials[0];
			// Remove brackets from tool info
			toolInfo = toolInfo.replace("[", "");
			toolInfo = toolInfo.replace("]", "");
			String[] toolInfoItems = toolInfo.split(",");
			toolCredentialsGwt.toolId = toolInfoItems[0];
			toolCredentialsGwt.toolName = toolInfoItems[1];
			toolCredentialsGwt.os = toolInfoItems[2];
			toolCredentialsGwt.authRequired = new Boolean(toolInfoItems[3])
					.booleanValue();
			// Get authentication parameters if required by the tool service
			if (toolCredentialsGwt.authRequired) {
				String[] credentials = toolAndCredentials[1].split(",");
				toolCredentialsGwt.authParamNames = new String[credentials.length];
				toolCredentialsGwt.authParamValues = new String[credentials.length];
				for (int j = 0; j < credentials.length; j++) {
					String credential = credentials[j];
					String[] nameAndValue = credential.split("=");
					String name = nameAndValue[0];
					String value = nameAndValue[1];
					toolCredentialsGwt.authParamNames[j] = name;
					toolCredentialsGwt.authParamValues[j] = value;
				}
			}
			toolsList.add(toolCredentialsGwt);
		}
		return toolsList;
	}

	private synchronized static void updateToolCredentials(String username,
			ArrayList<UserToolCredentials> toolCredentialsList,
			ArrayList<ToolInfoGwt> tools) {
		try {
			// For each tool, make sure there is a corresponding tool in the
			// user's list of credentials
			for (int i = 0; i < tools.size(); i++) {
				ToolInfoGwt tool = tools.get(i);
				boolean match = false;
				for (int j = 0; j < toolCredentialsList.size(); j++) {
					UserToolCredentials credential = toolCredentialsList.get(j);
					if (tool.getId().equals(credential.toolId)
							&& tool.getOs().equals(credential.os)) {
						match = true;
						break;
					}
				}
				if (!match) {
					log.debug("Adding new tool to credentials list: "
							+ tool.getId() + ", OS: " + tool.getOs());
					/*
					 * A tool exists that does not have a corresponding user
					 * credential, so create a new ToolCredentialsGwt object,
					 * add it to the tool credentials list, and update the
					 * database with the new credentials list. Note that the
					 * function parameter toolCredentialsList will have a
					 * changed value for the calling method.
					 */
					UserToolCredentials newCredential = new UserToolCredentials();
					newCredential.toolId = tool.getId();
					newCredential.toolName = tool.getName();
					newCredential.os = tool.getOs();
					newCredential.authRequired = tool.requiresAuthentication();
					if (newCredential.authRequired) {
						newCredential.authParamNames = tool
								.getAuthenticationParameterNames();
						String defaultParameterValue = "null";
						newCredential.authParamValues = new String[tool
								.getAuthenticationParameterNames().length];
						// Set all initial parameter values to the String "null"
						Arrays.fill(newCredential.authParamValues,
								defaultParameterValue);
					}
					// Add each tool credentials to the credentials list.
					toolCredentialsList.add(newCredential);
				}
			}
			// Update credentials in database
			saveUserToolCredentials(username, toolCredentialsList);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	public static Timestamp getTimestamp(String sql) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				// This returns an MySql Timestamp which is a subclass of Java Date.
				return resultSet.getTimestamp(1);
			}
		} catch (final SQLException e) {
			log.error(e.toString() + " using: " + sql);
		} finally {
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
		return null;
	}

	public static boolean exists(String sql) {
		return (getString(sql) != null);
	}

	public static String getString(String sql) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				return resultSet.getString(1);
			}
		} catch (final SQLException e) {
			log.error(e.toString() + " using: " + sql);
		} finally {
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
		return null;
	}

	public synchronized static boolean update(String sql) {
		Connection connection = null;
		Statement statement = null;
		try {
			//log.debug("Trying to execute: " + sql);
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			return true;
		} catch (final SQLException e) {
			log.error(e.toString());
			return false;
		} finally {
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
	}

	private static boolean cleanUpResultSet(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
				resultSet = null;
				return true;
			} catch (SQLException e) {
				log.error(e.toString());
				return false;
			}
		}
		return true;
	}

	public static boolean cleanUpPreparedStatement(
			PreparedStatement preparedStatement) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
				preparedStatement = null;
				return true;
			} catch (SQLException e) {
				log.error(e.toString());
				return false;
			}
		}
		return true;
	}

	public static boolean cleanUpConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
				return true;
			} catch (SQLException e) {
				log.error(e.toString());
				return false;
			}
		}
		return true;
	}

	public synchronized static boolean addTableColumn(String tableName, String columnName,
			String type) {
		return update("ALTER TABLE " + tableName + " ADD " + columnName + " "
				+ type);
	}

	public static boolean adminExists() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = null;
		boolean foundAdmin = false;
		try {
			connection = getConnection();
			sql = "SELECT * FROM users WHERE role='" + Role.ADMIN.name() + "'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				foundAdmin = true;
				break;
			}
		} catch (final SQLException e) {
			log.error(e.toString());
		} finally {
			sql = null;
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
		return foundAdmin;
	}

	public static long getLong(String sql) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				return resultSet.getLong(1);
			}
		} catch (final SQLException e) {
			log.error(e.toString() + " using: " + sql);
		} finally {
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
		return -1;
	}

	public static boolean getBoolean(String sql) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				return resultSet.getBoolean(1);
			}
		} catch (final SQLException e) {
			log.error(e.toString());
		} finally {
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
		return false;
	}

	public static String getCurrentProcessingAppId() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			// Check if an app is PROCESSING
			String sql = "SELECT appid FROM apps " + "WHERE appstatus='"
					+ AppStatus.PROCESSING.name() + "' ORDER BY "
					+ "submittime ASC";
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				// return true;
				return resultSet.getString(1);
			}
			return null;
		} catch (final SQLException e) {
			log.error(e.toString());
			return null;
		} finally {
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
	}

	/**
	 * Get the next app that has the given appstatus, in ascending order.
	 * 
	 * @return The appid of the next app.
	 */
	public synchronized static String getNextApp(AppStatus appStatus) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		final String sql = "SELECT appid FROM apps " + "WHERE appstatus='"
				+ appStatus.name() + "' ORDER BY " + "submittime ASC";
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				return resultSet.getString(1);
			}
		} catch (final SQLException e) {
			log.error(e.toString());
		} finally {
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
		return null;
	}

	public static String getAppName(String appid) {
		return getString("SELECT appname FROM apps " + "WHERE appid='" + appid
				+ "'");
	}

	public static String getAppStatus(String appid) {
		return getString("SELECT appstatus FROM apps " + "WHERE appid='"
				+ appid + "'");
	}

	public static String getPackageName(String appid) {
		return getString("SELECT packagename FROM apps " + "WHERE appid='"
				+ appid + "'");
	}

	public static String getVersionCode(String appid) {
		return getString("SELECT versioncode FROM apps " + "WHERE appid='"
				+ appid + "'");
	}

	public static String getVersionName(String appid) {
		return getString("SELECT versionname FROM apps " + "WHERE appid='"
				+ appid + "'");
	}

	public static DeviceOS getAppOS(String appid) {
		String appOSStr = getString("SELECT os FROM apps " + "WHERE appid='"
				+ appid + "'");
		if (appOSStr == null) {
			return null;
		} else {
			return DeviceOS.getOS(appOSStr);
		}
	}

	public static String getClientIPAddress(String appid) {
		return getString("SELECT clienthost FROM apps " + "WHERE appid='"
				+ appid + "'");
	}

	public synchronized static Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(AppVetProperties.DB_URL
					+ "/appvet?user=" + AppVetProperties.DB_USERNAME
					+ "&password=" + AppVetProperties.DB_PASSWORD);
			if (connection != null) {
				connection.setAutoCommit(true); // No need to manually commit
			} else {
				log.error("Could not connect to database.");
			}

			/**
			 * // Check connection count to ensure we have not maxed out // JDBC
			 * connections. String sql =
			 * "SELECT COUNT(*) FROM information_schema.PROCESSLIST";
			 * log.error("CONNECTION COUNT: " + getConnectionCount(connection,
			 * sql));
			 */
		} catch (final Exception e) {
			log.error("Could not connect to database: "
					+ e.toString()
					+ "\n"
					+ "Make sure your MySQL password in your AppVetProperties.xml file is correct");
		}
		return connection;
	}

	private static AppInfoGwt getAppInfo(ResultSet resultSet) {
		final AppInfoGwt appInfo = new AppInfoGwt();
		try {
			appInfo.appId = resultSet.getString(1);
			appInfo.lastUpdated = resultSet.getTimestamp(2);
			appInfo.appName = getNonNullAttributeValue(resultSet.getString(3));
			appInfo.packageName = getNonNullAttributeValue(resultSet
					.getString(4));
			appInfo.versionCode = getNonNullAttributeValue(resultSet
					.getString(5));
			appInfo.versionName = getNonNullAttributeValue(resultSet
					.getString(6));
			// Skip file and project name until end
			appInfo.submitTime = resultSet.getTimestamp(8);
			String appStatusString = resultSet.getString(9);
			appInfo.appStatus = AppStatus.getStatus(appStatusString);
			appInfo.ownerName = getNonNullAttributeValue(resultSet
					.getString(10));
			appInfo.clientHost = getNonNullAttributeValue(resultSet
					.getString(11));
			String osName = getNonNullAttributeValue(resultSet.getString(12));
			appInfo.os = DeviceOS.getOS(osName);
			appInfo.setAppFileAndProjectName(resultSet.getString(7), appInfo.os);
			// Check if icon exists
			String iconPath = AppVetProperties.APP_IMAGES_PATH + "/"
					+ appInfo.appId + ".png";
			File iconFile = new File(iconPath);
			if (iconFile.exists()) {
				// Set URL for this icon so that it can be displayed by GWT
				// client.
				// Note this URL is in $CATALINA_HOME/webapps/appvet_images.
				appInfo.iconURL = AppVetProperties.APPVET_APP_IMAGES_URL + "/"
						+ appInfo.appId + ".png";
			} else {
				// Set URL to null. This will force GWT gui to display default
				// Android icon.
				appInfo.iconURL = null;
			}
		} catch (final SQLException e) {
			log.error(e.toString());
		}
		return appInfo;
	}

	public static String getNonNullAttributeValue(String string) {
		return (string == null) ? "" : string;
	}

	public static String getAppFileName(String appid) {
		return getString("SELECT filename FROM apps " + "WHERE appid='" + appid
				+ "'");
	}

	public static String getOwner(String appid) {
		return getString("SELECT username FROM apps WHERE appid='" + appid
				+ "'");
	}

	public static String getOs(String appid) {
		return getString("SELECT os FROM apps WHERE appid='" + appid + "'");
	}

	public static String getPasswordHash(String username) {
		return getString("SELECT password FROM users WHERE username='"
				+ username + "'");
	}

	public static String getRoleStr(String username) {
		return getString("SELECT role FROM users "
				+ "WHERE username='" + username + "'");
	}

	public static String getSessionUser(String sessionId) {
		final String cmd = "SELECT username FROM sessions "
				+ "WHERE sessionId='" + sessionId + "'";
		return getString(cmd);
	}

	public static ArrayList<String> getTableColumnNames(String tableName) {
		ArrayList<String> columnNames = null;
		String sql = "SELECT * FROM " + tableName;
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		ResultSetMetaData rsmd = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			rsmd = resultSet.getMetaData();
			final int columnCount = rsmd.getColumnCount();
			columnNames = new ArrayList<String>();
			// Column count starts from 1
			for (int i = 1; i < (columnCount + 1); i++) {
				final String name = rsmd.getColumnName(i);
				columnNames.add(name);
			}
		} catch (SQLException e) {
			log.error(e.toString() + " using: " + sql);
		} finally {
			rsmd = null;
			cleanUpConnection(connection);
			cleanUpStatement(statement);
			cleanUpResultSet(resultSet);
		}
		return columnNames;
	}

	public synchronized static Timestamp getLastUpdatedTime(String appId) {
		return getTimestamp("SELECT lastupdated FROM apps WHERE appid='"
				+ appId + "'");
	}

	public static String getPBKDF2Password(String password) {
		try {
			return Authenticate.createHash(password);
		} catch (Exception e) {
			log.error(e.toString());
			return null;
		}
	}

	public synchronized static boolean updateAppMetadata(String appid, String appName,
			String packageName, String versionCode, String versionName) {
		return update("UPDATE apps SET appname='" + appName
				+ "', packagename='" + packageName + "', versioncode='"
				+ versionCode + "', versionname='" + versionName + "' "
				+ "WHERE appid='" + appid + "'");
	}

	private static boolean cleanUpStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
				statement = null;
				return true;
			} catch (SQLException e) {
				log.error(e.toString());
				return false;
			}
		} else {
			return true;
		}
	}
}
