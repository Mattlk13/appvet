package gov.nist.appvet.shared.backend;

import gov.nist.appvet.properties.AppVetProperties;


/**
 * This class defines an HTTP response message received by AppVet from a tool 
 * service.
 * 
 * @author steveq@nist.gov
 */
public class Response {
	
	private static final Logger log = AppVetProperties.log;

	public Protocol protocol = null;
	public String protocolXPath = null;
	public XmlUtil xml = null;
	public String configFileName = null;
	
	public String appVetRiskHeaderName = null;
	public boolean reportPayload = false; 
	public String payloadType = null;
	public String statusCode = null;
	
	public Response(Protocol protocol, String protocolXPath,
			XmlUtil xml, String configFileName) {
		this.protocol = protocol;
		this.protocolXPath = protocolXPath;
		this.xml = xml;
		this.configFileName = configFileName;
		
		loadResponse();
	}
	
	public void loadResponse() {

		appVetRiskHeaderName = xml.getXPathValue(protocolXPath
				+ "/Response/AppVetRiskHeaderName");
		log.debug("Risk header name: " + appVetRiskHeaderName);
		
		reportPayload = new Boolean(protocolXPath
				+ "/Response/ReportPayload").booleanValue();
		log.debug("Report payload: " + reportPayload);
		
		if (reportPayload) {
			payloadType = xml.getXPathValue(protocolXPath
					+ "/Response/PayloadType");
			log.debug("Payload type: " + payloadType);
		}

		statusCode = xml.getXPathValue(protocolXPath
				+ "/Response/StatusCode");
		log.debug("Status code: " + statusCode);


	}
}
