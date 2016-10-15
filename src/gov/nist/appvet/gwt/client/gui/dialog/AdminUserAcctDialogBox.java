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

	@SuppressWarnings("deprecation")
	public AdminUserAcctDialogBox(final ConfigInfoGwt configInfo,
			final UserInfo selectedUser, boolean useSSO, List<String> allUsersOrgLevels) {
		confInfo = configInfo;
		if (selectedUser == null) {
			newUser = true;
		}

		// Get orgs hierarchies
		if (allUsersOrgLevels == null) {
			log.warning("org hierarchies is null in acct");
		} else {
			allUsersOrgMemberships = allUsersOrgLevels;
		}

		setWidth("386px");

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
		final Label lblNewLabel = new Label("Last Name: ");
		horizontalPanel_1.add(lblNewLabel);
		horizontalPanel_1.setCellHorizontalAlignment(lblNewLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellVerticalAlignment(lblNewLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblNewLabel.setWidth("115px");
		horizontalPanel_1.setCellWidth(lblNewLabel, "50%");
		lblNewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lastNameTextBox = new TextBox();
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
		horizontalPanel_2.add(lblNewLabel_1);
		lblNewLabel_1.setWidth("115px");
		horizontalPanel_2.setCellWidth(lblNewLabel_1, "50%");
		horizontalPanel_2.setCellVerticalAlignment(lblNewLabel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setCellHorizontalAlignment(lblNewLabel_1,
				HasHorizontalAlignment.ALIGN_CENTER);
		lblNewLabel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		firstNameTextBox = new TextBox();
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
		horizontalPanel_3.add(lblUserId);
		lblUserId.setWidth("115px");
		horizontalPanel_3.setCellVerticalAlignment(lblUserId,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHorizontalAlignment(lblUserId,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3.setCellWidth(lblUserId, "50%");
		lblUserId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		userIdTextBox = new TextBox();
		userIdTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		userIdTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_3.add(userIdTextBox);
		horizontalPanel_3.setCellWidth(userIdTextBox, "50%");
		horizontalPanel_3.setCellVerticalAlignment(userIdTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHorizontalAlignment(userIdTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		userIdTextBox.setWidth("234px");

		final HorizontalPanel horizontalPanel_7 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_7);
		horizontalPanel_7
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_7
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblEmail = new Label("Email: ");
		horizontalPanel_7.add(lblEmail);
		lblEmail.setWidth("115px");
		horizontalPanel_7.setCellVerticalAlignment(lblEmail,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_7.setCellWidth(lblEmail, "50%");
		lblEmail.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		emailTextBox = new TextBox();
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
		horizontalPanel_8.add(lblRole);
		horizontalPanel_8.setCellWidth(lblRole, "150px");
		lblRole.setWidth("50px");
		horizontalPanel_8.setCellVerticalAlignment(lblRole,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblRole.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		adminRadioButton = new RadioButton("buttonGroup", "Admin");
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

		toolRadioButton = new RadioButton("buttonGroup", "Tool");
		horizontalPanel_8.add(toolRadioButton);
		horizontalPanel_8.setCellHorizontalAlignment(toolRadioButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_8.setCellVerticalAlignment(toolRadioButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		analystRadioButton = new RadioButton("buttonGroup", "Analyst");
		horizontalPanel_8.add(analystRadioButton);
		horizontalPanel_8.setCellHorizontalAlignment(analystRadioButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_8.setCellVerticalAlignment(analystRadioButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		userRadioButton = new RadioButton("buttongroup", "User");
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
		
		HorizontalPanel horizontalPanel_14 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_14);
		
		Label lblNewLabel_2 = new Label("Organization: ");
		lblNewLabel_2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_14.add(lblNewLabel_2);
		lblNewLabel_2.setWidth("115px");
		horizontalPanel_14.setCellWidth(lblNewLabel_2, "298px");
		horizontalPanel_14.setCellVerticalAlignment(lblNewLabel_2, HasVerticalAlignment.ALIGN_MIDDLE);
		
		editLevelsButton = new PushButton("Edit");
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
						}

					}
				});

			}
		});
		
		orgMembershipTextBox = new TextBox();
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
		changePasswordCheckBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				boolean changePassword = changePasswordCheckBox.isChecked();
				if (changePassword) {
					enableChangePassword(true);
				} else {
					enableChangePassword(false);
				}
			}
		});
		changePasswordCheckBox.setName("Change Password");
		horizontalPanel_5.add(changePasswordCheckBox);
		horizontalPanel_5.setCellVerticalAlignment(changePasswordCheckBox, HasVerticalAlignment.ALIGN_MIDDLE);
		
		Label lblNewLabel_3 = new Label("Change Password");
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
		passwordAgainLabel = new Label("Password (again): ");
		passwordLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_13.add(passwordLabel);
		horizontalPanel_13.setCellVerticalAlignment(passwordLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		passwordLabel.setWidth("115px");
		password1TextBox = new PasswordTextBox();
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
		horizontalPanel_4.add(password2TextBox);

		password2TextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		password2TextBox.setAlignment(TextAlignment.LEFT);
		password2TextBox.setSize("234px", "");

		passwordAgainLabel.setWidth("170px");
		passwordAgainLabel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setSize("200px", "50px");
		horizontalPanel.setStyleName("buttonPanelStyle");
		cancelButton = new PushButton("Cancel");
		cancelButton.setStyleName("grayButton shadow");
		cancelButton.setHTML("Cancel");
		horizontalPanel.add(cancelButton);
		final Label buttonSpacerLabel = new Label("");
		horizontalPanel.add(buttonSpacerLabel);
		horizontalPanel.setCellVerticalAlignment(buttonSpacerLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(buttonSpacerLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		buttonSpacerLabel.setSize("60px", "18px");
		horizontalPanel.setCellHorizontalAlignment(cancelButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(cancelButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		cancelButton.setSize("70px", "18px");
		submitButton = new PushButton("Submit");
		submitButton.setStyleName("greenButton shadow");
		horizontalPanel.add(submitButton);
		horizontalPanel.setCellHorizontalAlignment(submitButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(submitButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		submitButton.setSize("70px", "18px");

		final SimplePanel simplePanel = new SimplePanel();
		simplePanel.setStyleName("userFormPanel");
		simplePanel.setWidget(verticalPanel_1);
		final DockPanel dockPanel = new DockPanel();
		dockPanel.setStyleName("gwt-DialogBox");
		setWidget(dockPanel);
		dockPanel.setSize("386px", "");
		dockPanel.add(horizontalPanel, DockPanel.SOUTH);
		dockPanel.setCellVerticalAlignment(horizontalPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel.setCellHorizontalAlignment(horizontalPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		dockPanel.add(simplePanel, DockPanel.CENTER);

		/* Set all data at the end of constructor */
		if (newUser) {

			// Initialize some UI objects
			adminRadioButton.setValue(false);
			toolRadioButton.setValue(false);
			analystRadioButton.setValue(false);
			userRadioButton.setValue(false);
			// Since admin radio button is selected for new user
			orgMembershipTextBox.setText("");
			// Disable password checkbox here
			changePasswordCheckBox.setEnabled(false);

		} else {
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
				if (userRole == Role.ADMIN) {
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

				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (useSSO) {
			// SSO is used so disable password fields
			password1TextBox.setEnabled(false);
			password1TextBox.setVisible(false);
			password2TextBox.setEnabled(false);
			password2TextBox.setVisible(false);
			passwordAgainLabel.setVisible(false);
			passwordLabel.setVisible(false);
			passwordLabel3.setVisible(false);
		} else {
			if (newUser) {
				enableChangePassword(true);
			} else {
				enableChangePassword(false);
			}
		}

	}
	
	public void enableChangePassword(boolean enable) {
		if (enable) {
			changePasswordCheckBox.setChecked(true);
			password1TextBox.setEnabled(true);
			password2TextBox.setEnabled(true);
		} else {
			changePasswordCheckBox.setChecked(false);
			password1TextBox.setEnabled(false);
			password2TextBox.setEnabled(false);
		}
	}

	public void showMessageDialog(String windowTitle, String message,
			boolean isError) {
		messageDialogBox = new MessageDialogBox(message, isError);
		messageDialogBox.setText(windowTitle);
		messageDialogBox.center();
		messageDialogBox.closeButton.setFocus(true);
		messageDialogBox.closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				log.info("clicked close");
				messageDialogBox.hide();
				messageDialogBox = null;
				log.info("out of closed button");
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
			showMessageDialog("AppVet User Account", "No user role selected",
					true);
			return null;
		}
		
		if (analystRadioSelected || userRadioSelected) {
			if (orgMembership == null || orgMembership.isEmpty()) {
				showMessageDialog("AppVet User Account", "Organization cannot be empty",
						true);
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
