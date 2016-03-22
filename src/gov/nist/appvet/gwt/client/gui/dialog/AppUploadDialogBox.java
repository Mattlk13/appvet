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
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author steveq@nist.gov
 */
public class AppUploadDialogBox extends DialogBox {
	public PushButton cancelButton = null;
	public FormPanel uploadAppFileForm = null;
	public Label mainLabel = null;
	public FileUpload fileUpload = null;
	public PushButton submitButton = null;
	public Hidden hiddenAppPackage = null;
	public Hidden hiddenAppVersion = null;
	public Hidden hiddenAppOS = null;
	private Logger log = Logger.getLogger("AppUploadDialogBox");
	public Label statusLabel = null;

	public AppUploadDialogBox(String sessionId, String servletURL) {
		super(false, true);
		setWidth("100%");
		setAnimationEnabled(false);
		final VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.addStyleName("mainPanel");
		this.setWidget(mainPanel);
		mainPanel.setSize("114px", "100px");
		
		SimplePanel simplePanel = new SimplePanel();
		simplePanel.setStyleName("reportUploadPanel");
		mainPanel.add(simplePanel);
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSpacing(5);
		simplePanel.setWidget(verticalPanel);
		verticalPanel.setSize("100%", "91px");
		// Set label
		mainLabel = new Label("Select an Android (.apk) or iOS (.ipa) file:");
		verticalPanel.add(mainLabel);
		mainLabel.setDirection(Direction.LTR);
		mainPanel.setCellVerticalAlignment(mainLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		mainLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainLabel.setSize("366px", "18px");
		uploadAppFileForm = new FormPanel();
	
		verticalPanel.add(uploadAppFileForm);
		uploadAppFileForm.setAction(servletURL);
		uploadAppFileForm.setMethod(FormPanel.METHOD_POST);
		uploadAppFileForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		final VerticalPanel hiddenParamsPanel = new VerticalPanel();
		hiddenParamsPanel
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hiddenParamsPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		uploadAppFileForm.setWidget(hiddenParamsPanel);
		hiddenParamsPanel.setSize("", "");
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
		
		fileUpload.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				submitButton.setEnabled(true);
			}
		});
		hiddenParamsPanel.add(fileUpload);
		fileUpload.setWidth("360px");
		hiddenParamsPanel.setCellVerticalAlignment(fileUpload,
				HasVerticalAlignment.ALIGN_MIDDLE);
		hiddenParamsPanel.setCellHorizontalAlignment(fileUpload,
				HasHorizontalAlignment.ALIGN_CENTER);
		fileUpload.setTitle("Select app file to upload");
		fileUpload.setName("fileupload");
		fileUpload.getElement().setAttribute("accept", ".apk,.ipa");
		
		statusLabel = new Label("");
		mainPanel.add(statusLabel);
		mainPanel.setCellHeight(statusLabel, "30px");
		statusLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		statusLabel.setStyleName("gwt-StatusLabel");
		final HorizontalPanel horizontalButtonPanel = new HorizontalPanel();
		horizontalButtonPanel.setStyleName("buttonPanel");
		mainPanel.add(horizontalButtonPanel);
		horizontalButtonPanel.setHeight("50px");
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
		cancelButton.setHTML("Cancel");
		horizontalButtonPanel.add(cancelButton);
		horizontalButtonPanel.setCellHorizontalAlignment(cancelButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		cancelButton.setSize("70px", "18px");
		Label label = new Label("");
		horizontalButtonPanel.add(label);
		label.setSize("30px", "");
		submitButton = new PushButton("Submit");
		submitButton.setEnabled(false);
		horizontalButtonPanel.add(submitButton);
		horizontalButtonPanel.setCellHorizontalAlignment(submitButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		submitButton.setSize("70px", "18px");
	}
}
