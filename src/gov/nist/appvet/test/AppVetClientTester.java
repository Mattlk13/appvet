package gov.nist.appvet.test;


import gov.nist.appvet.shared.all.ReportFileType;
import gov.nist.appvet.shared.backend.ToolStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.Header;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/** This class tests the 
 * <a href="http://appvet.github.io/appvet/">AppVet Client API</a>.
 */
public class AppVetClientTester {

	private String username = "johndoe";
	private String password = "mYpAssword";
	private String appvetURL = "http://127.0.0.1:8080/appvet/AppVetServlet";
	private String appPath = "/home/test-apps/android/com.example.apk";
	private String reportPath = "/home/test-reports/test-report.html";
	private String getStatusURL = 
			appvetURL + "?command=GET_APP_STATUS;appid=xxx;sessionid=yyy";
	private static String reportsDirectory = "/home/reports";

	public void AppVetClientTeseter() {}

	/** AUTHENTICATE */
	public String authenticate() {
		System.out.println("Authenticating...");
		try {
			// Set up HTTP POST
			HttpPost httpPost = new HttpPost(appvetURL);
			String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
			httpPost.setHeader("Authorization", "Basic " + encoding);

			HttpParams httpParameters = new BasicHttpParams();
			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			// Execute client
			final HttpResponse response = httpClient.execute(httpPost);
			String responseStatus = response.getStatusLine().toString();
			System.out.println("Received from AppVet: " + responseStatus);
			if (responseStatus.indexOf("HTTP/1.1 200") > -1) {
				HttpEntity httpEntity = response.getEntity();
				InputStream inputStream = httpEntity.getContent();
				String sessionId = getStringFromInputStream(inputStream);
				System.out.println("Session ID: " + sessionId);
				return sessionId;
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
	}

	/** SUBMIT_APP */
	public String submitApp(String sessionId) {
		System.out.println("Submitting app...");

		try {
			// Set up multipart entity
			MultipartEntity entity = new MultipartEntity();
			entity.addPart("command",
					new StringBody("SUBMIT_APP", Charset.forName("UTF-8")));
			entity.addPart("sessionid", new StringBody(
					sessionId, Charset.forName("UTF-8")));
			File appFile = new File(appPath);
			FileBody fileBody = new FileBody(appFile);
			entity.addPart("fileupload", fileBody);

			// Set up HTTP POST
			HttpPost httpPost = new HttpPost(appvetURL);
			httpPost.setEntity(entity);

			HttpParams httpParameters = new BasicHttpParams();
			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			// Execute client
			final HttpResponse response = httpClient.execute(httpPost);
			String responseStatus = response.getStatusLine().toString();
			System.out.println("Received from AppVet: " + responseStatus);
			if (responseStatus.indexOf("HTTP/1.1 202") > -1) {
				HttpEntity httpEntity = response.getEntity();
				InputStream inputStream = httpEntity.getContent();
				String appId = getStringFromInputStream(inputStream);
				System.out.println("App ID: " + appId);
				return appId;
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
	}

	/** GET_APP_STATUS */
	public String getAppStatus(String sessionId, String appId) {
		System.out.println("Getting app status...");

		try {
			String testURL = 
					appvetURL + "?command=GET_APP_STATUS&appid=" 
							+ appId + "&sessionid=" + sessionId;

			// Set up HTTP GET
			System.out.println("URL: " + testURL);
			HttpGet httpGet = new HttpGet(testURL);

			HttpParams httpParameters = new BasicHttpParams();
			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			// Execute client
			final HttpResponse response = httpClient.execute(httpGet);
			String responseStatus = response.getStatusLine().toString();
			System.out.println("Received from AppVet: " + responseStatus);
			if (responseStatus.indexOf("HTTP/1.1 200") > -1) {
				HttpEntity httpEntity = response.getEntity();
				InputStream inputStream = httpEntity.getContent();
				String status = getStringFromInputStream(inputStream);
				System.out.println("Status: " + status);
				return status;
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
	}

	/** GET_APP_TOOLS_STATUS */
	public String getAppToolsStatus(String sessionId, String appId) {
		System.out.println("Getting app tools status...");

		try {
			String testURL = 
					appvetURL + "?command=GET_APP_TOOLS_STATUS&appid=" 
							+ appId + "&sessionid=" + sessionId;

			// Set up HTTP GET
			System.out.println("URL: " + testURL);
			HttpGet httpGet = new HttpGet(testURL);

			HttpParams httpParameters = new BasicHttpParams();
			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			// Execute client
			final HttpResponse response = httpClient.execute(httpGet);
			String responseStatus = response.getStatusLine().toString();
			System.out.println("Received from AppVet: " + responseStatus);
			if (responseStatus.indexOf("HTTP/1.1 200") > -1) {
				HttpEntity httpEntity = response.getEntity();
				InputStream inputStream = httpEntity.getContent();
				String status = getStringFromInputStream(inputStream);
				System.out.println("Status: " + status);
				return status;
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
	}

	/** GET_APP_TOOL_IDS */
	public String getAppToolIds(String sessionId, String appId) {
		System.out.println("Getting app tool IDs...");

		try {
			String testURL = 
					appvetURL + "?command=GET_APP_TOOL_IDS&appid=" 
							+ appId + "&sessionid=" + sessionId;

			// Set up HTTP GET
			System.out.println("URL: " + testURL);
			HttpGet httpGet = new HttpGet(testURL);

			HttpParams httpParameters = new BasicHttpParams();
			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			// Execute client
			final HttpResponse response = httpClient.execute(httpGet);
			String responseStatus = response.getStatusLine().toString();
			System.out.println("Received from AppVet: " + responseStatus);
			if (responseStatus.indexOf("HTTP/1.1 200") > -1) {
				HttpEntity httpEntity = response.getEntity();
				InputStream inputStream = httpEntity.getContent();
				String ids = getStringFromInputStream(inputStream);
				System.out.println("IDs: " + ids);
				return ids;
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
	}

	/** GET_TOOL_REPORT */
	public boolean getToolReport(String sessionId, String appId, String toolId) {
		System.out.println("Getting app tool report...");

		try {
			String testURL = 
					appvetURL + "?command=GET_TOOL_REPORT&appid=" 
							+ appId + "&sessionid=" + sessionId + "&toolid=" + toolId;

			// Set up HTTP GET
			System.out.println("URL: " + testURL);
			HttpGet httpGet = new HttpGet(testURL);

			HttpParams httpParameters = new BasicHttpParams();
			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			// Execute client
			final HttpResponse response = httpClient.execute(httpGet);
			String responseStatus = response.getStatusLine().toString();
			System.out.println("Received from AppVet: " + responseStatus);
			if (responseStatus.indexOf("HTTP/1.1 200") > -1) {
				// Get content type
				Header contentType = response.getEntity().getContentType();
				String contentTypeValue = contentType.getValue();
				System.out.println("Content type value: " + contentTypeValue);
				String fileExtension = null;

				if (contentTypeValue.equals("text/plain")) {
					fileExtension = ".txt";
				} else if (contentTypeValue.equals("text/html")) {
					fileExtension = ".html";
				} else if (contentTypeValue.equals("application/pdf")) {
					fileExtension = ".pdf";
				} else if (contentTypeValue.equals("application/rtf")) {
					fileExtension = ".rtf";
				} else {
					System.err.println("Unknown content type: " + contentTypeValue);
				}

				HttpEntity httpEntity = response.getEntity();
				InputStream inputStream = httpEntity.getContent();

				return writeFile(inputStream, appId, toolId, fileExtension);

			} else {
				return false;
			}
		} catch (Exception e) {
			System.err.println(e.toString());
			return false;
		}
	}

	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	private static boolean writeFile(InputStream is, String appId, String toolId, 
			String fileExtension) {
		
		try {
			byte[] buffer = new byte[is.available()];
			is.read(buffer);

			File targetFile = new File(reportsDirectory + "/report_" + appId + "-" + toolId + fileExtension);
			OutputStream outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
			outStream.close();
			is.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// SUBMIT_REPORT
	public boolean submitReport(String sessionId, String appId, String toolId, ToolStatus toolStatus) {

		try {
		    /*
		     * To send reports back to AppVet, the following parameters must be
		     * sent: - command: SUBMIT_REPORT - username: AppVet username -
		     * password: AppVet password - appid: The app ID - toolid: The ID of
		     * this tool - toolrisk: The risk assessment (LOW, MODERATE, HIGH,
		     * ERROR) - report: The report file.
		     */
		    MultipartEntity entity = new MultipartEntity();
		    entity.addPart("command",
			    new StringBody("SUBMIT_REPORT", Charset.forName("UTF-8")));
		    entity.addPart("sessionid", new StringBody(
		    		sessionId, Charset.forName("UTF-8")));
		    entity.addPart("appid",
			    new StringBody(appId, Charset.forName("UTF-8")));
		    entity.addPart("toolid",
			    new StringBody(toolId, Charset.forName("UTF-8")));
		    entity.addPart("toolrisk", new StringBody(toolStatus.name(),
			    Charset.forName("UTF-8")));
		    File report = new File(reportPath);
		    FileBody fileBody = new FileBody(report);
		    entity.addPart("file", fileBody);
		    
			// Set up HTTP POST
			HttpPost httpPost = new HttpPost(appvetURL);
			httpPost.setEntity(entity);

			HttpParams httpParameters = new BasicHttpParams();
			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			// Execute client
			final HttpResponse response = httpClient.execute(httpPost);
			String responseStatus = response.getStatusLine().toString();
			System.out.println("Received from AppVet: " + responseStatus);
			if (responseStatus.indexOf("HTTP/1.1 202") > -1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    return false;
		}
	}


	public static void main(String[] args) {

		AppVetClientTester tester = new AppVetClientTester();

		// AUTHENTICATE
		String sessionId = tester.authenticate();

		// SUBMIT_APP
		String appId = tester.submitApp(sessionId);

		// Wait a bit for app to process
		try {
			Thread.sleep(60000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// GET_APP_STATUS. This should be put into a loop until
		// status is no longer PENDING or PROCESSING
		String appStatus = tester.getAppStatus(sessionId, appId);

		// GET_APP_TOOLS_STATUS
		String toolsStatuses = tester.getAppToolsStatus(sessionId, appId);

		// GET_APP_TOOL_IDS
		String toolsStatus = tester.getAppToolIds(sessionId, appId);

		// GET_TOOL_REPORT
		boolean gotReport = tester.getToolReport(sessionId, appId, "appinfo");

		// SUBMIT_REPORT
		boolean submittedReport = tester.submitReport(sessionId, appId, "appinfo", ToolStatus.AVAILABLE);


	}

}