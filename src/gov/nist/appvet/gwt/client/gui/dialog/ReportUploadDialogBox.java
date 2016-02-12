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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	private static Logger log = Logger.getLogger("ReportUploadDialogBox");
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
	public ArrayList<ToolInfoGwt> permittedToolReports = new ArrayList<ToolInfoGwt>();

	public ReportUploadDialogBox(UserInfo userInfo, String sessionId,
			String appid, String servletURL, DeviceOS os,
			final ArrayList<ToolInfoGwt> tools) {
		super(false, true);
		
		setWidth("100%");
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
		grid.setTitle("grid");
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
		userLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		userLabel.setStyleName("reportUploadLabel");
		grid.setWidget(0, 0, userLabel);
		userLabel.setWidth("");
		final TextBox analystTextBox = new TextBox();
		analystTextBox.setName("User");
		analystTextBox.setTitle("User is non-editable");
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
		appIdLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(1, 0, appIdLabel);
		grid.getCellFormatter().setWidth(1, 0, "300px");
		grid.getCellFormatter().setHeight(1, 1, "18px");
		grid.getCellFormatter().setWidth(1, 1, "300px");
		final TextBox appIdTextBox = new TextBox();
		appIdTextBox.setName("App ID");
		appIdTextBox.setTitle("App ID is non-editable");
		appIdTextBox.setAlignment(TextAlignment.LEFT);
		appIdTextBox.setText(appid);
		appIdTextBox.setEnabled(false);
		// appIdTextBox.setReadOnly(true);
		grid.setWidget(1, 1, appIdTextBox);
		grid.getCellFormatter().setStyleName(1, 1, "reportUploadWidget");
		appIdTextBox.setSize("220px", "18px");
		final Label toolNameLabel = new Label("Tool: ");
		toolNameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(2, 0, toolNameLabel);
		toolNameLabel.setWidth("90px");
		grid.getCellFormatter().setWidth(2, 1, "200px");
		toolNamesComboBox = new ListBox();
		toolNamesComboBox.setName("Tool");
		toolNamesComboBox.setTitle("Select tool for uploaded report");
		grid.setWidget(2, 1, toolNamesComboBox);
		grid.getCellFormatter().setHeight(2, 1, "18px");
		grid.getCellFormatter().setStyleName(2, 1, "reportUploadWidget");
		toolNamesComboBox.setSize("231px", "22px");
		statusLabel = new HTML("");
		
		
		// Add tools to toolNamesComboBox. Note only ADMINS can submit
		// SUMMARY reports while only ADMINs and all ANALYSTs can submit
		// final determination reports (AUDITs).
		String roleStr = userInfo.getRole();
		if (roleStr == null) 
			log.severe("roleStr is null");
		Role role = Role.getRole(roleStr);
		if (role == null)
			log.severe("role is null");
		

		// Set new list with only permitted tools
		//log.info("tools size: " + tools.size());
		for (int i = 0; i < tools.size(); i++) {
			ToolInfoGwt tool = tools.get(i);
			if (tool.getType() == ToolType.SUMMARY) {
				if (role == Role.ADMIN){
					permittedToolReports.add(tool);
				}
				
			} else if (tool.getType() == ToolType.AUDIT) {
				if (role == Role.ADMIN || role == Role.ANALYST || role == Role.ORG_ANALYST || role == Role.DEPT_ANALYST) {
					permittedToolReports.add(tool);

				}
				
			} else if (tool.getType() == ToolType.TESTTOOL) {
				permittedToolReports.add(tool);

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
							log.warning("Report file type is null");
						} 
						
						String filter = "." + reportFileType;
						fileUpload.getElement().setAttribute("accept", filter);

						ToolType toolType = selectedTool.getType();

						if (toolType == ToolType.SUMMARY) {
							
							if (toolRiskComboBox != null) {
								toolRiskComboBox.setVisible(false);
								statusLabel.setText(selectedToolName
										+ " requires a " + reportFileType
										+ " report.");
								
							} else {
								log.warning("toolRiskComboBox is null");
							}
							break;
						} else if (toolType == ToolType.AUDIT) {
							
							if (toolRiskComboBox != null) {
								toolRiskComboBox.setVisible(true);
								String reportTemplateURL = selectedTool.getReportTemplateURL();
								//log.info("Report template URL: " + reportTemplateURL);
								if (reportTemplateURL != null) {
									// There is a report template available for download
									statusLabel.setHTML(selectedToolName
											+ " requires a " + reportFileType
											+ " report. Download <a href=\"" + reportTemplateURL + "\" target=\"_blank\"><b>template</b>.</a>");
								} else {
									statusLabel.setHTML(selectedToolName
											+ " requires a " + reportFileType
											+ " report.");
								}
								
							} else {
								log.warning("toolRiskComboBox is null");
							}
							break;
						} else {
							if (toolRiskComboBox != null) {
								toolRiskComboBox.setVisible(true);
								statusLabel.setText(selectedToolName
										+ " requires a " + reportFileType
										+ " report.");
							} else {
								log.warning("toolRiskComboBox is null");
							}
							break;
						}
					}
				}
			}
		});
		

		final Label lblReport = new Label("Report: ");
		lblReport.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(3, 0, lblReport);
		grid.getCellFormatter().setWidth(3, 1, "200px");
		fileUpload = new FileUpload();
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

		final Label riskLabel = new Label("Risk: ");
		riskLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(4, 0, riskLabel);
		grid.getCellFormatter().setHorizontalAlignment(4, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
		grid.getCellFormatter().setWidth(4, 1, "300px");
		grid.getCellFormatter().setHorizontalAlignment(1, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
		grid.getCellFormatter().setHorizontalAlignment(1, 1,
				HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setHorizontalAlignment(2, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
		grid.getCellFormatter().setVerticalAlignment(1, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setVerticalAlignment(2, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(3, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
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
		grid.getCellFormatter().setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		
		// Set report type filter for first tool in list
		String selectedToolName = toolNamesComboBox.getItemText(0);
		
		
		for (int k = 0; k < permittedToolReports.size(); k++) {
			ToolInfoGwt tool3 = permittedToolReports.get(k);
			if (tool3.getName().equals(selectedToolName)) {
				String reportFileType = tool3.getReportFileType();
				String filter = "." + reportFileType;
				fileUpload.getElement().setAttribute("accept", filter);

				ToolType toolType = tool3.getType();

				if (toolType == ToolType.SUMMARY) {

					if (toolRiskComboBox != null) {
						//toolRiskComboBox.setEnabled(false);
						toolRiskComboBox.setVisible(false);
						statusLabel.setText(selectedToolName
								+ " requires a " + reportFileType
								+ " report.");
					} else {
						log.warning("toolRiskComboBox is null");
					}
					break;
					
				} else if (toolType == ToolType.AUDIT) {
					
					if (toolRiskComboBox != null) {
						toolRiskComboBox.setVisible(true);
						String reportTemplateURL = tool3.getReportTemplateURL();
						if (reportTemplateURL != null) {
							// There is a report template available for download
							statusLabel.setHTML(selectedToolName
									+ " requires a " + reportFileType
									+ " report. Download <a href=\"" + reportTemplateURL + "\" target=\"_blank\"><b>template</b>.</a>");
						} else {
							statusLabel.setHTML(selectedToolName
									+ " requires a " + reportFileType
									+ " report.");
						}
						
					} else {
						log.warning("toolRiskComboBox is null");
					}
					break;

				} else {
					
					if (toolRiskComboBox != null) {
						toolRiskComboBox.setVisible(true);
						statusLabel.setText(selectedToolName
								+ " requires a " + reportFileType
								+ " report.");
					} else {
						log.warning("toolRiskComboBox is null");
					}
					
					break;
				}
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
				if (toolType == ToolType.SUMMARY) {
					// SUMMARY reports only have status of LOW (which is displayed as "AVAILABLE")
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
}
