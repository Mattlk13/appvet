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
package gov.nist.appvet.servlet.preprocessor;

import gov.nist.appvet.servlet.shared.Emailer;
import gov.nist.appvet.servlet.shared.ErrorMessage;
import gov.nist.appvet.shared.all.AppStatus;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.backend.AppInfo;
import gov.nist.appvet.shared.backend.AppStatusManager;
import gov.nist.appvet.shared.backend.AppVetProperties;
import gov.nist.appvet.shared.backend.Database;
import gov.nist.appvet.shared.backend.FileUtil;
import gov.nist.appvet.shared.backend.ToolAdapter;
import gov.nist.appvet.shared.backend.ToolStatus;
import gov.nist.appvet.shared.backend.ToolStatusManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class registers an app with AppVet.
 * 
 * @author steveq@nist.gov
 */
public class Registration {

	private AppInfo appInfo = null;

	
	public Registration(AppInfo appInfo) {
		this.appInfo = appInfo;
	}
	

	public boolean registerApp() {
		appInfo.log.debug("Registering app " + appInfo.appId);
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ToolAdapter registrationTool = ToolAdapter.getByToolId(
				appInfo.os, "registration");
		final String registrationReportPath = appInfo.getReportsPath() + "/"
				+ registrationTool.reportName;
		BufferedWriter regReportWriter = null;
		
		try {			
			regReportWriter = new BufferedWriter(new FileWriter(
					registrationReportPath));
			AppStatus appStatus = AppStatusManager.getAppStatus(appInfo.appId);
			
			if (appStatus == null) {
				
				// Add this app to the tools database table
				ArrayList<ToolAdapter> availableTools = null;
				if (appInfo.os == DeviceOS.ANDROID) {
					availableTools = AppVetProperties.androidTools;
				} else if (appInfo.os == DeviceOS.IOS) {
					availableTools = AppVetProperties.iosTools;
				}

				// Set default initial status (NA) for each tool
				for (int i = 0; i < availableTools.size(); i++) {
					final ToolAdapter tool = availableTools.get(i);
					setInitialToolStatus(appInfo, tool);
				}		
				
				
				// Add this app to the apps database table
				connection = Database.getConnection();
				preparedStatement = connection
						.prepareStatement("REPLACE INTO apps (appid, lastupdated, appname, "
						+ "packagename, versioncode, versionname, filename, "
						+ "submittime, appstatus, "
						+ "username, clienthost, os"
						+ ") "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
				// Set app ID.
				preparedStatement.setString(1, appInfo.appId);
				// Set last updated to now
				Timestamp timestamp = new Timestamp(new Date().getTime());
				preparedStatement.setTimestamp(2, timestamp);
				// Set app name.
				if (appInfo.appName == null) {
					// Displayed when first uploaded to AppVet.
					appInfo.appName = "Received";
				}
				preparedStatement.setString(3, appInfo.appName);
				// Set package name. If file submission, package name will be 
				// null at this point.*/
				preparedStatement.setString(4, appInfo.packageName);
				// Set version code.
				preparedStatement.setString(5, null);
				// Set version name. If file submission, version name will be 
				// null at this point.
				preparedStatement.setString(6, appInfo.versionName);
				// Set file name (note that filename uses underscores to
				// replace spaces. If app metdata submission, filename will 
				// be null.
				preparedStatement.setString(7, appInfo.getAppFileName());
				final java.sql.Timestamp timeStamp = new java.sql.Timestamp(
						new java.util.Date().getTime());
				// Set submission timestamp.
				preparedStatement.setTimestamp(8, timeStamp);
				// Set app status.
				preparedStatement.setString(9, AppStatus.REGISTERING.name());
				// Set username.
				preparedStatement.setString(10, appInfo.ownerName);
				// Set client hostname.
				preparedStatement.setString(11, appInfo.clientHost);
				// OS/platform.
				preparedStatement.setString(12, appInfo.os.name());
				// Execute update and close.
				preparedStatement.executeUpdate();
				preparedStatement.close();

				// Add app to Android or iOS tool status table.
				if (appInfo.os == DeviceOS.ANDROID) {
					preparedStatement = connection
							.prepareStatement("REPLACE INTO androidtoolstatus "
									+ "(appid) values (?)");
				} else if (appInfo.os == DeviceOS.IOS) {
					preparedStatement = connection
							.prepareStatement("REPLACE INTO iostoolstatus "
									+ "(appid) values (?)");
				}

				preparedStatement.setString(1, appInfo.appId);
				preparedStatement.executeUpdate();
				preparedStatement.close();

				// Close DB connection
				connection.close();
				
				// Create registration report
				regReportWriter.write("<HTML>\n");
				regReportWriter.write("<head>\n");
				regReportWriter.write("<style type=\"text/css\">\n");
				regReportWriter.write("h3 {font-family:arial;}\n");
				regReportWriter.write("p {font-family:arial;}\n");
				regReportWriter.write("</style>\n");
				regReportWriter.write("<title>Registration Report</title>\n");
				regReportWriter.write("</head>\n");
				regReportWriter.write("<body>\n");
				String appVetImagesUrl = AppVetProperties.URL
						+ "/images/appvet_logo.png";
				regReportWriter.write("<img border=\"0\" width=\"192px\" src=\""
						+ appVetImagesUrl + "\" alt=\"AppVet Mobile App Vetting System\" />");
				regReportWriter.write("<HR>\n");
				regReportWriter.write("<h3>Registration Report</h3>\n");
				regReportWriter.write("<pre>\n");
				final Date date = new Date();
				final SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd' 'HH:mm:ss.SSSZ");
				final String currentDate = format.format(date);				
				
				if (appInfo.getAppFileName() != null) {
					
					regReportWriter.write("File: \t\t" + 
							appInfo.getAppFileName() + "\n");
					
					if (!FileUtil.saveFileUpload(appInfo)) {
						regReportWriter.write("<font color=\"red\">"
								+ ErrorMessage.ERROR_SAVING_UPLOADED_FILE
										.getDescription() + "</font>");
						appInfo.log.error(ErrorMessage.ERROR_SAVING_UPLOADED_FILE
								.getDescription());
						ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
								registrationTool.toolId, ToolStatus.ERROR);
						return false;
					}
					
					appInfo.log.debug("Saved app: " + 
							appInfo.getAppFileName());
					
				}
				
				regReportWriter.write("Date: \t\t" + currentDate + "\n\n");
				regReportWriter.write("App ID: \t" + appInfo.appId + "\n");
				regReportWriter.write("Submitter: \t" + appInfo.ownerName
						+ "\n\n");
				regReportWriter
						.write("Status:\t\t<font color=\"black\">COMPLETED</font>\n");
				regReportWriter.write("</pre>\n");
				regReportWriter.write("</body>\n");
				regReportWriter.write("</HTML>\n");
				regReportWriter.close();
				appInfo.log.info("Registered app " + appInfo.appId);
				
				// Update registration status to LOW (i.e., COMPLETED).
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						registrationTool.toolId, ToolStatus.LOW);

				// Email notify
				if (AppVetProperties.emailEnabled) {
					UserInfo userInfo = Database.getUserInfo(appInfo.ownerName, null);
					String subject = "App " + appInfo.appId + " uploaded by '" + appInfo.ownerName + "'";
					String content = "App " + appInfo.appId + " was uploaded by '" + appInfo.ownerName + "'.";
					appInfo.log.debug("Emailing: " + subject);
					if (AppVetProperties.emailEnabled) {
						Emailer.sendEmail(userInfo.getEmail(), subject, content);
					}
				}
				
				appInfo.log.debug("App " + appInfo.appId + " has been uploaded by " + appInfo.ownerName);
				return true;
			} else {
				// Update registration status to ERROR.
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						registrationTool.toolId, ToolStatus.ERROR);
				appInfo.log.error(ErrorMessage.ERROR_APP_ALREADY_REGISTERED
						.getDescription());
				regReportWriter.write("<font color=\"red\">"
						+ ErrorMessage.ERROR_APP_ALREADY_REGISTERED
								.getDescription() + "</font>");
				// Close writer.
				regReportWriter.close();
				return false;
			}
			
		} catch (final Exception e) {
			
			appInfo.log.error(e.toString());
			return false;
			
		} finally {
			
			registrationTool = null;
			//registrationReportPath = null;
			Database.cleanUpBufferedWriter(regReportWriter);
			Database.cleanUpPreparedStatement(preparedStatement);
			Database.cleanUpConnection(connection);
			
		}
	}

	private static void setInitialToolStatus(AppInfo appInfo,
			ToolAdapter tool) {
		
		String sql = null;
		if (appInfo.os == DeviceOS.ANDROID) {
			sql = "UPDATE androidtoolstatus SET " + tool.toolId + "='NA' "
					+ "WHERE appid='" + appInfo.appId + "'";
		} else if (appInfo.os == DeviceOS.IOS) {
			sql = "UPDATE iostoolstatus SET " + tool.toolId + "='NA' "
					+ "WHERE appid='" + appInfo.appId + "'";
		}

		if (!Database.update(sql)) {
			appInfo.log.error("Failed to update tool start status");
		}
		
	}
}
