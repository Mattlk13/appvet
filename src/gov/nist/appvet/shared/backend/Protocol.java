package gov.nist.appvet.shared.backend;

/**
 * @author steveq@nist.gov
 */
public enum Protocol {

	SYNCHRONOUS ("Synchronous"), 
	ASYNCHRONOUS ("Asynchronous"), 
	PUSH ("Push"), 
	INTERNAL ("Internal");

	public String xmlTag;

	private Protocol(String xmlTag) {
		this.xmlTag = xmlTag;
	}
	public static Protocol getProtocol(String protocolName) {
		if (protocolName != null) {
			for (final Protocol protocol : Protocol.values()) {
				if (protocolName.equalsIgnoreCase(protocol.name())) {
					return protocol;
				}
			}
		}
		return null;
	}

};
