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
import gov.nist.appvet.servlet.shared.Emailer;
import gov.nist.appvet.servlet.toolmgr.ToolMgr;
import gov.nist.appvet.shared.all.AppStatus;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.Validate;
import gov.nist.appvet.shared.backend.AppInfo;
import gov.nist.appvet.shared.backend.AppStatusManager;
import gov.nist.appvet.shared.backend.Database;
import gov.nist.appvet.shared.backend.Logger;
import gov.nist.appvet.shared.backend.ToolAdapter;
import gov.nist.appvet.shared.backend.XmlUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AppVetProperties {
	public static boolean error = false;
	/** AppVet Github release version number. */
	public static final String APPVET_VERSION = "2.4"; 
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
	public static boolean SSO_ACTIVE = false;
	public static String SSO_UNAUTHORIZED_URL = null;
	public static String SSO_LOGOUT_URL = null;
	public static String SSO_USERNAME_PARAMNAME = null;
	public static String SSO_EMAIL_PARAMNAME = null;
	public static String ORG_LOGO_ALT_TEXT = null;
	public static String EMAIL_CONNECTION_TIMEOUT = "2000"; // In ms
	// Default admin username and password defined in AppVetProperties.xml file.
	// Admin username and password
	public static String DEFAULT_ADMIN_USERNAME = null;
	public static String DEFAULT_ADMIN_PASSWORD = null;
	public static String DEFAULT_ADMIN_EMAIL = null;
	public static final String DEFAULT_ADMIN_FIRSTNAME = "AppVet";
	public static final String DEFAULT_ADMIN_LASTNAME = "Administrator";
	public static final Role DEFAULT_ADMIN_ROLE = Role.ADMIN;

	static {
		//System.out.println("*** Starting AppVet v" + APPVET_VERSION + " ***");
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

	// Paths
	private static String CONFIG_FILE_PATH = null;
	public static String APPS_ROOT = null;
	public static String CONF_ROOT = null;
	public static String TOOLS_CONF_ROOT = null;
	public static String APP_IMAGES_PATH = null;
	public static String TEMP_ROOT = null;
	// Default icon image paths used to copy icons (not URLs to access directly)
	public static final String DEFAULT_ANDROID_ICON_PATH = CATALINA_HOME + "/webapps/appvet/images/android-icon-gray.png";
	public static final String DEFAULT_IOS_ICON_PATH = CATALINA_HOME + "/webapps/appvet/images/apple-icon-gray.png";
	// Database properties
	public static String DB_URL = null;
	public static String DB_USERNAME = null;
	public static String DB_PASSWORD = null;
	// Email properties (optional)
	public static boolean emailEnabled = false;
	public static String SMTP_HOST = null;
	public static String SMTP_PORT = null;
	public static boolean SMTP_AUTH = false;
	public static boolean ENABLE_TLS = false;
	public static String SENDER_EMAIL = null;
	public static String SENDER_NAME = null;
	public static String SENDER_EMAIL_PASSWORD = null;
	
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
	// The max timeout from the time an app begins its PROCESSING state (after registration) to 
	// the time that all tool reports have been received.
	public static int ToolServiceTimeout = 0;
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
	/** HTTP address of host /appvet directory */
	public static String APPVET_URL = null;
	/** HTTP address of host /appvet_images directory */
	public static String APPVET_APP_IMAGES_URL = null;
	/** HTTP address of host /appvet/AppVetServlet directory */
	public static String SERVLET_URL = null;
	public static boolean KEEP_APPS = false;
	/** Display parameters.*/
	public static int NUM_ROWS_APPS_LIST = 0;
	public static int NUM_ROWS_USERS_LIST = 0;
	/* Documentation URL */
	public static String DOCUMENTATION_URL = null;
	public static String DEFAULT_DOCUMENTATION_URL = "http://appvet.github.io/appvet/";
	/** The minimum number of organizational levels that must be specified for a USER or ANALYST.
	 */
	public static int minOrgLevelsRequired = -1;
	/** The maximum number of organizational levels that may be specified for a USER or ANALYST.*/
	public static int maxOrgLevels = -1;

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
		log.debug("---------- START AppVet PROPERTIES -------------------");
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
			//System.out.println("Created apps directory for AppVet");
		}
		
		TEMP_ROOT = APPVET_FILES_HOME + "/tmp";
		printVal("TEMP_ROOT", TEMP_ROOT);
		CONF_ROOT = APPVET_FILES_HOME + "/conf";
		printVal("CONF_ROOT", CONF_ROOT);
		TOOLS_CONF_ROOT = CONF_ROOT + "/tool_adapters";
		printVal("TOOLS_CONF_ROOT", TOOLS_CONF_ROOT);
		APP_IMAGES_PATH = CATALINA_HOME + "/webapps/appvet_images";
		File appvetImagesFile = new File(APP_IMAGES_PATH);
		if (!appvetImagesFile.exists()) {
			log.warn("Directory appvet_images not found in CATALINA webapps directory. Creating directory...");
			appvetImagesFile.mkdir();
		}
		printVal("APP_IMAGES_PATH", APP_IMAGES_PATH);
		
		// Default Admin properties
		DEFAULT_ADMIN_USERNAME = xml.getXPathValue("/AppVet/Admin/Username");
		printVal("DEFAULT_ADMIN_USERNAME", DEFAULT_ADMIN_USERNAME);
		DEFAULT_ADMIN_PASSWORD = xml.getXPathValue("/AppVet/Admin/Password");
		printVal("DEFAULT_ADMIN_PASSWORD", "**********");
		DEFAULT_ADMIN_EMAIL = xml.getXPathValue("/AppVet/Admin/Email");
		printVal("DEFAULT_ADMIN_EMAIL", DEFAULT_ADMIN_EMAIL);
		
		// Database properties
		DB_URL = xml.getXPathValue("/AppVet/Database/URL");
		printVal("DB_URL", DB_URL);
		DB_USERNAME = xml.getXPathValue("/AppVet/Database/UserName");
		printVal("DB_USERNAME", DB_USERNAME);
		DB_PASSWORD = xml.getXPathValue("/AppVet/Database/Password");
		printVal("DB_PASSWORD", "**********");
		
		// Always set the AppVet default Administrator upon startup. IMPORTANT:
		// Always remove any previous default Administrators in the database
		// directly through the database. You are not permitted to delete the
		// default AppVet Administrator from within AppVet.
		if (Database.adminAddNewUser(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, 
				DEFAULT_ADMIN_EMAIL, Role.ADMIN.name(),
				DEFAULT_ADMIN_LASTNAME, DEFAULT_ADMIN_FIRSTNAME)) {
			log.debug("Added AppVet Default Administrator '" + Role.ADMIN.name() + "' to database.");
		} else {
			log.error("Could not add AppVet Default Administrator '" + Role.ADMIN.name() + "' to database.");
		}

		// Email notification properties
		SMTP_HOST = xml.getXPathValue("/AppVet/Email/SMTPHost");
		printVal("SMTP_HOST", SMTP_HOST);
		SMTP_PORT = xml.getXPathValue("/AppVet/Email/SMTPPort");
		printVal("SMTP_PORT", SMTP_PORT);
		ENABLE_TLS = new Boolean(xml.getXPathValue("/AppVet/Email/EnableTLS")).booleanValue();
		printVal("ENABLE_TLS", ENABLE_TLS);
		SENDER_EMAIL = xml.getXPathValue("/AppVet/Email/SenderEmail");
		printVal("SENDER_EMAIL", SENDER_EMAIL);
		SENDER_NAME = xml.getXPathValue("/AppVet/Email/SenderName");
		printVal("SENDER_NAME", SENDER_NAME);
		SMTP_AUTH = new Boolean(xml.getXPathValue("/AppVet/Email/SMTPAuth")).booleanValue();
		printVal("SMTP_AUTH", SMTP_AUTH);
		
		if (SMTP_HOST != null &&
				SMTP_PORT != null &&
				SENDER_EMAIL != null &&
				SENDER_NAME != null) {
								
					if (SMTP_AUTH) {
						// SMTP server requires authentication so get password
						SENDER_EMAIL_PASSWORD = xml.getXPathValue("/AppVet/Email/SenderEmailPassword");
						//printVal("SENDER_EMAIL_PASSWORD", SENDER_EMAIL_PASSWORD);
						
						if (SENDER_EMAIL_PASSWORD == null) {
							emailEnabled = false;
							log.error("Email requires authentication but no password was provided. Disabling email");
						} else {
							emailEnabled = Emailer.testConnection();
						}
					} else {		
						emailEnabled = Emailer.testConnection();
					}
					
		} else {
			// All required Email parameters are null, so disable email
			log.debug("Email is disabled due to null email parameters");
		}
		
		// Tool services
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
		ToolServiceTimeout = new Integer(
				xml.getXPathValue("/AppVet/ToolServices/Timeout")).intValue();
		printVal("TOOL_TIMEOUT", ToolServiceTimeout);
		
		// SSO parameters
		SSO_ACTIVE = new Boolean(xml.getXPathValue("/AppVet/SSO/Active")).booleanValue();
		printVal("SSO_ACTIVE", SSO_ACTIVE);
		SSO_UNAUTHORIZED_URL = xml.getXPathValue("/AppVet/SSO/UnauthorizedURL");
		printVal("SSO_UNAUTHORIZED_URL", SSO_UNAUTHORIZED_URL);
		SSO_LOGOUT_URL = xml.getXPathValue("/AppVet/SSO/LogoutURL");
		printVal("SSO_LOGOUT_URL", SSO_LOGOUT_URL);
		SSO_USERNAME_PARAMNAME = xml.getXPathValue("/AppVet/SSO/UserParams/UserNameParamName");
		printVal("SSO_USERNAME_PARAMNAME", SSO_USERNAME_PARAMNAME);
		SSO_EMAIL_PARAMNAME = xml.getXPathValue("/AppVet/SSO/UserParams/EmailParamName");
		printVal("SSO_EMAIL_PARAMNAME", SSO_EMAIL_PARAMNAME);

		// Organizational logo ALT text
		ORG_LOGO_ALT_TEXT = xml.getXPathValue("/AppVet/OrgLogoAltText");
		printVal("ORG_LOGO_ALT_TEXT", ORG_LOGO_ALT_TEXT);
		if (ORG_LOGO_ALT_TEXT == null || ORG_LOGO_ALT_TEXT.isEmpty()) {
			// Set to default
			ORG_LOGO_ALT_TEXT = "Organization logo";
		}
		
		MAX_SESSION_IDLE_DURATION = new Long(
				xml.getXPathValue("/AppVet/Sessions/Timeout")).longValue();
		printVal("MAX_SESSION_IDLE_DURATION", MAX_SESSION_IDLE_DURATION);
		GET_UPDATES_DELAY = new Integer(
				xml.getXPathValue("/AppVet/Sessions/GetUpdatesDelay"))
				.intValue();
		printVal("GET_UPDATES_DELAY", GET_UPDATES_DELAY);


		HOST = xml.getXPathValue("/AppVet/Host/Hostname");
		printVal("HOST (Static)", HOST);
		SSL = new Boolean(xml.getXPathValue("/AppVet/Host/SSL")).booleanValue();
		printVal("SSL", SSL);
		PORT = xml.getXPathValue("/AppVet/Host/Port");
		if (!Validate.isNumeric(PORT)) {
			log.error("AppVet Port is not numeric");
		} else {
			printVal("PORT", PORT);
		}
		if (SSL) {
			HOST_URL = "https://" + HOST + ":" + PORT;
		} else {
			HOST_URL = "http://" + HOST + ":" + PORT;
		}
		printVal("HOST_URL", HOST_URL);
		APPVET_URL = HOST_URL + "/appvet";
		printVal("APPVET_URL", APPVET_URL);
		APPVET_APP_IMAGES_URL = HOST_URL + "/appvet_images";
		printVal("APPVET_APP_IMAGES_URL", APPVET_APP_IMAGES_URL);
		SERVLET_URL = APPVET_URL + "/AppVetServlet";
		printVal("SERVLET_URL", SERVLET_URL);
		
		KEEP_APPS = new Boolean(xml.getXPathValue("/AppVet/Apps/KeepApps"))
				.booleanValue();
		printVal("KEEP_APPS", KEEP_APPS);
		
		NUM_ROWS_APPS_LIST = new Integer(xml.getXPathValue("/AppVet/Display/NumRowsAppsList")).intValue();
		printVal("NUM_ROWS_APPS_LIST", NUM_ROWS_APPS_LIST);
		if (NUM_ROWS_APPS_LIST <= 0) {
			// Cannot have <=0 rows in apps list
			NUM_ROWS_APPS_LIST = 20;
		}
		NUM_ROWS_USERS_LIST = new Integer(xml.getXPathValue("/AppVet/Display/NumRowsUsersList")).intValue();
		printVal("NUM_ROWS_USERS_LIST", NUM_ROWS_USERS_LIST);
		if (NUM_ROWS_USERS_LIST <= 0) {
			// Cannot have <=0 rows in admin's users list
			NUM_ROWS_USERS_LIST = 20;
		}
		
		DOCUMENTATION_URL = xml.getXPathValue("/AppVet/DocumentationURL");
		if (DOCUMENTATION_URL == null || DOCUMENTATION_URL.isEmpty()) {
			DOCUMENTATION_URL = DEFAULT_DOCUMENTATION_URL;
		}
		
		
		minOrgLevelsRequired = new Integer(xml.getXPathValue("/AppVet/OrgHierarchy/MinOrgLevelsRequired")).intValue();
		printVal("minOrgLevelsRequired", minOrgLevelsRequired);
		maxOrgLevels = new Integer(xml.getXPathValue("/AppVet/OrgHierarchy/MaxOrgLevels")).intValue();
		printVal("maxOrgLevels", maxOrgLevels);

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

		setupTools(DeviceOS.ANDROID);
		setupTools(DeviceOS.IOS);
		checkForAppsStuckInProcessingState();
		startToolMgr();
		log.debug("---------- END AppVet PROPERTIES -------------------");
	}

	private static void printVal(String parameter, Object obj) {
		if (obj == null) {
			log.warn(parameter + ": \tnull");
		} else {
			log.info(parameter + ": \t" + obj.toString());
		}
	}

	private static void setupTools(DeviceOS os) {
		File folder = null;
		if (os == DeviceOS.ANDROID) {
			folder = new File(TOOLS_CONF_ROOT + "/android");
		} else if (os == DeviceOS.IOS) {
			folder = new File(TOOLS_CONF_ROOT + "/ios");
		} else {
			log.error("Unknown OS found. Could not set up tools");
			return;
		}
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null) {
			return;
		}
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
				if (!tableColumnNames.contains(tool.toolId)) {
					// Add to table "androidtoolstatus"
					tableColumnNames.add(tool.toolId);
					if (!Database.addTableColumn("androidtoolstatus", tool.toolId,
							"VARCHAR (120) DEFAULT \"NA\"")) {
						log.error("Could not add Android tool '" + tool.toolId
								+ "' to androidtoolstatus table");
					}
				}
			}
			tableColumnNames = null;
			log.debug("Found " + androidTools.size() + " Android tools");
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
				if (!tableColumnNames.contains(tool.toolId)) {
					// Add to table "iostools"
					tableColumnNames.add(tool.toolId);
					if (!Database.addTableColumn("iostoolstatus", tool.toolId,
							"VARCHAR (120)")) {
						log.error("Could not add iOS tool '" + tool.toolId
								+ "' to iostoolstatus table");
					}
				}
			}
			tableColumnNames = null;
			log.debug("Found " + iosTools.size() + " iOS tools");
		}
	}
	
	/** Handle app stuck in PROCESSING state. This method is called at launch
	 * of AppVet in case AppVet was previously shut down during the processing 
	 * of an app. Here, apps stuck in a PROCESSING state are due to one or 
	 * more tools stuck in a SUBMITTED state. To address this issue, this 
	 * method finds all apps stuck in a PROCESSING state and sets any of its
	 * tools stuck in a SUBMITTED state to an ERROR state. */
	public static void checkForAppsStuckInProcessingState() {

		Connection connection = null;
		Statement statement = null;
		ResultSet appStatus = null;
		String sql = null;
		try {
			connection = Database.getConnection();
			// Select apps where app status is PROCESSING
			sql = "SELECT * FROM apps WHERE appstatus='PROCESSING'";

			statement = connection.createStatement();
			appStatus = statement.executeQuery(sql);
			while (appStatus.next()) {
				// Get app ID
				String appId = appStatus.getString(1);
				AppInfo appInfo = new AppInfo(appId);
				appInfo.log.error("App was stuck in PROCESSING state upon startup of AppVet. Setting app state to ERROR.");
				AppStatusManager.setAppStatus(appInfo, AppStatus.ERROR);
			}
		} catch (final SQLException e) {
			log.error(e.toString());
		} finally {
			sql = null;
			statement = null;
			connection = null;
		}
	}
	
	/** This method launches the tool manager.*/
	public static void startToolMgr() {
		log.info("*** Starting AppVet tool manager "
				+ AppVetProperties.APPVET_VERSION + " on "
				+ AppVetProperties.SERVLET_URL);
		ToolMgr toolMgr = new ToolMgr();
		Thread toolMgrThread = new Thread(toolMgr);
		toolMgrThread.start();
	}
}
