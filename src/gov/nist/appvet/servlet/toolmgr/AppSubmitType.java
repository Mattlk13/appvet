package gov.nist.appvet.servlet.toolmgr;

import gov.nist.appvet.shared.backend.AppVetProperties;
import gov.nist.appvet.shared.backend.Logger;

/**
 * @author steveq@nist.gov
 */
public enum AppSubmitType {
	/* These MUST match AppSubmitType in ToolAdapter.xsd! */
	APP_FILE, APP_NAME,
	// Use NONE if tool not accessible but reports are uploaded manually.
	NONE;
	private static final Logger log = AppVetProperties.log;

	private AppSubmitType() {
	}

	public static AppSubmitType getSubmitType(String appSubmitName) {
		if (appSubmitName != null) {
			for (final AppSubmitType ast : AppSubmitType.values()) {
				if (appSubmitName.equalsIgnoreCase(ast.name())) {
					return ast;
				}
			}
		}
		log.error("Unknown app submit type: " + appSubmitName);
		return null;
	}
}
