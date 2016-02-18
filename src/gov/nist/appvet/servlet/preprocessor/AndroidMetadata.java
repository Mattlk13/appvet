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

import gov.nist.appvet.servlet.shared.ErrorMessage;
import gov.nist.appvet.servlet.shared.Native;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.backend.AppInfo;
import gov.nist.appvet.shared.backend.AppVetProperties;
import gov.nist.appvet.shared.backend.Database;
import gov.nist.appvet.shared.backend.FileUtil;
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
	
	private static final DeviceOS OS = DeviceOS.ANDROID;

	public static final String APKTOOL_WINDOWS_COMMAND = "apktool.bat";
	public static final String APKTOOL_LINUX_COMMAND = "apktool";
	
	/** Both Android and iOS MUST use the metadata tool ID 'appinfo' */
	private static final String METADATA_TOOL_ID = "appinfo";
	private static ToolAdapter appinfoTool = null;
	

	public static boolean getFromFile(AppInfo appInfo) {
		
		appinfoTool = ToolAdapter.getByToolId(OS, METADATA_TOOL_ID);
		if (appinfoTool == null) {
			appInfo.log.error("Android tool adapter 'appinfo' was not found. Cannot get app metadata.");
			return false;
		}
		
		appInfo.log
				.debug("Acquiring Android metadata for app " + appInfo.appId);
		// The ID "appinfo" is the default metadata tool ID.
		final ToolAdapter appinfoTool = ToolAdapter.getByToolId(appInfo.os,
				"appinfo");
		final String reportsPath = appInfo.getReportsPath();
		final String appinfoReportPath = reportsPath + "/"
				+ appinfoTool.reportName;
		BufferedWriter appinfoReport = null;
		try {
			appinfoReport = new BufferedWriter(
					new FileWriter(appinfoReportPath));
			appinfoReport.write("<HTML>\n");
			appinfoReport.write("<head>\n");
			appinfoReport.write("<style type=\"text/css\">\n");
			appinfoReport.write("h3 {font-family:arial;}\n");
			appinfoReport.write("p {font-family:arial;}\n");
			appinfoReport.write("</style>\n");
			appinfoReport.write("<title>Android Manifest Report</title>\n");
			appinfoReport.write("</head>\n");
			appinfoReport.write("<body>\n");
			String appVetImagesUrl = AppVetProperties.URL
					+ "/images/appvet_logo.png";
			appinfoReport.write("<img border=\"0\" width=\"192px\" src=\""
					+ appVetImagesUrl + "\" alt=\"AppVet Mobile App Vetting System\" />");
			appinfoReport.write("<HR>\n");
			appinfoReport.write("<h3>AndroidMetadata Pre-Processing Report"
					+ "</h3>\n");
			appinfoReport.write("<pre>\n");
			final Date date = new Date();
			final SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd' 'HH:mm:ss.SSSZ");
			final String currentDate = format.format(date);
			appinfoReport.write("File: \t\t" + appInfo.getAppFileName() + "\n");
			appinfoReport.write("Date: \t\t" + currentDate + "\n\n");
			appinfoReport.write("App ID: \t" + appInfo.appId + "\n");
			final String fileNameUpperCase = appInfo.getAppFileName()
					.toUpperCase();
			if (fileNameUpperCase.endsWith(".APK")) {
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						appinfoTool.toolId, ToolStatus.SUBMITTED);
				// Use apktool to decode APK file.
				if (!decodeApk(appInfo, appinfoReport)) {
					return false;
				}
				// Extract metadata from apktool-generated directory.
				if (!getMetaData(appInfo, appinfoReport)) {
					return false;
				}
			} else {
				appInfo.log.error("File " + appInfo.getAppFileName()
						+ " has invalid file extension.");
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						appinfoTool.toolId, ToolStatus.ERROR);
				return false;
			}
			// Set metadata processing to LOW.
			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
					appinfoTool.toolId, ToolStatus.LOW);
			appinfoReport
					.write("\nStatus:\t\t<font color=\"black\">COMPLETED</font>\n");
			appInfo.log.debug("End Android metadata preprocessing for app "
					+ appInfo.appId);
			return true;
		} catch (final IOException e) {
			appInfo.log.error(e.toString());
			return false;
		} finally {
			try {
				if (appinfoReport != null) {
					appinfoReport.write("</pre>\n");
					appinfoReport.write("</body>\n");
					appinfoReport.write("</HTML>\n");
					appinfoReport.close();
					appinfoReport = null;
				}
			} catch (final IOException e) {
				appInfo.log.error(e.toString());
			}
		}
	}

	/**
	 * Use apktool to decode APK file.
	 */
	private static boolean decodeApk(AppInfo appInfo,
			BufferedWriter appinfoReport) {
		if (AppVetProperties.APKTOOL_HOME == null) {
			appInfo.log
					.warn("APKTOOL_HOME, which is used to launch the apktool, is null.");
		}
		String appvetOS = Native.os;
		String apktoolCommand = AppVetProperties.APKTOOL_HOME + "/";
		if (appvetOS.toUpperCase().indexOf("WIN") > -1) {
			apktoolCommand += APKTOOL_WINDOWS_COMMAND;
		} else if (appvetOS.toUpperCase().indexOf("NUX") > -1) {
			apktoolCommand += APKTOOL_LINUX_COMMAND;
		}
		final String decodeCmd = apktoolCommand + " d "
				+ appInfo.getAppFilePath() + " -o " + appInfo.getProjectPath();
		appInfo.log.debug("Apktool cmd: " + decodeCmd);
		try {
			final StringBuffer reportBuffer = new StringBuffer();
			//final StringBuilder errorBuffer = new StringBuilder();
			
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
					appinfoReport
							.write("\n<font color=\"red\">"
									+ ErrorMessage.FILE_NOT_FOUND
											.getDescription()
									+ " (File removed by system; file may be malware)</font>");
					appInfo.log.error(ErrorMessage.FILE_NOT_FOUND
							.getDescription());
					ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
							appinfoTool.toolId, ToolStatus.ERROR);
				} else {
					appInfo.log.error(reportBuffer.toString());
					appinfoReport.write("\n<font color=\"red\">"
							+ ErrorMessage.ANDROID_APK_DECODE_ERROR
									.getDescription()
							+ " (File may be corrupted)</font>");
					appInfo.log.error(ErrorMessage.ANDROID_APK_DECODE_ERROR
							.getDescription());
					ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
							appinfoTool.toolId, ToolStatus.ERROR);
				}
				return false;
			}
		} catch (final IOException e) {
			appInfo.log.error(e.toString());
			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
					appinfoTool.toolId, ToolStatus.ERROR);
			try {
				appinfoReport.write("\n<font color=\"red\">"
						+ ErrorMessage.ANDROID_APK_DECODE_ERROR
								.getDescription() + "</font>");
			} catch (final IOException e1) {
				appInfo.log.error(e1.toString());
			}
			return false;
		}
	}
	
	private static boolean execute(String command, StringBuffer output) {
		List<String> commandArgs = Arrays.asList(command.split("\\s+"));
		ProcessBuilder pb = new ProcessBuilder(commandArgs);
		Process process = null;
		IOThreadHandler outputHandler = null;
		IOThreadHandler errorHandler = null;
		int exitValue = -1;
		try {
			//System.out.println("Executing " + command);
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
				} else {
					StringBuilder resultError = errorHandler.getOutput();
					output.append(resultError);
				}
				return true;
			} else {
				// Process exceed timeout or was interrupted
				StringBuilder resultOutput = outputHandler.getOutput();
				StringBuilder resultError = errorHandler.getOutput();
				if (resultOutput != null) {
					output.append(resultOutput);
					return false;
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
			if (outputHandler.isAlive()) {
				try {
					outputHandler.inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (errorHandler.isAlive()) {
				try {
					errorHandler.inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process.isAlive()) {
				process.destroy();
			} 
		}

	}

	private static class IOThreadHandler extends Thread {
		private InputStream inputStream;
		private StringBuilder output = new StringBuilder();
		private static final String lineSeparator = 
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
				br.close();
			}
		}

		public StringBuilder getOutput() {
			return output;
		}
	}

	private static boolean getMetaData(AppInfo appInfo,
			BufferedWriter appinfoReport) {
		final String projectPath = appInfo.getProjectPath();
		try {
			File manifestFile = new File(projectPath + "/AndroidManifest.xml");
			appInfo.log.debug("manifestFile: " + projectPath + "/AndroidManifest.xml");
			File stringsFile = new File(projectPath + "/res/values/strings.xml");
			appInfo.log.debug("stringsFilePath: " + projectPath + "/res/values/strings.xml");
			try {
				if (!manifestFile.exists()) {
					appInfo.log.error("Could not locate Android manifest: "
							+ manifestFile);
					appinfoReport
							.write("\n<font color=\"red\">"
									+ ErrorMessage.MISSING_ANDROID_MANIFEST_ERROR
											.getDescription()
									+ " (apktool did not generate manifest file).</font>");
					ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
							appinfoTool.toolId, ToolStatus.ERROR);
					return false;
				} else {
					appInfo.log.debug("Found manifest file at: " + projectPath + "/AndroidManifest.xml");
				}
				if (!stringsFile.exists()) {
					appInfo.log.error("Could not locate Strings file: "
							+ stringsFile);
					appinfoReport
							.write("\n<font color=\"red\">"
									+ ErrorMessage.MISSING_ANDROID_STRINGS_FILE_ERROR
											.getDescription()
									+ " (apktool did not generate strings res file).</font>");
					ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
							appinfoTool.toolId, ToolStatus.ERROR);
					return false;
				} else {
					appInfo.log.debug("Found strings file at: " + projectPath);
				}
				appInfo.log.debug("Found AndroidMetadata.xml file\tOK");
				// Get the XML element values.
				getElementValues(appInfo, manifestFile, stringsFile,
						appinfoReport);
				return true;
			} catch (Exception e) {
				appInfo.log.error(e.toString());
				return false;
			} finally {
				manifestFile = null;
			}
		} catch (final Exception e) {
			appInfo.log.error(e.toString());
			try {
				appinfoReport.write("\n<font color=\"red\">" + appinfoTool.name
						+ " "
						+ ErrorMessage.ANDROID_MANIFEST_ERROR.getDescription()
						+ "</font>");
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						appinfoTool.toolId, ToolStatus.ERROR);
			} catch (final IOException e1) {
				appInfo.log.error(e1.toString());
			}
			return false;
		}
	}

	/**
	 * Get metadata XML elements and their values.
	 */
	private static void getElementValues(AppInfo appInfo,
			File manifestFile, File stringsFile, BufferedWriter appinfoReport) {
		// Get package name.
		final XmlUtil manifestXml = new XmlUtil(manifestFile);
		appInfo.packageName = manifestXml.getXPathValue("/manifest/@package");
		print(appInfo, "Package name", appInfo.packageName, appinfoReport);
		// Set icon.
		final String iconValue = manifestXml
				.getXPathValue("/manifest/application/@icon");
		setIcon(appInfo, iconValue);
		// Get app name.
		final XmlUtil stringsXml = new XmlUtil(stringsFile);
		String appName = stringsXml
				.getXPathValue("/resources/string[@name='app_name']");
		appInfo.appName = appName.trim();
		print(appInfo, "App name", appInfo.appName, appinfoReport);
		// Check if apktool.yml file exists. If so, get metadata info.
		File apkToolYmlFile = new File(appInfo.getProjectPath()
				+ "/apktool.yml");
		if (apkToolYmlFile.exists()) {
			String line;
			try {
				InputStream fis = new FileInputStream(apkToolYmlFile);
				InputStreamReader isr = new InputStreamReader(fis,
						Charset.forName("UTF-8"));
				BufferedReader bufferedReader = new BufferedReader(isr);
				while ((line = bufferedReader.readLine()) != null) {
					// Remove all whitespace from line.
					String trimmed = line.replaceAll("\\s", "");
					String[] name_value = trimmed.split(":");
					if (name_value[0].equals("versionCode")) {
						// Remove single quotes from the value.
						appInfo.versionCode = name_value[1].replaceAll("'", "");
						appInfo.log
								.debug("VersionCode: " + appInfo.versionCode);
						print(appInfo, "Version code", appInfo.versionCode,
								appinfoReport);
					} else if (name_value[0].equals("versionName")) {
						// Remove single quotes from the value.
						appInfo.versionName = name_value[1].replaceAll("'", "");
						appInfo.log
								.debug("VersionName: " + appInfo.versionName);
						print(appInfo, "Version name", appInfo.versionName,
								appinfoReport);
					} else if (name_value[0].equals("minSdkVersion")) {
						// Remove single quotes from the value.
						String minSdkValue = name_value[1].replaceAll("'", "");
						appInfo.log.debug("Min SDK: " + minSdkValue);
						print(appInfo, "Min SDK", minSdkValue, appinfoReport);
					} else if (name_value[0].equals("targetSdkVersion")) {
						// Remove single quotes from the value.
						String targetSdkValue = name_value[1].replaceAll("'",
								"");
						appInfo.log.debug("Target SDK: " + targetSdkValue);
						print(appInfo, "Target SDK", targetSdkValue,
								appinfoReport);
					}
				}
				// Update metadata in DB.
				Database.updateAppMetadata(appInfo.appId, appInfo.appName,
						appInfo.packageName, appInfo.versionCode,
						appInfo.versionName);
				bufferedReader.close();
			} catch (FileNotFoundException e) {
				appInfo.log.error(e.toString());
			} catch (IOException e) {
				appInfo.log.error(e.toString());
			}
		} else {
			appInfo.log.error("Could not find file: "
					+ apkToolYmlFile.getAbsolutePath());
		}
	}

	private static void setIcon(AppInfo appInfo, String iconValue) {
		File iconFile = null;
		String defaultIcon = "android-icon-gray.png";
		if ((iconValue == null) || iconValue.isEmpty()) {
			appInfo.log.info("Could not locate app icon. Using default icon.");
			iconFile = new File("images/" + defaultIcon);
		} else {
			// Icon value will have the syntax '@'<directoryName>'/'<iconName>
			appInfo.log.info("Found icon: " + iconValue);
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
					iconFile = new File("images/" + defaultIcon);
				} else {
					appInfo.log.debug("Found icon at: "
							+ iconFile.getAbsolutePath());
				}
			}
		}
		// Save icon files in $CATALINA_HOME/webapps/appvet_images so that they
		// can be referenced quickly by URL
		File destFile = new File(AppVetProperties.APP_IMAGES + "/"
				+ appInfo.appId + ".png");
		if (iconFile != null && destFile != null) {
			FileUtil.copyFile(iconFile, destFile);
		}
	}

	private static File getIcon(String resFolderPath,
			String iconDirectoryName, String iconName) {
		File resDirectory = new File(resFolderPath);
		File[] resFiles = resDirectory.listFiles();
		File[] iconDirectoryFiles = null;
		try {
			for (final File resFile : resFiles) {
				if (resFile.isDirectory()) {
					// Icon could be in one of several directories containing
					// <iconDirectoryName>
					if (resFile.getName().contains(iconDirectoryName)) {
						iconDirectoryFiles = resFile.listFiles();
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

	private static boolean print(AppInfo appInfo,
			String parameter, String value, BufferedWriter appinfoReport) {
		if ((value == null) || value.isEmpty()) {
			appInfo.log.warn(parameter
					+ " not found in AndroidMetadata manifest");
			return true;
		} else {
			appInfo.log.info(parameter + ": \t" + value);
			try {
				appinfoReport.write(parameter + ": \t" + value + "\n");
			} catch (final IOException e) {
				appInfo.log.error(e.toString());
			}
			return false;
		}
	}

	private AndroidMetadata() {
	}
}
