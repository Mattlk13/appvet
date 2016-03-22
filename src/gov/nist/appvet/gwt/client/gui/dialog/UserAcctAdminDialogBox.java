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
import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.shared.all.Role;
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
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;

/**
 * @author steveq@nist.gov
 */
public class UserAcctAdminDialogBox extends DialogBox {
	private Logger log = Logger.getLogger("UserAcctAdminDialogBox");
	public PushButton cancelButton = null;
	public PushButton submitButton = null;
	public TextBox lastNameTextBox = null;
	public TextBox firstNameTextBox = null;
	public TextBox userIdTextBox = null;
	public PasswordTextBox password1TextBox = null;
	public PasswordTextBox password2TextBox = null;
	public TextBox emailTextBox = null;
	public SimplePanel orgUnitPanel = null;
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
	public SuggestBox level1SuggestBox = null;
	public SuggestBox level2SuggestBox = null;
	public SuggestBox level3SuggestBox = null;
	public SuggestBox level4SuggestBox = null;
	public List<String> hierarchies = null;
	public ConfigInfoGwt confInfo = null;

	@SuppressWarnings("deprecation")
	public UserAcctAdminDialogBox(final ConfigInfoGwt configInfo,
			final UserInfo userInfo, boolean useSSO, List<String> orgHierarchies) {
		confInfo = configInfo;
		if (userInfo == null) {
			newUser = true;
		}

		// Get orgs hierarchies
		if (orgHierarchies == null) {
			log.warning("org hierarchies is null in acct");
		} else {
			hierarchies = orgHierarchies;
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
				orgUnitPanel.setVisible(false);
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
				orgUnitPanel.setVisible(true);
			}
		});

		analystRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				orgUnitPanel.setVisible(true);
			}
		});
		toolRadioButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				orgUnitPanel.setVisible(false);
			}
		});

		orgUnitPanel = new SimplePanel();
		orgUnitPanel.setStyleName("usersDockPanel");
		verticalPanel_1.add(orgUnitPanel);

		VerticalPanel verticalPanel = new VerticalPanel();
		orgUnitPanel.setWidget(verticalPanel);
		verticalPanel.setSize("100%", "100%");

		HorizontalPanel horizontalPanel_5 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_5);

		Label level1Label = new Label(configInfo.getOrgLevel1Name() + " (Required): ");
		horizontalPanel_5.add(level1Label);
		horizontalPanel_5.setCellVerticalAlignment(level1Label,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setCellWidth(level1Label, "50%");
		level1Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		level1Label.setWidth("170px");

		// Note: GWT is limited in its ability to set suggestions after the
		// SuggestBox is created. Since users will enter data in real-time,
		// it is unknown what oracle values should be set in level2, level3, 
		// and level4 SuggestBoxes. There, we simply set oracle values to 
		// known hierarchy level names.
		MultiWordSuggestOracle level1Oracle = getOracle(0);
		level1SuggestBox = new SuggestBox(level1Oracle);
		horizontalPanel_5.add(level1SuggestBox);
		level1SuggestBox.setWidth("180px");

		HorizontalPanel horizontalPanel_6 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_6);

		Label level2Label = new Label(configInfo.getOrgLevel2Name() + " (Required): ");
		horizontalPanel_6.add(level2Label);
		horizontalPanel_6.setCellVerticalAlignment(level2Label,
				HasVerticalAlignment.ALIGN_MIDDLE);
		level2Label.setWidth("170px");
		level2Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		MultiWordSuggestOracle level2Oracle = getOracle(1);
		level2SuggestBox = new SuggestBox(level2Oracle);
		horizontalPanel_6.add(level2SuggestBox);
		level2SuggestBox.setWidth("180px");

		HorizontalPanel horizontalPanel_9 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_9);

		Label level3Label = new Label(configInfo.getOrgLevel3Name() + ": ");
		horizontalPanel_9.add(level3Label);
		level3Label.setWidth("170px");
		horizontalPanel_9.setCellVerticalAlignment(level3Label,
				HasVerticalAlignment.ALIGN_MIDDLE);
		level3Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		MultiWordSuggestOracle level3Oracle = getOracle(2);
		level3SuggestBox = new SuggestBox(level3Oracle);
		horizontalPanel_9.add(level3SuggestBox);
		level3SuggestBox.setWidth("180px");

		HorizontalPanel horizontalPanel_12 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_12);

		Label level4Label = new Label(configInfo.getOrgLevel4Name() + ": ");
		horizontalPanel_12.add(level4Label);
		level4Label.setWidth("170px");
		horizontalPanel_12.setCellVerticalAlignment(level4Label,
				HasVerticalAlignment.ALIGN_MIDDLE);
		level4Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		MultiWordSuggestOracle level4Oracle = getOracle(3);
		level4SuggestBox = new SuggestBox(level4Oracle);
		horizontalPanel_12.add(level4SuggestBox);
		level4SuggestBox.setWidth("180px");

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
			orgUnitPanel.setVisible(false);

		} else {
			Role userRole = null;
			try {
				userRole = Role.getRole(userInfo.getRoleStr());
				// Initialize some UI objects
				lastNameTextBox.setText(userInfo.getLastName());
				firstNameTextBox.setText(userInfo.getFirstName());
				userIdTextBox.setText(userInfo.getUserName());
				userIdTextBox.setReadOnly(true);
				String lastLoginStr = dateTimeFormat.format(userInfo
						.getLastLogon());
				lastLogonTextBox.setText(lastLoginStr);
				fromHostTextBox.setText(userInfo.getFromHost());
				emailTextBox.setText(userInfo.getEmail());
				if (userRole == Role.ADMIN) {
					adminRadioButton.setValue(true);
					orgUnitPanel.setVisible(false);
				} else if (userRole == Role.TOOL_PROVIDER) {
					toolRadioButton.setValue(true);
					orgUnitPanel.setVisible(false);
				} else if (userRole == Role.ANALYST) {
					analystRadioButton.setValue(true);
					orgUnitPanel.setVisible(true);
					// Set the org hierarchy list box
					ArrayList<String> orgUnitHierarchy = Role
							.getOrgHierarchy(userInfo.getRoleStr());
					setLevelsSuggestBox(orgUnitHierarchy);
				} else if (userRole == Role.USER) {
					userRadioButton.setValue(true);
					orgUnitPanel.setVisible(true);
					// Set the org hierarchy list box
					ArrayList<String> orgUnitHierarchy = Role
							.getOrgHierarchy(userInfo.getRoleStr());
					setLevelsSuggestBox(orgUnitHierarchy);
				} else if (userRole == Role.NEW) {
					orgUnitPanel.setVisible(false);
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
			password1TextBox.setEnabled(true);
			password1TextBox.setVisible(true);
			password2TextBox.setEnabled(true);
			password2TextBox.setVisible(true);
			passwordAgainLabel.setVisible(true);
			passwordLabel.setVisible(true);
			passwordLabel3.setVisible(true);
		}
	}
	
	public MultiWordSuggestOracle getOracle(int levelIndex) {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		if (hierarchies == null) {
			log.warning("Hierarchies is null");
			return null;
		}
		for (int i = 0; i < hierarchies.size(); i++) {
			String hierarchy = hierarchies.get(i);
			String[] levels = hierarchy.split(",");
			if (levels[levelIndex] != null) {
				oracle.add(levels[levelIndex]);
			}
		}
		return oracle;
	}

	public void setLevelsSuggestBox(ArrayList<String> orgUnitHierarchy) {
		if (orgUnitHierarchy != null && !orgUnitHierarchy.isEmpty()) {
			// Add org units to list box
			for (int i = 0; i < orgUnitHierarchy.size(); i++) {
				String orgUnitHierarchyStr = orgUnitHierarchy.get(i);
				if (orgUnitHierarchyStr != null
						&& !orgUnitHierarchyStr.isEmpty()) {
					if (i == 0) {
						level1SuggestBox.setText(orgUnitHierarchyStr);
					} else if (i == 1) {
						level2SuggestBox.setText(orgUnitHierarchyStr);
					} else if (i == 2) {
						level3SuggestBox.setText(orgUnitHierarchyStr);
					} else if (i == 3) {
						level4SuggestBox.setText(orgUnitHierarchyStr);
					}
				}
			}
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
				messageDialogBox.hide();
				messageDialogBox = null;
			}
		});
	}

	/** Get database representation of role. */
	public String getRoleStr() {
		String roleStr = "";
		boolean adminRadioSelected = adminRadioButton.getValue();
		boolean toolRadioSelected = toolRadioButton.getValue();
		boolean analystRadioSelected = analystRadioButton.getValue();
		boolean userRadioSelected = userRadioButton.getValue();
		if (!adminRadioSelected && !toolRadioSelected && !analystRadioSelected
				&& !userRadioSelected) {
			showMessageDialog("AppVet User Account", "No user role selected",
					true);
		}
		if (adminRadioSelected) {
			roleStr += Role.ADMIN.name();
			return roleStr;
		} else if (toolRadioSelected) {
			roleStr += Role.TOOL_PROVIDER.name();
			return roleStr;
		} else if (analystRadioSelected) {
			roleStr += Role.ANALYST.name() + ":";
		} else if (userRadioSelected) {
			roleStr += Role.USER.name() + ":";
		} else {
			log.severe("Invalid role");
			return null;
		}
		String level1 = level1SuggestBox.getValue();
		if (level1 == null || level1.isEmpty()) {
			// Level1 is required
			//log.severe("Invalid " + confInfo.getOrgLevel1Name());
			return null;
		} else {
			roleStr += level1;
		}
		String level2 = level2SuggestBox.getValue();
		if (level2 == null || level2.isEmpty()) {
			// Level2 is required
			//log.severe("Invalid " + confInfo.getOrgLevel2Name());
			return null;
		} else {
			roleStr += "," + level2;
		}

		String level3 = level3SuggestBox.getValue();
		if (level3 == null || level3.isEmpty()) {
			// Check if level 4 is not null
		} else {
			roleStr += "," + level3;
		}
		
		String level4 = level4SuggestBox.getValue();
		if (level4 == null || level4.isEmpty()) {
			// Level4 is not required. Do nothing.
		} else {
			roleStr += "," + level4;
		}

		return roleStr;
	}
	
	/** Get database representation of role. */
	public boolean validateRoleAndHierarchies() {
		boolean adminRadioSelected = adminRadioButton.getValue();
		boolean toolRadioSelected = toolRadioButton.getValue();
		boolean analystRadioSelected = analystRadioButton.getValue();
		boolean userRadioSelected = userRadioButton.getValue();
		if (!adminRadioSelected && !toolRadioSelected && !analystRadioSelected
				&& !userRadioSelected) {
			showMessageDialog("AppVet User Account", "No user role selected",
					true);
			return false;
		}

		if (adminRadioSelected || toolRadioSelected) {
			return true;
		}
		
		// Check the following if user is ANALYST or USER
		String level1 = level1SuggestBox.getValue();
		if (level1 == null || level1.isEmpty()) {
			// Level1 is required
			log.severe("Invalid " + confInfo.getOrgLevel1Name());
			showMessageDialog("AppVet User Account", "Invalid " + confInfo.getOrgLevel1Name(),
					true);
			return false;
		}
		String level2 = level2SuggestBox.getValue();
		if (level2 == null || level2.isEmpty()) {
			// Level2 is required
			showMessageDialog("AppVet User Account", "Invalid " + confInfo.getOrgLevel2Name(),
					true);
			return false;
		}
		
		String level3 = level3SuggestBox.getValue();
		if (level3 == null || level3.isEmpty()) {
			// Check if level 4 is not null and not empty
			String level4 = level4SuggestBox.getValue();
			if (level4 != null && !level4.isEmpty()) {
				// Level 3 is invalid
				showMessageDialog("AppVet User Account", confInfo.getOrgLevel3Name() + " cannot be null if " + 
						confInfo.getOrgLevel4Name() + " is not null.",
						true);
				return false;
			}
		}
		return true;
	}

	public void killDialogBox(DialogBox dialogBox) {
		if (dialogBox != null) {
			dialogBox.hide();
			dialogBox = null;
		}
	}
}
