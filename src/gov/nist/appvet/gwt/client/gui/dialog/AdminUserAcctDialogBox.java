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

import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserInfo;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
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
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimpleCheckBox;

/**
 * @author steveq@nist.gov
 */
public class AdminUserAcctDialogBox extends DialogBox {
	private Logger log = Logger.getLogger("AdminUserAcctDialogBox");
	public PushButton cancelButton = null;
	public PushButton submitButton = null;
	public PushButton reactivateButton = null;
	public TextBox lastNameTextBox = null;
	public TextBox firstNameTextBox = null;
	public TextBox userIdTextBox = null;
	public SimpleCheckBox changePasswordCheckBox = null;
	public PasswordTextBox password1TextBox = null;
	public PasswordTextBox password2TextBox = null;
	public TextBox emailTextBox = null;
	public boolean newUser = false;
	public Label passwordLabel = null;
	public Label passwordAgainLabel = null;
	private final DateTimeFormat dateTimeFormat = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm:ss");
	private MessageDialogBox messageDialogBox = null;
	public RadioButton adminRadioButton = null;
	public RadioButton toolRadioButton = null;
	public RadioButton analystRadioButton = null;
	public RadioButton userRadioButton = null;
	public PushButton editLevelsButton = null;
	public OrgLevelsDialogBox orgLevelsDialogBox = null;
	public TextBox orgMembershipTextBox = null;
	public List<String> allUsersOrgMemberships = null;
	public ConfigInfoGwt confInfo = null;
	public UserInfo selectedUser = null;

	@SuppressWarnings("deprecation")
	public AdminUserAcctDialogBox(final ConfigInfoGwt configInfo,
			final UserInfo selectedUser, boolean useSSO, List<String> allUsersOrgLevels) {
		log.info("trace a");
		confInfo = configInfo;
		if (selectedUser == null) {
			newUser = true;
		} else {
			log.info("trace b");

			this.selectedUser = selectedUser;
		}
		log.info("trace c");

		// Get orgs hierarchies
		if (allUsersOrgLevels == null) {
			log.warning("org hierarchies is null in acct");
		} else {
			allUsersOrgMemberships = allUsersOrgLevels;
		}
		log.info("trace d");

		setWidth("386px");
		log.info("trace d1");


		passwordAgainLabel = new Label("Password (again): ");
		
		passwordAgainLabel.getElement().setAttribute("for", "password2-textbox");
		

		log.info("trace e");

		passwordAgainLabel.setWidth("170px");
		passwordAgainLabel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setSize("403px", "50px");
		horizontalPanel.setStyleName("buttonPanelStyle");
		cancelButton = new PushButton("Cancel");
		cancelButton.setStyleName("grayButton shadow");
		Roles.getButtonRole().setAriaLabelProperty(cancelButton.getElement(), "Cancel Button");

		cancelButton.setTitle("Cancel");
		cancelButton.setHTML("Cancel");
		horizontalPanel.add(cancelButton);
		horizontalPanel.setCellHorizontalAlignment(cancelButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(cancelButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellWidth(cancelButton, "33%");

		cancelButton.setSize("70px", "18px");
		submitButton = new PushButton("Submit");
		submitButton.setStyleName("greenButton shadow");
		Roles.getButtonRole().setAriaLabelProperty(submitButton.getElement(), "Submit Button");
		log.info("trace f");

		submitButton.setTitle("Submit");
		log.info("trace fa");

		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			log.info("trace fb");

			submitButton.setEnabled(false);
		} else {
			log.info("trace fc");

			submitButton.setEnabled(true);
		}
		log.info("trace f1");

		horizontalPanel.add(submitButton);
		horizontalPanel.setCellHorizontalAlignment(submitButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(submitButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		submitButton.setSize("70px", "18px");
		horizontalPanel.setCellWidth(submitButton, "33%");
		log.info("trace f2");


		reactivateButton = new PushButton("Re-activate");
		reactivateButton.setStyleName("greenButton shadow");

		Roles.getButtonRole().setAriaLabelProperty(reactivateButton.getElement(), "Reactivate Button");
		// Only enable if DEACTIVATED
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			reactivateButton.setEnabled(true);
			reactivateButton.setFocus(true);
		} else {
			reactivateButton.setEnabled(false);
			//reactivateButton.setVisible(false);
		}
		log.info("trace f3");

		horizontalPanel.add(reactivateButton);
		horizontalPanel.setCellHorizontalAlignment(reactivateButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(reactivateButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		reactivateButton.setSize("70px", "18px");
		horizontalPanel.setCellWidth(reactivateButton, "33%");
		log.info("trace f4");

		final DockPanel dockPanel = new DockPanel();
		dockPanel.setStyleName("gwt-DialogBox");
		setWidget(dockPanel);
		dockPanel.setSize("386px", "");
		log.info("trace g");

		final VerticalPanel verticalPanel_1 = new VerticalPanel();
		verticalPanel_1.setSize("100%", "100%");
		verticalPanel_1.setSpacing(5);
		verticalPanel_1.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_1
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_1);
		horizontalPanel_1
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lastNameLabel = new Label("Last Name: ");
		lastNameLabel.getElement().setAttribute("for", "last-name-textbox");
		horizontalPanel_1.add(lastNameLabel);
		horizontalPanel_1.setCellHorizontalAlignment(lastNameLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellVerticalAlignment(lastNameLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lastNameLabel.setWidth("115px");
		horizontalPanel_1.setCellWidth(lastNameLabel, "50%");
		lastNameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lastNameTextBox = new TextBox();
		lastNameTextBox.getElement().setId("last-name-textbox");
		lastNameTextBox.setTitle("Last name");
		lastNameTextBox.setName("Last name");
		lastNameTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		lastNameTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_1.add(lastNameTextBox);
		horizontalPanel_1.setCellHorizontalAlignment(lastNameTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellVerticalAlignment(lastNameTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellWidth(lastNameTextBox, "50%");
		lastNameTextBox.setWidth("234px");
		final HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_2);
		horizontalPanel_2
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblNewLabel_1 = new Label("First Name: ");
		lastNameLabel.getElement().setAttribute("for", "first-name-textbox");
		log.info("trace h");

		horizontalPanel_2.add(lblNewLabel_1);
		lblNewLabel_1.setWidth("115px");
		horizontalPanel_2.setCellWidth(lblNewLabel_1, "50%");
		horizontalPanel_2.setCellVerticalAlignment(lblNewLabel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setCellHorizontalAlignment(lblNewLabel_1,
				HasHorizontalAlignment.ALIGN_CENTER);
		lblNewLabel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		firstNameTextBox = new TextBox();
		firstNameTextBox.getElement().setId("first-name-textbox");

		firstNameTextBox.setTitle("First name");
		firstNameTextBox.setName("First name");
		firstNameTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		firstNameTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_2.add(firstNameTextBox);
		horizontalPanel_2.setCellWidth(firstNameTextBox, "50%");
		horizontalPanel_2.setCellVerticalAlignment(firstNameTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setCellHorizontalAlignment(firstNameTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		firstNameTextBox.setWidth("234px");
		final HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_3);
		horizontalPanel_3
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblUserId = new Label("User ID:");
		lblUserId.getElement().setAttribute("for", "user-id-textbox");

		horizontalPanel_3.add(lblUserId);
		lblUserId.setWidth("115px");
		horizontalPanel_3.setCellVerticalAlignment(lblUserId,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHorizontalAlignment(lblUserId,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3.setCellWidth(lblUserId, "50%");
		lblUserId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		userIdTextBox = new TextBox();
		userIdTextBox.getElement().setId("user-id-textbox");

		userIdTextBox.setTitle("Username");
		userIdTextBox.setName("Username");
		userIdTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		userIdTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_3.add(userIdTextBox);
		horizontalPanel_3.setCellWidth(userIdTextBox, "50%");
		horizontalPanel_3.setCellVerticalAlignment(userIdTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHorizontalAlignment(userIdTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		userIdTextBox.setWidth("234px");
		log.info("trace i");

		final HorizontalPanel horizontalPanel_7 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_7);
		horizontalPanel_7
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_7
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblEmail = new Label("Email: ");
		lblUserId.getElement().setAttribute("for", "email-textbox");

		horizontalPanel_7.add(lblEmail);
		lblEmail.setWidth("115px");
		horizontalPanel_7.setCellVerticalAlignment(lblEmail,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_7.setCellWidth(lblEmail, "50%");
		lblEmail.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		emailTextBox = new TextBox();
		emailTextBox.getElement().setId("email-textbox");

		emailTextBox.setTitle("Email");
		emailTextBox.setName("Email");
		emailTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		emailTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_7.add(emailTextBox);
		horizontalPanel_7.setCellVerticalAlignment(emailTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_7.setCellHorizontalAlignment(emailTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_7.setCellWidth(emailTextBox, "50%");
		emailTextBox.setWidth("234px");
		final HorizontalPanel horizontalPanel_8 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_8);
		verticalPanel_1.setCellHorizontalAlignment(horizontalPanel_8,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_1.setCellVerticalAlignment(horizontalPanel_8,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_1.setCellWidth(horizontalPanel_8, "100%");
		horizontalPanel_8
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblRole = new Label("Role: ");
		lblUserId.getElement().setAttribute("for", "admin-radiobutton");

		horizontalPanel_8.add(lblRole);
		horizontalPanel_8.setCellWidth(lblRole, "150px");
		lblRole.setWidth("50px");
		horizontalPanel_8.setCellVerticalAlignment(lblRole,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblRole.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		adminRadioButton = new RadioButton("buttonGroup", "Admin");
		adminRadioButton.getElement().setId("admin-radiobutton");

		adminRadioButton.setTitle("Administrator role");
		adminRadioButton.setName("Administrator role");
		horizontalPanel_8.add(adminRadioButton);
		horizontalPanel_8.setCellHorizontalAlignment(adminRadioButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_8.setCellVerticalAlignment(adminRadioButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		adminRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				editLevelsButton.setEnabled(false);
				orgMembershipTextBox.setText("");
			}
		});
		adminRadioButton.setWidth("140px");
		log.info("trace j");

		toolRadioButton = new RadioButton("buttonGroup", "Tool");
		toolRadioButton.setTitle("Test tool role");
		toolRadioButton.setName("Test tool role");
		horizontalPanel_8.add(toolRadioButton);
		horizontalPanel_8.setCellHorizontalAlignment(toolRadioButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_8.setCellVerticalAlignment(toolRadioButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		analystRadioButton = new RadioButton("buttonGroup", "Analyst");
		analystRadioButton.setTitle("Analyst role");
		analystRadioButton.setName("Analyst role");
		horizontalPanel_8.add(analystRadioButton);
		horizontalPanel_8.setCellHorizontalAlignment(analystRadioButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_8.setCellVerticalAlignment(analystRadioButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		userRadioButton = new RadioButton("buttonGroup", "User");
		userRadioButton.setTitle("User role");
		userRadioButton.setName("User role");
		horizontalPanel_8.add(userRadioButton);
		userRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				editLevelsButton.setEnabled(true);
				if (newUser) {
					orgMembershipTextBox.setText("");
				} else {
					String orgMembershipLevels;
					try {
						orgMembershipLevels = Role.getOrgMembershipLevelsStr(selectedUser.getRoleAndOrgMembership());
						orgMembershipTextBox.setText(orgMembershipLevels);
					} catch (Exception e) {
						log.severe(e.getMessage());
					}
				}

			}
		});

		analystRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				editLevelsButton.setEnabled(true);
				if (newUser) {
					orgMembershipTextBox.setText("");
				} else {
					String orgMembershipLevels;
					try {
						orgMembershipLevels = Role.getOrgMembershipLevelsStr(selectedUser.getRoleAndOrgMembership());
						orgMembershipTextBox.setText(orgMembershipLevels);
					} catch (Exception e) {
						log.severe(e.getMessage());
					}
				}

			}
		});
		toolRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				editLevelsButton.setEnabled(false);
				orgMembershipTextBox.setText("");
			}
		});
		log.info("trace k");

		HorizontalPanel horizontalPanel_14 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_14);

		Label lblNewLabel_2 = new Label("Organization: ");
		lblNewLabel_2.getElement().setAttribute("for", "org-memb-textbox");

		lblNewLabel_2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_14.add(lblNewLabel_2);
		lblNewLabel_2.setWidth("115px");
		horizontalPanel_14.setCellWidth(lblNewLabel_2, "298px");
		horizontalPanel_14.setCellVerticalAlignment(lblNewLabel_2, HasVerticalAlignment.ALIGN_MIDDLE);

		editLevelsButton = new PushButton("Edit");
		Roles.getButtonRole().setAriaLabelProperty(editLevelsButton.getElement(), "Edit Org Membership Button");
		editLevelsButton.setTitle("Edit organizational membership and role");
		editLevelsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				String orgMembership = orgMembershipTextBox.getText();
				if (newUser) {
					orgLevelsDialogBox = new OrgLevelsDialogBox(configInfo, allUsersOrgMemberships, orgMembership);
				} else {
					orgLevelsDialogBox = new OrgLevelsDialogBox(configInfo, allUsersOrgMemberships, orgMembership);
				}
				orgLevelsDialogBox.setText("Organization Membership");
				orgLevelsDialogBox.center();

				orgLevelsDialogBox.cancelButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(orgLevelsDialogBox);

						Scheduler.get().scheduleDeferred(new Command() {
							public void execute() {
								editLevelsButton.setFocus(true);
							}
						});
					}
				});

				orgLevelsDialogBox.okButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						int numLevels = orgLevelsDialogBox.listBox.getItemCount();
						String levelsStr = "";
						for (int i = 0; i < numLevels; i++) {
							// Extract just the level name
							String[] levelDisplay = orgLevelsDialogBox.listBox.getItemText(i).split(": ");
							String levelName = levelDisplay[1];

							if (levelName != null) {
								if (i > 0) {
									levelsStr += ",";
								}
								levelsStr += levelName;

							} else if (levelName == null) {
								break;
							}
						}
						// Check if minimum number of levels were set:
							String[] definedLevels = levelsStr.split(",");
							if (definedLevels.length < configInfo.getMinOrgLevelsRequired()) {
								showMessageDialog("AppVet Error", "Must specify all required levels", true);
							} else {
								orgMembershipTextBox.setText(levelsStr);
								killDialogBox(orgLevelsDialogBox);	

								Scheduler.get().scheduleDeferred(new Command() {
									public void execute() {
										editLevelsButton.setFocus(true);
									}
								});
							}

					}
				});

			}
		});
		log.info("trace l");

		orgMembershipTextBox = new TextBox();
		emailTextBox.getElement().setId("org-memb-textbox");
		orgMembershipTextBox.setTitle("Organizational membership and role");
		orgMembershipTextBox.setName("Organizational membership and role");
		orgMembershipTextBox.setReadOnly(true);
		horizontalPanel_14.add(orgMembershipTextBox);
		orgMembershipTextBox.setWidth("167");
		editLevelsButton.setStyleName("grayButton shadow");
		horizontalPanel_14.add(editLevelsButton);
		horizontalPanel_14.setCellVerticalAlignment(editLevelsButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_14.setCellHorizontalAlignment(editLevelsButton, HasHorizontalAlignment.ALIGN_CENTER);
		editLevelsButton.setSize("70px", "18px");
		editLevelsButton.setEnabled(false);

		HorizontalPanel horizontalPanel_5 = new HorizontalPanel();
		horizontalPanel_5.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_1.add(horizontalPanel_5);
		verticalPanel_1.setCellVerticalAlignment(horizontalPanel_5, HasVerticalAlignment.ALIGN_MIDDLE);

		changePasswordCheckBox = new SimpleCheckBox();
		changePasswordCheckBox.setTitle("Change password");
		changePasswordCheckBox.setName("Change password");
		changePasswordCheckBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				boolean changePassword = changePasswordCheckBox.isChecked();
				if (changePassword) {
					password1TextBox.setEnabled(true);
					password2TextBox.setEnabled(true);
				} else {
					password1TextBox.setEnabled(false);
					password2TextBox.setEnabled(false);
				}
			}
		});
		changePasswordCheckBox.setName("Change Password");
		changePasswordCheckBox.getElement().setId("changepassword");

		horizontalPanel_5.add(changePasswordCheckBox);
		horizontalPanel_5.setCellVerticalAlignment(changePasswordCheckBox, HasVerticalAlignment.ALIGN_MIDDLE);

		Label lblNewLabel_3 = new Label("Change Password");
		lblNewLabel_3.getElement().setAttribute("for", "changepassword");
		lblNewLabel_3.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_5.add(lblNewLabel_3);
		horizontalPanel_5.setCellVerticalAlignment(lblNewLabel_3, HasVerticalAlignment.ALIGN_MIDDLE);
		lblNewLabel_3.setWidth("345px");

		final HorizontalPanel horizontalPanel_13 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_13);
		horizontalPanel_13
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_13
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_13.setWidth("366px");

		passwordLabel = new Label("Password: ");
		passwordLabel.getElement().setAttribute("for", "password1-textbox");

		passwordLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_13.add(passwordLabel);
		horizontalPanel_13.setCellVerticalAlignment(passwordLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		passwordLabel.setWidth("115px");
		password1TextBox = new PasswordTextBox();
		password1TextBox.getElement().setId("password1-textbox");
		password1TextBox.setTitle("Enter password");
		password1TextBox.setName("Enter password");
		horizontalPanel_13.add(password1TextBox);

		password1TextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		password1TextBox.setAlignment(TextAlignment.LEFT);
		password1TextBox.setSize("234px", "");

		final HorizontalPanel horizontalPanel_4 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_4);
		horizontalPanel_4.setWidth("170px");
		horizontalPanel_4
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_4
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		Label passwordLabel3 = new Label("Password (again): ");
		passwordLabel3
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_4.add(passwordLabel3);
		passwordLabel3.setWidth("115px");
		password2TextBox = new PasswordTextBox();
		emailTextBox.getElement().setId("password2-textbox");
		password2TextBox.setTitle("Enter password again");
		password2TextBox.setName("Enter password again");
		horizontalPanel_4.add(password2TextBox);

		password2TextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		password2TextBox.setAlignment(TextAlignment.LEFT);
		password2TextBox.setSize("234px", "");
		log.info("trace m");


		final SimplePanel simplePanel = new SimplePanel();
		simplePanel.setStyleName("userFormPanel");
		simplePanel.setWidget(verticalPanel_1);
		dockPanel.add(simplePanel, DockPanel.CENTER);
		dockPanel.add(horizontalPanel, DockPanel.SOUTH);
		dockPanel.setCellVerticalAlignment(horizontalPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel.setCellHorizontalAlignment(horizontalPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		

		
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			lastNameTextBox.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			firstNameTextBox.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			userIdTextBox.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			emailTextBox.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			adminRadioButton.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			toolRadioButton.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			analystRadioButton.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			userRadioButton.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			editLevelsButton.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			orgMembershipTextBox.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			changePasswordCheckBox.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			password1TextBox.setEnabled(false);
		}
		if (selectedUser != null && selectedUser.getFromHost().equals("DEACTIVATED")) {
			password2TextBox.setEnabled(false);
		}
		log.info("trace n");

		/* Set all data at the end of constructor */
		if (newUser) {
			// Initialize some UI objects
			adminRadioButton.setValue(false);
			toolRadioButton.setValue(false);
			analystRadioButton.setValue(false);
			userRadioButton.setValue(false);
			orgMembershipTextBox.setText("");
			password1TextBox.setEnabled(false);
			password2TextBox.setEnabled(false);
		} else {
			log.info("trace o");

			Role userRole = null;
			try {
				userRole = Role.getRole(selectedUser.getRoleAndOrgMembership());
				// Initialize some UI objects
				lastNameTextBox.setText(selectedUser.getLastName());
				firstNameTextBox.setText(selectedUser.getFirstName());
				userIdTextBox.setText(selectedUser.getUserName());
				userIdTextBox.setReadOnly(true);
				String lastLoginStr = dateTimeFormat.format(selectedUser
						.getLastLogon());
				emailTextBox.setText(selectedUser.getEmail());
				if (selectedUser.getFromHost().equals("DEACTIVATED")) {
					editLevelsButton.setEnabled(false);
				} else if (userRole == Role.ADMIN) {
					adminRadioButton.setValue(true);
					editLevelsButton.setEnabled(false);
					orgMembershipTextBox.setText("");
				} else if (userRole == Role.TOOL_PROVIDER) {
					toolRadioButton.setValue(true);
					editLevelsButton.setEnabled(false);
					orgMembershipTextBox.setText("");
				} else if (userRole == Role.ANALYST) {
					analystRadioButton.setValue(true);
					editLevelsButton.setEnabled(true);
					String orgMembershipLevels = Role.getOrgMembershipLevelsStr(selectedUser.getRoleAndOrgMembership());
					orgMembershipTextBox.setText(orgMembershipLevels);
				} else if (userRole == Role.USER) {
					userRadioButton.setValue(true);
					editLevelsButton.setEnabled(true);
					String orgMembershipLevels = Role.getOrgMembershipLevelsStr(selectedUser.getRoleAndOrgMembership());
					orgMembershipTextBox.setText(orgMembershipLevels);
				} else if (userRole == Role.NEW) {
					//orgUnitPanel.setVisible(false);
				}
				password1TextBox.setEnabled(false);
				password2TextBox.setEnabled(false);
				log.info("trace p");

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		log.info("trace q");

		if (useSSO) {
			log.info("trace q1");
			changePasswordCheckBox.setEnabled(false);

		} else {
			log.info("trace q2");

			changePasswordCheckBox.setEnabled(true);

		}
		log.info("trace r");

	}

	/** This fixes focus for dialog boxes in Firefox and IE browsers */
	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				lastNameTextBox.setFocus(true);
			}
		});
	}

	public void showMessageDialog(String windowTitle, String message,
			boolean isError) {
		messageDialogBox = new MessageDialogBox(message, isError);
		messageDialogBox.setText(windowTitle);
		messageDialogBox.center();
		messageDialogBox.closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				log.info("clicked close");
				messageDialogBox.hide();
				messageDialogBox = null;
				log.info("out of closed button");
				Scheduler.get().scheduleDeferred(new Command() {
					public void execute() {
						orgLevelsDialogBox.listBox.setFocus(true);
					}
				});
			}
		});
	}

	/** Get database representation of role. */
	public String getRoleAndMembership(String orgMembership) {
		boolean adminRadioSelected = adminRadioButton.getValue();
		boolean toolRadioSelected = toolRadioButton.getValue();
		boolean analystRadioSelected = analystRadioButton.getValue();
		boolean userRadioSelected = userRadioButton.getValue();
		if (!adminRadioSelected && !toolRadioSelected && !analystRadioSelected
				&& !userRadioSelected) {
			//			showMessageDialog("AppVet User Account", "No user role selected",
			//					true);
			return null;
		}

		if (analystRadioSelected || userRadioSelected) {
			if (orgMembership == null || orgMembership.isEmpty()) {
				//				showMessageDialog("AppVet User Account", "Organization cannot be empty",
				//						true);
				return null;
			}
		}

		if (adminRadioSelected) {
			return Role.ADMIN.name();
		} else if (toolRadioSelected) {
			return Role.TOOL_PROVIDER.name();
		} else if (analystRadioSelected) {
			return Role.ANALYST.name() + ":" + orgMembership;
		} else if (userRadioSelected) {
			return Role.USER.name() + ":" + orgMembership;
		} else {
			log.severe("Invalid role");
			return null;
		}
	}

	public void killDialogBox(DialogBox dialogBox) {
		if (dialogBox != null) {
			dialogBox.hide();
			dialogBox = null;
		}
	}
}
