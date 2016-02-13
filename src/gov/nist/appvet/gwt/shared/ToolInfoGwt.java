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
package gov.nist.appvet.gwt.shared;

import gov.nist.appvet.shared.all.ToolType;

import com.google.gwt.user.client.rpc.IsSerializable;

// TODO: See if this class can be deleted since most of this information is
// already contained in UserInfoGwt.ToolCredentialsGwt for tool credentials.
/**
 * @author steveq@nist.gov
 */
public class ToolInfoGwt implements IsSerializable {
	// -------------- Updated via tool configuration file -------------
	private String id = null;
	private String name = null;
	private boolean authenticationRequired = false;
	private String[] authenticationParameterNames = null;
	private String reportFileType = null;
	private String os = null; // From DeviceOS.name()
	// TODO Change toolType to type ToolType since it is added as a GWT module
	private ToolType toolType = null;
	private String restrictionType = null;
	private String reportTemplateURL = null;
	private String iconURL = null;

	public ToolInfoGwt() {
	}
	
	public ToolType getType() {
		return toolType;
	}
	
	public void setType(ToolType type) {
		this.toolType = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String toolId) {
		this.id = toolId;
	}

	public String getName() {
		return name;
	}

	public void setName(String toolName) {
		this.name = toolName;
	}

	public boolean requiresAuthentication() {
		return authenticationRequired;
	}

	public void setAuthenticationRequired(boolean credentialsRequired) {
		this.authenticationRequired = credentialsRequired;
	}

	public String getReportFileType() {
		return reportFileType;
	}

	public void setReportFileType(String reportFileType) {
		this.reportFileType = reportFileType;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String[] getAuthenticationParameterNames() {
		return authenticationParameterNames;
	}

	public void setAuthenticationParameterNames(
			String[] authenticationParameterNames) {
		this.authenticationParameterNames = authenticationParameterNames;
	}

	public String getRestrictionType() {
		return restrictionType;
	}

	public void setRestrictionType(String restrictionType) {
		this.restrictionType = restrictionType;
	}

	public String getReportTemplateURL() {
		return reportTemplateURL;
	}

	public void setReportTemplateURL(String reportTemplateURL) {
		this.reportTemplateURL = reportTemplateURL;
	}

	public String getIconURL() {
		return iconURL;
	}

	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}
}
