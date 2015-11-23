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

import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.gwt.shared.UserInfoGwt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author steveq@nist.gov
 */
public class UserAcctDialogBox extends DialogBox {
	public UserInfoGwt userInfoGwt = null;
	public PushButton okButton = null;
	public PushButton updateButton = null;
	public TextBox lastNameTextBox = null;
	public TextBox firstNameTextBox = null;
	public TextBox userIdTextBox = null;
	public PasswordTextBox currentPasswordTextBox = null;
	public PasswordTextBox password1TextBox = null;
	public PasswordTextBox password2TextBox = null;
	public TextBox organizationTextBox = null;
	public TextBox departmentTextBox = null;
	public TextBox emailTextBox = null;
	public ToolAuthParamDialogBox toolAuthParamDialogBox = null;
	private static Logger log = Logger.getLogger("UserAcctDialogBox");
	
	public UserAcctDialogBox(final ConfigInfoGwt configInfoGwt) {
		setSize("400px", "406px");
		this.userInfoGwt = configInfoGwt.getUserInfo();
		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSize("100%", "100%");
		verticalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setStyleName("verticalPanel");
		verticalPanel.setSpacing(5);
		final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel_1
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel_1);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_1,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setWidth("");
		final Label lblUserId = new Label("User Name:");
		lblUserId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_1.add(lblUserId);
		horizontalPanel_1.setCellHorizontalAlignment(lblUserId,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellWidth(lblUserId, "30%");
		lblUserId.setWidth("170px");
		horizontalPanel_1.setCellVerticalAlignment(lblUserId,
				HasVerticalAlignment.ALIGN_MIDDLE);
		final TextBox userIdTextBox_1 = new TextBox();
		userIdTextBox_1.setEnabled(false);
		userIdTextBox_1.setAlignment(TextAlignment.LEFT);
		userIdTextBox_1.setText(userInfoGwt.getUserName());
		userIdTextBox_1.setReadOnly(true);
		horizontalPanel_1.add(userIdTextBox_1);
		horizontalPanel_1.setCellHorizontalAlignment(userIdTextBox_1,
				HasHorizontalAlignment.ALIGN_CENTER);
		userIdTextBox_1.setSize("180px", "20px");
		horizontalPanel_1.setCellVerticalAlignment(userIdTextBox_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellWidth(userIdTextBox_1, "70%");
		final HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		horizontalPanel_2
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel_2);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_2,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_2,
				HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblNewLabel = new Label("Last Name: ");
		horizontalPanel_2.add(lblNewLabel);
		horizontalPanel_2.setCellHorizontalAlignment(lblNewLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(lblNewLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblNewLabel.setWidth("170px");
		horizontalPanel_2.setCellWidth(lblNewLabel, "50%");
		lblNewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lastNameTextBox = new TextBox();
		lastNameTextBox.setEnabled(false);
		lastNameTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_2.add(lastNameTextBox);
		horizontalPanel_2.setCellHorizontalAlignment(lastNameTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(lastNameTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setCellWidth(lastNameTextBox, "50%");
		lastNameTextBox.setText(userInfoGwt.getLastName());
		lastNameTextBox.setSize("180px", "20px");
		final HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		horizontalPanel_3
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.add(horizontalPanel_3);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_3,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_3,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblNewLabel_1 = new Label("First Name: ");
		horizontalPanel_3.add(lblNewLabel_1);
		horizontalPanel_3.setCellHorizontalAlignment(lblNewLabel_1,
				HasHorizontalAlignment.ALIGN_CENTER);
		lblNewLabel_1.setWidth("170px");
		horizontalPanel_3.setCellWidth(lblNewLabel_1, "50%");
		horizontalPanel_3.setCellVerticalAlignment(lblNewLabel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblNewLabel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		firstNameTextBox = new TextBox();
		firstNameTextBox.setEnabled(false);
		firstNameTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_3.add(firstNameTextBox);
		horizontalPanel_3.setCellHorizontalAlignment(firstNameTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3.setCellVerticalAlignment(firstNameTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		firstNameTextBox.setText(userInfoGwt.getFirstName());
		firstNameTextBox.setSize("180px", "20px");
		
		HorizontalPanel horizontalPanel_10 = new HorizontalPanel();
		horizontalPanel_10.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_10.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.add(horizontalPanel_10);
		
		Label label_1 = new Label("Organization: ");
		label_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_10.add(label_1);
		label_1.setWidth("170px");
		
		organizationTextBox = new TextBox();
		organizationTextBox.setText(userInfoGwt.getOrganization());
		organizationTextBox.setEnabled(false);
		organizationTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_10.add(organizationTextBox);
		organizationTextBox.setSize("180px", "20px");
		final HorizontalPanel horizontalPanel_4 = new HorizontalPanel();
		horizontalPanel_4
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_4
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel_4);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_4,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_4,
				HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblDepartment = new Label("Department");
		horizontalPanel_4.add(lblDepartment);
		horizontalPanel_4.setCellVerticalAlignment(lblDepartment,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_4.setCellHorizontalAlignment(lblDepartment,
				HasHorizontalAlignment.ALIGN_CENTER);
		lblDepartment.setWidth("170px");
		horizontalPanel_4.setCellWidth(lblDepartment, "50%");
		lblDepartment
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		departmentTextBox = new TextBox();
		departmentTextBox.setText(userInfoGwt.getDepartment());
		departmentTextBox.setEnabled(false);
		departmentTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_4.add(departmentTextBox);
		horizontalPanel_4.setCellHorizontalAlignment(departmentTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_4.setCellVerticalAlignment(departmentTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_4.setCellWidth(departmentTextBox, "50%");
		departmentTextBox.setSize("180px", "20px");
		
		final HorizontalPanel horizontalPanel_5 = new HorizontalPanel();
		horizontalPanel_5
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_5
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel_5);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_5,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_5,
				HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblEmail = new Label("Email: ");
		horizontalPanel_5.add(lblEmail);
		horizontalPanel_5.setCellVerticalAlignment(lblEmail,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setCellHorizontalAlignment(lblEmail,
				HasHorizontalAlignment.ALIGN_CENTER);
		lblEmail.setWidth("170px");
		horizontalPanel_5.setCellWidth(lblEmail, "50%");
		lblEmail.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		emailTextBox = new TextBox();
		emailTextBox.setEnabled(false);
		emailTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_5.add(emailTextBox);
		horizontalPanel_5.setCellHorizontalAlignment(emailTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_5.setCellVerticalAlignment(emailTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setCellWidth(emailTextBox, "50%");
		emailTextBox.setText(userInfoGwt.getEmail());
		emailTextBox.setSize("180px", "20px");
		final Label lblNewLabel_2 = new Label("Change Password");
		lblNewLabel_2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lblNewLabel_2.setWidth("340px");
		final HorizontalPanel horizontalPanel_6 = new HorizontalPanel();
		horizontalPanel_6
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel_6);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_6,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_6,
				HasHorizontalAlignment.ALIGN_CENTER);
		final Label lblNewLabel_3 = new Label("New Password: ");
		horizontalPanel_6.add(lblNewLabel_3);
		lblNewLabel_3.setWidth("170px");
		horizontalPanel_6.setCellVerticalAlignment(lblNewLabel_3,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_6.setCellHorizontalAlignment(lblNewLabel_3,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6.setCellWidth(lblNewLabel_3, "50%");
		lblNewLabel_3.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		password1TextBox = new PasswordTextBox();
		password1TextBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateButton.setEnabled(true);
			}
		});
		password1TextBox.setEnabled(true);
		password1TextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_6.add(password1TextBox);
		horizontalPanel_6.setCellHorizontalAlignment(password1TextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6.setCellVerticalAlignment(password1TextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_6.setCellWidth(password1TextBox, "50%");
		password1TextBox.setSize("180px", "20px");
		final HorizontalPanel horizontalPanel_7 = new HorizontalPanel();
		horizontalPanel_7
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_7
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel_7);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_7,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_7,
				HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblNewLabel_4 = new Label("New Password (again): ");
		horizontalPanel_7.add(lblNewLabel_4);
		horizontalPanel_7.setCellHorizontalAlignment(lblNewLabel_4,
				HasHorizontalAlignment.ALIGN_CENTER);
		lblNewLabel_4.setWidth("170px");
		horizontalPanel_7.setCellWidth(lblNewLabel_4, "50%");
		horizontalPanel_7.setCellVerticalAlignment(lblNewLabel_4,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblNewLabel_4.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		password2TextBox = new PasswordTextBox();
		password2TextBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateButton.setEnabled(true);
			}
		});
		password2TextBox.setEnabled(true);
		password2TextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_7.add(password2TextBox);
		horizontalPanel_7.setCellHorizontalAlignment(password2TextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_7.setCellWidth(password2TextBox, "50%");
		password2TextBox.setSize("180px", "20px");
		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setSize("200px", "50px");
		okButton = new PushButton("OK");
		okButton.setHTML("OK");
		horizontalPanel.add(okButton);
		horizontalPanel.setCellHorizontalAlignment(okButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(okButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		okButton.setSize("70px", "18px");
		verticalPanel.setCellVerticalAlignment(horizontalPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		final SimplePanel simplePanel = new SimplePanel();
		simplePanel.setStyleName("userFormPanel");
		simplePanel.setWidget(verticalPanel);
		HorizontalPanel horizontalPanel_9 = new HorizontalPanel();
		horizontalPanel_9
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_9
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		verticalPanel.add(horizontalPanel_9);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_9,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_9,
				HasHorizontalAlignment.ALIGN_CENTER);
		Label label = new Label("");
		label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_9.add(label);
		label.setWidth("170px");
		updateButton = new PushButton("Update Password");
		horizontalPanel_9.add(updateButton);
		horizontalPanel_9.setCellVerticalAlignment(updateButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_9.setCellHorizontalAlignment(updateButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		updateButton.setEnabled(false);
		horizontalPanel.setCellVerticalAlignment(updateButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(updateButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		updateButton.setSize("180px", "18px");
		HorizontalPanel horizontalPanel_8 = new HorizontalPanel();
		horizontalPanel_8
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel_8);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_8,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_8,
				HasVerticalAlignment.ALIGN_MIDDLE);
		Label toolAuthenticationLabel = new Label("Tool Authentication:");
		toolAuthenticationLabel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_8.add(toolAuthenticationLabel);
		horizontalPanel_8.setCellHorizontalAlignment(toolAuthenticationLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		toolAuthenticationLabel.setWidth("170px");
		PushButton toolAuthenticationButton = new PushButton("Edit Credentials");
		toolAuthenticationButton.setHTML("Edit Credentials");
		toolAuthenticationButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				getToolsAuthentication(configInfoGwt);
			}
		});
		horizontalPanel_8.add(toolAuthenticationButton);
		horizontalPanel_8.setCellHorizontalAlignment(toolAuthenticationButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		toolAuthenticationButton.setWidth("180px");
		horizontalPanel_8.setCellVerticalAlignment(toolAuthenticationButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		final DockPanel dockPanel = new DockPanel();
		dockPanel.setStyleName("gwt-DialogBox");
		setWidget(dockPanel);
		dockPanel.setSize("380px", "378px");
		dockPanel.add(simplePanel, DockPanel.CENTER);
		dockPanel.add(horizontalPanel, DockPanel.SOUTH);
		dockPanel.setCellVerticalAlignment(horizontalPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel.setCellHorizontalAlignment(horizontalPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
	}

	public void getToolsAuthentication(final ConfigInfoGwt configInfoGwt) {
		toolAuthParamDialogBox = new ToolAuthParamDialogBox(configInfoGwt);
		toolAuthParamDialogBox.setText("Tool Account Information");
		toolAuthParamDialogBox.center();
		toolAuthParamDialogBox.okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toolAuthParamDialogBox.hide();
			}
		});
	}
}
