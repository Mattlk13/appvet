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

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.SimpleRadioButton;

/**
 * Yes/No (i.e., OK/Cancel)  
 * @author steveq@nist.gov
 */
public class SetAlertDialogBox extends DialogBox {
	public PushButton okButton = null;
	public PushButton cancelButton = null;
	public SimpleRadioButton alertNormalRadioButton = null;
	public SimpleRadioButton alertWarningRadioButton = null;
	public SimpleRadioButton alertCriticalRadioButton = null;
	public TextArea alertTextArea = null;

	public SetAlertDialogBox() {
		super(false, true);
		setSize("", "200px");
		setAnimationEnabled(false);
		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName("dialogVPanel");
		verticalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.setWidget(verticalPanel);
		verticalPanel.setSize("290px", "");
		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		verticalPanel_1.setStyleName("messagePanel");
		verticalPanel.add(verticalPanel_1);
		verticalPanel_1.setWidth("320px");
		verticalPanel.setCellWidth(verticalPanel_1, "100%");
		final SimplePanel simplePanel = new SimplePanel();
		verticalPanel_1.add(simplePanel);
		verticalPanel_1.setCellWidth(simplePanel, "100%");
		simplePanel.setHeight("20px");
		
		DockPanel dockPanel = new DockPanel();
		verticalPanel_1.add(dockPanel);
		dockPanel.setWidth("");
		
		Grid grid = new Grid(3, 2);
		dockPanel.add(grid, DockPanel.WEST);
		grid.setWidth("100%");
		
		alertNormalRadioButton = new SimpleRadioButton("group1");
		alertNormalRadioButton.setChecked(true);
		grid.setWidget(0, 0, alertNormalRadioButton);
		
		Label lblNormal = new Label("Normal");
		lblNormal.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(0, 1, lblNormal);
		
		alertWarningRadioButton = new SimpleRadioButton("group1");
		grid.setWidget(1, 0, alertWarningRadioButton);
		
		Label lblNewLabel = new Label("Warning");
		lblNewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(1, 1, lblNewLabel);
		
		alertCriticalRadioButton = new SimpleRadioButton("group1");
		grid.setWidget(2, 0, alertCriticalRadioButton);
		
		Label lblNewLabel_1 = new Label("Critical");
		lblNewLabel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(2, 1, lblNewLabel_1);
		grid.getCellFormatter().setVerticalAlignment(2, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		grid.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		
		alertTextArea = new TextArea();
		alertTextArea.setStyleName("appvetTextArea");
		dockPanel.add(alertTextArea, DockPanel.CENTER);
		alertTextArea.setSize("233px", "100%");
		final SimplePanel simplePanel_2 = new SimplePanel();
		verticalPanel_1.add(simplePanel_2);
		verticalPanel_1.setCellWidth(simplePanel_2, "100%");
		simplePanel_2.setHeight("20px");
		final HorizontalPanel horizontalButtonPanel = new HorizontalPanel();
		horizontalButtonPanel.setStyleName("buttonPanel");
		horizontalButtonPanel
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalButtonPanel.setSpacing(5);
		horizontalButtonPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.add(horizontalButtonPanel);
		verticalPanel.setCellVerticalAlignment(horizontalButtonPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalButtonPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalButtonPanel.setWidth("320px");
		verticalPanel.setCellWidth(horizontalButtonPanel, "100%");
		cancelButton = new PushButton("No");
		cancelButton.setHTML("Cancel");
		horizontalButtonPanel.add(cancelButton);
		cancelButton.setSize("70px", "18px");
		horizontalButtonPanel.setCellVerticalAlignment(cancelButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalButtonPanel.setCellHorizontalAlignment(cancelButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		okButton = new PushButton("Yes");
		okButton.setHTML("Ok");
		horizontalButtonPanel.add(okButton);
		horizontalButtonPanel.setCellHorizontalAlignment(okButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalButtonPanel.setCellVerticalAlignment(okButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		okButton.setSize("70px", "18px");
	}
}
