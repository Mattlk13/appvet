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
package gov.nist.appvet.tools.preprocessor;

import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.shared.Database;
import gov.nist.appvet.shared.FileUtil;
import gov.nist.appvet.shared.Logger;
import gov.nist.appvet.shared.app.AppInfo;
import gov.nist.appvet.shared.os.DeviceOS;
import gov.nist.appvet.shared.status.ToolStatus;
import gov.nist.appvet.shared.status.ToolStatusManager;
import gov.nist.appvet.toolmgr.ToolAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

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
			log.error("iOS tool adapter 'appinfo' was not found. Cannot get app metadata.");
			return false;
		}
		
		log.debug("Acquiring iOS metadata for app " + appInfo.appId);
		final String reportsPath = appInfo.getReportsPath();
		if (reportsPath == null) {
			log.error("Reports path is null for app " + appInfo.appId);
			return false;
		}
	
		if (appinfoTool.reportName == null) {
			log.error("App info tool report name is null.");
			return false;
		}
		
		final String appinfoReportPath = reportsPath + "/"
				+ appinfoTool.reportName;

		BufferedWriter appinfoReport = null;

		NSObject playListName = null;
		NSObject softwareVersionBundleId = null;
		NSObject bundleVersion = null;
		NSObject releaseDate = null;

		try {
			boolean iconFound = false;
			final String ipaFilePath = appInfo.getIdPath() + "/"
					+ appInfo.getAppFileName();
			File ipaFile = new File(ipaFilePath);
			if (ipaFile.exists()) {
				if (ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						appinfoTool.toolId, ToolStatus.SUBMITTED)) {
					log.debug("Set " + appinfoTool.toolId + " status for app " + appInfo.appId);
				} else {
					log.error("Could not set " + appinfoTool.toolId + " status for app " + appInfo.appId);
					return false;
				}
			} else {
				log.error("IPA file for app " + appInfo.appId + " does not exist.");
				return false;
			}
			String zipFilePath = appInfo.getIdPath() + "/" + appInfo.appName
					+ ".zip";
			File destFile = new File(zipFilePath);
			FileUtil.copyFile(ipaFile, destFile);

			String destIconPath = AppVetProperties.APP_IMAGES + "/"
					+ appInfo.appId + ".png";
			String destPlistPath = appInfo.getIdPath() + "/"
					+ appInfo.getAppFileName() + ".plist";
			ZipFile zipFile = new ZipFile(destFile);
			Enumeration<? extends ZipEntry> iosFileEntries = zipFile.entries();
			
			try {
				log.debug("Examining IPA file entries for app " + appInfo.appId);
				// Check if there is an iTunesArtwork file. If there isn't,
				// we will need to scan below for a png file.
				while (iosFileEntries.hasMoreElements()) {
					ZipEntry entry = iosFileEntries.nextElement();
					if (entry.getName().indexOf("iTunesArtwork") > -1) {
						log.debug("Found IPA artwork for app " + appInfo.appId);
						iconFound = true;
						InputStream inputStream = zipFile.getInputStream(entry);
						int numBytesRead;
						byte[] byteArray = new byte[1024];
						FileOutputStream fos = new FileOutputStream(
								destIconPath);
						while ((numBytesRead = inputStream.read(byteArray, 0,
								byteArray.length)) != -1) {
							fos.write(byteArray, 0, numBytesRead);
						}
						inputStream.close();
						fos.close();
					} else if (entry.getName().indexOf(".plist") > -1) {
						log.debug("Found plist for app " + appInfo.appId);

						// Extract the PLIST file from the zip file
						InputStream inputStream = zipFile.getInputStream(entry);
						int numBytesRead;
						byte[] byteArray = new byte[1024];
						FileOutputStream fos = new FileOutputStream(
								destPlistPath);
						while ((numBytesRead = inputStream.read(byteArray, 0,
								byteArray.length)) != -1) {
							fos.write(byteArray, 0, numBytesRead);
						}
						inputStream.close();
						fos.close();
					}
				}

/*				// Not sure if the following is needed
				if (!iconFound) {
					// Look for the first png file
					while (iosFileEntries.hasMoreElements()) {
						ZipEntry entry = iosFileEntries.nextElement();
						if (entry.getName().toLowerCase().indexOf(".png") > -1) {
							iconFound = true;
							InputStream inputStream = zipFile
									.getInputStream(entry);

							int numBytesRead;
							byte[] byteArray = new byte[1024];
							FileOutputStream fos = new FileOutputStream(
									destIconPath);

							while ((numBytesRead = inputStream.read(byteArray,
									0, byteArray.length)) != -1) {
								fos.write(byteArray, 0, numBytesRead);
							}
							inputStream.close();
							fos.close();
							break;
						}
					}

				}*/

				// Couldn't find icon, so just use default Apple icon
				if (!iconFound) {
					log.debug("Couldnt find icon for app " + appInfo.appId + ". Using default icon.");
					String appIcon = null;
					if (appInfo.os == DeviceOS.ANDROID) {
						appIcon = "default_android_large.png";
					} else if (appInfo.os == DeviceOS.IOS) {
						appIcon = "default_ios_large.png";
					}
					File sourceIcon = new File(AppVetProperties.APP_IMAGES
							+ "/" + appIcon);
					if (!sourceIcon.exists()) {
						log.error("Default IOS or Android icon is missing from Tomcat/webapps/appvet_images. Aborting metadata processing.");
						return false;
					}
					File destIcon = new File(AppVetProperties.APP_IMAGES + "/"
							+ appInfo.appId + ".png");
					log.debug("Copying " + sourceIcon.getAbsolutePath() + " to " + 
							destIcon.getAbsolutePath());
					FileUtil.copyFile(sourceIcon, destIcon);
				}

				zipFile.close();

				// Get PLIST information
				try {
					log.debug("Getting plist info for app " + appInfo.appId);
					NSObject x = PropertyListParser.parse(new File(
							destPlistPath));
					// check the data in it
					NSDictionary d = (NSDictionary) x;
					playListName = d.get("playlistName");
					if (playListName == null) {
						log.warn("iOS playlist name is null. Setting to 'N/A'");
						playListName.wrap("N/A");
					}
					softwareVersionBundleId = d.get("softwareVersionBundleId");
					if (softwareVersionBundleId == null) {
						log.warn("iOS software bundle ID is null. Setting to 'N/A'");
						softwareVersionBundleId.wrap("N/A");
					}
					bundleVersion = d.get("bundleVersion");
					if (bundleVersion == null) {
						log.warn("iOS software version is null. Setting to 'N/A'");
						bundleVersion.wrap("N/A");
					}
					releaseDate = d.get("releaseDate");
					if (releaseDate == null) {
						log.warn("iOS software release date is null. Setting to 'N/A'");
						releaseDate.wrap("N/A");
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Could not get plist info for app " + appInfo.appId);
					return false;
				}

				FileUtil.deleteFile(destPlistPath);

			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			appinfoReport = new BufferedWriter(
					new FileWriter(appinfoReportPath));
			if (appinfoReport == null) {
				log.error("Metadata report for app " + appInfo.appId + " is null. Aborting metadata processing.");
				return false;
			}

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
					+ appVetImagesUrl + "\" alt=\"AppVet Mobile App Vetting System\" />");
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
			if (playListName == null || playListName.toString() == null) {
				log.warn("iOS playlist name is null. Setting app name to 'N/A'");
				appInfo.appName = "N/A";
			} else {
				appInfo.appName = playListName.toString();
			}
			log.debug("Playlist Name: \t\t" + appInfo.appName + "\n");
			appinfoReport.write("Playlist Name: \t\t" + appInfo.appName + "\n");

			if (softwareVersionBundleId == null || softwareVersionBundleId.toString() == null) {
				log.warn("iOS software bundle ID is null. Setting package name to 'N/A'");
				appInfo.packageName = "N/A";
			} else {
				appInfo.packageName = softwareVersionBundleId.toString();
			}
			log.debug("Bundle ID: \t\t" + appInfo.packageName + "\n");
			appinfoReport.write("Bundle ID: \t\t" + appInfo.packageName + "\n");

			if (bundleVersion == null || bundleVersion.toString() == null) {
				log.warn("iOS bundle version is null. Setting version name to 'N/A'");
				appInfo.versionName = "N/A";
			} else {
				appInfo.versionName = bundleVersion.toString();
			}
			log.debug("Bundle Version: \t" + appInfo.versionName
					+ "\n");
			appinfoReport.write("Bundle Version: \t" + appInfo.versionName
					+ "\n");

			if (releaseDate == null || releaseDate.toString() == null) {
				appinfoReport.write("Release Date: \t\tN/A\n");
			} else {
				appinfoReport.write("Release Date: \t\t" + releaseDate.toString()
						+ "\n");
			}

			appinfoReport.write("App ID: \t" + appInfo.appId + "\n");

			final String fileNameUpperCase = appInfo.getAppFileName()
					.toUpperCase();
			if (fileNameUpperCase == null) {
				log.error("Filename upper case is null.");
				return false;
			}
			if (fileNameUpperCase.endsWith(".IPA")) {
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						appinfoTool.toolId, ToolStatus.SUBMITTED);

			} else {
				log.error("App " + appInfo.appId + " is not an .IPA file.");
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						appinfoTool.toolId, ToolStatus.ERROR);
				return false;
			}

			// Update metadata.
			if (Database.updateAppMetadata(appInfo.appId, appInfo.appName,
					appInfo.packageName, appInfo.versionCode,
					appInfo.versionName)) {
				log.debug("Updated iOS metadata for app " + appInfo.appId);
			} else {
				log.error("Could not update iOS metadata for app " + appInfo.appId);
				return false;
			}

			if (ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
					appinfoTool.toolId, ToolStatus.LOW)) {
				log.debug("Updated tool status for app " + appInfo.appId);
			} else {
				log.error("Could not update tool status for app " + appInfo.appId);
				return false;
			}
			appinfoReport
					.write("\nStatus:\t\t<font color=\"green\">LOW</font>\n");
			log.debug("End iOS metadata preprocessing for app "
					+ appInfo.appId);

			return true;

		} catch (final IOException e) {
			log.error(e.toString());
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
				log.error(e.toString());
			}
		}
	}

	private IOSMetadata() {
	}
}
