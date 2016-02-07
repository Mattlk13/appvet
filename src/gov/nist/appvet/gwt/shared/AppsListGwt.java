package gov.nist.appvet.gwt.shared;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AppsListGwt implements IsSerializable {
	Date appsLastChecked = null;
	List<AppInfoGwt> apps = null;
}
