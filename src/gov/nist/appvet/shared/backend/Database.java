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
import gov.nist.appvet.shared.all.AppStatus;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.all.UserToolCredentials;

import java.io.BufferedWriter;
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
	
	
	public static boolean setAlertMessage(String username, SystemAlert alert) {
		// Clear existing message
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
			preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
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
	
	
	public static boolean deleteAlerts() {
		return update("DELETE FROM alerts");
	}
	
	
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

	
	public static boolean addTableColumn(String tableName, String columnName,
			String type) {
		return update("ALTER TABLE " + tableName + " ADD " + columnName + " "
				+ type);
	}
	

	public static boolean appExists(String appid) {
		return exists("SELECT * FROM apps " + "where appid='" + appid + "'");
	}
	

	

	public static boolean deleteUser(String username) {
		return update("DELETE FROM users " + "where username='" + username
				+ "'");
	}
	
	
	/** If AppVet is shutdown while an app is in the PROCESSING state, set 
	 * the status of the app from PROCESSING to ERROR upon the next startup
	 * of AppVet.
	 */
	public static void setProcessingStatusToError() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = null;
		try {
			connection = getConnection();
			sql = "SELECT * FROM apps where appstatus='" + AppStatus.PROCESSING.name() + "'";
			//arrayList = new ArrayList<UserInfoGwt>();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String appId = resultSet.getString(1);
				AppInfo appInfo = new AppInfo(appId);
				appInfo.log.warn("Found app " + appId + " in interrupted PROCESSING state. Changing status to ERROR.");
				update ("UPDATE apps SET appstatus='" + AppStatus.ERROR.name() + "' "
				+ "WHERE appId='" + appId + "'");
			}
		} catch (final SQLException e) {
			log.error(e.toString());
		} finally {
			sql = null;
			cleanUpResultSet(resultSet);
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}		
	}
	

	public static boolean adminAddNewUser(String username, String password,
			String org, String email, String role, String lastName,
			String firstName) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection
					.prepareStatement("REPLACE INTO users (username, password, "
							+ "org, email, role, lastName, firstName) "
							+ "values (?, ?, ?, ?, ?, ?, ?)");
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, getPBKDF2Password(password));
			preparedStatement.setString(3, org);
			preparedStatement.setString(4, email);
			preparedStatement.setString(5, role);
			preparedStatement.setString(6, lastName);
			preparedStatement.setString(7, firstName);
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
	

	public static boolean adminAddNewUser(UserInfo userInfo) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		final String username = userInfo.getUserName();

		try {
			connection = getConnection();
			preparedStatement = connection
					.prepareStatement(""
							+ "REPLACE INTO users (username, lastName, firstName, org, dept, email, role) values "
							+ "(?, ?, ?, ?, ?, ?, ?)");
			log.debug("Admin Adding user: " + username);
			preparedStatement.setString(1, username);
			log.debug("Admin Adding lastname: " + userInfo.getLastName());
			preparedStatement.setString(2, userInfo.getLastName());
			log.debug("Admin Adding firstname: " + userInfo.getFirstName());
			preparedStatement.setString(3, userInfo.getFirstName());
			log.debug("Admin Adding organization: " + userInfo.getOrganization());
			preparedStatement.setString(4, userInfo.getOrganization());
			log.debug("Admin Adding dept: " + userInfo.getDepartment());
			preparedStatement.setString(5, userInfo.getDepartment());
			log.debug("Admin Adding email: " + userInfo.getEmail());
			preparedStatement.setString(6, userInfo.getEmail());
			log.debug("Admin Adding role: " + userInfo.getRole());
			preparedStatement.setString(7, userInfo.getRole());
			preparedStatement.executeUpdate();
			if (userInfo.isChangePassword()) {
				final String password = userInfo.getPassword();
				try {
					if (setPBKDF2Password(username, password)) {
						return true;
					} else {
						return false;
					}
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
	public static boolean updateUser(UserInfo userInfo) {
		Connection connection = null;
		Statement statement = null;
		try {
			log.debug("Admin Updating user: " + userInfo.getUserName());
			log.debug("Admin Updating lastname: " + userInfo.getLastName());
			log.debug("Admin Updating firstname: " + userInfo.getFirstName());
			log.debug("Admin Updating organization: " + userInfo.getOrganization());
			log.debug("Admin Updating dept: " + userInfo.getDepartment());
			log.debug("Admin Updating email: " + userInfo.getEmail());
			log.debug("Admin Updating role: " + userInfo.getRole());
			
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("UPDATE users SET " + "username='"
					+ userInfo.getUserName() + "', org='"
					+ userInfo.getOrganization() + "', dept='"
					+ userInfo.getDepartment() + "', email='"
					+ userInfo.getEmail() + "', role='" + userInfo.getRole()
					+ "', lastName='" + userInfo.getLastName()
					+ "', firstName='" + userInfo.getFirstName()
					+ "' WHERE username='" + userInfo.getUserName() + "'");
			if (userInfo.isChangePassword()) {
				final String userName = userInfo.getUserName();
				final String password = userInfo.getPassword();
				try {
					if (setPBKDF2Password(userName, password)) {
						return true;
					} else {
						return false;
					}
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
	

	public static UserInfo getUserInfo(String username,
			ArrayList<ToolInfoGwt> tools) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		UserInfo userInfo = null;
		String sql = null;
		try {
			sql = "SELECT * FROM users where username='" + username + "'";
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			userInfo = new UserInfo();
			resultSet.next();
			userInfo.setUserName(resultSet.getString(1));
			userInfo.setPassword(getNonNullAttributeValue(resultSet.getString(2)));
			userInfo.setLastName(getNonNullAttributeValue(resultSet.getString(3)));
			userInfo.setFirstName(getNonNullAttributeValue(resultSet.getString(4)));
			userInfo.setOrganization(getNonNullAttributeValue(resultSet.getString(5)));
			userInfo.setDepartment(getNonNullAttributeValue(resultSet.getString(6)));
			userInfo.setEmail(getNonNullAttributeValue(resultSet.getString(7)));
			userInfo.setRole(getNonNullAttributeValue(resultSet.getString(8)));
			userInfo.setLastLogon(resultSet.getTimestamp(9));
			userInfo.setFromHost(getNonNullAttributeValue(resultSet.getString(10)));
			
			if (tools != null && tools.size() != 0) {
				String toolCredentialsStr = resultSet.getString(11);
				
				ArrayList<UserToolCredentials> toolCredentialsList = null;
				if (toolCredentialsStr == null) {
					// Create new tool credentials list
					toolCredentialsList = createToolCredentialsList(username, tools);
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
	

	private static ArrayList<UserToolCredentials> createToolCredentialsList(
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
	
	
	/** Kill timed-out tools in SUBMITTED state to ERROR forcing app to change
	 * status to ERROR.
	 */
	public static boolean killProcessingTools(String appId, DeviceOS os) {
		AppInfo appInfo = new AppInfo(appId);

		String sql = null;
		if (os == DeviceOS.ANDROID) {
			String androidTableName = "androidtoolstatus";
			ArrayList<String> androidTools = getTableColumnNames(androidTableName);
			for (int i = 1; i < androidTools.size(); i++) {
				// Skip appid column
				String toolId = androidTools.get(i);
				sql = "SELECT " + toolId + " FROM " + androidTableName + " WHERE appid='" + appId + "'";
				//log.debug("SQL for tool timeout: " + sql);
				String toolStatusString = Database.getString(sql);
				//log.debug("toolStatusString: " + toolStatusString);
				ToolStatus toolStatus = ToolStatus.getStatus(toolStatusString);
				if (toolStatus == null) {
					log.error("Unknown Android tool status encountered while killing active tool");
					return false;
				}
				if (toolStatus == ToolStatus.SUBMITTED) {
					ToolStatusManager.setToolStatus(os, appId, toolId, ToolStatus.ERROR);
					log.warn("Tool " + toolId + " exceeded timeout. Setting tool status to " + ToolStatus.ERROR);
					appInfo.log.warn("Tool " + toolId + " exceeded timeout. Setting tool status to " + ToolStatus.ERROR);

//					if (ToolStatusManager.setToolStatus(os, appId, toolId, ToolStatus.ERROR)) {
//						// Write error message to app's log
//						log.warn("Tool " + toolId + " exceeded timeout. Setting tool status to " + ToolStatus.ERROR);
//						appInfo.log.warn("Tool " + toolId + " exceeded timeout. Setting tool status to " + ToolStatus.ERROR);
//					} else {
//						log.error("Could not update " + toolId + " status for app " + appId + " to ERROR.");
//						return false;
//					}
				}
			}
			return true;
		} else if (os == DeviceOS.IOS){
			String iosTableName = "iostoolstatus";
			ArrayList<String> androidTools = getTableColumnNames(iosTableName);
			for (int i = 1; i < androidTools.size(); i++) {
				// Skip appid column
				String toolId = androidTools.get(i);
				sql = "SELECT " + toolId + " FROM " + iosTableName + " WHERE appid='" + appId + "'";
				//log.debug("SQL for tool timeout: " + sql);
				String toolStatusString = Database.getString(sql);
				//log.debug("toolStatusString: " + toolStatusString);
				ToolStatus toolStatus = ToolStatus.getStatus(toolStatusString);
				if (toolStatus == null) {
					log.error("Unknown iOS tool status encountered while killing active tool");
					return false;
				}
				if (toolStatus == ToolStatus.SUBMITTED) {
					ToolStatusManager.setToolStatus(os, appId, toolId, ToolStatus.ERROR);
					log.warn("Tool " + toolId + " exceeded timeout. Setting tool status to " + ToolStatus.ERROR);
					appInfo.log.warn("Tool " + toolId + " exceeded timeout. Setting tool status to " + ToolStatus.ERROR);

//					if (ToolStatusManager.setToolStatus(os, appId, toolId, ToolStatus.ERROR)) {
//						// Write error message to app's log
//						log.warn("Tool " + toolId + " exceeded timeout. Setting tool status to " + ToolStatus.ERROR);
//						appInfo.log.warn("Tool " + toolId + " exceeded timeout. Setting tool status to " + ToolStatus.ERROR);
//					} else {
//						log.error("Could not update " + toolId + " status for app " + appId + " to ERROR.");
//						return false;
//					}
				}
			}
			return true;

		} else {
			log.error("Unknown OS for getting processing tool IDs.");
			return false;
		}
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
	public static void saveUserToolCredentials(String username,
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
	

	public static ArrayList<UserToolCredentials> getUserToolCredentials(
			String username) {
		String sql = "Select toolsAuth FROM users where username='" + username
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
	

	private static void updateToolCredentials(String username,
			ArrayList<UserToolCredentials> toolCredentialsList,
			ArrayList<ToolInfoGwt> tools) {
		try {
			// For each tool, make sure there is a corresponding tool in the
			// user's list of credentials
			for (int i = 0; i < tools.size(); i++) {
				ToolInfoGwt tool = tools.get(i);
				boolean match = false;
				for (int j = 0; j < toolCredentialsList.size(); j++) {
					UserToolCredentials credential = toolCredentialsList
							.get(j);
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
	

	private static void printToolCredentials(String username,
			ArrayList<UserToolCredentials> credentialsList) {
		for (int i = 0; i < credentialsList.size(); i++) {
			UserToolCredentials toolCredentials = credentialsList.get(i);
			log.debug("[" + toolCredentials.toolId + ","
					+ toolCredentials.toolName + "," + toolCredentials.os + ","
					+ toolCredentials.authRequired + "]");
			if (toolCredentials.authRequired) {
				for (int j = 0; j < toolCredentials.authParamNames.length; j++) {
					log.debug(toolCredentials.authParamNames[j] + "="
							+ toolCredentials.authParamValues[j]);
				}
			}
		}
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
	

	/**
	 * @param role If null, select all users.
	 * @return
	 */
	public static List<UserInfo> getUsers(Role role) {
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
				sql = "SELECT * FROM users WHERE role='" + role + "' ORDER BY lastName ASC";
			} 
			
			arrayList = new ArrayList<UserInfo>();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				userInfo = new UserInfo();
				userInfo.setUserName(resultSet.getString(1));
				userInfo.setPassword(getNonNullAttributeValue(resultSet.getString(2)));
				userInfo.setLastName(getNonNullAttributeValue(resultSet.getString(3)));
				userInfo.setFirstName(getNonNullAttributeValue(resultSet.getString(4)));
				userInfo.setOrganization(getNonNullAttributeValue(resultSet
						.getString(5)));
				userInfo.setDepartment(getNonNullAttributeValue(resultSet
						.getString(6)));
				userInfo.setEmail(getNonNullAttributeValue(resultSet.getString(7)));
				userInfo.setRole(getNonNullAttributeValue(resultSet.getString(8)));
				userInfo.setLastLogon(resultSet.getTimestamp(9));
				userInfo.setFromHost(getNonNullAttributeValue(resultSet.getString(10)));
				arrayList.add(userInfo);
			}
		} catch (final SQLException e) {
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
	
	
	public static Timestamp getTimestamp(String sql) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
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


	public static boolean exists(String sql) {
		if (getString(sql) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public synchronized static boolean update(String sql) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			//log.debug("Updated database using: " + sql);
			return true;
		} catch (final SQLException e) {
			log.error("Could not update database using: " + sql + "\n" + e.toString());
			return false;
		} finally {
			cleanUpStatement(statement);
			cleanUpConnection(connection);
		}
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
			String sql = "SELECT appid FROM apps " + "where appstatus='"
					+ AppStatus.PROCESSING.name() + "' ORDER BY "
					+ "submittime ASC";
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				//return true;
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
	
	
	/** For an app that has timed-out waiting for one or more reports to 
	 * be received, set all tools in a SUBMITTED state to ERROR.
	 * @param appId
	 */
	public static void setAppProcessingTimeoutToError(String appId) {
		
	}
	

	/**
	 * Get the next app that has the given appstatus, in ascending order.
	 * 
	 * @return The appid of the next app.
	 */
	public static String getNextApp(AppStatus appStatus) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		final String sql = "SELECT appid FROM apps " + "where appstatus='"
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
		return getString("SELECT appname FROM apps " + "where appid='" + appid
				+ "'");
	}
	
	public static String getAppStatus(String appid) {
		return getString("SELECT appstatus FROM apps " + "where appid='" + appid
				+ "'");
	}	

	public static String getPackageName(String appid) {
		return getString("SELECT packagename FROM apps " + "where appid='"
				+ appid + "'");
	}
	

	public static String getVersionCode(String appid) {
		return getString("SELECT versioncode FROM apps " + "where appid='"
				+ appid + "'");
	}
	

	public static String getVersionName(String appid) {
		return getString("SELECT versionname FROM apps " + "where appid='"
				+ appid + "'");
	}
	

	public static DeviceOS getAppOS(String appid) {
		String appOSStr = getString("SELECT os FROM apps " + "where appid='"
				+ appid + "'");
		if (appOSStr == null) {
			return null;
		} else {
			return DeviceOS.getOS(appOSStr);
		}
	}
	

	public static String getClientIPAddress(String appid) {
		return getString("SELECT clienthost FROM apps " + "where appid='"
				+ appid + "'");
	}
	

	public static Connection getConnection() {
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
			 // Check connection count to ensure we have not maxed out 
			 // JDBC connections.
			String sql = "SELECT COUNT(*) FROM information_schema.PROCESSLIST";
			log.error("CONNECTION COUNT: " + getConnectionCount(connection, sql));
			 */
		} catch (final Exception e) {
			log.error("Could not connect to database: " + e.toString());
		}
		return connection;
	}
	

	public static AppsListGwt getAllApps(String username) {
		Connection connection = null;
		AppsListGwt appsListGwt = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = null;
		Role userRole = getRole(username);

		try {
			// Get apps based on user's role
			userRole = getRole(username);
			switch (userRole) {
			case ADMIN: 
				// Admin can view all apps
				sql = "SELECT * FROM apps ORDER BY submittime DESC";
				break;
			case ANALYST: 
				// Analyst can view all apps
				sql = "SELECT * FROM apps ORDER BY submittime DESC";
				break;
			case ORG_ANALYST: 
				// Filtered below for just organization's apps
				sql = "SELECT * FROM apps ORDER BY submittime DESC";
				break;
			case DEPT_ANALYST: 
				// Filtered below for just department's apps
				sql = "SELECT * FROM apps ORDER BY submittime DESC";
				break;
			case TOOL_PROVIDER: 
				// Tool provider can only view apps submitted by them (for testing)
				sql = "SELECT * FROM apps where username='" + username + 
					"' ORDER BY submittime DESC";
				break; 
			case USER: 
				// Users can only see apps they have submitted
				sql = "SELECT * FROM apps where username='" + username + 
					"' ORDER BY submittime DESC";
				break;
			default: 
				log.error("Unknown user role: " + userRole);
				return null;
			}
			
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			String userOrg = Database.getOrganization(username);
			String userDept = Database.getDepartment(username); 
			ArrayList<AppInfoGwt> appsList = new ArrayList<AppInfoGwt>();
			
			while (resultSet.next()) {
				AppInfoGwt appInfo = getAppInfo(resultSet);
				
				if (userRole == Role.ORG_ANALYST) {
					// An ORG_ANALYST can view all apps from an org
					String appSubmitterUser = appInfo.ownerName;
					String submitterOrg = Database.getOrganization(appSubmitterUser);
					if (userOrg.equals(submitterOrg)) {
						appsList.add(appInfo);
					}
				} else if (userRole == Role.DEPT_ANALYST) {
					// A DEPT_ANALYST can view all apps from a dept in an org
					String appSubmitterUser = appInfo.ownerName;
					String submitterOrg = Database.getOrganization(appSubmitterUser);
					String submitterDept = Database.getDepartment(appSubmitterUser);
					if (userOrg.equals(submitterOrg) && userDept.equals(submitterDept)) {
						appsList.add(appInfo);
					}
				} else {
					appsList.add(appInfo);
				}				
			}
			
			// Return lastChecked timestamp and apps
			appsListGwt = new AppsListGwt();
			appsListGwt.appsLastChecked = 
					new Date(System.currentTimeMillis());
			appsListGwt.apps = appsList;
		} catch (final SQLException e) {
			log.error(username + ": " + e.toString());
		} finally {
			cleanUpConnection(connection);
			cleanUpStatement(statement);
			cleanUpResultSet(resultSet);
		}
		return appsListGwt;
	}
	

	public static AppsListGwt getUpdatedApps(String username,
			Date lastClientUpdateDate) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		Role userRole = null;
		String sql = null;
		AppsListGwt appsListGwt = null;
		

		try {
			Timestamp lastClientUpdate = new Timestamp(lastClientUpdateDate.getTime());
			
			// Get apps based on user's role
			userRole = getRole(username);
			switch (userRole) {
			case ADMIN: 
				// Admin can view all apps
				sql = "SELECT * FROM apps where lastupdated > '" + lastClientUpdate + "'";
				break;
			case ANALYST: 
				// Analyst can view all apps
				sql = "SELECT * FROM apps where lastupdated > '" + lastClientUpdate + "'";
				break;
			case ORG_ANALYST: 
				// Filtered below for just organization's apps
				sql = "SELECT * FROM apps where lastupdated > '" + lastClientUpdate + "'";
				break;
			case DEPT_ANALYST:
				// Filtered below for just department's apps for an org
				sql = "SELECT * FROM apps where lastupdated > '" + lastClientUpdate + "'";
				break;
			case TOOL_PROVIDER:
				// Tool provider can only view apps submitted by them (for testing)
				sql = "SELECT * FROM apps where username = '" + username + 
					"' and lastupdated > '" + lastClientUpdate + "'";
				break; 
			case USER: 
				// Users can only see apps they have submitted
				sql = "SELECT * FROM apps where username = '" + username + 
					"' and lastupdated > '" + lastClientUpdate + "'";
				break;
			default: 
				log.error("Unknown user role: " + userRole);
				return null;
			}
			//log.debug("SQL: " + sql);

			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			
			if (resultSet.wasNull()) {
				log.warn("resultSet was null");
			}
			
			String userOrg = Database.getOrganization(username);
			String userDept = Database.getDepartment(username); 
			ArrayList<AppInfoGwt> appsList = new ArrayList<AppInfoGwt>();
			
			while (resultSet.next()) {
				AppInfoGwt appInfo = getAppInfo(resultSet);
				log.debug("App " + appInfo.appId + " updated at " + appInfo.lastUpdated.toString() + ", lastClientCheck for " + username + ": " + lastClientUpdate);
				
				if (userRole == Role.ORG_ANALYST) {
					// An ORG_ANALYST can view all apps from an org
					String appSubmitterUser = appInfo.ownerName;
					String submitterOrg = Database.getOrganization(appSubmitterUser);
					if (userOrg.equals(submitterOrg)) {
						appsList.add(appInfo);
					}
				} else if (userRole == Role.DEPT_ANALYST) {
					// A DEPT_ANALYST can view all apps from a dept in an org
					String appSubmitterUser = appInfo.ownerName;
					String submitterOrg = Database.getOrganization(appSubmitterUser);
					String submitterDept = Database.getDepartment(appSubmitterUser);
					if (userOrg.equals(submitterOrg) && userDept.equals(submitterDept)) {
						appsList.add(appInfo);
					}
				} else {
					appsList.add(appInfo);
				}
			}
			
			// Return lastChecked timestamp and apps
			appsListGwt = new AppsListGwt();
			appsListGwt.appsLastChecked = 
					new Date(System.currentTimeMillis());
			appsListGwt.apps = appsList;	
			
		} catch (final SQLException e) {
			log.error(username + ": " + e.toString());
		} finally {
			sql = null;
			userRole = null;
			cleanUpConnection(connection);
			cleanUpStatement(statement);
			cleanUpResultSet(resultSet);
		}
		
		return appsListGwt;
	}
	

	public static AppInfoGwt getAppInfo(ResultSet resultSet) {
		final AppInfoGwt appInfo = new AppInfoGwt();
		try {
			appInfo.appId = resultSet.getString(1);
			appInfo.lastUpdated = resultSet.getTimestamp(2);
			appInfo.appName = getNonNullAttributeValue(resultSet.getString(3));
			appInfo.packageName = getNonNullAttributeValue(resultSet.getString(4));
			appInfo.versionCode = getNonNullAttributeValue(resultSet.getString(5));
			appInfo.versionName = getNonNullAttributeValue(resultSet.getString(6));
			// Skip file and project name until end			
			appInfo.submitTime = resultSet.getTimestamp(8);
			String appStatusString = resultSet.getString(9);
			appInfo.appStatus = AppStatus.getStatus(appStatusString);
			appInfo.ownerName = getNonNullAttributeValue(resultSet.getString(10));
			appInfo.clientHost = getNonNullAttributeValue(resultSet.getString(11));
			String osName = getNonNullAttributeValue(resultSet.getString(12));
			appInfo.os = DeviceOS.getOS(osName);
			appInfo.setAppFileAndProjectName(resultSet.getString(7), appInfo.os);
		} catch (final SQLException e) {
			log.error(e.toString());
		}
		return appInfo;
	}
	

	public static String getNonNullAttributeValue(String string) {
		return (string == null) ? "" : string;
	}
	

	public static String getAppFileName(String appid) {
		return getString("SELECT filename FROM apps " + "where appid='" + appid
				+ "'");
	}
	

	public static String getOwner(String appid) {
		return getString("SELECT username FROM apps " + "where appid='" + appid
				+ "'");
	}
	

	public static String getOs(String appid) {
		return getString("SELECT os FROM apps " + "where appid='" + appid + "'");
	}
	

	protected static String getPasswordHash(String username) {
		return getString("SELECT password FROM users " + "where username='"
				+ username + "'");
	}
	

	public static Role getRole(String username) {
		String roleString = getString("SELECT role FROM users "
				+ "where username='" + username + "'");
		final Role userRole = Role.getRole(roleString);
		roleString = null;
		if (userRole == null) {
			log.error("Error getting user role");
		}
		return userRole;
	}
	

	public static String getOrganization(String username) {
		return getString("SELECT org FROM users " + "where username='"
				+ username + "'");
	}
	
	
	public static String getDepartment(String username) {
		return getString("SELECT dept FROM users " + "where username='"
				+ username + "'");
	}
	

	

	public static String getSessionUser(String sessionId) {
		final String cmd = "SELECT username FROM sessions "
				+ "where sessionId='" + sessionId + "'";
		return getString(cmd);
	}
	

/*	public static Date getSubmitTime(String appid) {
		Timestamp value = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = "SELECT submittime FROM apps " + "where appid='" + appid
				+ "'";
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				return resultSet.getTimestamp(1);
			}
		} catch (final SQLException e) {
			log.error(e.toString() + " using: " + sql);
		} finally {
			sql = null;
			cleanUpConnection(connection);
			cleanUpStatement(statement);
			cleanUpResultSet(resultSet);
		}
		return value;
	}*/
	

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
	

	

	public static boolean removeSession(String sessionId, String clientIpAddress) {
		return update("DELETE FROM sessions WHERE (clientaddress='"
				+ clientIpAddress + "' OR clientaddress='127.0.0.1') "
				+ "AND sessionid='" + sessionId + "'");
	}
	
	
	public synchronized static Timestamp getLastUpdatedTime(String appId) {
		return getTimestamp("SELECT lastupdated FROM apps WHERE appid='" + appId + "'");
	}
	

	public synchronized static boolean setLastUpdatedTime(String appId) {
		Date lastUpdatedTime = getLastUpdatedTime(appId);
		log.debug("[SetLastUpdateTime] for "  + appId + " was " + lastUpdatedTime.toString());
		
		// To prevent race condition, wait n ms
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return update("UPDATE apps SET lastupdated = NOW() WHERE appid='" + appId + "'");

	}
	

	public static String getPBKDF2Password(String password) {
		try {
			return Authenticate.createHash(password);
		} catch (Exception e) {
			log.error(e.toString());
			return null;
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
	

	

	public static boolean updateAppMetadata(String appid, String appName,
			String packageName, String versionCode, String versionName) {
		return update("UPDATE apps SET appname='" + appName
				+ "', packagename='" + packageName + "', versioncode='"
				+ versionCode + "', versionname='" + versionName + "' "
				+ "WHERE appid='" + appid + "'");
	}
	

	public static boolean updateClientHost(String username, String host) {
		return update("UPDATE users SET fromhost ='" + host
				+ "' WHERE username = '" + username + "'");
	}
	

	public static boolean updateUserLogonTime(String username) {
//		Date logonDate = new Date();
//		SimpleDateFormat dateFormat = new SimpleDateFormat(
//				"yyyy-MM-dd HH:mm:ss");
//		String currentTime = dateFormat.format(logonDate);
		return update("UPDATE users SET lastlogon = NOW() WHERE username = '" + username + "'");
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
		} else {
			return true;
		}
	}
	

	// Deleting an app will not be immediately reflected to users
	// (other than the user deleting the app) until a new AppVet session is
	// started. A better but more complex approach is to
	// update the app's status to "DELETED" and update users' display.
	public static boolean deleteApp(DeviceOS os, String appid) {
		final boolean appDeleted = update("DELETE FROM apps " + "where appid='"
				+ appid + "'");
		boolean statusDeleted = false;
		if (os == DeviceOS.ANDROID) {
			statusDeleted = update("DELETE FROM androidtoolstatus "
					+ "where appid='" + appid + "'");
		} else {
			statusDeleted = update("DELETE FROM iostoolstatus "
					+ "where appid='" + appid + "'");
		}
		if (appDeleted && statusDeleted) {
			return true;
		} else {
			return false;
		}
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
		} else {
			return true;
		}
	}
	

	public static boolean cleanUpBufferedWriter(BufferedWriter bufferedWriter) {
		if (bufferedWriter != null) {
			try {
				bufferedWriter.close();
				bufferedWriter = null;
				return true;
			} catch (Exception e) {
				log.error(e.toString());
				return false;
			}
		} else {
			return true;
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
		} else {
			return true;
		}
	}
	
	
	/*--------------------------- Sessions ---------------------------*/
	
	
	public synchronized static String createNewSession(String username,
			String clientIpAddress) {
		UUID uuid = UUID.randomUUID();
		String sessionId = uuid.toString().replaceAll("-", "");
		// Add session duration to the current time to get expiration time
		Date expirationTime = new Date(System.currentTimeMillis() + 
				AppVetProperties.MAX_SESSION_IDLE_DURATION);
		String sql = "INSERT INTO sessions (sessionid, username, expiretime, clientaddress) "
				+ "VALUES(?, ?, ?, ?)";
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
		} finally {
			sql = null;
			uuid = null;
			expirationTime = null;
			cleanUpPreparedStatement(ps);
			cleanUpConnection(connection);
		}
		return sessionId;
	}
	
	
	/** 
	 * @return Session exipiration if it exists. If session does not
	 * exist (or no longer exists), null is returned.
	 */
	public static Date getSessionExpiration(String sessionId,
			String clientIpAddress) {
		
		if (clientIpAddress.equals("0:0:0:0:0:0:0:1")) {
			clientIpAddress = "127.0.0.1";
		}
		
		String sql = "SELECT expiretime FROM sessions "
				+ "where (clientaddress='" + clientIpAddress + "') "
				+ "AND sessionid='" + sessionId + "'";
		//log.debug("Getting session expiration using: " + sql);
		return getTimestamp(sql);
	}
	
	
	public static boolean updateSessionExpiration(String sessionId,
			String clientIpAddress, Date newSesionTimeout) {
		Timestamp sessionExpiration = new Timestamp(newSesionTimeout.getTime());
		return update("UPDATE sessions SET expiretime='"
				+ sessionExpiration + "' WHERE (clientaddress='"
				+ clientIpAddress + "' OR clientaddress='127.0.0.1') "
				+ "AND sessionid='" + sessionId + "'");
	}
	

	public static boolean clearExpiredSessions() {
		Timestamp currentTime = new Timestamp(new Date().getTime());
		String sql = "DELETE FROM sessions WHERE expiretime < '"
				+ currentTime + "'";
		log.debug("Clearing sessions with SQL: " + sql);
		return update(sql);
	}
	
	
	/**
	 * Check if current session exists and has not expired. If sessionId, 
	 * clientIpAddress, or
	 * any other data is invalid, this method will return false.
	 */
	public static boolean sessionIsGood(String sessionId,
			String clientIpAddress) {
		
		if (sessionId == null || sessionId.isEmpty()) {
			log.debug("Session ID is null while authenticating session. "
					+ "Cannot authenticate session.");
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
			update("DELETE FROM sessions WHERE sessionid='" + sessionId
					+ "'");
			return false;
		} else {
			// Session has not expired yet so return false
			return true;
		}
	}
	
	
	/*--------------------------- Constructor ---------------------------*/

	private Database() {
	}
}
