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

import java.util.logging.Logger;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * @author steveq@nist.gov
 */
public class AppUploadDialogBox extends DialogBox {
	public FocusPanel focusPanel = null;
	public PushButton cancelButton = null;
	public FormPanel uploadAppFileForm = null;
	public FileUpload fileUpload = null;
	public RadioButton androidRadioButton = null;
	public RadioButton iosRadioButton = null;
	public PushButton submitButton = null;
	public Hidden hiddenAppPackage = null;
	public Hidden hiddenAppVersion = null;
	public Hidden hiddenAppOS = null;
	private Logger log = Logger.getLogger("AppUploadDialogBox");
	public Label statusLabel = null;

	public AppUploadDialogBox(String sessionId, String servletURL) {
		super(false, true);
		setWidth("389px");
		setAnimationEnabled(false);
		
		focusPanel = new FocusPanel();
		this.setWidget(focusPanel);
		focusPanel.setSize("361px", "163px");

		
		final VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.addStyleName("mainPanel");
		mainPanel.setSize("361px", "100px");
		
		focusPanel.add(mainPanel);
		
		Label selectLabel = new Label("Select Android (.apk) or iOS (.ipa) app:");
		selectLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainPanel.add(selectLabel);
		mainPanel.setCellVerticalAlignment(selectLabel, HasVerticalAlignment.ALIGN_BOTTOM);
		selectLabel.setSize("341px", "32px");
		uploadAppFileForm = new FormPanel();
		mainPanel.add(uploadAppFileForm);
		uploadAppFileForm.setWidth("");
		uploadAppFileForm.setAction(servletURL);
		uploadAppFileForm.setMethod(FormPanel.METHOD_POST);
		uploadAppFileForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		
		final VerticalPanel hiddenParamsPanel = new VerticalPanel();
		hiddenParamsPanel
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hiddenParamsPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		uploadAppFileForm.setWidget(hiddenParamsPanel);
		hiddenParamsPanel.setSize("100%", "");
		final Hidden hiddenCommand = new Hidden();
		hiddenCommand.setValue("SUBMIT_APP");
		hiddenCommand.setName("command");
		hiddenParamsPanel.add(hiddenCommand);
		hiddenCommand.setWidth("");
		final Hidden hiddenSessionId = new Hidden();
		hiddenSessionId.setName("sessionid");
		hiddenSessionId.setValue(sessionId);
		hiddenParamsPanel.add(hiddenSessionId);
		hiddenSessionId.setWidth("");
		fileUpload = new FileUpload();
		Roles.getButtonRole().setAriaLabelProperty(fileUpload.getElement(), "Select File Button");

		fileUpload.setTitle("Choose app to upload");
		hiddenParamsPanel.add(fileUpload);
		hiddenParamsPanel.setCellWidth(fileUpload, "100%");
		fileUpload.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				submitButton.setEnabled(true);
			}
		});
	
		fileUpload.setSize("354px", "23px");
		fileUpload.setTitle("Select app file to upload");
		fileUpload.setName("fileupload");
		fileUpload.getElement().setAttribute("accept", ".apk,.ipa");
		hiddenParamsPanel.setCellVerticalAlignment(fileUpload,
				HasVerticalAlignment.ALIGN_BOTTOM);
		hiddenParamsPanel.setCellHorizontalAlignment(fileUpload,
				HasHorizontalAlignment.ALIGN_CENTER);
		
		statusLabel = new Label("");
		statusLabel.setStyleName("statusLabel");
		hiddenParamsPanel.add(statusLabel);
		final HorizontalPanel horizontalButtonPanel = new HorizontalPanel();
		horizontalButtonPanel.setStyleName("buttonPanel");
		mainPanel.add(horizontalButtonPanel);
		mainPanel.setCellWidth(horizontalButtonPanel, "100%");
		horizontalButtonPanel.setSize("361px", "50px");
		horizontalButtonPanel
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.setCellVerticalAlignment(horizontalButtonPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.setCellHorizontalAlignment(horizontalButtonPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalButtonPanel.setSpacing(10);
		horizontalButtonPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cancelButton = new PushButton("Cancel");
		Roles.getButtonRole().setAriaLabelProperty(cancelButton.getElement(), "Cancel Button");

		cancelButton.setTitle("Cancel");
		cancelButton.setTabIndex(0);
		cancelButton.setStyleName("grayButton shadow");
		cancelButton.setHTML("Cancel");
		horizontalButtonPanel.add(cancelButton);
		horizontalButtonPanel.setCellWidth(cancelButton, "50%");
		horizontalButtonPanel.setCellHorizontalAlignment(cancelButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		cancelButton.setSize("70px", "18px");
		submitButton = new PushButton("Submit");
		Roles.getButtonRole().setAriaLabelProperty(submitButton.getElement(), "Submit Button");

		submitButton.setTitle("Submit");
		submitButton.setTabIndex(0);
		submitButton.setStyleName("greenButton shadow");
		submitButton.setEnabled(false);
		horizontalButtonPanel.add(submitButton);
		horizontalButtonPanel.setCellWidth(submitButton, "50%");
		horizontalButtonPanel.setCellHorizontalAlignment(submitButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		submitButton.setSize("70px", "18px");

	}
	
	/** This fixes focus for dialog boxes in Firefox and IE browsers */
	@Override
	public void show() {
	    super.show();
	    Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	focusPanel.setFocus(true);
	        }
	    });
	}
}
