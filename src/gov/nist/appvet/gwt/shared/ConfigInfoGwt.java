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

import gov.nist.appvet.shared.all.UserInfo;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author steveq@nist.gov
 */
public class ConfigInfoGwt implements IsSerializable {
	private UserInfo userInfo = null;
	private String hostname = null;
	private String appVetHostUrl = null;
	//private String appvetProxyUrl = null; // Only used if AppVet behind proxy
	private String appVetUrl = null;
	private String appVetServletUrl = null;
	private String appVetVersion = null;
	private String orgLogoAltText = null;
	private String sessionId = null;
	private long maxIdleTime = 0;
	private int getUpdatesDelay = 0;
	private Date sessionExpiration = null;
	private ArrayList<ToolInfoGwt> tools = null;
	private String systemMessage = null;
	private int numRowsAppsList = 0;
	private int numRowsUsersList = 0;
	private String documentationURL = null;

	public ConfigInfoGwt() {
	}

	public String getAppVetHostUrl() {
		return appVetHostUrl;
	}
	
//	public String getAppVetProxyUrl() {
//		return appvetProxyUrl;
//	}

	public String getAppVetServletUrl() {
		return appVetServletUrl;
	}

	public String getAppVetUrl() {
		return appVetUrl;
	}

	public String getAppVetVersion() {
		return appVetVersion;
	}

	public String getHostname() {
		return hostname;
	}

	public long getMaxIdleTime() {
		return maxIdleTime;
	}

	public Date getSessionExpiration() {
		return sessionExpiration;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getSystemMessage() {
		return systemMessage;
	}

	public int getUpdatesDelay() {
		return getUpdatesDelay;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setAppVetHostUrl(String appvetHostUrl) {
		this.appVetHostUrl = appvetHostUrl;
	}
	
//	public void setAppVetProxyUrl(String appvetProxyUrl) {
//		this.appvetProxyUrl = appvetProxyUrl;
//	}

	public void setAppVetServletUrl(String appvetServletUrl) {
		this.appVetServletUrl = appvetServletUrl;
	}

	public void setAppVetUrl(String appvetUrl) {
		this.appVetUrl = appvetUrl;
	}

	public void setAppVetVersion(String appvetVersion) {
		this.appVetVersion = appvetVersion;
	}

	public void setGetUpdatesDelay(int value) {
		getUpdatesDelay = value;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setMaxIdleTime(long maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	public void setSessionExpiration(Date sessionExpiration) {
		this.sessionExpiration = sessionExpiration;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setSystemMessage(String systemMessage) {
		this.systemMessage = systemMessage;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public ArrayList<ToolInfoGwt> getTools() {
		return tools;
	}

	public void setTools(ArrayList<ToolInfoGwt> tools) {
		this.tools = tools;
	}

//	public String getAppvetProxyUrl() {
//		return appvetProxyUrl;
//	}
//
//	public void setAppvetProxyUrl(String appvetProxyUrl) {
//		this.appvetProxyUrl = appvetProxyUrl;
//	}

	public String getOrgLogoAltText() {
		return orgLogoAltText;
	}

	public void setOrgLogoAltText(String orgLogoAltText) {
		this.orgLogoAltText = orgLogoAltText;
	}

	public int getNumRowsAppsList() {
		return numRowsAppsList;
	}

	public void setNumRowsAppsList(int numRowsAppsList) {
		this.numRowsAppsList = numRowsAppsList;
	}

	public int getNumRowsUsersList() {
		return numRowsUsersList;
	}

	public void setNumRowsUsersList(int numRowsUsersList) {
		this.numRowsUsersList = numRowsUsersList;
	}

	public String getDocumentationURL() {
		return documentationURL;
	}

	public void setDocumentationURL(String documentationURL) {
		this.documentationURL = documentationURL;
	}
}
