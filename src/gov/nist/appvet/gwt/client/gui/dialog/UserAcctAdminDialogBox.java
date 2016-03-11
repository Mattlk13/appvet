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

import gov.nist.appvet.gwt.client.GWTService;
import gov.nist.appvet.gwt.client.GWTServiceAsync;
import gov.nist.appvet.shared.all.OrgDepts;
import gov.nist.appvet.shared.all.OrgUnit;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserRoleInfo;
import gov.nist.appvet.shared.all.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author steveq@nist.gov
 */
public class UserAcctAdminDialogBox extends DialogBox {
	private static Logger log = Logger.getLogger("UserAcctAdminDialogBox");

	public PushButton cancelButton = null;
	public PushButton submitButton = null;
//	public PushButton setGroupsButton = null;
	public TextBox lastNameTextBox = null;
	public TextBox firstNameTextBox = null;
	public TextBox userIdTextBox = null;
	public PasswordTextBox password1TextBox = null;
	public PasswordTextBox password2TextBox = null;
	public TextBox emailTextBox = null;
	public PushButton addGroupsButton = null;
	public PushButton deleteGroupsButton = null;
	public PushButton editGroupsButton = null;
	public DockPanel orgUnitsPanel = null;
	public ListBox orgUnitsListBox = null;
	public OrgUnitDialogGox orgUnitDialogBox = null;
	public boolean orgUnitsHaveChanged = false;
	public List<OrgDepts> orgDeptsList = null;
	public boolean newUser = false;
	public Label passwordAgainLabel = null;
	private final DateTimeFormat dateTimeFormat = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm:ss");
	private static MessageDialogBox messageDialogBox = null;
	private final static GWTServiceAsync appVetServiceAsync = GWT
			.create(GWTService.class);
	public RadioButton adminRadioButton = null;
	public RadioButton toolRadioButton = null;
	public RadioButton userAnalystRadioButton = null;
	public Label groupsLabel = null;
	public Label footNoteLabel = null;
	public UserRoleInfo userRoleInfo = null;
	//public ArrayList<OrgUnit> orgUnits = null;

	@SuppressWarnings("deprecation")
	public UserAcctAdminDialogBox(final UserInfo userInfo, boolean useSSO) {
		log.info("In useracct");
		if (userInfo == null) {
			log.info("creating new groups1");
			newUser = true;
		} else {
			userRoleInfo = userInfo.getUserRoleInfo();
		}

		// Get orgs and depts now
		// getOrgDeptList();

		setWidth("386px");

		if (newUser) {
			passwordAgainLabel = new Label("Password (again): ");
		} else {
			passwordAgainLabel = new Label("Password Reset (again): ");
		}
		passwordAgainLabel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		log.info("trace b");

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
		lblNewLabel.setWidth("170px");
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
		lastNameTextBox.setWidth("180px");
		final HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_2);
		horizontalPanel_2
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblNewLabel_1 = new Label("First Name: ");
		horizontalPanel_2.add(lblNewLabel_1);
		lblNewLabel_1.setWidth("170px");
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
		firstNameTextBox.setWidth("180px");
		final HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_3);
		horizontalPanel_3
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblUserId = new Label("User ID:");
		horizontalPanel_3.add(lblUserId);
		lblUserId.setWidth("170px");
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
		userIdTextBox.setWidth("180px");

		final HorizontalPanel horizontalPanel_7 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_7);
		horizontalPanel_7
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_7
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblEmail = new Label("Email: ");
		horizontalPanel_7.add(lblEmail);
		lblEmail.setWidth("170px");
		horizontalPanel_7.setCellVerticalAlignment(lblEmail,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_7.setCellHorizontalAlignment(lblEmail,
				HasHorizontalAlignment.ALIGN_CENTER);
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
		emailTextBox.setWidth("180px");
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
		horizontalPanel_8.setCellWidth(lblRole, "166px");
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
				orgUnitsPanel.setVisible(false);
			}
		});
		adminRadioButton.setWidth("140px");

		toolRadioButton = new RadioButton("buttonGroup", "Tool");
		horizontalPanel_8.add(toolRadioButton);
		horizontalPanel_8.setCellHorizontalAlignment(toolRadioButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_8.setCellVerticalAlignment(toolRadioButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		userAnalystRadioButton = new RadioButton("buttonGroup", "User/Analyst");
		horizontalPanel_8.add(userAnalystRadioButton);
		horizontalPanel_8.setCellHorizontalAlignment(userAnalystRadioButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_8.setCellVerticalAlignment(userAnalystRadioButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		userAnalystRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				orgUnitsPanel.setVisible(true);
			}
		});
		userAnalystRadioButton.setChecked(true);
		toolRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				orgUnitsPanel.setVisible(false);
			}
		});

		orgUnitsPanel = new DockPanel();
		orgUnitsPanel.setStyleName("usersDockPanel");
		verticalPanel_1.add(orgUnitsPanel);
		orgUnitsPanel.setHeight("168px");

		orgUnitsListBox = new ListBox();
		orgUnitsPanel.add(orgUnitsListBox, DockPanel.CENTER);
		orgUnitsPanel.setCellVerticalAlignment(orgUnitsListBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		orgUnitsPanel.setCellHorizontalAlignment(orgUnitsListBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		orgUnitsListBox.setSize("360px", "115px");
		orgUnitsListBox.setVisibleItemCount(5);

		groupsLabel = new Label("Groups: Top Level, ..., Bottom Level");
		groupsLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		orgUnitsPanel.add(groupsLabel, DockPanel.NORTH);
		orgUnitsPanel.setCellVerticalAlignment(groupsLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		groupsLabel.setWidth("362px");
		orgUnitsPanel.setCellHorizontalAlignment(groupsLabel,
				HasHorizontalAlignment.ALIGN_CENTER);

		HorizontalPanel horizontalPanel_5 = new HorizontalPanel();
		horizontalPanel_5
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		orgUnitsPanel.add(horizontalPanel_5, DockPanel.SOUTH);
		orgUnitsPanel.setCellVerticalAlignment(horizontalPanel_5,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setSize("362px", "31px");

		addGroupsButton = new PushButton("Add");
		addGroupsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				setGroup(userInfo.getFullName(), -1, true);
			}
		});
		horizontalPanel_5.add(addGroupsButton);
		horizontalPanel_5.setCellVerticalAlignment(addGroupsButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setCellHorizontalAlignment(addGroupsButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		addGroupsButton.setWidth("70px");

		deleteGroupsButton = new PushButton("Delete");
		deleteGroupsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				int selectedGroupIndex = orgUnitsListBox.getSelectedIndex();
				// Remove from orgUnits
				if (userRoleInfo.getOrgUnits() != null) {
					userRoleInfo.getOrgUnits().remove(selectedGroupIndex);
				}
				// Remove from list box
				orgUnitsListBox.removeItem(selectedGroupIndex);
			}
		});
		horizontalPanel_5.add(deleteGroupsButton);
		horizontalPanel_5.setCellVerticalAlignment(deleteGroupsButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setCellHorizontalAlignment(deleteGroupsButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		deleteGroupsButton.setWidth("70px");

		editGroupsButton = new PushButton("Edit");
		editGroupsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				int selectedGroupIndex = orgUnitsListBox.getSelectedIndex();
				if (selectedGroupIndex > -1) {
					setGroup(userInfo.getFullName(), selectedGroupIndex, false);
				}
			}
		});
		horizontalPanel_5.add(editGroupsButton);
		horizontalPanel_5.setCellVerticalAlignment(editGroupsButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		editGroupsButton.setWidth("70px");
		horizontalPanel_5.setCellHorizontalAlignment(editGroupsButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		final HorizontalPanel horizontalPanel_13 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_13);
		horizontalPanel_13
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_13
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_13.setWidth("366px");

		Label passwordLabel = null;
		if (newUser) {
			passwordLabel = new Label("Set Password: ");
		} else {
			passwordLabel = new Label("Change Password: ");
		}

		passwordLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_13.add(passwordLabel);
		horizontalPanel_13.setCellVerticalAlignment(passwordLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		passwordLabel.setWidth("170px");
		password1TextBox = new PasswordTextBox();
		horizontalPanel_13.add(password1TextBox);

		password1TextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		password1TextBox.setAlignment(TextAlignment.LEFT);
		password1TextBox.setSize("180px", "");

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
		passwordLabel3.setWidth("170px");
		password2TextBox = new PasswordTextBox();
		horizontalPanel_4.add(password2TextBox);

		password2TextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		password2TextBox.setAlignment(TextAlignment.LEFT);
		password2TextBox.setSize("180px", "");

		passwordAgainLabel.setWidth("170px");
		passwordAgainLabel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		final HorizontalPanel horizontalPanel_10 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_10);
		horizontalPanel_10
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_10
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblLastLogon = new Label("Last Logon: ");
		horizontalPanel_10.add(lblLastLogon);
		lblLastLogon.setWidth("170px");
		horizontalPanel_10.setCellVerticalAlignment(lblLastLogon,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_10.setCellHorizontalAlignment(lblLastLogon,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_10.setCellWidth(lblLastLogon, "50%");
		lblLastLogon.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		final TextBox lastLogonTextBox = new TextBox();
		lastLogonTextBox.setEnabled(false);
		lastLogonTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		lastLogonTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_10.add(lastLogonTextBox);
		horizontalPanel_10.setCellVerticalAlignment(lastLogonTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_10.setCellHorizontalAlignment(lastLogonTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_10.setCellWidth(lastLogonTextBox, "50%");
		lastLogonTextBox.setReadOnly(true);
		lastLogonTextBox.setWidth("180px");
		final HorizontalPanel horizontalPanel_11 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_11);
		horizontalPanel_11
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_11
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblFromHost = new Label("From Host: ");
		horizontalPanel_11.add(lblFromHost);
		lblFromHost.setWidth("170px");
		horizontalPanel_11.setCellWidth(lblFromHost, "50%");
		horizontalPanel_11.setCellVerticalAlignment(lblFromHost,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_11.setCellHorizontalAlignment(lblFromHost,
				HasHorizontalAlignment.ALIGN_CENTER);
		lblFromHost.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		final TextBox fromHostTextBox = new TextBox();
		fromHostTextBox.setEnabled(false);
		fromHostTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		fromHostTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_11.add(fromHostTextBox);
		horizontalPanel_11.setCellVerticalAlignment(fromHostTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_11.setCellHorizontalAlignment(fromHostTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_11.setCellWidth(fromHostTextBox, "50%");
		fromHostTextBox.setReadOnly(true);
		fromHostTextBox.setWidth("180px");
		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setSize("200px", "50px");
		horizontalPanel.setStyleName("buttonPanelStyle");
		cancelButton = new PushButton("Cancel");
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
		submitButton.setEnabled(true);
		horizontalPanel.add(submitButton);
		horizontalPanel.setCellHorizontalAlignment(submitButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(submitButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		submitButton.setSize("70px", "18px");

		// If not a new user, set fields with the user's current info.
		if (!newUser) {
			lastNameTextBox.setText(userInfo.getLastName());
			firstNameTextBox.setText(userInfo.getFirstName());
			userIdTextBox.setText(userInfo.getUserName());
			userIdTextBox.setReadOnly(true);
			String lastLoginStr = dateTimeFormat
					.format(userInfo.getLastLogon());
			lastLogonTextBox.setText(lastLoginStr);
			fromHostTextBox.setText(userInfo.getFromHost());
			emailTextBox.setText(userInfo.getEmail());
			UserRoleInfo roles = userInfo.getUserRoleInfo();

			if (roles.getRole() == Role.ADMIN) {
				adminRadioButton.setValue(true);
			} else if (roles.getRole() == Role.TOOL_PROVIDER) {
				toolRadioButton.setValue(true);
			} else {
				userAnalystRadioButton.setValue(true);
				// Set the org units list box
				if (userRoleInfo.getOrgUnits() != null && !userRoleInfo.getOrgUnits().isEmpty()) {
					// Add user groups to list box
					for (int i = 0; i < userRoleInfo.getOrgUnits().size(); i++) {
						orgUnitsListBox
								.addItem(userRoleInfo.getOrgUnits().get(i).getOrgUnitStr());
					}
				}
			}
		}
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
			password1TextBox.setEnabled(true);
			password1TextBox.setVisible(true);
			password2TextBox.setEnabled(true);
			password2TextBox.setVisible(true);
			passwordAgainLabel.setVisible(true);
			passwordLabel.setVisible(true);
			passwordLabel3.setVisible(true);
		}
		log.info("trace c");

	}

	public static void showMessageDialog(String windowTitle, String message,
			boolean isError) {
		messageDialogBox = new MessageDialogBox(message, isError);
		messageDialogBox.setText(windowTitle);
		messageDialogBox.center();
		messageDialogBox.closeButton.setFocus(true);
		messageDialogBox.closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				messageDialogBox.hide();
				messageDialogBox = null;
			}
		});
	}

	public boolean allFieldsFilled() {
		if (!lastNameTextBox.getText().isEmpty()
				&& !firstNameTextBox.getText().isEmpty()
				&& !userIdTextBox.getText().isEmpty()
				&& !emailTextBox.getText().isEmpty()) {
			if (newUser) {
				if (!password1TextBox.getText().isEmpty()
						&& !password2TextBox.getText().isEmpty()) {
					// Password boxes must be filled for new user
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	// TODO: Update for groups
	// public void getOrgDeptList() {
	// appVetServiceAsync.getOrgDeptsList(new AsyncCallback<List<OrgDepts>>() {
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// log.severe("Got error trying to get org dept list");
	// showMessageDialog("AppVet Error", "Could not retrieve orgs/depts",
	// true);
	// }
	//
	// @Override
	// public void onSuccess(List<OrgDepts> result) {
	// orgDeptsList = result;
	// }
	// });
	// }

	public void setGroup(final String userFullName, 
			final int selectedOrgUnitIndex, final boolean newOrgUnit) {
		log.info("Starting org unit dialog box");
		if (newOrgUnit) {
			orgUnitDialogBox = new OrgUnitDialogGox(null);
		} else {
			orgUnitDialogBox = new OrgUnitDialogGox(userRoleInfo.getOrgUnits().get(selectedOrgUnitIndex));
		}
		orgUnitDialogBox.setText("Add/Edit Group for " + userFullName);
		orgUnitDialogBox.center();
		orgUnitDialogBox.cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				killDialogBox(orgUnitDialogBox);
				return;
			}
		});
		orgUnitDialogBox.okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Check if all required fields have been set
				if (orgUnitDialogBox.level1RadioButton.getValue()) {
					if (orgUnitDialogBox.level1SuggestionBox.getText() == null
							|| orgUnitDialogBox.level1SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level1 name",
								true);
						return;
					}
				} else if (orgUnitDialogBox.level2RadioButton.getValue()) {
					if (orgUnitDialogBox.level1SuggestionBox.getText() == null
							|| orgUnitDialogBox.level1SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level1 name",
								true);
						return;
					} else if (orgUnitDialogBox.level2SuggestionBox.getText() == null
							|| orgUnitDialogBox.level2SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level2 name",
								true);
						return;
					}
				} else if (orgUnitDialogBox.level3RadioButton.getValue()) {
					if (orgUnitDialogBox.level1SuggestionBox.getText() == null
							|| orgUnitDialogBox.level1SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level1 name",
								true);
						return;
					} else if (orgUnitDialogBox.level2SuggestionBox.getText() == null
							|| orgUnitDialogBox.level2SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level2 name",
								true);
						return;
					} else if (orgUnitDialogBox.level3SuggestionBox.getText() == null
							|| orgUnitDialogBox.level3SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level3 name",
								true);
						return;
					}
				} else if (orgUnitDialogBox.level4RadioButton.getValue() ||
						orgUnitDialogBox.userRadioButton.getValue()) {
					if (orgUnitDialogBox.level1SuggestionBox.getText() == null
							|| orgUnitDialogBox.level1SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level1 name",
								true);
						return;
					} else if (orgUnitDialogBox.level2SuggestionBox.getText() == null
							|| orgUnitDialogBox.level2SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level2 name",
								true);
						return;
					} else if (orgUnitDialogBox.level3SuggestionBox.getText() == null
							|| orgUnitDialogBox.level3SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level3 name",
								true);
						return;
					} else if (orgUnitDialogBox.level4SuggestionBox.getText() == null
							|| orgUnitDialogBox.level4SuggestionBox.getText().isEmpty()) {
						showMessageDialog("AppVet Group Error", "Invalid Level4 name",
								true);
						return;
					}
				}
				
				// Flag that the groups have changed.
				orgUnitsHaveChanged = true;
				
				log.info("userRadioButton: " + orgUnitDialogBox.userRadioButton.getValue());
				log.info("level1Name: " + orgUnitDialogBox.level1SuggestionBox.getValue());
				log.info("level1Analyst: " + orgUnitDialogBox.level1RadioButton.getValue());
				log.info("level2Name: " + orgUnitDialogBox.level2SuggestionBox.getValue());
				log.info("level2Analyst: " + orgUnitDialogBox.level2RadioButton.getValue());
				log.info("level3Name: " + orgUnitDialogBox.level3SuggestionBox.getValue());
				log.info("level3Analyst: " + orgUnitDialogBox.level3RadioButton.getValue());
				log.info("level4Name: " + orgUnitDialogBox.level4SuggestionBox.getValue());
				log.info("level4Analyst: " + orgUnitDialogBox.level4RadioButton.getValue());

				if (newOrgUnit) {
					log.info("Adding new orgUnits");
					Role newRole = null;
					
					// Create new group object
					if (orgUnitDialogBox.userRadioButton.getValue()) {
						newRole = Role.USER;
					} else {
						// If the role isn't USER, it must be ANALYST
						newRole = Role.ANALYST;
					}
					ArrayList<String> hierarchy = new ArrayList<String>();
					String level1 = orgUnitDialogBox.level1SuggestionBox.getValue();
					if (level1 != null && !level1.isEmpty()) {
						hierarchy.add(level1);
					}
					String level2 = orgUnitDialogBox.level2SuggestionBox.getValue();
					if (level2 != null && !level2.isEmpty()) {
						hierarchy.add(level2);
					}
					String level3 = orgUnitDialogBox.level3SuggestionBox.getValue();
					if (level3 != null && !level3.isEmpty()) {
						hierarchy.add(level3);
					}
					String level4 = orgUnitDialogBox.level4SuggestionBox.getValue();
					if (level4 != null && !level4.isEmpty()) {
						hierarchy.add(level4);
					}
					OrgUnit newGroup;
					try {
						newGroup = new OrgUnit(newRole, hierarchy);
						if (userRoleInfo == null) {
							userRoleInfo = new UserRoleInfo(newRole);
						}
						userRoleInfo.getOrgUnits().add(newGroup);
						// Add new group to list box
						orgUnitsListBox.addItem(newGroup.toString());
						// Enable buttons
						deleteGroupsButton.setEnabled(true);
						editGroupsButton.setEnabled(true);
					} catch (Exception e) {
						log.severe("Could not add new org unit: " + e.getMessage());
					}
					log.info("Got to end of new orgUnits");
				} else {
					log.info("Editing orgUnits");
				Role newRole = null;
					
					// Create new group object
					if (orgUnitDialogBox.userRadioButton.getValue()) {
						newRole = Role.USER;
					} else {
						// If the role isn't USER, it must be ANALYST
						newRole = Role.ANALYST;
					}
					ArrayList<String> hierarchy = new ArrayList<String>();
					String level1 = orgUnitDialogBox.level1SuggestionBox.getValue();
					if (level1 != null && !level1.isEmpty()) {
						hierarchy.add(level1);
					}
					String level2 = orgUnitDialogBox.level2SuggestionBox.getValue();
					if (level2 != null && !level2.isEmpty()) {
						hierarchy.add(level2);
					}
					String level3 = orgUnitDialogBox.level3SuggestionBox.getValue();
					if (level3 != null && !level3.isEmpty()) {
						hierarchy.add(level3);
					}
					String level4 = orgUnitDialogBox.level4SuggestionBox.getValue();
					if (level4 != null && !level4.isEmpty()) {
						hierarchy.add(level4);
					}
					OrgUnit newGroup;
					try {
						newGroup = new OrgUnit(newRole, hierarchy);
						// Overwrite the existing org unit in the list box
						userRoleInfo.getOrgUnits().set(selectedOrgUnitIndex, newGroup);
					} catch (Exception e) {
						log.severe("Could not add new org unit: " + e.getMessage());
					}
					log.info("Got to end of edited orgUnits");
				}

				// Kill group acct box only after we have retrieved the data
				killDialogBox(orgUnitDialogBox);
			}
		});
		
	}

	public static void killDialogBox(DialogBox dialogBox) {
		if (dialogBox != null) {
			dialogBox.hide();
			dialogBox = null;
		}
	}
}
