package gov.nist.appvet.shared.backend;


/**
 * @author steveq@nist.gov
 */
public class Transaction {

	public Request request = null;
	public Response response = null;

	public Transaction(
			Protocol protocol, String protocolXPath,
			XmlUtil xml, String configFileName) {

		request = new Request(protocol, protocolXPath, xml, configFileName);
		response = new Response(protocol, protocolXPath, xml, configFileName);
	}

}
