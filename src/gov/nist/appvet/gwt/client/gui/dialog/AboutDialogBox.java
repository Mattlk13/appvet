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
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author steveq@nist.gov
 */
public class AboutDialogBox extends DialogBox {
	public final PushButton closeButton;
	public Label messageLabel = null;

	public AboutDialogBox(String version) {
		super(false, true);
		setGlassEnabled(true);
		setSize("376px", "209px");
		setAnimationEnabled(false);
		final String message = "Version " + version;
		final DockPanel dockPanel = new DockPanel();
		dockPanel.setStyleName("aboutDialogButtonPanel");
		dockPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.setWidget(dockPanel);
		dockPanel.setSize("348px", "195px");
		
		SimplePanel simplePanel_1 = new SimplePanel();
		dockPanel.add(simplePanel_1, DockPanel.CENTER);
		dockPanel.setCellVerticalAlignment(simplePanel_1, HasVerticalAlignment.ALIGN_BOTTOM);
		simplePanel_1.setHeight("96px");
		final VerticalPanel verticalPanel = new VerticalPanel();
		simplePanel_1.setWidget(verticalPanel);
		verticalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setSize("100%", "100%");
		Image appvetLogoAbout = new Image("../appvet_images/appvet_logo_main.png");
		appvetLogoAbout.setWidth("200px");
		appvetLogoAbout.setHeight("25px");
		//image.setTitle("AppVet Mobile App Vetting System");
		appvetLogoAbout.setAltText("Carwash AppVet");
		verticalPanel.add(appvetLogoAbout);
		verticalPanel.setCellVerticalAlignment(appvetLogoAbout, HasVerticalAlignment.ALIGN_BOTTOM);
		verticalPanel.setCellHorizontalAlignment(appvetLogoAbout,
				HasHorizontalAlignment.ALIGN_CENTER);
		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		verticalPanel.setCellWidth(horizontalPanel, "100%");
		horizontalPanel.setWidth("350px");
		messageLabel = new Label(message);
		horizontalPanel.add(messageLabel);
		horizontalPanel.setCellHorizontalAlignment(messageLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(messageLabel,
				HasVerticalAlignment.ALIGN_BOTTOM);
		messageLabel.setStyleName("");
		verticalPanel.setCellVerticalAlignment(messageLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(messageLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		messageLabel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		messageLabel.setSize("350px", "18px");
		
		SimplePanel simplePanel = new SimplePanel();
		dockPanel.add(simplePanel, DockPanel.SOUTH);
		dockPanel.setCellVerticalAlignment(simplePanel, HasVerticalAlignment.ALIGN_BOTTOM);
		simplePanel.setHeight("44px");
		closeButton = new PushButton("Close");
		simplePanel.setWidget(closeButton);
		closeButton.setSize("70px", "18px");
		closeButton.setStyleName("greenButton shadow");
	}
}
