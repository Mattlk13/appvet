package gov.nist.appvet.shared.all;

/**
 * Enumeration of possible incoming servlet parameters for both GET and POST
 * HTTP requests.
 * 
 * @author steveq@nist.gov
 */
public enum AppVetParameter {
	USERNAME("username"), PASSWORD("password"), SESSIONID("sessionid"), COMMAND(
			"command"), APPID("appid"), 
			// Report is used if a submitted report file is contained as an
			// HTML form element rather than as an attachment.
			REPORT("report"), 
			TOOLID("toolid"), TOOLRISK(
			"toolrisk"), APPPACKAGE("app_package_name"), APPVERSION(
			"app_version"), APPOS("app_os");
	public String value;

	private AppVetParameter(String value) {
		this.value = value;
	}
}
