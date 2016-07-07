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
package gov.nist.appvet.shared.all;

/**
 * @author steveq@nist.gov
 */
public enum ToolType {
	PREPROCESSOR("images/icon-metadata.png", "Tool icon"), 
	TESTTOOL(
			"images/icon-tool.png", "Tool icon"), 
	REPORT(
			"images/icon-tool.png", "Report icon"),
	AUDIT("images/icon-tool.png", "Approval/Rejection icon"),

	// Leave SUMMARY for backward compatibility in AV3.0 (07-02-16)
	SUMMARY ("images/icon-seal.png", "Tool icon")
	;

	private final String defaultIconURL;
	private final String defaultAltText;

	private ToolType(String defaultIconURL, String defaultAltText) {
		this.defaultIconURL = defaultIconURL;
		this.defaultAltText = defaultAltText;
	}

	public static ToolType getAnalysisType(String analysisName) {
		if (analysisName != null) {
			for (final ToolType a : ToolType.values()) {
				if (analysisName.equalsIgnoreCase(a.name())) {
					return a;
				}
			}
		}
		return null;
	}

	public String getDefaultIconURL() {
		return defaultIconURL;
	}

	public String getDefaultAltText() {
		return defaultAltText;
	}
}
