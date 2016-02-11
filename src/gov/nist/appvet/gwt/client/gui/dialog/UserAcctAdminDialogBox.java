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
import gov.nist.appvet.gwt.client.gui.table.appslist.UsersListPagingDataGrid;
import gov.nist.appvet.shared.all.OrgDepts;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserInfo;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

/**
 * @author steveq@nist.gov
 */
public class UserAcctAdminDialogBox extends DialogBox {
	private static Logger log = Logger.getLogger("UserAcctAdminDialogBox");

	public PushButton cancelButton = null;
	public PushButton okButton = null;
	public TextBox lastNameTextBox = null;
	public TextBox firstNameTextBox = null;
	public TextBox userIdTextBox = null;
	public PasswordTextBox password1TextBox = null;
	public PasswordTextBox password2TextBox = null;
	public TextBox emailTextBox = null;
	public ListBox roleComboBox = null;
	public SuggestBox orgSuggestBox = null;
	public MultiWordSuggestOracle orgOracle = null;
	public SuggestBox deptSuggestBox = null;
	public MultiWordSuggestOracle deptOracle = null;
	public List<UserInfo> allUsers = null;
	public List<OrgDepts> orgDeptsList = null;
	public boolean newUser = false;
	public UsersListPagingDataGrid<UserInfo> usersListTable = null;
	public SimpleCheckBox changePasswordCheckBox = null;
	public Label passwordAgainLabel = null;
	private final DateTimeFormat dateTimeFormat = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm:ss");
	private static MessageDialogBox messageDialogBox = null;
	private final static GWTServiceAsync appVetServiceAsync = GWT
			.create(GWTService.class);

	@SuppressWarnings("deprecation")
	public UserAcctAdminDialogBox(UserInfo userInfo,
			UsersListPagingDataGrid<UserInfo> usersListTable,
			List<UserInfo> allUsers) {
		
		// Get orgs and depts now
		getOrgDeptList();
		
		setWidth("386px");
		this.usersListTable = usersListTable;
		this.allUsers = allUsers;
		this.orgDeptsList = orgDeptsList;
		
		if (userInfo == null) {
			newUser = true;
		}
		changePasswordCheckBox = new SimpleCheckBox();

		if (newUser) {
			passwordAgainLabel = new Label("Password (again): ");
		} else {
			passwordAgainLabel = new Label("Password Reset (again): ");
		}
		passwordAgainLabel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
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
		lastNameTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				if (allFieldsFilled()) {
					okButton.setEnabled(true);
				}
			}
		});
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
		firstNameTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				if (allFieldsFilled()) {
					okButton.setEnabled(true);
				}
			}
		});
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
		userIdTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				if (allFieldsFilled()) {
					okButton.setEnabled(true);
				}
			}
		});
		userIdTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		userIdTextBox.setAlignment(TextAlignment.LEFT);
		horizontalPanel_3.add(userIdTextBox);
		horizontalPanel_3.setCellWidth(userIdTextBox, "50%");
		horizontalPanel_3.setCellVerticalAlignment(userIdTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHorizontalAlignment(userIdTextBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		userIdTextBox.setWidth("180px");
		final HorizontalPanel horizontalPanel_6 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_6);
		horizontalPanel_6
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblOrganization = new Label("Organization: ");
		horizontalPanel_6.add(lblOrganization);
		lblOrganization.setWidth("170px");
		horizontalPanel_6.setCellVerticalAlignment(lblOrganization,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_6.setCellHorizontalAlignment(lblOrganization,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6.setCellWidth(lblOrganization, "50%");
		lblOrganization
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		orgOracle = new MultiWordSuggestOracle();
		orgSuggestBox = new SuggestBox(orgOracle);
		orgSuggestBox.getTextBox().addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent arg0) {
				if (orgDeptsList != null) {
					for (int i = 0; i < orgDeptsList.size(); i++) {
						orgOracle.add(orgDeptsList.get(i).orgName);
					}
				} else {
					log.warning("orgDeptsList is null in orgSuggestBox.onFocus()");
				}
			}
		});
//		orgSuggestBox.addValueChangeHandler(new ValueChangeHandler<String>() {
//			public void onValueChange(ValueChangeEvent<String> arg0) {
//				deptSuggestBox.setEnabled(true);
//			}
//		});
		horizontalPanel_6.add(orgSuggestBox);
		horizontalPanel_6.setCellVerticalAlignment(orgSuggestBox, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_6.setCellHorizontalAlignment(orgSuggestBox, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6.setCellWidth(orgSuggestBox, "50%");
		orgSuggestBox.setWidth("180px");
		
		HorizontalPanel horizontalPanel_14 = new HorizontalPanel();
		horizontalPanel_14.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_14.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_1.add(horizontalPanel_14);
		
		Label lblDepartment = new Label("Department: ");
		lblDepartment.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_14.add(lblDepartment);
		lblDepartment.setWidth("170px");
		
		deptOracle = new MultiWordSuggestOracle();
		deptSuggestBox = new SuggestBox(deptOracle);
		deptSuggestBox.getTextBox().addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent arg0) {
				deptOracle.clear();
				String selectedOrg = orgSuggestBox.getValue();
				for (int i = 0; i < orgDeptsList.size(); i++) {
					OrgDepts orgDepts = orgDeptsList.get(i);
					if (selectedOrg.equals(orgDepts.orgName)) {
						for (int j = 0; j < orgDepts.deptNames.length; j++) {
							deptOracle.add(orgDepts.deptNames[j]);
						}
						break;
					}
				}				
			}
			
		});
		
		horizontalPanel_14.add(deptSuggestBox);
		horizontalPanel_14.setCellVerticalAlignment(deptSuggestBox, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_14.setCellHorizontalAlignment(deptSuggestBox, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_14.setCellWidth(deptSuggestBox, "50%");
		deptSuggestBox.setWidth("180px");
		
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
		emailTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				if (allFieldsFilled()) {
					okButton.setEnabled(true);
				}
			}
		});
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
		horizontalPanel_8
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_8
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblRole = new Label("Role: ");
		horizontalPanel_8.add(lblRole);
		horizontalPanel_8.setCellHorizontalAlignment(lblRole,
				HasHorizontalAlignment.ALIGN_CENTER);
		lblRole.setWidth("170px");
		horizontalPanel_8.setCellVerticalAlignment(lblRole,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_8.setCellWidth(lblRole, "50%");
		lblRole.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		roleComboBox = new ListBox();
		roleComboBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				if (allFieldsFilled()) {
					okButton.setEnabled(true);
				}
			}
		});
		horizontalPanel_8.add(roleComboBox);
		horizontalPanel_8.setCellWidth(roleComboBox, "50%");
		horizontalPanel_8.setCellVerticalAlignment(roleComboBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_8.setCellHorizontalAlignment(roleComboBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		roleComboBox.addItem(Role.USER.name());
		roleComboBox.addItem(Role.TOOL_PROVIDER.name());
		roleComboBox.addItem(Role.DEPT_ANALYST.name());
		roleComboBox.addItem(Role.ORG_ANALYST.name());
		roleComboBox.addItem(Role.ANALYST.name());
		roleComboBox.addItem(Role.ADMIN.name());
		
		roleComboBox.setWidth("190px");
		final HorizontalPanel horizontalPanel_13 = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel_13);
		horizontalPanel_13
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_13
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_13.setWidth("366px");
		changePasswordCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (changePasswordCheckBox.isChecked()) {
					password1TextBox.setEnabled(true);
					password2TextBox.setEnabled(true);
				} else {
					password1TextBox.setText(null);
					password1TextBox.setEnabled(false);
					password2TextBox.setText(null);
					password2TextBox.setEnabled(false);
				}
			}
		});
		horizontalPanel_13.add(changePasswordCheckBox);
		horizontalPanel_13.setCellVerticalAlignment(changePasswordCheckBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		final Label lblNewLabel_2 = new Label("Change Password");
		lblNewLabel_2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_13.add(lblNewLabel_2);
		horizontalPanel_13.setCellVerticalAlignment(lblNewLabel_2,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblNewLabel_2.setWidth("150px");
		password1TextBox = new PasswordTextBox();
		horizontalPanel_13.add(password1TextBox);
		password1TextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				if (allFieldsFilled()) {
					okButton.setEnabled(true);
				}
			}
		});
		password1TextBox.setEnabled(false);
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
		//horizontalPanel_4.add(passwordLabel);
		//passwordLabel.setWidth("170px");
		//horizontalPanel_4.setCellWidth(passwordLabel, "50%");
		//horizontalPanel_4.setCellVerticalAlignment(passwordLabel,
		//		HasVerticalAlignment.ALIGN_MIDDLE);
		//horizontalPanel_4.setCellHorizontalAlignment(passwordLabel,
		//		HasHorizontalAlignment.ALIGN_CENTER);
		
		Label lblNewLabel_3 = new Label("Password (again)");
		lblNewLabel_3.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_4.add(lblNewLabel_3);
		lblNewLabel_3.setWidth("170px");
		password2TextBox = new PasswordTextBox();
		horizontalPanel_4.add(password2TextBox);
		password2TextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				if (allFieldsFilled()) {
					okButton.setEnabled(true);
				}
			}
		});
		password2TextBox.setEnabled(false);
		password2TextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		password2TextBox.setAlignment(TextAlignment.LEFT);
		password2TextBox.setSize("180px", "");
		//passwordLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
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
		okButton = new PushButton("Submit");
		okButton.setEnabled(false);
		horizontalPanel.add(okButton);
		horizontalPanel.setCellHorizontalAlignment(okButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(okButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		okButton.setSize("70px", "18px");
		// If not a new user, set fields with the user's current info.
		if (!newUser) {
			lastNameTextBox.setText(userInfo.getLastName());
			firstNameTextBox.setText(userInfo.getFirstName());
			userIdTextBox.setText(userInfo.getUserName());
			userIdTextBox.setReadOnly(true);
			String lastLoginStr = dateTimeFormat.format(userInfo.getLastLogon());
			lastLogonTextBox.setText(lastLoginStr);
			fromHostTextBox.setText(userInfo.getFromHost());
			orgSuggestBox.setText(userInfo.getOrganization());
			deptSuggestBox.setText(userInfo.getDepartment());
			emailTextBox.setText(userInfo.getEmail());		
			
			if (userInfo.getRole().equals(Role.USER.name())) {
				roleComboBox.setSelectedIndex(0);
			} else if (userInfo.getRole().equals(Role.TOOL_PROVIDER.name())) {
				roleComboBox.setSelectedIndex(1);
			} else if (userInfo.getRole().equals(Role.DEPT_ANALYST.name())) {
				roleComboBox.setSelectedIndex(2);
			} else if (userInfo.getRole().equals(Role.ORG_ANALYST.name())) {
				roleComboBox.setSelectedIndex(3);
			} else if (userInfo.getRole().equals(Role.ANALYST.name())) {
				roleComboBox.setSelectedIndex(4);
			} else if (userInfo.getRole().equals(Role.ADMIN.name())) {
				roleComboBox.setSelectedIndex(5);
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
		if (!lastNameTextBox.getText().isEmpty() &&
				!firstNameTextBox.getText().isEmpty() &&
				!userIdTextBox.getText().isEmpty() &&
				!orgSuggestBox.getText().isEmpty() &&
				!deptSuggestBox.getText().isEmpty() &&
				!emailTextBox.getText().isEmpty()) {
			if (newUser) {
				if (!password1TextBox.getText().isEmpty() &&
						!password2TextBox.getText().isEmpty()) {
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
	
	
	public void getOrgDeptList() {
		appVetServiceAsync.getOrgDeptsList(new AsyncCallback<List<OrgDepts>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				log.severe("Got error trying to get org dept list");
				showMessageDialog("AppVet Error", "Could not retrieve orgs/depts",
						true);
			}

			@Override
			public void onSuccess(List<OrgDepts> result) {
				orgDeptsList = result;
			}
		});
	}
}
