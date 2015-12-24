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
package gov.nist.appvet.properties;

/**
 * @author steveq@nist.gov
 */
import gov.nist.appvet.shared.Database;
import gov.nist.appvet.shared.Logger;
import gov.nist.appvet.shared.os.DeviceOS;
import gov.nist.appvet.shared.validate.Validate;
import gov.nist.appvet.toolmgr.ToolAdapter;
import gov.nist.appvet.xml.XmlUtil;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class AppVetProperties {
	public static boolean error = false;
	/** AppVet version number. */
	public static final String APPVET_VERSION = "2.0";
	// Logging
	public static Logger log = null;
	private static String APPVET_LOG_NAME = "appvet_log.txt";
	public static String APPVET_LOG_PATH = null;
	public static String LOG_LEVEL = null;
	public static String LOG_TO_CONSOLE = null;
	public static String APP_LOG_NAME = "app_log.txt";
	// System environment variables must be set prior to launching AppVet.
	// Ensure /etc/profile defines the following environment variables.
	public static String JAVA_HOME = null;
	public static String JRE_HOME = null;
	public static String CATALINA_HOME = null; // Tomcat
	public static String CATALINA_BASE = null; // Tomcat
	public static String APKTOOL_HOME = null;  // For Android only
	public static String APPVET_FILES_HOME = null;
	public static boolean useSSO = false;

	static {
		JAVA_HOME = System.getenv("JAVA_HOME");
		if (JAVA_HOME == null || JAVA_HOME.isEmpty()) {
			System.err.println("The JAVA_HOME environment variable is null!");
		}
		JRE_HOME = System.getenv("JRE_HOME");
		if (JRE_HOME == null || JRE_HOME.isEmpty()) {
			System.err.println("The JRE_HOME environment variable is null!");
		}
		CATALINA_HOME = System.getenv("CATALINA_HOME");
		if (CATALINA_HOME == null || CATALINA_HOME.isEmpty()) {
			System.err.println("The CATALINA_HOME environment variable is null!");
		}
		CATALINA_BASE = System.getenv("CATALINA_BASE");
		if (CATALINA_BASE == null || CATALINA_BASE.isEmpty()) {
			System.err.println("The CATALINA_BASE environment variable is null!");
		}
		APKTOOL_HOME = System.getenv("APKTOOL_HOME");
		if (APKTOOL_HOME == null || APKTOOL_HOME.isEmpty()) {
			System.err.println("The APKTOOL_HOME environment variable is null!");
		}

		APPVET_FILES_HOME = System.getenv("APPVET_FILES_HOME");
		if (APPVET_FILES_HOME == null || APPVET_FILES_HOME.isEmpty()) {
			System.err.println("The APPVET_FILES_HOME environment variable is null!");
		}
	}
	// Admin username and password
	public static String ADMIN_USERNAME = null;
	public static String ADMIN_PASSWORD = null;
	// Paths
	private static String CONFIG_FILE_PATH = null;
	public static String APPS_ROOT = null;
	public static String CONF_ROOT = null;
	public static String TOOLS_CONF_ROOT = null;
	public static String APP_IMAGES = null;
	public static String TEMP_ROOT = null;
	// Database properties
	public static String DB_URL = null;
	public static String DB_USERNAME = null;
	public static String DB_PASSWORD = null;
	/** Timeout in milliseconds until a URL connection is established. */
	public static int CONNECTION_TIMEOUT = 0;
	/**
	 * Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the
	 * timeout for waiting for data or, put differently, a maximum period
	 * inactivity between two consecutive data packets).
	 */
	public static int SO_TIMEOUT = 0;
	/** Max session idle duration. */
	public static long MAX_SESSION_IDLE_DURATION = 0;
	// Delay for retrieving updates
	public static int GET_UPDATES_DELAY = 0;
	// Max timeout for running a tool
	public static int TOOL_TIMEOUT = 0;
	// Delay when polling for pending APKs to process (in ms)
	public static int TOOL_MGR_POLLING_INTERVAL = 0;
	// Max delay between starting each test (in ms)
	public static int TOOL_MGR_STAGGER_INTERVAL = 0;
	// URLs
	public static boolean SSL = false;
	public static String PORT = null;
	/** IP address of host */
	public static String HOST = null;
	/** HTTP address of host */
	public static String HOST_URL = null;
	/** Only used if AppVet behind a proxy */
	public static String PROXY_URL = null;
	/** HTTP address of host /appvet directory */
	public static String URL = null;
	/** HTTP address of host /appvet/AppVetServlet directory */
	public static String SERVLET_URL = null;
	public static boolean KEEP_APPS = false;
	// Tools
	public static ArrayList<ToolAdapter> androidTools = null;
	public static ArrayList<ToolAdapter> iosTools = null;
	public static String STATUS_MESSAGE = null;
	static {
		CONFIG_FILE_PATH = APPVET_FILES_HOME + "/conf/AppVetProperties.xml";
		final File configFile = new File(CONFIG_FILE_PATH);
		final String configFileName = configFile.getName();
		if (configFileName == null) {
			System.err.println("ERROR: Config file name is null.");
		}
		if (!configFile.exists()) {
			System.err.println("ERROR: AppVet config file does not exist.");
		}
		final XmlUtil xml = new XmlUtil(configFile);
		APPVET_LOG_PATH = APPVET_FILES_HOME + "/logs/" + APPVET_LOG_NAME;
		LOG_LEVEL = xml.getXPathValue("/AppVet/Logging/Level");
		LOG_TO_CONSOLE = xml.getXPathValue("/AppVet/Logging/ToConsole");
		log = new Logger(APPVET_LOG_PATH, "APPVET");
		log.debug("---------- START AppVet PROPERTIES -------------------",
				false);
		printVal("APPVET VERSION", APPVET_VERSION);
		printVal("JAVA_HOME", JAVA_HOME);
		printVal("JRE_HOME", JRE_HOME);
		printVal("CATALINA_HOME", CATALINA_HOME);
		printVal("CATALINA_BASE", CATALINA_BASE);
		printVal("APKTOOL_HOME", APKTOOL_HOME);
		printVal("APPVET_FILES_HOME", APPVET_FILES_HOME);
		printVal("LOG_PATH", APPVET_LOG_PATH);
		printVal("LOG_LEVEL", LOG_LEVEL);
		printVal("LOG_TO_CONSOLE", LOG_TO_CONSOLE);
		printVal("APP_LOG_NAME", APP_LOG_NAME);
		APPS_ROOT = APPVET_FILES_HOME + "/apps";
		printVal("APPS_ROOT", APPS_ROOT);
		File appsDir = new File(APPS_ROOT);
		if (!appsDir.exists()) {
			appsDir.mkdirs();			
			System.out.println("Created apps directory for AppVet");
		}
		TEMP_ROOT = APPVET_FILES_HOME + "/tmp";
		printVal("TEMP_ROOT", TEMP_ROOT);
		CONF_ROOT = APPVET_FILES_HOME + "/conf";
		printVal("CONF_ROOT", CONF_ROOT);
		TOOLS_CONF_ROOT = CONF_ROOT + "/tool_adapters";
		printVal("TOOLS_CONF_ROOT", TOOLS_CONF_ROOT);
		APP_IMAGES = CATALINA_HOME + "/webapps/appvet_images";
		printVal("APP_IMAGES", APP_IMAGES);
		DB_URL = xml.getXPathValue("/AppVet/Database/URL");
		printVal("DB_URL", DB_URL);
		DB_USERNAME = xml.getXPathValue("/AppVet/Database/UserName");
		printVal("DB_USERNAME", DB_USERNAME);
		DB_PASSWORD = xml.getXPathValue("/AppVet/Database/Password");
		printVal("DB_PASSWORD", DB_PASSWORD);
		CONNECTION_TIMEOUT = new Integer(
				xml.getXPathValue("/AppVet/ToolServices/ConnectionTimeout"))
				.intValue();
		printVal("CONNECTION_TIMEOUT", CONNECTION_TIMEOUT);
		SO_TIMEOUT = new Integer(
				xml.getXPathValue("/AppVet/ToolServices/SocketTimeout"))
				.intValue();
		printVal("SO_TIMEOUT", SO_TIMEOUT);
		TOOL_MGR_POLLING_INTERVAL = new Integer(
				xml.getXPathValue("/AppVet/ToolServices/PollingInterval"))
				.intValue();
		printVal("TOOL_MGR_POLLING_INTERVAL", TOOL_MGR_POLLING_INTERVAL);
		TOOL_MGR_STAGGER_INTERVAL = new Integer(
				xml.getXPathValue("/AppVet/ToolServices/StaggerInterval"))
				.intValue();
		printVal("TOOL_MGR_STAGGER_INTERVAL", TOOL_MGR_STAGGER_INTERVAL);
		TOOL_TIMEOUT = new Integer(
				xml.getXPathValue("/AppVet/ToolServices/Timeout")).intValue();
		printVal("TOOL_TIMEOUT", TOOL_TIMEOUT);
		useSSO = new Boolean(xml.getXPathValue("/AppVet/UseSSO")).booleanValue();
		MAX_SESSION_IDLE_DURATION = new Long(
				xml.getXPathValue("/AppVet/Sessions/Timeout")).longValue();
		printVal("MAX_SESSION_IDLE_DURATION", MAX_SESSION_IDLE_DURATION);
		GET_UPDATES_DELAY = new Integer(
				xml.getXPathValue("/AppVet/Sessions/GetUpdatesDelay"))
				.intValue();
		printVal("GET_UPDATES_DELAY", GET_UPDATES_DELAY);
		ADMIN_USERNAME = xml.getXPathValue("/AppVet/Admin/Username");
		if (ADMIN_USERNAME == null || ADMIN_USERNAME.isEmpty()) {
			System.err.println("Default admin username is null or empty");
		}
		ADMIN_PASSWORD = xml.getXPathValue("/AppVet/Admin/Password");
		if (ADMIN_PASSWORD == null || ADMIN_PASSWORD.isEmpty()) {
			System.err.println("Default admin password is null or empty");
		}
		HOST = xml.getXPathValue("/AppVet/Host/Hostname");
		if (HOST.equals("DHCP")) {
			try {
				InetAddress addr = InetAddress.getLocalHost();
				HOST = addr.getHostAddress();
				printVal("HOST (DHCP)", HOST);
			} catch (UnknownHostException e) {
				System.err.println(e.toString());
			}
		} else {
			printVal("HOST (Static)", HOST);
		}
		SSL = new Boolean(xml.getXPathValue("/AppVet/Host/SSL")).booleanValue();
		printVal("SSL", SSL);
		PORT = xml.getXPathValue("/AppVet/Host/Port");
		if (!Validate.isNumeric(PORT)) {
			System.err.println("AppVet Port is not numeric");
		} else {
			printVal("PORT", PORT);
		}
		if (SSL) {
			HOST_URL = "https://" + HOST + ":" + PORT;
		} else {
			HOST_URL = "http://" + HOST + ":" + PORT;
		}
		printVal("HOST_URL", HOST_URL);
		URL = HOST_URL + "/appvet";
		printVal("URL", URL);
		SERVLET_URL = URL + "/AppVetServlet";
		printVal("SERVLET_URL", SERVLET_URL);
		
		PROXY_URL = xml.getXPathValue("/AppVet/ProxyUrl");
		printVal("PROXY_URL", PROXY_URL);
		
		KEEP_APPS = new Boolean(xml.getXPathValue("/AppVet/Apps/KeepApps"))
				.booleanValue();
		printVal("KEEP_APPS", KEEP_APPS);
		// Apache logging
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
				"true");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http",
				"ERROR");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http.wire",
				"ERROR");
		androidTools = new ArrayList<ToolAdapter>();
		iosTools = new ArrayList<ToolAdapter>();
		if (!Database.adminExists()) {
			System.err.println("No AppVet administrator found in database.");
		}
		setupTools(DeviceOS.ANDROID);
		setupTools(DeviceOS.IOS);
		log.debug("---------- END AppVet PROPERTIES -------------------", false);
	}

	private static void printVal(String parameter, Object obj) {
		if (obj == null) {
			log.error(parameter + ": \tnull", false);
		} else {
			log.debug(parameter + ": \t" + obj.toString(), false);
		}
	}

	private static void setupTools(DeviceOS os) {
		File folder = null;
		if (os == DeviceOS.ANDROID) {
			folder = new File(TOOLS_CONF_ROOT + "/android");
		} else if (os == DeviceOS.IOS) {
			folder = new File(TOOLS_CONF_ROOT + "/ios");
		} else {
			log.error("Unknown OS found");
		}
		File[] listOfFiles = folder.listFiles();
		for (final File toolConfigFile : listOfFiles) {
			if (toolConfigFile.isFile()) {
				final String toolConfigFileName = toolConfigFile.getName();
				if (toolConfigFileName.endsWith(".xml")) {
					ToolAdapter adapter = null;
					// ADD CUSTOM TOOL SERVICE ADAPTERS HERE
					if (toolConfigFileName.equals("mytool.xml")) {
						// Note that MyTool must extend ToolAdapter
						// adapter = new MyTool(toolConfigFile);
					} else if (toolConfigFileName.equals("yourtool.xml")) {
						// Note that YourTool must extend ToolAdapter
						// adapter = new YourTool(toolConfigFile);
					} else {
						adapter = new ToolAdapter(toolConfigFile);
					}
					if (os == DeviceOS.ANDROID) {
						androidTools.add(adapter);
					} else if (os == DeviceOS.IOS) {
						iosTools.add(adapter);
					}
				}
			}
		}
		listOfFiles = null;
		folder = null;
		ArrayList<String> tableColumnNames = null;
		if (os == DeviceOS.ANDROID) {
			folder = new File(TOOLS_CONF_ROOT + "/android");
			tableColumnNames = Database
					.getTableColumnNames("androidtoolstatus");
			// Check that all Android tools have a column in the
			// androidtoolstatus table.
			if (tableColumnNames == null) {
				log.error("Could not get androidtoolstatus column names");
				return;
			}
			for (int i = 0; i < androidTools.size(); i++) {
				final ToolAdapter tool = androidTools.get(i);
				if (!tableColumnNames.contains(tool.id)) {
					// Add to table "androidtoolstatus"
					tableColumnNames.add(tool.id);
					if (!Database.addTableColumn("androidtoolstatus", tool.id,
							"VARCHAR (120) DEFAULT \"NA\"")) {
						log.error("Could not add Android tool '" + tool.id
								+ "' to androidtoolstatus table");
					}
				}
			}
			tableColumnNames = null;
			log.debug("Found " + androidTools.size() + " Android tools", false);
		} else if (os == DeviceOS.IOS) {
			folder = new File(TOOLS_CONF_ROOT + "/ios");
			tableColumnNames = Database.getTableColumnNames("iostoolstatus");
			// Check that all iOS tools have a column in the iostoolstatus
			// table.
			if (tableColumnNames == null) {
				log.error("Could not get iostoolstatus column names");
				return;
			}
			for (int i = 0; i < iosTools.size(); i++) {
				final ToolAdapter tool = iosTools.get(i);
				if (!tableColumnNames.contains(tool.id)) {
					// Add to table "iostools"
					tableColumnNames.add(tool.id);
					if (!Database.addTableColumn("iostoolstatus", tool.id,
							"VARCHAR (120)")) {
						log.error("Could not add iOS tool '" + tool.id
								+ "' to iostoolstatus table");
					}
				}
			}
			tableColumnNames = null;
			log.debug("Found " + iosTools.size() + " iOS tools", false);
		}
	}
}
