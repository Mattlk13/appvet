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

import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.servlet.shared.ErrorMessage;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.backend.AppInfo;
import gov.nist.appvet.shared.backend.Database;
import gov.nist.appvet.shared.backend.FileUtil;
import gov.nist.appvet.shared.backend.Logger;
import gov.nist.appvet.shared.backend.ToolAdapter;
import gov.nist.appvet.shared.backend.ToolStatus;
import gov.nist.appvet.shared.backend.ToolStatusManager;
import gov.nist.appvet.shared.backend.XmlUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author steveq@nist.gov
 */
public class AndroidMetadata {
	
	private final DeviceOS OS = DeviceOS.ANDROID;

	public final String APKTOOL_WINDOWS_COMMAND = "apktool.bat";
	public final String APKTOOL_LINUX_COMMAND = "apktool";
	
	/** Both Android and iOS MUST use the metadata tool ID 'appinfo' */
	private final String METADATA_TOOL_ID = "appinfo";
	private ToolAdapter appinfoTool = null;
	
	public AndroidMetadata() {
	}

	public boolean getMetadata(AppInfo appInfo) {
		appinfoTool = ToolAdapter.getByToolId(OS, METADATA_TOOL_ID);
		if (appinfoTool == null) {
			appInfo.log.error("Tool adapter 'appinfo' was not found. "
					+ "Cannot get app metadata.");
			return false;
		}
		// Set status for metadata
		ToolStatusManager.setToolStatus(appInfo,
				appinfoTool.toolId, ToolStatus.SUBMITTED);
		
		// Decode/extract app file
		if (!decodeApk(appInfo)) {
			// Update metadata in DB
			updateDbMetadata(appInfo);
			ToolStatusManager.setToolStatus(appInfo,
					appinfoTool.toolId, ToolStatus.ERROR);
			return false;
		}
		// Extract metadata from decoded app
		if (!getMetaData(appInfo)) {
			// Update metadata in DB
			updateDbMetadata(appInfo);
			ToolStatusManager.setToolStatus(appInfo,
					appinfoTool.toolId, ToolStatus.ERROR);
			return false;
		}
		// Update metadata in DB
		updateDbMetadata(appInfo);
		writeReport(appInfo, null);
		// Set metadata processing to LOW.
		ToolStatusManager.setToolStatus(appInfo,
				appinfoTool.toolId, ToolStatus.LOW);
		return true;
	}
	
	public void updateDbMetadata(AppInfo appInfo) {
		if (appInfo.appName == null || appInfo.appName.isEmpty() || 
				appInfo.appName.equals("Received")) {
			// Name has not been found or set. If app project name is 
			// available, use that
			String appProjectName = appInfo.getAppProjectName();
			if (appProjectName != null && !appProjectName.isEmpty()) {
				appInfo.appName = appProjectName;
			} else {
				appInfo.appName = "N/A";
			}
		}
		if (appInfo.packageName == null || appInfo.packageName.isEmpty()) {
			appInfo.packageName = "N/A";
		}
		if (appInfo.versionCode == null || appInfo.versionCode.isEmpty()) {
			appInfo.versionCode = "N/A";
		}
		if (appInfo.versionName == null || appInfo.versionName.isEmpty()) {
			appInfo.versionName = "N/A";
		}
		Database.updateAppMetadata(appInfo.appId, appInfo.appName,
				appInfo.packageName, appInfo.versionCode,
				appInfo.versionName);
	}
	
	private void writeReport(AppInfo appInfo, String errorMessage) {
		// The ID "appinfo" is the default metadata tool ID.
		final ToolAdapter appinfoTool = ToolAdapter.getByToolId(appInfo.os,
				METADATA_TOOL_ID);
		final String reportsPath = appInfo.getReportsPath();
		final String appInfoReportPath = reportsPath + "/"
				+ appinfoTool.reportName;
		BufferedWriter bufferedWriter = null;
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(appInfoReportPath);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("<HTML>\n");
			bufferedWriter.write("<head>\n");
			bufferedWriter.write("<style type=\"text/css\">\n");
			bufferedWriter.write("h3 {font-family:arial;}\n");
			bufferedWriter.write("p {font-family:arial;}\n");
			bufferedWriter.write("</style>\n");
			bufferedWriter.write("<title>Android Manifest Report</title>\n");
			bufferedWriter.write("</head>\n");
			bufferedWriter.write("<body>\n");
			String appVetImagesUrl = AppVetProperties.APPVET_URL
					+ "/images/appvet_logo.png";
			bufferedWriter.write("<img border=\"0\" width=\"192px\" src=\""
					+ appVetImagesUrl + "\" alt=\"AppVet Mobile App Vetting System\" />");
			bufferedWriter.write("<HR>\n");
			bufferedWriter.write("<h3>AndroidMetadata Pre-Processing Report"
					+ "</h3>\n");
			bufferedWriter.write("<pre>\n");
			final Date date = new Date();
			final SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd' 'HH:mm:ss.SSSZ");
			final String currentDate = format.format(date);
			bufferedWriter.write("App ID: \t" + appInfo.appId + "\n");
			bufferedWriter.write("File: \t\t" + appInfo.getAppFileName() + "\n");
			bufferedWriter.write("Date: \t\t" + currentDate + "\n\n");
			
			if (errorMessage != null && !errorMessage.isEmpty()) {
				// Write error
				bufferedWriter.write(errorMessage);
			} else {
				bufferedWriter.write("Package: \t" + appInfo.packageName + "\n");
				bufferedWriter.write("Version name: \t" + appInfo.versionName + "\n");
				bufferedWriter.write("Version code: \t" + appInfo.versionCode + "\n");
				if (appInfo.minSDK == null || appInfo.minSDK.isEmpty()) {
					bufferedWriter.write("Min SDK: \tN/A\n");
				} else {
					bufferedWriter.write("Min SDK: \t" + appInfo.minSDK + "\n");
				}
				if (appInfo.targetSDK == null || appInfo.targetSDK.isEmpty()) {
					bufferedWriter.write("Target SDK: \tN/A\n");
				} else {
					bufferedWriter.write("Target SDK: \t" + appInfo.targetSDK + "\n");
				}
				bufferedWriter
				.write("\nStatus:\t\t<font color=\"black\">COMPLETED</font>\n");
			}
			
			bufferedWriter.write("</pre>\n");
			bufferedWriter.write("</body>\n");
			bufferedWriter.write("</HTML>\n");
			bufferedWriter.close();
			fileWriter.close();
		} catch (Exception e) {
			appInfo.log.error(e.toString());
		} finally {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bufferedWriter = null;
			}
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fileWriter = null;
			}
		}
	}

	/**
	 * Use apktool to decode APK file.
	 */
	private boolean decodeApk(AppInfo appInfo) {
		if (AppVetProperties.APKTOOL_HOME == null) {
			appInfo.log
					.warn("APKTOOL_HOME, which is used to launch the apktool, is null.");
		}
		String appvetOS = System.getProperty("os.name");
		String apktoolCommand = AppVetProperties.APKTOOL_HOME + "/";
		if (appvetOS.toUpperCase().indexOf("WIN") > -1) {
			apktoolCommand += APKTOOL_WINDOWS_COMMAND;
		} else if (appvetOS.toUpperCase().indexOf("NUX") > -1) {
			apktoolCommand += APKTOOL_LINUX_COMMAND;
		}
		
		final String decodeCmd = apktoolCommand + " d "
				+ appInfo.getAppFilePath() + " -o " + appInfo.getProjectPath();
		final StringBuffer reportBuffer = new StringBuffer();	
		// Execute apktool
		boolean executed = execute(decodeCmd, reportBuffer);
		if (executed) {
			appInfo.log.debug("Decoded " + appInfo.appName
					+ " APK file successfully.");
			return true;
		} else {
			if (reportBuffer.indexOf("FileNotFound") >= 0
					|| reportBuffer
							.indexOf("was not found or was not readable") >= 0) {
				// Anti-virus on system may have removed app if it was
				// malware
				appInfo.log.error(reportBuffer.toString());
				writeReport(appInfo, "\n<font color=\"red\">"
								+ ErrorMessage.FILE_NOT_FOUND
										.getDescription()
								+ " (APK file removed by system; file may be malware)</font>");
			} else {
				appInfo.log.error(reportBuffer.toString());
				writeReport(appInfo, "\n<font color=\"red\">"
						+ ErrorMessage.ANDROID_APK_DECODE_ERROR
								.getDescription()
						+ ". APK file may be corrupted.</font>");
			}
			return false;
		}
	}
	
	/** IMPORTANT: Make sure that apktool is in a user-owned directory
	 * with executable permissions for root. Otherwise, apktool will not 
	 * execute properly.
	 */
	private boolean execute(String command, StringBuffer output) {
		List<String> commandArgs = Arrays.asList(command.split("\\s+"));
		ProcessBuilder pb = new ProcessBuilder(commandArgs);
		Process process = null;
		IOThreadHandler outputHandler = null;
		IOThreadHandler errorHandler = null;
		int exitValue = -1;
		try {
			process = pb.start();
			outputHandler = new IOThreadHandler(process.getInputStream());
			outputHandler.start();
			errorHandler = new IOThreadHandler(process.getErrorStream());
			errorHandler.start();
			if (process.waitFor(300000, TimeUnit.MILLISECONDS)) {
				// Process has waited and exited within the timeout
				exitValue = process.exitValue();
				if (exitValue == 0) {
					StringBuilder resultOut = outputHandler.getOutput();
					output.append(resultOut);
					return true;
				} else {
					StringBuilder resultError = errorHandler.getOutput();
					output.append(resultError);
					return false;
				}
			} else {
				// Process exceed timeout or was interrupted
				StringBuilder resultOutput = outputHandler.getOutput();
				StringBuilder resultError = errorHandler.getOutput();
				if (resultOutput != null) {
					output.append(resultOutput);
				} else if (resultError != null) {
					output.append(resultError);
				} else {
					output.append("Apktool timed-out");
				}
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (outputHandler != null && outputHandler.isAlive()) {
				try {
					outputHandler.inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
			if (errorHandler != null && errorHandler.isAlive()) {
				try {
					errorHandler.inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process != null && process.isAlive()) {
				process.destroy();
			} 
		}

	}

	private class IOThreadHandler extends Thread {
		private InputStream inputStream;
		private StringBuilder output = new StringBuilder();
		private final String lineSeparator = 
				System.getProperty("line.separator");;

		IOThreadHandler(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		public void run() {
			Scanner br = null;
			try {
				br = new Scanner(new InputStreamReader(inputStream));
				String line = null;
				while (br.hasNextLine()) {
					line = br.nextLine();
					output.append(line + lineSeparator);
				}
			} finally {
				if (br != null) {
					br.close();
				}
			}
		}

		public StringBuilder getOutput() {
			return output;
		}
	}

	private boolean getMetaData(AppInfo appInfo) {
		final String projectPath = appInfo.getProjectPath();
		try {
			File manifestFile = new File(projectPath + "/AndroidManifest.xml");
			appInfo.log.debug("manifestFile: " + projectPath + "/AndroidManifest.xml");
			if (!manifestFile.exists()) {
				appInfo.log.error("Apktool could not generate Android 'manifest' file which holds app metadata information. APK file may be corrupted.");
				writeReport(appInfo, "\n<font color=\"red\">ERROR: Apktool could not generate Android 'manifest' file which holds app metadata information. APK file may be corrupted.</font>");
				return false;
			}
			File stringsFile = new File(projectPath + "/res/values/strings.xml");
			appInfo.log.debug("stringsFilePath: " + projectPath + "/res/values/strings.xml");
			if (!stringsFile.exists()) {
				appInfo.log.error("Apktool could not generate Android 'strings' file which holds app metadata information. APK file may be corrupted.");
				writeReport(appInfo, "\n<font color=\"red\">ERROR: Apktool could not generate Android 'strings' file which holds app metadata information. APK file may be corrupted.</font>");
				return false;
			}
			appInfo.log.debug("Found AndroidMetadata.xml file\tOK");
			
			// Get the app metadata XML values.
			return getElementValues(appInfo, manifestFile, stringsFile);
			
		} catch (final Exception e) {
			appInfo.log.error(e.toString());
			writeReport(appInfo, "\n<font color=\"red\">"
					+ "ERROR: Could not generate required Android metdata files. APK file may be corrupted."
					+ "</font>");
			return false;
		}
	}

	/**
	 * Get metadata XML elements and their values.
	 */
	private boolean getElementValues(AppInfo appInfo,
			File manifestFile, File stringsFile) {
		final XmlUtil manifestXml = new XmlUtil(manifestFile);
		// Get package name
		final String manifestPackage = manifestXml.getXPathValue("/manifest/@package");
		if (manifestPackage == null || manifestPackage.isEmpty()) {
			appInfo.log.error("Could not retrieve package name from path '/manifest/@package' in Android manifest file. APK file may be corrupted.");
			writeReport(appInfo, "\n<font color=\"red\">"
					+ "ERROR: Could not retrieve package name from path '/manifest/@package' in Android manifest file. APK file may be corrupted.</font>");					
			return false;
		} else {
			appInfo.packageName = manifestPackage;
		}
		// Get icon file
		final String iconValue = manifestXml.getXPathValue("/manifest/application/@icon");
		setIcon(appInfo, iconValue);
		// Get app name
		final XmlUtil stringsXml = new XmlUtil(stringsFile);
		String appName = stringsXml
				.getXPathValue("/resources/string[@name='app_name']");
		if (appName == null || appName.isEmpty()) {
			appInfo.log.warn("Could not retrieve app name from path '/resources/string[@name='app_name']' in Android strings file. Using app project name.");
			String appProjectName = appInfo.getAppProjectName();
			if (appProjectName != null && !appProjectName.isEmpty()) {
				appInfo.appName = appProjectName;
			}
		} else {
			appInfo.appName = appName.trim();
		}
		// Check if apktool.yml file exists. If so, get remaining metadata info.
		File apkToolYmlFile = new File(appInfo.getProjectPath() + "/apktool.yml");
		if (apkToolYmlFile.exists()) {
			String line;
			InputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader bufferedReader = null;
			try {
				fis = new FileInputStream(apkToolYmlFile);
				isr = new InputStreamReader(fis,
						Charset.forName("UTF-8"));
				bufferedReader = new BufferedReader(isr);
				while ((line = bufferedReader.readLine()) != null) {
					// Remove all whitespace from line.
					String trimmed = line.replaceAll("\\s", "");
					String[] name_value = trimmed.split(":");
					if (name_value[0].equals("versionCode")) {
						// Remove single quotes from the value.
						if (name_value[1] != null) {
							appInfo.versionCode = name_value[1].replaceAll("'", "");
							appInfo.log
									.debug("VersionCode: " + appInfo.versionCode);	
						}
					} else if (name_value[0].equals("versionName")) {
						// Remove single quotes from the value.
						if (name_value[1] != null) {
							appInfo.versionName = name_value[1].replaceAll("'", "");
							appInfo.log
									.debug("VersionName: " + appInfo.versionName);
						}
					} else if (name_value[0].equals("minSdkVersion")) {
						// Remove single quotes from the value.
						if (name_value[1] != null) {
							appInfo.minSDK = name_value[1].replaceAll("'", "");
							appInfo.log.debug("Min SDK: " + appInfo.minSDK);
						}
					} else if (name_value[0].equals("targetSdkVersion")) {
						// Remove single quotes from the value.
						if (name_value[1] != null) {
							appInfo.targetSDK = name_value[1].replaceAll("'", "");
							appInfo.log.debug("Target SDK: " + appInfo.targetSDK);
						}
					}
				}
				bufferedReader.close();
				isr.close();
				fis.close();
				return true;
			} catch (FileNotFoundException e) {
				appInfo.log.error(e.toString());
				writeReport(appInfo, "\n<font color=\"red\">"
						+ "ERROR: Could not find apktool.yml file for extracting metadata. APK file may be corrupted.</font>");					
				return false;
			} catch (IOException e) {
				appInfo.log.error(e.toString());
				writeReport(appInfo, "\n<font color=\"red\">"
						+ "ERROR: Could not apktool.yml file. Cannot extract remaining metadata. APK file may be corrupted.</font>");					
				return false;
			} finally {
				isr = null;
				fis = null;
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					bufferedReader = null;
				}
			}
		} else {
			appInfo.log.error("Could not find file 'apktool.yml' file to extract app version, minSDK, or targetSDK. APK file may be corrupted.");
			writeReport(appInfo, "\n<font color=\"red\">"
					+ "Could not find file 'apktool.yml' file to extract app version, minSDK, or targetSDK. APK file may be corrupted.</font>");
			return false;
		}
	}

	private void setIcon(AppInfo appInfo, String iconValue) {
		File iconFile = null;
		if (iconValue == null || iconValue.isEmpty()) {
			appInfo.log.warn("Could not retrieve icon name from "
					+ "path '/manifest/application/@icon' in Android manifest file. Using default Android icon.");
			iconFile = new File(AppVetProperties.DEFAULT_ANDROID_ICON_PATH);
			appInfo.log.debug("Using default icon path: " + iconFile.getAbsolutePath());
		} else {
			// Icon value will have the syntax '@'<directoryName>'/'<iconName>
			if ((iconValue.indexOf("@") == 0) && iconValue.contains("/")) {
				final int slashIndex = iconValue.indexOf("/");
				final String directoryName = iconValue.substring(1, slashIndex);
				final String iconName = iconValue.substring(slashIndex + 1,
						iconValue.length());
				final String resFolderPath = appInfo.getProjectPath() + "/res";
				iconFile = getIcon(resFolderPath, directoryName, iconName);
				if ((iconFile == null) || !iconFile.exists()) {
					// Use default icon
					appInfo.log
							.warn("No icon file found. Using default icon...");
					iconFile = new File(AppVetProperties.DEFAULT_ANDROID_ICON_PATH);
					if (iconFile.exists()) {
						appInfo.log.debug("Using  icon file at: " + iconFile.getAbsolutePath());
					}
				} else {
					appInfo.log.debug("Found icon at: "
							+ iconFile.getAbsolutePath());
				}
			}	
		}

		// Save icon file in $CATALINA_HOME/webapps/appvet_images so that they
		// can be referenced quickly by URL
		writeIconFile(appInfo, iconFile);
	}
	
	public void writeIconFile(AppInfo appInfo, File sourceFile) {
		// Save icon file in $CATALINA_HOME/webapps/appvet_images so that they
		// can be referenced quickly by URL
		File destFile = new File(AppVetProperties.APP_IMAGES_PATH + "/"
				+ appInfo.appId + ".png");
		if (sourceFile != null && destFile != null) {
			if (FileUtil.copyFile(sourceFile, destFile)) {
				appInfo.log.debug("Copied icon image to " + destFile);
			} else {
				appInfo.log.warn("Could not copy icon image to " + destFile);
			}
		}
	}

	private File getIcon(String resFolderPath,
			String iconDirectoryName, String iconName) {
		File resDirectory = new File(resFolderPath);
		File[] resFiles = resDirectory.listFiles();
		File[] iconDirectoryFiles = null;
		try {
			if (resFiles == null) {
				return null;
			}
			for (final File resFile : resFiles) {
				if (resFile.isDirectory()) {
					// Icon could be in one of several directories containing
					// <iconDirectoryName>
					if (resFile.getName().contains(iconDirectoryName)) {
						iconDirectoryFiles = resFile.listFiles();
						if (iconDirectoryFiles == null) {
							return null;
						}
						for (final File iconFile : iconDirectoryFiles) {
							if (iconFile.getName().equals(iconName + ".png")) {
								return iconFile;
							}
						}
					}
				}
			}
			return null;
		} finally {
			iconDirectoryFiles = null;
			resFiles = null;
			resDirectory = null;
		}
	}
}