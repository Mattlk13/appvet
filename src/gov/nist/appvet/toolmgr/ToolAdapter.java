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
package gov.nist.appvet.toolmgr;

import gov.nist.appvet.gwt.shared.UserToolCredentialsGwt;
import gov.nist.appvet.properties.AppVetProperties;
import gov.nist.appvet.servlet.shared.SSLWrapper;
import gov.nist.appvet.shared.AppSubmitType;
import gov.nist.appvet.shared.Database;
import gov.nist.appvet.shared.ErrorMessage;
import gov.nist.appvet.shared.Logger;
import gov.nist.appvet.shared.ReportFileType;
import gov.nist.appvet.shared.analysis.AnalysisType;
import gov.nist.appvet.shared.app.AppInfo;
import gov.nist.appvet.shared.os.DeviceOS;
import gov.nist.appvet.shared.status.ToolStatus;
import gov.nist.appvet.shared.status.ToolStatusManager;
import gov.nist.appvet.toolmgr.messaging.Protocol;
import gov.nist.appvet.toolmgr.messaging.Request;
import gov.nist.appvet.toolmgr.messaging.Response;
import gov.nist.appvet.toolmgr.messaging.Transaction;
import gov.nist.appvet.xml.XmlUtil;

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
	// ID used as a database table column name
	public String id = null; 
	public AnalysisType analysisType = null;
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

		id = xml.getXPathValue("/ToolAdapter/Description/Id");
		checkNullString(configFileName, "id", id);

		String analysisValue = xml
				.getXPathValue("/ToolAdapter/Description/Category");
		analysisType = AnalysisType.getAnalysisType(analysisValue);

		String osStr = xml.getXPathValue("/ToolAdapter/Description/OS");
		os = DeviceOS.getOS(osStr);
		if (os == null) {
			log.error("Tool OS is null.");
		}

		vendorName = xml.getXPathValue("/ToolAdapter/Description/VendorName");

		webSite = xml.getXPathValue("/ToolAdapter/Description/VendorWebsite");

		// Authorization requirements
		final String booleanStringValue = xml
				.getXPathValue("/ToolAdapter/Description/AuthenticationRequired");
		authenticationRequired = new Boolean(booleanStringValue).booleanValue();

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

		// App submission format type (i.e., binary app file or app name,
		// package, etc. as a string)
		String appSubmitStr = xml
				.getXPathValue("/ToolAdapter/Description/AppSubmit");
		log.debug("AppSubmitStr: " + appSubmitStr);
		appSubmitType = AppSubmitType.getSubmitType(appSubmitStr);
		log.debug("appSubmitType: " + appSubmitType);
		// Report configuration
		final String reportFileTypeString = xml
				.getXPathValue("/ToolAdapter/Description/ReportFile");
		checkNullString(configFileName, "reportFileTypeString",
				reportFileTypeString);
		reportFileType = ReportFileType.getFileType(reportFileTypeString);
		reportName = generateReportName();

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

		log.debug("Adding Tool:\n" + "Config file: " + configFileName + "\n"
				+ "Tool name: " + name + "\n" 
				+ "Tool ID: " + id + "\n"
				+ "Analysis type: " + analysisType + "\n" 
				+ "OS: " + os.name()
				+ "\n" + "Vendor: " + vendorName + "\n" 
				+ "Website: " + webSite
				+ "\n" + 
				"App submit type: " + appSubmitType.name() 
				+ "\n"
				+ "Report file type: " + reportFileType.name() + "\n"
				+ "AppVet protocol: " + protocol + "\n", 
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

			if (adapter.id.equals(toolId)) {
				return true;
			}

		}

		for (int i = 0; i < AppVetProperties.iosTools.size(); i++) {
			final ToolAdapter adapter = AppVetProperties.iosTools.get(i);

			if (adapter.id.equals(toolId)) {
				return true;
			}

		}

		return false;

	}

	public static ToolAdapter getByToolId(DeviceOS os, String toolId) {

		if (os == DeviceOS.ANDROID) {

			for (int i = 0; i < AppVetProperties.androidTools.size(); i++) {
				final ToolAdapter adapter = AppVetProperties.androidTools
						.get(i);

				if (adapter.id.equals(toolId)) {
					return adapter;
				}

			}

			log.error("Android tool id '" + toolId + "' does not exist!");
			return null;

		} else if (os == DeviceOS.IOS) {

			for (int i = 0; i < AppVetProperties.iosTools.size(); i++) {
				final ToolAdapter adapter = AppVetProperties.iosTools.get(i);

				if (adapter.id.equals(toolId)) {
					return adapter;
				}

			}

			log.error("iOS tool id '" + toolId + "' does not exist!");
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
	
	

	public static ToolAdapter getAudit(DeviceOS os) {

		if (os == DeviceOS.ANDROID) {

			for (int i = 0; i < AppVetProperties.androidTools.size(); i++) {
				AnalysisType analysisType = AppVetProperties.androidTools
						.get(i).analysisType;
				if (analysisType == AnalysisType.AUDIT) {
					return AppVetProperties.androidTools.get(i);
				}

			}

			log.error("Android audit report does not exist!");
			return null;
		} else if (os == DeviceOS.IOS) {

			for (int i = 0; i < AppVetProperties.iosTools.size(); i++) {
				AnalysisType analysisType = AppVetProperties.iosTools.get(i).analysisType;

				if (analysisType == AnalysisType.AUDIT) {
					return AppVetProperties.iosTools.get(i);
				}

			}

			log.error("iOS audit report does not exist!");
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
			ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId, this.id,
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
						
						if (paramValue.equals("APPVET_DEFINED")) {
							
							if (paramName.equals("appid")) {
								appInfo.log.debug("Found " + paramName + " = "
										+ "'APPVET_DEFINED' for tool '" + id
										+ "'. Setting to appid = '"
										+ appInfo.appId + "'");
								paramValue = appInfo.appId;
							} else {
								appInfo.log
										.error("Found "
												+ paramName
												+ " = "
												+ "'APPVET_DEFINED' for tool '"
												+ id
												+ "' but no actual value is set by AppVet. Aborting.");
								return null;
							}
							
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
			return id + reportSuffix + "." + ReportFileType.TXT.value;
		case HTML:
			return id + reportSuffix + "." + ReportFileType.HTML.value;
		case PDF:
			return id + reportSuffix + "." + ReportFileType.PDF.value;
		case RTF:
			return id + reportSuffix + "." + ReportFileType.RTF.value;
		case XML:
			return id + reportSuffix + "." + ReportFileType.XML.value;
		case JSON:
			return id + reportSuffix + "." + ReportFileType.JSON.value;
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
		log.debug("CALLING RUN METHOD FOR " + id);

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
			log.debug("Tool " + id + ": Checking tool credentials for "
					+ appInfo.ownerName);
			// Get user's tool credentials
			ArrayList<UserToolCredentialsGwt> userToolCredentials = 
					Database.getUserToolCredentials(appInfo.ownerName);
			
			for (int i = 0; i < userToolCredentials.size(); i++) {
				UserToolCredentialsGwt userToolCredential = userToolCredentials
						.get(i);
				
				if (userToolCredential.toolId.equals(id)
						&& userToolCredential.os.equals(os.name())) {

					for (int j = 0; j < userToolCredential.authParamNames.length; j++) {
						String paramName = userToolCredential.authParamNames[j];
						String paramValue = userToolCredential.authParamValues[j];
//						log.debug("Adding " + appInfo.ownerName
//								+ " tool credentials for " + id);
//						authParamNames.add(paramName);
//						authParamValues.add(paramValue);
						if (paramValue == null || paramValue.isEmpty()
								|| paramValue.equals("null")) {
							log.warn("Tool authentication parameter value "
									+ "cannot be null. Setting tool to NA");
							ToolStatusManager.setToolStatus(appInfo.os,
									appInfo.appId, this.id, ToolStatus.NA);
							return;
						} else {
							log.debug("Adding " + appInfo.ownerName
									+ " tool credentials for " + id);
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

			try {
				Date startDate = new Date();
				final long startTime = startDate.getTime();
				startDate = null;
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						this.id, ToolStatus.SUBMITTED);
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
							this.id, ToolStatus.ERROR);
					return;
				}
				HttpPost httpPost = new HttpPost(appVetRequest.URL);
				httpPost.setEntity(entity);
				appInfo.log.info(name + " adapter sending app " + appInfo.appId
						+ " to " + appVetRequest.URL);

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
					final InputStream inputStream = responseEntity.getContent();
					final String reportPath = appInfo.getReportsPath() + "/"
							+ generateReportName();
					File fileOut = new File(reportPath);
					FileOutputStream fileOutputStream = new FileOutputStream(
							fileOut, false);
					int c;
					while ((c = inputStream.read()) != -1) {
						fileOutputStream.write(c);
					}
					fileOutputStream.flush();
					inputStream.close();
					fileOutputStream.close();
					fileOutputStream = null;
					fileOut = null;

					String appvetRiskHeaderName = toolResponse.appVetRiskHeaderName;
					log.debug("appvetRiskHeaderName: " + appvetRiskHeaderName);
					String toolResult = httpResponse.getFirstHeader(
							appvetRiskHeaderName).getValue();
					appInfo.log.debug("Received tool result: " + toolResult
							+ " from " + this.id);

					ToolStatus toolStatus = ToolStatus.getStatus(toolResult);

					if (toolStatus == null) {
						appInfo.log.error("Tool status is null!");
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.id, ToolStatus.ERROR);
						return;
					} else {
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.id, toolStatus);
					}

				} else if (protocol == Protocol.ASYNCHRONOUS) {

					final String httpResponseVal = httpResponse.getStatusLine()
							.toString();
					if ((httpResponseVal.indexOf("HTTP/1.1 202 Accepted") > -1)
							|| (httpResponseVal.indexOf("HTTP/1.1 200 OK") > -1)) {
						appInfo.log.info("Received from " + id + ": "
								+ httpResponseVal);
					} else if (httpResponseVal.indexOf("HTTP/1.1 404 Not Found") > -1) {
						appInfo.log.error("Received from " + id + ": "
								+ httpResponseVal
								+ ". Make sure tool has been added to Tomcat");
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.id, ToolStatus.ERROR);
					} else if (httpResponseVal.indexOf("HTTP/1.1 400") > -1) {
						appInfo.log.error("Received from " + id + ": "
								+ httpResponseVal
								+ ". Check tool request parameters");
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.id, ToolStatus.ERROR);
					} else {
						appInfo.log.error("Tool '" + id + "' received: "
								+ httpResponseVal
								+ ". Could not process app.");
						ToolStatusManager.setToolStatus(appInfo.os,
								appInfo.appId, this.id, ToolStatus.ERROR);
					}
				}

				entity = null;
				httpclient = null;
				httpParameters = null;
				Date endDate = new Date();
				final long endTime = endDate.getTime();
				endDate = null;
				final long elapsedTime = endTime - startTime;
				appInfo.log.info(name + " elapsed: "
						+ Logger.formatElapsed(elapsedTime));
			} catch (final Exception e) {
				appInfo.log.error(e.toString());
				ToolStatusManager.setToolStatus(appInfo.os, appInfo.appId,
						this.id, ToolStatus.ERROR);
			}
		}
	}

}
