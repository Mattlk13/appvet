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

import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.servlet.shared.ErrorMessage;
import gov.nist.appvet.servlet.shared.ReportFileType;
import gov.nist.appvet.servlet.toolmgr.AppSubmitType;
import gov.nist.appvet.servlet.toolmgr.SSLWrapper;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.RestrictionType;
import gov.nist.appvet.shared.all.ToolType;
import gov.nist.appvet.shared.all.UserToolCredentials;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * @author steveq@nist.gov
 */
public class ToolAdapter implements Runnable {

	private static final Logger log = AppVetProperties.log;
	// Display Name (e.g., Androwarn (Maaaaz))
	public String name = null; 
	// Tool ID (and tools database table column name)
	public String toolId = null; 
	public ToolType toolType = null;
	
	// public RestrictionType restrictionType = null; // Not used
	public DeviceOS os = null;
	public String vendorName = null;
	public String webSite = null;
	public AppSubmitType appSubmitType = null;
	public boolean authenticationRequired = false;
	public String[] authenticationParams = null;
	public ReportFileType reportFileType = null;
	public String reportName = null;
	public String[] reportLowPhrases = null;
	public String[] reportModeratePhrases = null;
	public String[] reportHighPhrases = null;
	public String[] reportErrorPhrases = null;

	public Protocol protocol = null;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	public Thread thread = null;
	public AppInfo appInfo = null;
	public String reportTemplateURL = null;
	public String iconURL = null;
	public String iconAltText = null;

	public ToolAdapter(File configFile) {

		if (!configFile.exists()) {
			log.error("Test service config file " + configFile.getName()
					+ " does not exist.");
			return;
		}
		XmlUtil xml = new XmlUtil(configFile);
		final String configFileName = configFile.getName();

		// Tool configuration
		name = xml.getXPathValue("/ToolAdapter/Description/Name");
		checkNullString(configFileName, "name", name);

		toolId = xml.getXPathValue("/ToolAdapter/Description/Id");
		checkNullString(configFileName, "id", toolId);

		String toolValue = xml
				.getXPathValue("/ToolAdapter/Description/Category");
		toolType = ToolType.getAnalysisType(toolValue);
		
		//log.debug("Loading tool adapter " + toolId + " '" + name + "' of type: " + toolType.name());


		String osStr = xml.getXPathValue("/ToolAdapter/Description/OS");
		os = DeviceOS.getOS(osStr);
		if (os == null) {
			log.error("Tool OS is null.");
		}

		vendorName = xml.getXPathValue("/ToolAdapter/Description/VendorName");

		webSite = xml.getXPathValue("/ToolAdapter/Description/VendorWebsite");
		
		// String restrictionValue = xml
		//		.getXPathValue("/ToolAdapter/Description/Restriction"); // Not used
		// restrictionType = RestrictionType.getRestrictionType(restrictionValue); // Not used

		// Authorization requirements
		final String booleanStringValue = xml
				.getXPathValue("/ToolAdapter/Description/AuthenticationRequired");
		authenticationRequired = new Boolean(booleanStringValue).booleanValue();
		
		// Report template download URL (optional)
		reportTemplateURL = xml.getXPathValue("/ToolAdapter/Description/ReportTemplateURL"); 
		if (reportTemplateURL != null) {
			log.debug("Report template URL: " + reportTemplateURL);
		} else {
			log.warn("Report template URL for tool '" + toolId + "' is null");
		}
		
		// Report icon (optional
		iconURL = xml.getXPathValue("/ToolAdapter/Description/Icon/URL");
		if (iconURL != null) {
			//log.debug("Icon URL: " + iconURL);
		} else {
			//log.warn("Icon URL for tool '" + toolId + "' is null");
		}
		
		iconAltText = xml.getXPathValue("/ToolAdapter/Description/Icon/AltText");
		if (iconAltText != null) {
			//log.debug("Icon ALT Text: " + iconAltText);
		} else {
			//log.warn("Icon ALT Text for tool '" + toolId + "' is null");
		}
		
		if (authenticationRequired) {
			// Get authorization parameter names
			final String authenticationParamsString = xml
					.getXPathValue("/ToolAdapter/Description/AuthenticationParameters");
			// log.debug("Authentication parameter: " +
			// authenticationParamsString);
			final String authenticationParamsStringNoWhitespace = authenticationParamsString
					.replaceAll("\\s", ""); // remove whitespace
			// log.debug("Authentication no whitespace string: " +
			// authenticationParamsStringNoWhitespace);
			authenticationParams = authenticationParamsStringNoWhitespace
					.split(",");
		} else {
			// log.debug("No authorization required for " + name + " tool.");
		}
		
		final String reportFileTypeString = xml
				.getXPathValue("/ToolAdapter/Description/ReportFile");

		// App submission format type (i.e., binary app file or app name,
		// package, etc. as a string)
		String appSubmitStr = xml
				.getXPathValue("/ToolAdapter/Description/AppSubmit");
		//log.debug("AppSubmitStr: " + appSubmitStr);
		appSubmitType = AppSubmitType.getSubmitType(appSubmitStr);
		//log.debug("appSubmitType: " + appSubmitType);
		// Report configuration
		final String reportTemplateURL = xml
				.getXPathValue("/ToolAdapter/Description/ReportTemplateURL");
		//log.debug("Report file type string: " + reportFileTypeString);
		checkNullString(configFileName, "reportFileTypeString",
				reportFileTypeString);
		reportFileType = ReportFileType.getFileType(reportFileTypeString);
		//log.debug("Report file: " + reportFileType.name());
		reportName = generateReportName();
		//log.debug("Report name: " + reportName);

		// Protocol config
		for (Protocol p : Protocol.values()) {
			if (xml.xpathExists("/ToolAdapter/Protocol/" + p.xmlTag)) {
				protocol = p;
				break;
			}
		}

		String protocolXPath = "/ToolAdapter/Protocol";
		checkNullString(configFileName, "protocolXPath", protocolXPath);
		protocolXPath += "/" + protocol.xmlTag;

		log.debug("Adding Tool Adapter: " + configFileName + ":\n" 
				+ "-Name: " + name + "\n" 
				+ "-ID: " + toolId + "\n"
				+ "-Type: " + toolType + "\n" 
				+ "-Report File Type: " + reportFileType.name() + "\n"
				+ "-Report template URL: " + reportTemplateURL + "\n"
				+ "-OS: " + os.name() + "\n" 
				//+ "-Restriction Type: " + restrictionType + "\n" // Not used
				+ "-Vendor: " + vendorName + "\n" 
				+ "-Website: " + webSite + "\n" 
				+ "-App Submit Type: " + appSubmitType.name() + "\n"
				+ "-AppVet Protocol: " + protocol + "\n",
				false);

		switch (protocol) {
		case SYNCHRONOUS:
			Transaction transaction = new Transaction(protocol, protocolXPath,
					xml, configFileName);
			transactions.add(transaction);
			break;
		case ASYNCHRONOUS:
			transaction = new Transaction(protocol, protocolXPath, xml,
					configFileName);
			transactions.add(transaction);
			break;
		case PUSH:
			break;
		case INTERNAL:
			break;
		}

		xml = null;
	}

	/** This method sets a tool to specific app information. */
	public void setApp(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public static boolean isValidToolId(String toolId) {
		for (int i = 0; i < AppVetProperties.androidTools.size(); i++) {
			final ToolAdapter adapter = AppVetProperties.androidTools.get(i);
			if (adapter.toolId.equals(toolId)) {
				return true;
			}
		}

		for (int i = 0; i < AppVetProperties.iosTools.size(); i++) {
			final ToolAdapter adapter = AppVetProperties.iosTools.get(i);
			if (adapter.toolId.equals(toolId)) {
				return true;
			}
		}

		return false;
	}

	public static ToolAdapter getByToolId(DeviceOS os, String toolId) {
		
		if (os == null) {
			log.error("Cannot get tool adapter for null os.");
			return null;
		}
		if (toolId == null) {
			log.error("Cannot get tool adapter for null tool ID.");
			return null;
		}

		if (os == DeviceOS.ANDROID) {

			for (int i = 0; i < AppVetProperties.androidTools.size(); i++) {
				final ToolAdapter adapter = AppVetProperties.androidTools.get(i);
				if (adapter.toolId.equals(toolId)) {
					//log.debug("Found Android tool adapter for " + toolId);
					return adapter;
				}
			}

			log.error("Could not find Android tool adapter '" + toolId + "'.");
			return null;

		} else if (os == DeviceOS.IOS) {

			for (int i = 0; i < AppVetProperties.iosTools.size(); i++) {
				final ToolAdapter adapter = AppVetProperties.iosTools.get(i);
				if (adapter.toolId.equals(toolId)) {
					//log.debug("Found iOS tool adapter for " + toolId);
					return adapter;
				}
			}

			log.error("Could not find iOS tool adapter '" + toolId + "'.");
			return null;

		} else {
			log.error("Invalid OS: " + os);
			return null;
		}

	}
	
	public static ToolAdapter getByToolName(DeviceOS os, String toolName) {

		if (os == DeviceOS.ANDROID) {

			for (int i = 0; i < AppVetProperties.androidTools.size(); i++) {
				final ToolAdapter adapter = AppVetProperties.androidTools
						.get(i);

				if (adapter.name.equals(toolName)) {
					return adapter;
				}

			}

			log.error("Android tool '" + toolName + "' does not exist!");
			return null;

		} else if (os == DeviceOS.IOS) {

			for (int i = 0; i < AppVetProperties.iosTools.size(); i++) {
				final ToolAdapter adapter = AppVetProperties.iosTools.get(i);

				if (adapter.name.equals(toolName)) {
					return adapter;
				}

			}

			log.error("iOS tool '" + toolName + "' does not exist!");
			return null;

		} else {
			log.error("Invalid OS: " + os);
			return null;
		}

	}

	
	public static String getHtmlReportString(String reportPath, AppInfo appInfo) {
		byte[] encoded = null;
		
		try {
			encoded = Files.readAllBytes(Paths.get(reportPath));
			return Charset.defaultCharset().decode(ByteBuffer.wrap(encoded))
					.toString();
		} catch (final IOException e) {
			appInfo.log.error(e.toString());
			return null;
		} finally {
			encoded = null;
		}
		
	}

	
	public static String getPdfReportString(String reportPath, AppInfo appInfo) {
		File file = new File(reportPath);
		PDDocument pddDocument = null;
		PDFTextStripper textStripper = null;
		
		try {
			pddDocument = PDDocument.load(file);
			textStripper = new PDFTextStripper();
			textStripper.setStartPage(1);
			textStripper.setEndPage(1);
			final String report = textStripper.getText(pddDocument);
			return report;
		} catch (final IOException e) {
			appInfo.log.error(e.toString());
			return null;
		} finally {
			if (pddDocument != null) {
				try {
					pddDocument.close();
					pddDocument = null;
				} catch (IOException e) {
					appInfo.log.error(e.toString());
				}
			}
			textStripper = null;
			file = null;
		}
		
	}

	
	public static String getTextReportString(String reportPath, AppInfo appInfo) {
		byte[] encoded = null;
		
		try {
			encoded = Files.readAllBytes(Paths.get(reportPath));
			return Charset.defaultCharset().decode(ByteBuffer.wrap(encoded))
					.toString();
		} catch (final IOException e) {
			appInfo.log.error(e.toString());
			return null;
		} finally {
			encoded = null;
		}
		
	}

	
	public void checkNullString(String fileName, String parameter, String value) {
		if ((value == null) || value.isEmpty()) {
			log.error("Required parameter '" + parameter + "' in file "
					+ fileName + " is null or empty.");
		}
	}

	
	public void shutdown(AppInfo appInfo, boolean sendMobilizeReport) {
		
		if ((protocol == Protocol.PUSH) || (protocol == Protocol.INTERNAL)) {
			// PUSH adapters should not have a thread to clean up.
			return;
		}
		
		if (thread.isAlive()) {
			appInfo.log.debug("Thread for " + name
					+ " is still alive.  Interrupting...");
			thread.interrupt();
			appInfo.log.error(ErrorMessage.TOOL_TIMEOUT_ERROR.getDescription());
			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId, this.toolId,
					ToolStatus.ERROR);
		}
		
		thread = null;
	}

	/**
	 * This method is used to POST binary files.
	 */
	public MultipartEntity getMultipartEntity(Request request,
			ArrayList<String> authParamNames, ArrayList<String> authParamValues) {
		final MultipartEntity entity = new MultipartEntity();
		File apkFile = null;
		FileBody fileBody = null;
		
		try {
			String fileUploadParamName = null;
			
			// Add parameter name-value pairs
			if (request.formParameterNames != null) {
				
				for (int i = 0; i < request.formParameterNames.size(); i++) {
					final String paramName = request.formParameterNames.get(i);
					String paramValue = request.formParameterValues.get(i);
					
					if (paramValue.equals("APP_FILE")) {
						fileUploadParamName = paramName;
					} else {
						
						if (paramValue.equals("APPVET_ID")) {
							
							appInfo.log.debug("Found " + paramName + " = "
									+ "'APPVET_ID' for tool '" + toolId
									+ "'. Setting to appid = '"
									+ appInfo.appId + "'");
							
							paramValue = appInfo.appId;
							
						} else if (paramValue.equals("APP_PACKAGE")) {
							appInfo.log.debug("Found " + paramName + " = "
									+ "'APP_PACKAGE' for tool '" + toolId
									+ "'. Setting to appid = '"
									+ appInfo.packageName + "'");
							
							paramValue = appInfo.packageName;
						}
						
						if ((paramName == null) || paramName.isEmpty()) {
							appInfo.log.warn("Param name is null or empty "
									+ "for tool '" + name + "'");
						}
						
						StringBody partValue = new StringBody(paramValue,
								Charset.forName("UTF-8"));
						entity.addPart(paramName, partValue);
						partValue = null;
					}
				}
			}

			// Add authentication parameters if they exist
			if (authenticationRequired) {
				
				if (authParamNames != null && !authParamNames.isEmpty()) {
					
					for (int i = 0; i < authParamNames.size(); i++) {
						StringBody partValue = new StringBody(
								authParamValues.get(i),
								Charset.forName("UTF-8"));
						entity.addPart(authParamNames.get(i), partValue);
					}
					
				}

			}

			// If submitting APPFILE, add file. Otherwise, send app name
			if (appSubmitType == AppSubmitType.APP_FILE) {
				final String apkFilePath = appInfo.getIdPath() + "/"
						+ appInfo.getAppFileName();
				appInfo.log.debug("Sending file: " + apkFilePath);
				apkFile = new File(apkFilePath);
				fileBody = new FileBody(apkFile);
				entity.addPart(fileUploadParamName, fileBody);
			} else if (appSubmitType == AppSubmitType.APP_NAME) {
				// TODO Add app name to entity here
			} else {
				// If appSubmitType == AppSubmitType.NONE do nothing
			}

			return entity;
		} catch (final UnsupportedEncodingException e) {
			appInfo.log.error(e.toString());
		}
		return null;
	}

	public String generateReportName() {
		final String reportSuffix = "_security_report";
		switch (reportFileType) {
		case TXT:
			return toolId + reportSuffix + "." + ReportFileType.TXT.value;
		case HTML:
			return toolId + reportSuffix + "." + ReportFileType.HTML.value;
		case PDF:
			return toolId + reportSuffix + "." + ReportFileType.PDF.value;
		case DOCX:
			return toolId + reportSuffix + "." + ReportFileType.DOCX.value;	
		case RTF:
			return toolId + reportSuffix + "." + ReportFileType.RTF.value;
		case XML:
			return toolId + reportSuffix + "." + ReportFileType.XML.value;
		case JSON:
			return toolId + reportSuffix + "." + ReportFileType.JSON.value;
		default:
			return null;
		}
	}

	public Thread getThread() {
		thread = new Thread(this);
		return thread;
	}

	@Override
	public void run() {
		log.debug("Starting tool adapter for " + toolId);

		if ((protocol == Protocol.PUSH) || (protocol == Protocol.INTERNAL)) {
			// PUSH/INTERNAL adapters do not send requests to a service.
			return;
		}
		if (appInfo == null) {
			log.error("AppInfo object is null in tool adapter. "
					+ "Aborting processing app.");
			return;
		}

		// Store param names and values
		ArrayList<String> authParamNames = new ArrayList<String>();
		ArrayList<String> authParamValues = new ArrayList<String>();

		// Add authentication parameters if they exist
		if (authenticationRequired) {
			log.debug("Tool " + toolId + ": Checking tool credentials for "
					+ appInfo.ownerName);
			// Get user's tool credentials
			ArrayList<UserToolCredentials> userToolCredentials = 
					Database.getUserToolCredentials(appInfo.ownerName);
			
			for (int i = 0; i < userToolCredentials.size(); i++) {
				UserToolCredentials userToolCredential = userToolCredentials
						.get(i);
				
				if (userToolCredential.toolId.equals(toolId)
						&& userToolCredential.os.equals(os.name())) {

					for (int j = 0; j < userToolCredential.authParamNames.length; j++) {
						
						String paramName = userToolCredential.authParamNames[j];
						String paramValue = userToolCredential.authParamValues[j];

						if (paramValue == null || paramValue.isEmpty()
								|| paramValue.equals("null")) {
							appInfo.log.warn("Tool authentication parameter value "
									+ "cannot be null. Setting tool to NA");
							ToolStatusManager.setToolStatus(appInfo.os,
									appInfo.appId, this.toolId, ToolStatus.NA);
							return;
						} else {
							appInfo.log.debug("Adding " + appInfo.ownerName
									+ " tool credentials for " + toolId);
							authParamNames.add(paramName);
							authParamValues.add(paramValue);
						}
						
					}
				}
				
			}

		}

		for (int i = 0; i < transactions.size(); i++) {
			Transaction transaction = transactions.get(i);
			Request appVetRequest = transaction.request;
			Response toolResponse = transaction.response;

			File fileOut = null;
			FileOutputStream fileOutputStream = null;
			InputStream inputStream = null;
			try {
				Date startDate = new Date();
				final long startTime = startDate.getTime();
				startDate = null;
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						this.toolId, ToolStatus.SUBMITTED);
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						AppVetProperties.CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParameters,
						AppVetProperties.SO_TIMEOUT);
				HttpClient httpclient = new DefaultHttpClient(httpParameters);
				httpclient = SSLWrapper.wrapClient(httpclient);
				MultipartEntity entity = getMultipartEntity(appVetRequest,
						authParamNames, authParamValues);
				if (entity == null) {
					appInfo.log.error("MultipartEntity is null. Aborting "
							+ name);
					ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
							this.toolId, ToolStatus.ERROR);
					return;
				}
				
				String targetURL = appVetRequest.URL;
				
				/* Need special case for RRF tool that appends appID to URL path */
				if (toolId.equals("androidrrf") || toolId.equals("rrf")) {
					targetURL += "/" + appInfo.appId;
					appInfo.log.info("RRF tool using modified target URL: " + targetURL);
				}
				
				HttpPost httpPost = new HttpPost(targetURL);

				
				httpPost.setEntity(entity);
				appInfo.log.info(name + " adapter sending app " + appInfo.appId
						+ " to " + targetURL);

				// Send the app to the tool
				final HttpResponse httpResponse = httpclient.execute(httpPost);
				httpPost = null;
				appInfo.log.info(name + " adapter received: "
						+ httpResponse.getStatusLine());

				if (protocol == Protocol.SYNCHRONOUS
				// || protocol == Protocol.MULTISYNCHRONOUS
				) {
					// We only handle report from Synchronous tools here.
					// Reports for
					// asynchronous tools are handled by the AppVetServlet. Also
					// note that only ASCII content is received from
					// a tool, not an attached file. If the content ASCII
					// content represents binary content, the content must
					// written to a binary file (e.g., PDF file).
					final HttpEntity responseEntity = httpResponse.getEntity();
					inputStream = responseEntity.getContent();
					final String reportPath = appInfo.getReportsPath() + "/"
							+ generateReportName();
					fileOut = new File(reportPath);
					fileOutputStream = new FileOutputStream(
							fileOut, false);
					int c;
					while ((c = inputStream.read()) != -1) {
						fileOutputStream.write(c);
					}
					fileOutputStream.flush();
					inputStream.close();
					fileOutputStream.close();


					String appvetRiskHeaderName = toolResponse.appVetRiskHeaderName;
					log.debug("appvetRiskHeaderName: " + appvetRiskHeaderName);
					String toolResult = httpResponse.getFirstHeader(
							appvetRiskHeaderName).getValue();
					appInfo.log.debug("Received tool result: " + toolResult
							+ " from " + this.toolId);

					ToolStatus toolStatus = ToolStatus.getStatus(toolResult);

					if (toolStatus == null) {
						appInfo.log.error("Tool status is null!");
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.toolId, ToolStatus.ERROR);
						return;
					} else {
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.toolId, toolStatus);
					}

				} else if (protocol == Protocol.ASYNCHRONOUS) {

					final String httpResponseVal = httpResponse.getStatusLine()
							.toString();
					if ((httpResponseVal.indexOf("HTTP/1.1 202 Accepted") > -1)
							|| (httpResponseVal.indexOf("HTTP/1.1 200 OK") > -1)) {
						// Received 200 OK
					} else if (httpResponseVal.indexOf("HTTP/1.1 404 Not Found") > -1) {
						appInfo.log.error("Received from " + toolId + ": "
								+ httpResponseVal
								+ ". Make sure tool service is running at: " + targetURL);
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.toolId, ToolStatus.ERROR);
					} else if (httpResponseVal.indexOf("HTTP/1.1 400") > -1) {
						appInfo.log.error("Received from " + toolId + ": "
								+ httpResponseVal
								+ ". Make sure parameters sent to " + this.toolId + " are correct.");
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.toolId, ToolStatus.ERROR);
					} else {
						appInfo.log.error("Tool '" + toolId + "' received: "
								+ httpResponseVal
								+ ". Could not process app.");
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.toolId, ToolStatus.ERROR);
					}
				}

				entity = null;
				httpclient = null;
				httpParameters = null;
				Date endDate = new Date();
				final long endTime = endDate.getTime();
				endDate = null;
				final long elapsedTime = endTime - startTime;
//				appInfo.log.info(name + " elapsed: "
//						+ Logger.formatElapsed(elapsedTime));
			} catch (final Exception e) {
				appInfo.log.error(e.toString());
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						this.toolId, ToolStatus.ERROR);
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					inputStream = null;
				}
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					fileOutputStream = null;
				}
				fileOut = null;
			}
		}
	}

}
