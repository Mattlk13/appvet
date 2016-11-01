package gov.nist.appvet.servlet.preprocessor;

import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.backend.AppInfo;
import gov.nist.appvet.shared.backend.Database;
import gov.nist.appvet.shared.backend.FileUtil;
import gov.nist.appvet.shared.backend.Logger;
import gov.nist.appvet.shared.backend.ToolAdapter;
import gov.nist.appvet.shared.backend.ToolStatus;
import gov.nist.appvet.shared.backend.ToolStatusManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.kylinworks.PNGConverter;

/**
 * @author steveq@nist.gov
 */
public class IOSMetadata {

	private final DeviceOS OS = DeviceOS.IOS;
	/** Both iOS and Android MUST use the metadata tool ID 'appinfo' */
	private final String METADATA_TOOL_ID = "appinfo";
	private ToolAdapter appinfoTool = null;
	
	public IOSMetadata() {}

	public boolean getMetadata(AppInfo appInfo) {
		appinfoTool = ToolAdapter.getByToolId(OS, METADATA_TOOL_ID);
		if (appinfoTool == null) {
			appInfo.log.error("iOS tool adapter 'appinfo' was not found. "
					+ "Cannot get app metadata.");
			return false;
		}

		// Set status for metadata
		ToolStatusManager.setToolStatus(appInfo,
				appinfoTool.toolId, ToolStatus.SUBMITTED);

		// Copy IPA file to ZIP file so we can expand it
		final String ipaFilePath = appInfo.getIdPath() + "/" + appInfo.getAppFileName();
		String zipFilePath = appInfo.getIdPath() + "/" + appInfo.appName + ".zip";

		if (!copyFile(ipaFilePath, zipFilePath)) {
			// Write error report
			writeReport(appInfo, "\n<font color=\"red\">"
					+ "ERROR: Could not copy IPA file to zip file. IPA file may be corrupted."
					+ "</font>");
			// Update metadata in DB
			updateDbMetadata(appInfo);
			ToolStatusManager.setToolStatus(appInfo,
					appinfoTool.toolId, ToolStatus.ERROR);
			return false;
		}

		String extractedZipPath = appInfo.getIdPath() + "/"
				+ appInfo.appName;
		// Decode/extract app file
		try {
			ZipFile zipFile = new ZipFile(zipFilePath);
			zipFile.extractAll(extractedZipPath);
		} catch (ZipException e) {
			appInfo.log.error(e.toString());
			// Write error report
			writeReport(appInfo, "\n<font color=\"red\">"
					+ "ERROR: Could not extract IPA zip file. IPA file may be corrupted."
					+ "</font>");
			// Update metadata in DB
			updateDbMetadata(appInfo);
			ToolStatusManager.setToolStatus(appInfo,
					appinfoTool.toolId, ToolStatus.ERROR);
			return false;
		}

		// Find icon
		File destDir = new File(extractedZipPath);
		getIcon(destDir, appInfo);

		// Extract metadata from decoded app
		searchForPlist(destDir, appInfo);

		// Update metadata in DB
		updateDbMetadata(appInfo);
		writeReport(appInfo, null);
		// Set metadata processing to LOW.
		ToolStatusManager.setToolStatus(appInfo,
				appinfoTool.toolId, ToolStatus.LOW);
		appInfo.log.debug("End iOS metadata preprocessing for app "
				+ appInfo.appId);
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
		} else {
			appInfo.log.debug("Got here instaed");
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
			bufferedWriter.write("<title>iOS Metadata Report</title>\n");
			bufferedWriter.write("</head>\n");
			bufferedWriter.write("<body>\n");
			String appVetImagesUrl = AppVetProperties.APPVET_URL
					+ "/images/appvet_logo.png";
			bufferedWriter.write("<img border=\"0\" width=\"192px\" src=\""
					+ appVetImagesUrl + "\" alt=\"AppVet Mobile App Vetting System\" />");
			bufferedWriter.write("<HR>\n");
			bufferedWriter.write("<h3>iOS Metadata Report"
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
			bufferedWriter = null;
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

	public boolean copyFile(String sourceFilePath, String destFilePath) {
		if (sourceFilePath == null || destFilePath == null) {
			return false;
		}
		File sourceFile = new File(sourceFilePath);
		if (!sourceFile.exists()) {
			System.err.println("File " + sourceFilePath + " does not exist");
			return false;
		}
		File destFile = new File(destFilePath);
		try {
			Files.copy(sourceFile.toPath(), destFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (final IOException e) {
			System.err.println(e.toString());
			return false;
		} finally {
			sourceFile = null;
			destFile = null;
		}
	}

	public void getIcon(File dir, AppInfo appInfo) {
		try {
			File[] files = dir.listFiles();
			if (files == null) {
				return;
			}
			for (File file : files) {
				if (file.isDirectory()) {
					getIcon(file, appInfo);
				} else {
					// Check if icon was already written
					String iconPath = AppVetProperties.APP_IMAGES_PATH + "/" + appInfo.appId + ".png";
					File iconFile = new File(iconPath);
					if (!iconFile.exists()) {
						// File is not a directory
						String fileName = file.getName();
						String lowercaseFileName = fileName.toLowerCase();
						if (lowercaseFileName.indexOf("itunesartwork") > -1) {
							writeIconFile(appInfo, file);
						} else if (lowercaseFileName.indexOf("icon") > -1
								&& lowercaseFileName.endsWith(".png")) {
							writeIconFile(appInfo, file);
						}
					}
				}
			}
		} catch (Exception e) {
			appInfo.log.warn("Could not retrieve icon file. Using default iOS icon");
		}
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
		// Convert destination to PNG file from Apple optimized to PNG
		new PNGConverter(destFile);
	}

	public void searchForPlist(File dir, AppInfo appInfo) {
		try {
			File[] files = dir.listFiles();
			if (files == null) {
				return;
			}
			for (File file : files) {
				// Check if we have everything
				if (appInfo.appName != null
						&& !appInfo.appName.equals("Received")
						&& appInfo.versionName != null
						&& appInfo.packageName != null) {
					return;
				}
				if (file.isDirectory()) {
					searchForPlist(file, appInfo);
				} else {
					// File is not a directory
					String fileName = file.getName();
					String lowercaseFileName = fileName.toLowerCase();
					if (lowercaseFileName.indexOf("plist") > -1) {
						getPlistInfo(file.getPath(), appInfo);
					}
				}
			}
		} catch (Exception e) {
			appInfo.log.warn("Error processing plist.");
		}
	}

	private void getPlistInfo(String destPlistPath, AppInfo appInfo) {
		try {
			NSObject x = PropertyListParser.parse(new File(destPlistPath));
			NSDictionary d = (NSDictionary) x;
			String[] keys = d.allKeys();

			if (keys.length <= 0) {
				appInfo.log.warn("NO KEYS FOUND in " + destPlistPath);
				return;
			}

			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				if (appInfo.appName == null
						|| appInfo.appName.equals("Received")) {
					if (isAppNameKey(key)) {
						NSObject value = d.get(key);
						if (containsValidChars(value.toString())) {
							appInfo.appName = value.toString();
						}
					}
				}
				if (appInfo.versionName == null) {
					if (isAppVersionKey(key)) {
						NSObject value = d.get(key);
						if (containsValidChars(value.toString())) {
							appInfo.versionName = value.toString();
						}
					}
				}
				if (appInfo.packageName == null) {
					if (isAppPackageKey(key)) {
						NSObject value = d.get(key);
						if (containsValidChars(value.toString())) {
							appInfo.packageName = value.toString();
						}
					}
				}
			}
		} catch (Exception e) {
			appInfo.log.error("Error processing plist file. IPA file may be corrupt.");
		}
	}

	public boolean containsValidChars(String value) {
		if (value.contains("$") || value.contains("{") || value.contains(":")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isAppNameKey(String key) {
		if (key.equals("bundleDisplayName") /* iTunes plist item */
				|| key.equals("itemName") /* iTunes plist item */
				|| key.equals("playlistName") /* iTunes plist item */
				|| key.equals("CFBundleDisplayName")
				|| key.equals("CFBundleName")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAppPackageKey(String key) {
		if (key.equals("softwareVersionBundleId") /* iTunes plist item */
				|| key.equals("CFBundleIdentifier")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAppVersionKey(String key) {
		if (key.equals("bundleShortVersionString") /* iTunes plist item */
				|| key.equals("bundleVersion") /* iTunes plist item */
				|| key.equals("CFBundleVersion")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAppIconKey(String key) {
		if (key.equals("CFBundlePrimaryIcon") /* iTunes plist item */
				|| key.equals("CFBundleIconFile")
				|| key.equals("CFBundleIcons")) /* iTunes plist item */
		{
			return true;
		} else {
			return false;
		}
	}

}
