package gov.nist.appvet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SystemAlert  implements IsSerializable {
	public SystemAlertType type = null;
	public String message = null;
}
