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
package gov.nist.appvet.gwt.client.gui.dialog;

import java.util.ArrayList;
import java.util.logging.Logger;

import gov.nist.appvet.gwt.shared.ToolInfoGwt;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.ToolType;
import gov.nist.appvet.shared.all.UserInfo;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author steveq@nist.gov
 */
public class ReportUploadDialogBox extends DialogBox {
	private Logger log = Logger.getLogger("ReportUploadDialogBox");
	public FormPanel uploadReportForm = null;
	public HTML statusLabel = null;
	public FileUpload fileUpload = null;
	public PushButton submitButton = null;
	public PushButton cancelButton = null;
	public String servletURL = null;
	public ListBox toolRiskComboBox = null;
	public ListBox toolNamesComboBox = null;
	public Hidden hiddenToolID = null;
	public Hidden hiddenToolRisk = null;
	public Label riskLabel = null;
	public ArrayList<ToolInfoGwt> permittedToolReports = new ArrayList<ToolInfoGwt>();

	public ReportUploadDialogBox(UserInfo userInfo, String sessionId,
			String appid, String servletURL, DeviceOS os,
			final ArrayList<ToolInfoGwt> tools) {
		super(false, true);
		setWidth("400px");
		setAnimationEnabled(false);
		final VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.setWidget(dialogVPanel);
		dialogVPanel.setSize("", "");
		this.servletURL = servletURL;
		final SimplePanel simplePanel = new SimplePanel();
		simplePanel.setStyleName("reportUploadPanel");
		dialogVPanel.add(simplePanel);
		dialogVPanel.setCellVerticalAlignment(simplePanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		dialogVPanel.setCellHorizontalAlignment(simplePanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		simplePanel.setSize("", "");
		uploadReportForm = new FormPanel();
		simplePanel.setWidget(uploadReportForm);
		uploadReportForm.setSize("", "");
		uploadReportForm.setAction(servletURL);
		uploadReportForm.setMethod(FormPanel.METHOD_POST);
		uploadReportForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		uploadReportForm.setWidget(verticalPanel);
		verticalPanel.setSize("", "");
		final Hidden hiddenAppid = new Hidden();
		hiddenAppid.setTitle("appid");
		hiddenAppid.setName("appid");
		hiddenAppid.setValue(appid);
		verticalPanel.add(hiddenAppid);
		final Hidden hiddenUsername = new Hidden();
		hiddenAppid.setTitle("username");
		hiddenUsername.setName("username");
		hiddenUsername.setValue(userInfo.getUserName());
		verticalPanel.add(hiddenUsername);
		final Hidden hiddenSessionId = new Hidden();
		hiddenAppid.setTitle("sessionid");
		hiddenSessionId.setName("sessionid");
		hiddenSessionId.setValue(sessionId);
		verticalPanel.add(hiddenSessionId);

		final Hidden hiddenCommand = new Hidden();
		hiddenCommand.setTitle("command");
		hiddenCommand.setValue("SUBMIT_REPORT");
		hiddenCommand.setName("command");
		verticalPanel.add(hiddenCommand);
		hiddenToolID = new Hidden();
		hiddenToolID.setTitle("toolid");
		hiddenToolID.setName("toolid");
		verticalPanel.add(hiddenToolID);

		hiddenToolRisk = new Hidden();
		hiddenToolRisk.setTitle("toolrisk");
		hiddenToolRisk.setName("toolrisk");
		verticalPanel.add(hiddenToolRisk);

		final Grid grid = new Grid(5, 2);
		//grid.setTitle("grid");
		grid.setCellPadding(5);
		grid.setStyleName("grid");
		verticalPanel.add(grid);
		grid.setHeight("210px");
		verticalPanel.setCellVerticalAlignment(grid,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(grid,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellWidth(grid, "100%");
		final Label userLabel = new Label("User:");
		userLabel.getElement().setAttribute("for", "analyst-textbox");

		userLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(0, 0, userLabel);
		grid.getCellFormatter().setWidth(0, 0, "300px");
		userLabel.setSize("96px", "22px");
		final TextBox analystTextBox = new TextBox();
		analystTextBox.getElement().setId("analyst-textbox");

		analystTextBox.setName("User");
		analystTextBox.setTitle("User is non-editable");
		analystTextBox.setName("User is non-editable");
		analystTextBox.setAlignment(TextAlignment.LEFT);
		analystTextBox.setText(userInfo.getUserName());
		analystTextBox.setEnabled(false);
		// analystTextBox.setReadOnly(true);
		grid.setWidget(0, 1, analystTextBox);
		grid.getCellFormatter().setHeight(0, 1, "18px");
		grid.getCellFormatter().setWidth(0, 1, "200px");
		grid.getCellFormatter().setStyleName(0, 1, "reportUploadWidget");
		analystTextBox.setSize("220px", "18px");
		final Label appIdLabel = new Label("App ID: ");
		appIdLabel.getElement().setAttribute("for", "app-id-textbox");

		appIdLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(1, 0, appIdLabel);
		grid.getCellFormatter().setWidth(1, 0, "300px");
		grid.getCellFormatter().setHeight(1, 1, "18px");
		grid.getCellFormatter().setWidth(1, 1, "300px");
		final TextBox appIdTextBox = new TextBox();
		appIdTextBox.getElement().setId("app-id-textbox");

		appIdTextBox.setName("App ID");
		appIdTextBox.setTitle("App ID is non-editable");
		analystTextBox.setName("App ID is non-editable");
		appIdTextBox.setAlignment(TextAlignment.LEFT);
		appIdTextBox.setText(appid);
		appIdTextBox.setEnabled(false);
		// appIdTextBox.setReadOnly(true);
		grid.setWidget(1, 1, appIdTextBox);
		grid.getCellFormatter().setStyleName(1, 1, "reportUploadWidget");
		appIdTextBox.setSize("220px", "18px");
		final Label toolNameLabel = new Label("Tool: ");
		toolNameLabel.getElement().setAttribute("for", "tool-name-combobox");

		toolNameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(2, 0, toolNameLabel);
		toolNameLabel.setWidth("90px");
		grid.getCellFormatter().setWidth(2, 1, "200px");
		toolNamesComboBox = new ListBox();
		toolNamesComboBox.getElement().setId("tool-name-combobox");

		toolNamesComboBox.setName("Tool");
		toolNamesComboBox.setTitle("Select tool for uploaded report");
		grid.setWidget(2, 1, toolNamesComboBox);
		grid.getCellFormatter().setHeight(2, 1, "18px");
		grid.getCellFormatter().setStyleName(2, 1, "reportUploadWidget");
		toolNamesComboBox.setSize("231px", "22px");
		statusLabel = new HTML("");
		
		// Add tools to toolNamesComboBox
		String submitterRoleStr = userInfo.getRoleAndOrgMembership();
		Role submitterRole = null;
		try {
			submitterRole = Role.getRole(submitterRoleStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Set new list with only permitted tools
		for (int i = 0; i < tools.size(); i++) {
			ToolInfoGwt tool = tools.get(i);
			if (tool.getOs().equals(os.name())) {
				
				/* PUT YOUR SPECIFIC POLICY HERE FOR PERMITTING
				 *  TOOL REPORT TO BE UPLOADED */
				
				/*
				 * THE FOLLOWING SHOULD MATCH THE POLICIES DEFINED IN YOUR
				 * AppVetServlet.submitReport()!
				 */
				
				if (tool.getType() == ToolType.SUMMARY) { // TODO: For AV3, SUMMARY was removed (now uses only REPORT)
					if (tool.getId().equals("androidsummary") || tool.getId().equals("iossummary")) {
						// Only ADMINs are permitted to submit summary reports
						if (submitterRole == Role.ADMIN){
							permittedToolReports.add(tool);
						}
					} else if (tool.getId().equals("golive")) {
						// Only ADMINs and ANALYSTs are permitted to submit GoLive reports
						if (submitterRole == Role.ADMIN || submitterRole == Role.ANALYST){
							permittedToolReports.add(tool);
						}
					} else if (tool.getId().equals("approval")) {
						// All users are permitted to submit Approval reports
						permittedToolReports.add(tool);
					} 
				} else if (tool.getType() == ToolType.AUDIT) {  // TODO: For AV3, AUDIT was removed (now uses only REPORT)
					// Only ADMINs and ANALYSTs can submit Audit reports
					if (submitterRole == Role.ADMIN || submitterRole == Role.ANALYST) {
						permittedToolReports.add(tool);
					}
				} else if (tool.getType() == ToolType.TESTTOOL || 
						tool.getType() == ToolType.REPORT) {
					// All users are permitted to submit TESTTOOL or REPORT reports
					permittedToolReports.add(tool);
				} 
			}
		}
		
		// Set tools in combo box
		for (int i = 0; i < permittedToolReports.size(); i++) {
			ToolInfoGwt tool = permittedToolReports.get(i);
			if (tool.getOs().equals(os.name())) {
				toolNamesComboBox.addItem(tool.getName());
			}
		}
				
		toolNamesComboBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				int i = toolNamesComboBox.getSelectedIndex();
				String selectedToolName = toolNamesComboBox.getItemText(i);
				
				for (int j = 0; j < permittedToolReports.size(); j++) {
					ToolInfoGwt selectedTool = permittedToolReports.get(j);
					if (selectedTool.getName().equals(selectedToolName)) {
						String reportFileType = selectedTool
								.getReportFileType();
						if (reportFileType == null) {
							log.warning("Report file type for " + selectedTool.getId() + " is null");
						} 
						
						String filter = "." + reportFileType;
						fileUpload.getElement().setAttribute("accept", filter);

						ToolType toolType = selectedTool.getType();
						
						/* PUT YOUR SPECIFIC POLICY HERE FOR 
						 * ALLOWING A TOOL RISK TO BE SET BY THE UPLOADER */

						if (toolType == ToolType.SUMMARY) {

							// SUMMARY reports should not display risk combo box
							if (toolRiskComboBox != null) {
								// Tool risk should not be shown for SUMMARY reports
								toolRiskComboBox.setVisible(false);
								riskLabel.setVisible(false);
							} else {
								log.warning("toolRiskComboBox for "  + selectedTool.getId() + " is null");
								if (riskLabel != null) {
									riskLabel.setVisible(false);
								}
							}
							
						} else {
							if (toolRiskComboBox != null) {
								toolRiskComboBox.setVisible(true);
								riskLabel.setVisible(true);
							} else {
								log.warning("toolRiskComboBox for "  + selectedTool.getId() +  " is null");
								if (riskLabel != null) {
									riskLabel.setVisible(false);
								}
							}
						}
						
//						String reportTemplateURL = selectedTool.getReportTemplateURL();
//						if (reportTemplateURL != null) {
//							// There is a report template available for download
//							statusLabel.setHTML(selectedToolName
//									+ " requires a " + reportFileType
//									+ " report. Download <a href=\"" + reportTemplateURL + "\" target=\"_blank\"><b>template</b>.</a>");
//						} else {
//							log.warning("Report template for " + selectedTool.getId() + " is null");
//							statusLabel.setHTML(selectedToolName
//									+ " requires a " + reportFileType
//									+ " report.");
//						}
						
						break;
					}
				}
			}
		});
		

		final Label lblReport = new Label("Report: ");
		lblReport.getElement().setAttribute("for", "report-upload-button");

		lblReport.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(3, 0, lblReport);
		grid.getCellFormatter().setWidth(3, 1, "200px");
		fileUpload = new FileUpload();
		fileUpload.getElement().setId("report-upload-button");
		Roles.getButtonRole().setAriaLabelProperty(fileUpload.getElement(), "Select File Button");

		fileUpload.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				submitButton.setEnabled(true);
			}
		});
		fileUpload.setName("fileupload");
		fileUpload.setTitle("Select report to upload");
		grid.setWidget(3, 1, fileUpload);
		grid.getCellFormatter().setHeight(3, 1, "18px");
		grid.getCellFormatter().setStyleName(3, 1, "reportUploadWidget");
		fileUpload.setSize("189px", "22px");

		riskLabel = new Label("Risk: ");
		riskLabel.getElement().setAttribute("for", "tool-risk-listbox");

		riskLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(4, 0, riskLabel);
		grid.getCellFormatter().setWidth(4, 1, "300px");
		grid.getCellFormatter().setHorizontalAlignment(1, 1,
				HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setVerticalAlignment(1, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setVerticalAlignment(2, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setVerticalAlignment(3, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setVerticalAlignment(4, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setVerticalAlignment(1, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(2, 1,
				HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setVerticalAlignment(2, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(3, 1,
				HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setVerticalAlignment(3, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(4, 1,
				HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setVerticalAlignment(4, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		toolRiskComboBox = new ListBox();
		toolRiskComboBox.getElement().setId("tool-risk-listbox");

		toolRiskComboBox.setName("Risk");
		toolRiskComboBox.setTitle("Select security risk");
		toolRiskComboBox.addItem("LOW");
		toolRiskComboBox.addItem("MODERATE");
		toolRiskComboBox.addItem("HIGH");
		grid.setWidget(4, 1, toolRiskComboBox);
		grid.getCellFormatter().setHeight(4, 1, "18px");
		grid.getCellFormatter().setStyleName(4, 1, "reportUploadWidget");
		toolRiskComboBox.setSize("231px", "22px");
		grid.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		
		// Set report type filter for first tool in list
		String selectedToolName = toolNamesComboBox.getItemText(0);
		
		// Set first tool in tool list
		for (int k = 0; k < permittedToolReports.size(); k++) {
			ToolInfoGwt tool3 = permittedToolReports.get(k);
			if (tool3.getName().equals(selectedToolName)) {
				
				String reportFileType = tool3.getReportFileType();
				String filter = "." + reportFileType;
				fileUpload.getElement().setAttribute("accept", filter);

				ToolType toolType = tool3.getType();
				
				/* PUT YOUR SPECIFIC POLICY HERE FOR 
				 * ALLOWING A TOOL RISK TO BE SET BY THE UPLOADER */

				if (toolType == ToolType.SUMMARY) {

					// SUMMARY reports should not display risk combo box
					if (toolRiskComboBox != null) {
						// Tool risk should not be shown for SUMMARY reports
						toolRiskComboBox.setVisible(false);
						riskLabel.setVisible(false);
					} else {
						log.warning("toolRiskComboBox for "  + tool3.getId() +  " is null");
						if (riskLabel != null) {
							riskLabel.setVisible(false);
						}
					}
					
				} else {
					if (toolRiskComboBox != null) {
						toolRiskComboBox.setVisible(true);
						riskLabel.setVisible(true);
					} else {
						log.warning("toolRiskComboBox for "  + tool3.getId() +  " is null");
						if (riskLabel != null) {
							riskLabel.setVisible(false);
						}
					}
				}
				
//				String reportTemplateURL = tool3.getReportTemplateURL();
//				if (reportTemplateURL != null) {
//					// There is a report template available for download
//					statusLabel.setHTML(selectedToolName
//							+ " requires a " + reportFileType
//							+ " report. Download <a href=\"" + reportTemplateURL + "\" target=\"_blank\"><b>template</b>.</a>");
//				} else {
//					statusLabel.setHTML(selectedToolName
//							+ " requires a " + reportFileType
//							+ " report.");
//				}
				break;
			}
		}
		

		// statusLabel = new Label("");
		dialogVPanel.add(statusLabel);
		statusLabel.setStyleName("submissionRequirementsLabel");
		verticalPanel.setCellWidth(statusLabel, "100%");
		verticalPanel.setCellVerticalAlignment(statusLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(statusLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		statusLabel.setHeight("20px");
		final HorizontalPanel horizontalButtonPanel = new HorizontalPanel();
		horizontalButtonPanel
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalButtonPanel.setStyleName("reportUploadButtonPanel");
		dialogVPanel.add(horizontalButtonPanel);
		dialogVPanel.setCellVerticalAlignment(horizontalButtonPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		dialogVPanel.setCellHorizontalAlignment(horizontalButtonPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		dialogVPanel.setCellWidth(horizontalButtonPanel, "100%");
		horizontalButtonPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalButtonPanel.setSize("210px", "");
		cancelButton = new PushButton("Cancel");
		Roles.getButtonRole().setAriaLabelProperty(cancelButton.getElement(), "Cancel Button");

		cancelButton.setTitle("Cancel");
		cancelButton.setStyleName("grayButton shadow");
		cancelButton.setHTML("Cancel");
		horizontalButtonPanel.add(cancelButton);
		cancelButton.setSize("70px", "18px");
		horizontalButtonPanel.setCellVerticalAlignment(cancelButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalButtonPanel.setCellHorizontalAlignment(cancelButton,
				HasHorizontalAlignment.ALIGN_CENTER);

		Label lblNewLabel = new Label("");
		horizontalButtonPanel.add(lblNewLabel);
		lblNewLabel.setWidth("50px");
		submitButton = new PushButton("Submit");
		Roles.getButtonRole().setAriaLabelProperty(submitButton.getElement(), "Submit Button");

		submitButton.setTitle("Submit");
		submitButton.setStyleName("greenButton shadow");
		submitButton.setEnabled(false);
		horizontalButtonPanel.add(submitButton);
		horizontalButtonPanel.setCellHorizontalAlignment(submitButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalButtonPanel.setCellVerticalAlignment(submitButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		submitButton.setSize("70px", "18px");
		verticalPanel.setCellWidth(horizontalButtonPanel, "100%");
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Set toolid first
				String toolID = null;
				ToolType toolType = null;
				int selectedToolNameIndex = toolNamesComboBox
						.getSelectedIndex();
				String selectedToolName = toolNamesComboBox
						.getValue(selectedToolNameIndex);
				for (int i = 0; i < permittedToolReports.size(); i++) {
					if (selectedToolName.equals(permittedToolReports.get(i).getName())) {
						toolID = permittedToolReports.get(i).getId();
						toolType = permittedToolReports.get(i).getType();
						break;
					}
				}

				String risk = null;
				if (toolType == ToolType.SUMMARY) {  // TODO: For AV3, SUMMARY was removed (now uses only REPORT)
					// SUMMARY reports only have a status of LOW (which is displayed as "AVAILABLE")
					risk = "AVAILABLE";
				} else {
					int selectedRiskIndex = toolRiskComboBox.getSelectedIndex();
					risk = toolRiskComboBox.getValue(selectedRiskIndex);
				}

				if (toolID != null) {
					hiddenToolID.setValue(toolID);
					hiddenToolRisk.setValue(risk);
					uploadReportForm.submit();
				}
			}
		});
		
	}
	
	/** This fixes focus for dialog boxes in Firefox and IE browsers */
	@Override
	public void show() {
	    super.show();
	    Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	toolNamesComboBox.setFocus(true);
	        }
	    });
	}
}
