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


import gov.nist.appvet.gwt.shared.ToolInfoGwt;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * @author steveq@nist.gov
 */
public class ToolAdapterEnabledEditDialogBox extends DialogBox {
	public PushButton cancelButton = null;
	public Label mainLabel = null;
	public PushButton okButton = null;
	public Label statusLabel = null;
	public List<String> allUserOrgLevels = null;
	RadioButton enableRadioButton = null;
	RadioButton disableRadioButton = null;

	public ToolAdapterEnabledEditDialogBox(ToolInfoGwt testTool, String disabledEnabledStr) {
		super(false, true);
		setWidth("298px");
		setAnimationEnabled(false);
		final VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.addStyleName("mainPanel");
		this.setWidget(mainPanel);
		mainPanel.setSize("270px", "157px");

		// Set label
		mainLabel = new Label("Enable or disable '" + testTool.getId() + "'");
		mainPanel.add(mainLabel);
		mainLabel.setDirection(Direction.LTR);
		mainPanel.setCellVerticalAlignment(mainLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		mainLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainLabel.setSize("", "32px");
		
		enableRadioButton = new RadioButton("group1", "Enable");
		enableRadioButton.setTitle("Enable tool");
		enableRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				okButton.setEnabled(true);
			}
		});
		enableRadioButton.setStyleName("h1");
		mainPanel.add(enableRadioButton);
		enableRadioButton.setSize("229px", "20px");
		
		disableRadioButton = new RadioButton("group1", "Disable");
		disableRadioButton.setTitle("Disable tool");
		disableRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				okButton.setEnabled(true);
			}
		});
		mainPanel.add(disableRadioButton);
		disableRadioButton.setHeight("20px");
		
		if (disabledEnabledStr.equals("disabled")) {
			enableRadioButton.setValue(false);
			disableRadioButton.setValue(true);
		} else if (disabledEnabledStr.equals("enabled")) {
			enableRadioButton.setValue(true);
			disableRadioButton.setValue(false);
		}

		final HorizontalPanel horizontalButtonPanel = new HorizontalPanel();
		horizontalButtonPanel.setStyleName("buttonPanel");
		mainPanel.add(horizontalButtonPanel);
		horizontalButtonPanel.setSize("270px", "50px");
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
		cancelButton.setTitle("Cancel");
		cancelButton.setStyleName("grayButton shadow");
		cancelButton.setHTML("Cancel");
		horizontalButtonPanel.add(cancelButton);
		horizontalButtonPanel.setCellHorizontalAlignment(cancelButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		cancelButton.setSize("70px", "18px");
		okButton = new PushButton("Ok");
		okButton.setTitle("Ok");
		okButton.setEnabled(false);
		okButton.setStyleName("greenButton shadow");
		horizontalButtonPanel.add(okButton);
		horizontalButtonPanel.setCellHorizontalAlignment(okButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		okButton.setSize("70px", "18px");

	}
	
	/** This fixes focus for dialog boxes in Firefox and IE browsers */
	@Override
	public void show() {
	    super.show();
	    Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	enableRadioButton.setFocus(true);
	        }
	    });
	}
}
