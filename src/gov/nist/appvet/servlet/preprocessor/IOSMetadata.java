package gov.nist.appvet.servlet.preprocessor;

import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.backend.AppInfo;
import gov.nist.appvet.shared.backend.AppVetProperties;
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

	private static final Logger log = AppVetProperties.log;
	private static final DeviceOS OS = DeviceOS.IOS;
	/** Both iOS and Android MUST use the metadata tool ID 'appinfo' */
	private static final String METADATA_TOOL_ID = "appinfo";
	private static ToolAdapter appinfoTool = null;

	public static boolean getFromFile(AppInfo appInfo) {

		appinfoTool = ToolAdapter.getByToolId(OS, METADATA_TOOL_ID);
		if (appinfoTool == null) {
			appInfo.log.error("iOS tool adapter 'appinfo' was not found. "
					+ "Cannot get app metadata.");
			return false;
		}
		appInfo.log.debug("Acquiring iOS metadata for app " + appInfo.appId);

		final String reportsPath = appInfo.getReportsPath();
		if (reportsPath == null) {
			appInfo.log.error("Reports path is null for app " + appInfo.appId);
			return false;
		}

		if (appinfoTool.reportName == null) {
			appInfo.log.error("App info tool report name is null.");
			return false;
		}

		final String appinfoReportPath = reportsPath + "/"
				+ appinfoTool.reportName;

		try {
			final String ipaFilePath = appInfo.getIdPath() + "/"
					+ appInfo.getAppFileName();
			File ipaFile = new File(ipaFilePath);

			if (ipaFile.exists()) {
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						appinfoTool.toolId, ToolStatus.SUBMITTED);
			} else {
				appInfo.log.error("IPA file for app " + appInfo.appId
						+ " does not exist.");
				return false;
			}

			// Copy IPA file to ZIP file
			String zipFilePath = appInfo.getIdPath() + "/" + appInfo.appName
					+ ".zip";
			File zipFileDest = new File(zipFilePath);
			copyFile(ipaFilePath, zipFilePath);
			appInfo.log.debug("Copied " + ipaFile.getName() + " to "
					+ zipFileDest.getName());
			String extractedZipPath = appInfo.getIdPath() + "/"
					+ appInfo.appName;

			// Unzip file
			try {
				ZipFile zipFile = new ZipFile(zipFilePath);
				zipFile.extractAll(extractedZipPath);
			} catch (ZipException e) {
				e.printStackTrace();
				writeErrorReport(appInfo, appinfoReportPath, e.getMessage());
				return false;
			}

			// Find icon
			File destDir = new File(extractedZipPath);
			getIcon(destDir, appInfo);

			// Get metadata
			searchForPlist(destDir, appInfo);

			// Test
			appInfo.log.debug("appInfo.appName: " + appInfo.appName);
			appInfo.log.debug("appInfo.versionName: " + appInfo.versionName);
			appInfo.log.debug("appInfo.packageName: " + appInfo.packageName);

			// Get icon
			if (appInfo.iconSrcPath == null) {
				appInfo.iconSrcPath = AppVetProperties.URL + "/images/apple-icon-gray.png";
			}

			String iconDestPath = AppVetProperties.APP_IMAGES + "/"
					+ appInfo.appId + ".png";
			FileUtil.copyFile(appInfo.iconSrcPath, iconDestPath);
			// Creates the PNG file from Apple optimized to PNG
			new PNGConverter(new File(iconDestPath));

			appInfo.log.debug("Copied " + appInfo.iconSrcPath + " to "
					+ AppVetProperties.APP_IMAGES + "/" + appInfo.appId
					+ ".png");

			BufferedWriter appinfoReport = new BufferedWriter(new FileWriter(
					appinfoReportPath));
			appinfoReport.write("<HTML>\n");
			appinfoReport.write("<head>\n");
			appinfoReport.write("<style type=\"text/css\">\n");
			appinfoReport.write("h3 {font-family:arial;}\n");
			appinfoReport.write("p {font-family:arial;}\n");
			appinfoReport.write("</style>\n");
			appinfoReport.write("<title>iOS Manifest Report</title>\n");
			appinfoReport.write("</head>\n");
			appinfoReport.write("<body>\n");
			String appVetImagesUrl = AppVetProperties.URL
					+ "/images/appvet_logo.png";
			appinfoReport.write("<img border=\"0\" width=\"192px\" src=\""
					+ appVetImagesUrl
					+ "\" alt=\"AppVet Mobile App Vetting System\" />");
			appinfoReport.write("<HR>\n");
			appinfoReport
					.write("<h3>iOS Metadata Pre-Processing Report</h3>\n");
			appinfoReport.write("<pre>\n");

			final Date date = new Date();
			final SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd' 'HH:mm:ss.SSSZ");
			final String currentDate = format.format(date);

			appinfoReport.write("File: \t\t" + appInfo.getAppFileName() + "\n");
			appinfoReport.write("Date: \t\t" + currentDate + "\n\n");
			appinfoReport.write("App Name: \t\t" + appInfo.appName + "\n");
			appinfoReport.write("Bundle ID: \t\t" + appInfo.packageName + "\n");
			appinfoReport.write("Bundle Version: \t" + appInfo.versionName
					+ "\n");
			appinfoReport.write("App ID: \t" + appInfo.appId + "\n");

			// TODO: This shouldnt be null at this point
//			final String fileNameUpperCase = appInfo.getAppFileName()
//					.toUpperCase();
//			if (fileNameUpperCase == null) {
//				appInfo.log.error("Filename upper case is null.");
//				return false;
//			}
			
			// Update status
			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
					appinfoTool.toolId, ToolStatus.SUBMITTED);

//			// TODO: Remove this - this should have been caught before here.
//			if (fileNameUpperCase.endsWith(".IPA")) {
//				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
//						appinfoTool.toolId, ToolStatus.SUBMITTED);
//			} else {
//				appInfo.log.error("App " + appInfo.appId
//						+ " is not an .IPA file.");
//				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
//						appinfoTool.toolId, ToolStatus.ERROR);
//				return false;
//			}

			// Update metadata.
			if (Database.updateAppMetadata(appInfo.appId, appInfo.appName,
					appInfo.packageName, appInfo.versionCode,
					appInfo.versionName)) {
				appInfo.log.debug("Updated iOS metadata for app "
						+ appInfo.appId);
			} 
//			else {
//				appInfo.log.error("Could not update iOS metadata for app "
//						+ appInfo.appId);
//				return false;
//			}

			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
					appinfoTool.toolId, ToolStatus.LOW);

			appinfoReport
					.write("\nStatus:\t\t<font color=\"black\">COMPLETED</font>\n");
			appinfoReport.close();
			
			appInfo.log.debug("End iOS metadata preprocessing for app "
					+ appInfo.appId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			writeErrorReport(appInfo, appinfoReportPath, e.getMessage());
			return false;
		}

	}

	public static void writeErrorReport(AppInfo appInfo,
			String appinfoReportPath, String errorMessage) {
		BufferedWriter appinfoReport;
		
		// Set default icon now since app icon has not yet been set
		String iconSrcPath = AppVetProperties.CATALINA_BASE + "/webapps/appvet/images/apple-icon-gray.png";
		String iconDestPath = AppVetProperties.APP_IMAGES + "/"
				+ appInfo.appId + ".png";
		if (FileUtil.copyFile(iconSrcPath, iconDestPath))
			log.debug("Copied file from " + iconSrcPath + " to " + iconDestPath);
		else
			log.error("Could not copy file from " + iconSrcPath + " to " + iconDestPath);
		
		try {
			appinfoReport = new BufferedWriter(
					new FileWriter(appinfoReportPath));
			appinfoReport.write("<HTML>\n");
			appinfoReport.write("<head>\n");
			appinfoReport.write("<style type=\"text/css\">\n");
			appinfoReport.write("h3 {font-family:arial;}\n");
			appinfoReport.write("p {font-family:arial;}\n");
			appinfoReport.write("</style>\n");
			appinfoReport.write("<title>iOS Metadata Report</title>\n");
			appinfoReport.write("</head>\n");
			appinfoReport.write("<body>\n");
			String appVetImagesUrl = AppVetProperties.URL
					+ "/images/appvet_logo.png";
			appinfoReport.write("<img border=\"0\" width=\"192px\" src=\""
					+ appVetImagesUrl
					+ "\" alt=\"AppVet Mobile App Vetting System\" />");
			appinfoReport.write("<HR>\n");
			appinfoReport
					.write("<h3>iOS Metadata Pre-Processing Report</h3>\n");
			appinfoReport.write("<pre>\n");

			final Date date = new Date();
			final SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd' 'HH:mm:ss.SSSZ");
			final String currentDate = format.format(date);

			if (appInfo.getAppFileName() != null) {
				appinfoReport.write("File: \t\t" + appInfo.getAppFileName()
						+ "\n");
			} else {
				appinfoReport.write("File: \t\tUnknown\n");
			}
			appinfoReport.write("Date: \t\t" + currentDate + "\n\n");

			if (appInfo.appName == null || appInfo.appName.isEmpty()) {
				appinfoReport.write("App Name: \t\t" + appInfo.appName + "\n");
			} else {
				appInfo.appName = "Unknown";
				appinfoReport.write("App Name: \t\tUnknown\n");
			}

			if (appInfo.packageName == null || appInfo.packageName.isEmpty()) {
				appInfo.packageName = "Unknown";
				appinfoReport.write("Bundle ID: \t\tUnknown\n");			
			} else {
				appinfoReport.write("Bundle ID: \t\t" + appInfo.packageName
						+ "\n");
			}

			if (appInfo.versionName == null || appInfo.versionName.isEmpty()) {
				appInfo.versionName = "Unknown";
				appinfoReport.write("Bundle Version: \tUnknown\n");
			} else {
				appinfoReport.write("Bundle Version: \t" + appInfo.versionName
						+ "\n");
			}
			
			if (appInfo.versionCode == null || appInfo.versionCode.isEmpty()) {
				appInfo.versionCode = appInfo.versionName;
			}
			

			appinfoReport.write("App ID: \t" + appInfo.appId + "\n");

			// Update metadata.
			if (Database.updateAppMetadata(appInfo.appId, appInfo.appName,
					appInfo.packageName, appInfo.versionCode,
					appInfo.versionName)) {
				appInfo.log.debug("Updated iOS metadata for app "
						+ appInfo.appId);
			}
			


			// Set tool status to ERROR
			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
					appinfoTool.toolId, ToolStatus.ERROR);

			appinfoReport
					.write("\nStatus:\t\t<font color=\"red\">ERROR</font>\n");
			
			appinfoReport
				.write("\nDescription:\t\t" + errorMessage + "\n");
			
			appinfoReport.close();
			
			appInfo.log.debug("End iOS metadata preprocessing for app "
					+ appInfo.appId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
					appinfoTool.toolId, ToolStatus.ERROR);
		}

	}

	public static boolean copyFile(String sourceFilePath, String destFilePath) {
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

	public static void getIcon(File dir, AppInfo appInfo) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					// appInfo.log.debug("directory:" +
					// file.getCanonicalPath());
					getIcon(file, appInfo);
				} else {
					// Check if we already have icon
					if (appInfo.iconSrcPath == null) {
						// File is not a directory
						String fileName = file.getName();
						String lowercaseFileName = fileName.toLowerCase();
						if (lowercaseFileName.indexOf("itunesartwork") > -1) {
							System.out.println("Found iTunesArtwork: "
									+ fileName);
							appInfo.iconSrcPath = file.getAbsolutePath();
						} else if (lowercaseFileName.indexOf("icon") > -1
								&& lowercaseFileName.endsWith(".png")) {
							System.out.println("Found png file: " + fileName);
							appInfo.iconSrcPath = file.getAbsolutePath();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void searchForPlist(File dir, AppInfo appInfo) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				// Check if we have everything
				if (appInfo.appName != null
						&& !appInfo.appName.equals("Received")
						&& appInfo.versionName != null
						&& appInfo.packageName != null) {
					return;
				}

				if (file.isDirectory()) {
					// appInfo.log.debug("directory:" +
					// file.getCanonicalPath());
					searchForPlist(file, appInfo);
				} else {
					// File is not a directory
					String fileName = file.getName();
					String lowercaseFileName = fileName.toLowerCase();
					if (lowercaseFileName.indexOf("plist") > -1) {
						System.out.println("Found plist");
						getPlistInfo(file.getPath(), appInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getPlistInfo(String destPlistPath, AppInfo appInfo) {
		System.out.println("Found plist: " + destPlistPath);
		try {
			NSObject x = PropertyListParser.parse(new File(destPlistPath));
			NSDictionary d = (NSDictionary) x;
			String[] keys = d.allKeys();

			if (keys.length <= 0) {
				System.out.println("NO KEYS FOUND in " + destPlistPath);
				return;
			}

			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				// appInfo.log.debug("***** KEY " + key + " = " +
				// value.toString());
				if (appInfo.appName == null
						|| appInfo.appName.equals("Received")) {
					if (isAppNameKey(key)) {
						NSObject value = d.get(key);
						if (containsValidChars(value.toString())) {
							appInfo.appName = value.toString();
							System.out.println("Set appName to: "
									+ appInfo.appName);
						}
					}
				}

				if (appInfo.versionName == null) {
					if (isAppVersionKey(key)) {
						NSObject value = d.get(key);
						if (containsValidChars(value.toString())) {
							appInfo.versionName = value.toString();
							System.out.println("Set appVersion to: "
									+ appInfo.versionName);
						}
					}
				}

				if (appInfo.packageName == null) {
					if (isAppPackageKey(key)) {
						NSObject value = d.get(key);
						if (containsValidChars(value.toString())) {
							appInfo.packageName = value.toString();
							System.out.println("Set packageName to: "
									+ appInfo.packageName);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean containsValidChars(String value) {
		if (value.contains("$") || value.contains("{") || value.contains(":")) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isAppNameKey(String key) {
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

	public static boolean isAppPackageKey(String key) {
		if (key.equals("softwareVersionBundleId") /* iTunes plist item */
				|| key.equals("CFBundleIdentifier")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isAppVersionKey(String key) {
		if (key.equals("bundleShortVersionString") /* iTunes plist item */
				|| key.equals("bundleVersion") /* iTunes plist item */
				|| key.equals("CFBundleVersion")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isAppIconKey(String key) {
		if (key.equals("CFBundlePrimaryIcon") /* iTunes plist item */
				|| key.equals("CFBundleIconFile")
				|| key.equals("CFBundleIcons")) /* iTunes plist item */
		{
			return true;
		} else {
			return false;
		}
	}

	private IOSMetadata() {

	}

}
