package gov.nist.appvet.toolmgr.messaging;

import gov.nist.appvet.xml.XmlUtil;

/**
 * @author steveq@nist.gov
 */
public class Transaction {

	//private static final Logger log = AppVetProperties.log;
	//public Type type = null;
	public Request request = null;
	public Response response = null;

	public Transaction(
			//Type type, 
			Protocol protocol, String protocolXPath,
			XmlUtil xml, String configFileName) {

		//this.type = type;
		request = new Request(protocol, protocolXPath, xml, configFileName);
		response = new Response(protocol, protocolXPath, xml, configFileName);
	}

	/**
	 * This enum maps between transaction type and xpath directories for
	 * transactions defined in ToolAdapter.xsd. Transaction types
	 * that do not include "GET" in their name are typically used for 
	 * asynchronous services. Synchronous services will typically submit
	 * an app and immediately acquire a report in the response and are of 
	 * type SUBMIT_APPFILE_GET_APPREPORT. Multi-synchronous transactions are
	 * typically used with APIs that require multiple transactions. For 
	 * example, there will be one transaction for uploading an app and
	 * retrieving an ID, another for checking the app's status, and another
	 * for retrieving the app's report.
	 */
/*	public enum Type {
		// These should match TransactionType in ToolAdapter.xsd and
		// xpath directories in ToolAdapter.xml.
		SUBMIT_APPFILE ("SubmitAppFile"),
		SUBMIT_APPFILE_GET_APPID ("SubmitAppFileGetAppId"),
		SUBMIT_APPFILE_GET_APPREPORT ("SubmitAppFileGetAppReport"),	
		SUBMIT_APPNAME ("SubmitAppName"),
		SUBMIT_APPNAME_GET_APPID ("SubmitAppNameGetAppId"),
		SUBMIT_APPNAME_GET_APPREPORT ("SubmitAppNameGetAppReport"),	
		SUBMIT_APPID_GET_APPSTATUS ("SubmitAppIdGetAppStatus"),
		SUBMIT_APPID_GET_APPREPORT ("SubmitAppIdGetAppReport"),
		SUBMIT_APP_METADATA_GET_APPID("SubmitAppMetadataGetAppId"),
		SUBMIT_APP_METADATA_GET_REPORT("SubmitAppMetadataGetAppReport")
		
		;

		private String xpath;

		Type(String xpath) {
			this.xpath = xpath;
		}

		public static Type getTransaction(String transactionName) {
			if (transactionName != null) {
				for (final Type m : Type.values()) {
					if (transactionName.equalsIgnoreCase(m.name())) {
						return m;
					}
				}
			}
			return null;
		}

		public static String getXPath(Type transaction) {
			return transaction.xpath;
		}

	};*/

}
