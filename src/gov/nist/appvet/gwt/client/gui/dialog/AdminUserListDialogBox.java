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
import gov.nist.appvet.gwt.shared.AppInfoGwt;
import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.all.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * @author steveq@nist.gov
 */
public class AdminUserListDialogBox extends DialogBox {
	private Logger log = Logger.getLogger("AdminUserListDialogBox");
	public FocusPanel focusPanel = null;
	public PushButton doneButton = null;
	private final GWTServiceAsync appVetServiceAsync = GWT
			.create(GWTService.class);
	public MessageDialogBox messageDialogBox = null;
	public AdminUserAcctDialogBox userAcctAdminDialogBox = null;
	public List<UserInfo> allUsers = null;
	public SingleSelectionModel<UserInfo> usersSelectionModel = null;
	public UsersListPagingDataGrid<UserInfo> usersListTable = null;
	public UserInfo selectedUser = null;
	public boolean searchMode = true;
	public TextBox searchTextBox = null;
	public PushButton addUserButton = null;
	public List<String> allUsersOrgMemberships = new ArrayList<String>();
	public enum BadFieldValue {last_name, first_name, username, email, org, role_memb, password};
	public BadFieldValue badField = null;

	public AdminUserListDialogBox(final ConfigInfoGwt configInfo, final boolean useSSO) {
		super(false, true);
		setSize("", "450px");
		setAnimationEnabled(false);
		usersSelectionModel = new SingleSelectionModel<UserInfo>();
		usersSelectionModel
		.addSelectionChangeHandler(new UserListHandler(this));
		//final DockPanel dockPanel = new DockPanel();
		focusPanel = new FocusPanel();
		setWidget(focusPanel);
		focusPanel.setSize("100%", "100%");

		//		dockPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		//		dockPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		//		setWidget(dockPanel);
		//		dockPanel.setSize("", "417px");

		//				dockPanel.add(focusPanel, DockPanel.CENTER);
		final VerticalPanel verticalPanel = new VerticalPanel();
		focusPanel.setWidget(verticalPanel);
		verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setStyleName("usersCenterPanel");
		verticalPanel.setSize("", "");
		final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel_1.setStyleName("usersHorizPanel");
		horizontalPanel_1
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel_1);
		horizontalPanel_1.setWidth("365px");
		verticalPanel.setCellVerticalAlignment(horizontalPanel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		searchTextBox = new TextBox();
		searchTextBox.setTitle("Search users");
		searchTextBox.setName("Search users");
		searchTextBox.setStyleName("h1");
		searchTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		searchTextBox.setAlignment(TextAlignment.LEFT);

		searchTextBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchTextBox.setText("");
			}
		});

/*		// THE FOLLOWING WAS REMOVED AS IT PREVENTS 508 TAB NAVIGATION
		searchTextBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event_) {
				final boolean enterPressed = KeyCodes.KEY_ENTER == event_
						.getNativeEvent().getKeyCode();
				final String searchString = searchTextBox.getText();
				if (enterPressed) {
					search();
				}
			}
		});

		searchTextBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public final void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == 9) {
					event.preventDefault();
					event.stopPropagation();
					if(event.getSource() instanceof TextBox) {
						TextBox ta = (TextBox) event.getSource();
						int index = ta.getCursorPos();
						String text = ta.getText();
						ta.setText(text.substring(0, index) 
								+ "\t" + text.substring(index));
						ta.setCursorPos(index + 1);
					}
				}
			}
		});*/
		
		
		horizontalPanel_1.add(searchTextBox);
		horizontalPanel_1.setCellWidth(searchTextBox, "60%");
		horizontalPanel_1.setCellVerticalAlignment(searchTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		searchTextBox.setSize("200px", "18px");
		final PushButton searchButton = new PushButton("Search");
		searchButton.setTitle("Search users");
		searchButton.setStyleName("grayButton shadow");
		searchButton.setTitle("Search Users");

		horizontalPanel_1.add(searchButton);
		horizontalPanel_1.setCellWidth(searchButton, "20%");
		horizontalPanel_1.setCellVerticalAlignment(searchButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellHorizontalAlignment(searchButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		searchButton.setSize("55px", "18px");
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search();
			}
		});
		
		
		final PushButton viewAllButton = new PushButton("View All");
		viewAllButton.setTitle("View all users");
		viewAllButton.setStyleName("grayButton shadow");
		viewAllButton.setTitle("View All Users");
		viewAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchMode = false;
				setAllUsers(allUsers);
			}
		});
		horizontalPanel_1.add(viewAllButton);
		horizontalPanel_1.setCellWidth(viewAllButton, "20%");
		horizontalPanel_1.setCellHorizontalAlignment(viewAllButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellVerticalAlignment(viewAllButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		viewAllButton.setSize("55px", "18px");
		final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		dockLayoutPanel.setStyleName("usersDockPanel");
		verticalPanel.add(dockLayoutPanel);
		verticalPanel.setCellVerticalAlignment(dockLayoutPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(dockLayoutPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		dockLayoutPanel.setSize("", "380px");
		usersListTable = new UsersListPagingDataGrid<UserInfo>();
		usersListTable.setTitle("Users list");
		usersListTable.setPageSize(configInfo.getNumRowsUsersList());
		usersListTable.dataGrid.setSize("370px", "342px");
		usersListTable.dataGrid.setSelectionModel(usersSelectionModel);
		dockLayoutPanel.add(usersListTable);
		usersListTable.setWidth("");
		final HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		horizontalPanel_2
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setStyleName("buttonPanel");
		verticalPanel.add(horizontalPanel_2);
		horizontalPanel_2.setWidth("371px");
		verticalPanel.setCellWidth(horizontalPanel_2, "365px");
		verticalPanel.setCellVerticalAlignment(horizontalPanel_2,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_2,
				HasHorizontalAlignment.ALIGN_CENTER);
		addUserButton = new PushButton("Add");
		addUserButton.setTitle("Add user");
		addUserButton.setStyleName("grayButton shadow");
		addUserButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				editUser(configInfo, true, useSSO);
			}
		});
		addUserButton.setHTML("Add");
		horizontalPanel_2.add(addUserButton);
		horizontalPanel_2.setCellWidth(addUserButton, "25%");
		horizontalPanel_2.setCellHorizontalAlignment(addUserButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(addUserButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		addUserButton.setSize("70px", "18px");
		final PushButton editUserButton = new PushButton("Edit");
		editUserButton.setTitle("Edit user");
		editUserButton.setStyleName("grayButton shadow");
		editUserButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				editUser(configInfo, false, useSSO);
			}
		});
		final PushButton deleteUserButton = new PushButton("Delete");
		deleteUserButton.setStyleName("grayButton shadow");
		deleteUserButton.setTitle("Delete user");
		deleteUserButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final UserInfo selected = usersSelectionModel
						.getSelectedObject();
				final YesNoConfirmDialog deleteConfirmDialogBox = new YesNoConfirmDialog(
						"<p align=\"center\">\r\nAre you sure you want to delete user"
								+ " '" + selected.getUserName() + "'?\r\n</p>");
				deleteConfirmDialogBox.setText("Confirm Delete");
				deleteConfirmDialogBox.center();
				deleteConfirmDialogBox.cancelButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(deleteConfirmDialogBox);
						return;
					}
				});
				deleteConfirmDialogBox.okButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(deleteConfirmDialogBox);
						if (selected != null) {
							deleteUser(selectedUser.getUserName());
						}
					}
				});
			}
		});
		deleteUserButton.setHTML("Delete");
		horizontalPanel_2.add(deleteUserButton);
		horizontalPanel_2.setCellWidth(deleteUserButton, "25%");
		horizontalPanel_2.setCellVerticalAlignment(deleteUserButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setCellHorizontalAlignment(deleteUserButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		deleteUserButton.setSize("70px", "18px");
		editUserButton.setHTML("Edit");
		horizontalPanel_2.add(editUserButton);
		horizontalPanel_2.setCellWidth(editUserButton, "25%");
		horizontalPanel_2.setCellHorizontalAlignment(editUserButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(editUserButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		editUserButton.setSize("70px", "18px");
		doneButton = new PushButton("Done");
		doneButton.setTitle("Done");
		doneButton.setStyleName("greenButton shadow");
		doneButton.setHTML("Done");
		horizontalPanel_2.add(doneButton);
		horizontalPanel_2.setCellWidth(doneButton, "25%");
		horizontalPanel_2.setCellHorizontalAlignment(doneButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(doneButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		doneButton.setSize("70px", "18px");
		getUsersList();
	}

	public void deleteUser(final String username) {
		appVetServiceAsync.deleteUser(username, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				showMessageDialog("AppVet Error", "User deletion error", true);
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					allUsers = usersListTable.deleteUser(username);
				} else {
					showMessageDialog("AppVet Error", "Error deleting user "
							+ username, true);
				}
			}
		});
	}

	public void editUser(final ConfigInfoGwt configInfo, 
			final boolean newUser, final boolean ssoActive) {

		if (newUser) {
			userAcctAdminDialogBox = 
					new AdminUserAcctDialogBox(configInfo, null, ssoActive, allUsersOrgMemberships);
			userAcctAdminDialogBox.setText("Add User");
			userAcctAdminDialogBox.lastNameTextBox.setFocus(true);

		} else {
			selectedUser = usersSelectionModel.getSelectedObject();
			if (selectedUser.isDefaultAdmin()) {
				showMessageDialog("Account Info", "Cannot change info for "
						+ "default AppVet administrator", false);
				return;
			}
			userAcctAdminDialogBox = 
					new AdminUserAcctDialogBox(configInfo, selectedUser, ssoActive, allUsersOrgMemberships);
			userAcctAdminDialogBox.setText(selectedUser.getFirstName() + " "
					+ selectedUser.getLastName());
			userAcctAdminDialogBox.lastNameTextBox.setFocus(true);
		}
		userAcctAdminDialogBox.center();
		userAcctAdminDialogBox.cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				userAcctAdminDialogBox.hide();
				userAcctAdminDialogBox = null;
			}
		});

		userAcctAdminDialogBox.submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Verify last name
				final String newLastName = userAcctAdminDialogBox.lastNameTextBox.getText();
				if (!Validate.isAlpha(newLastName)) {
					badField = BadFieldValue.last_name;
					showMessageDialog("Account Setting Error", "Invalid last name",
							true);
					return;
				}
				// Verify first name
				final String newFirstName = userAcctAdminDialogBox.firstNameTextBox.getText();
				if (!Validate.isAlpha(newFirstName)) {
					badField = BadFieldValue.first_name;
					showMessageDialog("Account Setting Error", "Invalid first name",
							true);
					return;
				}
				// Verify username
				final String newUserName = userAcctAdminDialogBox.userIdTextBox.getText();
				if (!Validate.isValidUserName(newUserName)) {
					badField = BadFieldValue.username;
					showMessageDialog("Account Setting Error", "Invalid username", true);
					return;
				}
				
				// Verify email
				final String newEmail = userAcctAdminDialogBox.emailTextBox.getText();
				if (!Validate.isValidEmail(newEmail)) {
					badField = BadFieldValue.email;
					showMessageDialog("Account Setting Error", "Invalid email", true);
					return;
				}
				// Verify organization (no need to check since organization can contain any characters)
				final String orgMembership = userAcctAdminDialogBox.orgMembershipTextBox.getText();
				
				// Verify role and membership
				final String newRoleAndMembership = userAcctAdminDialogBox.getRoleAndMembership(orgMembership);
				if (newRoleAndMembership == null || newRoleAndMembership.isEmpty()) {
					badField = BadFieldValue.role_memb;
					showMessageDialog("Account Setting Error", "Invalid Role and/or Membership", true);
					return;
				}
				
				// Verify password
				final String newPassword1 = userAcctAdminDialogBox.password1TextBox
						.getValue();
				final String newPassword2 = userAcctAdminDialogBox.password2TextBox
						.getValue();
				if (!ssoActive) {
					// Password is required for NON-SSO mode
					//String password = newPassword1;
					//String passwordAgain = userInfo.getPasswordAgain();
					if (newPassword1 != null && !newPassword1.isEmpty()
							&& newPassword2 != null && !newPassword2.isEmpty()) {
						if (!Validate.isValidPassword(newPassword1)) {
							badField = BadFieldValue.password;
							showMessageDialog("Account Setting Error",
									"Invalid password", true);
							return;
						}
						if (!newPassword1.equals(newPassword2)) {
							badField = BadFieldValue.password;
							showMessageDialog("Account Setting Error",
									"Passwords do not match", true);
							userAcctAdminDialogBox.password1TextBox.setFocus(true);
							return;
						}
					} else {
						if (newUser) {
							badField = BadFieldValue.password;
							showMessageDialog("Account Setting Error",
									"Password is empty or null", true);
							return;
						}
					}
				} else {
					// SSO is active so we ignore password fields (since passwords
					// are handled by the organization's SSO environment).
				}
				
				// If all fields valid, set userInfo object and submit
				final UserInfo userInfo = new UserInfo();
				userInfo.setUserName(newUserName);
				userInfo.setPasswords(newPassword1, newPassword2);
				userInfo.setLastName(newLastName);
				userInfo.setFirstName(newFirstName);
				userInfo.setEmail(newEmail);
				userInfo.setRoleAndOrgMembership(newRoleAndMembership);
				if (newUser) {
					userInfo.setNewUser(true);
				}
//
//				// Validate new user info
//				if (!userInfoIsValid(userInfo, ssoActive)) {
//					return;
//				}

				// Send updated user info to server
				submitUserInfo(newUser, userInfo);
				userAcctAdminDialogBox.hide();
				userAcctAdminDialogBox = null;
			}
		});
	}

	public void setAllUsers(List<UserInfo> allUsers) {
		final UserInfo currentlySelectedUser = usersSelectionModel
				.getSelectedObject();
		int currentlySelectedIndex = 0;
		if (currentlySelectedUser != null) {
			currentlySelectedIndex = getUsersListIndex(currentlySelectedUser,
					allUsers);
		}
		usersListTable.setDataList(allUsers);
		if (allUsers.size() > 0) {
			usersSelectionModel.setSelected(
					allUsers.get(currentlySelectedIndex), true);
		}
	}

	public void getUsersList() {
		appVetServiceAsync.getAllUsers(new AsyncCallback<List<UserInfo>>() {
			@Override
			public void onFailure(Throwable caught) {
				showMessageDialog("AppVet Error", "Could not retrieve users",
						true);
			}

			@Override
			public void onSuccess(List<UserInfo> usersList) {
				allUsers = usersList;
				if (usersList == null) {
					showMessageDialog("AppVet Error", "No users available",
							true);
					return;
				} else if ((usersList != null) && (usersList.size() > 0)) {
					setAllUsers(usersList);
				}
				// Set org membership list
				try {
					allUsersOrgMemberships.clear();
					for (int i = 0; i < usersList.size(); i++) {
						UserInfo userInfo = usersList.get(i);
						String orgMembershipString = Role.getOrgMembershipLevelsStr(userInfo.getRoleAndOrgMembership());
						if (orgMembershipString != null) {
							allUsersOrgMemberships.add(orgMembershipString);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public int getUsersListIndex(UserInfo item, List<UserInfo> usersList) {
		if (item != null) {
			for (int i = 0; i < usersList.size(); i++) {
				if (item.getLastName().equals(usersList.get(i).getLastName())) {
					return i;
				}
			}
		}
		return 0;
	}
	
	public void search() {
		if (searchTextBox.getValue() == null || searchTextBox.getValue().isEmpty()) {
			log.info("Search box is empty");
		} else {
			log.info("Searching");
			final String[] tokens = searchTextBox.getValue().split("\\s+");
			if (tokens != null) {
				log.info("trace 1");
				final ArrayList<UserInfo> searchList = new ArrayList<UserInfo>();
				log.info("trace 2");

				for (int i = 0; i < tokens.length; i++) {
					log.info("trace 3");

					if (Validate.isLegalSearchString(tokens[i])) {
						log.info("trace 4");

						for (int j = 0; j < allUsers.size(); j++) {
							log.info("trace 5");

							final UserInfo userInfo = allUsers.get(j);
							log.info("trace 6");

							if (userInfo.tokenMatch(tokens[i])) {
								log.info("trace 7");

								searchList.add(userInfo);
								log.info("trace 8");

							}
						}
					}
				}
				log.info("trace 9");

				usersListTable.setDataList(searchList);
				log.info("trace 10");

				if (searchList.size() > 0) {
					log.info("trace 11");

					usersSelectionModel.setSelected(searchList.get(0), true);
					log.info("trace 12");

				}
			}
		}

	}

	public void submitUserInfo(final boolean newUser, final UserInfo userInfo) {
		appVetServiceAsync.adminSetUser(userInfo,
				new AsyncCallback<List<UserInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				log.severe(caught.getMessage());
				showMessageDialog("AppVet Error",
						"User update retrieval error", true);
			}

			@Override
			public void onSuccess(List<UserInfo> result) {
				if (result.size() == 0) {
					showMessageDialog("Update Error",
							"Error adding or updating user", true);
				} else {
					if (newUser) {
						showMessageDialog("Update Status", "User '"
								+ userInfo.getUserName()
								+ "' added successfully", false);
					} else {
						showMessageDialog("Update Status", "User '"
								+ userInfo.getUserName()
								+ "' updated successfully", false);
					}

					allUsers = result;
					setAllUsers(allUsers);
					// Set org hierarchies list
					try {
						allUsersOrgMemberships.clear();
						for (int i = 0; i < result.size(); i++) {
							UserInfo userInfo = result.get(i);
							String orgMembershipString = Role.getOrgMembershipLevelsStr(userInfo.getRoleAndOrgMembership());
							if (orgMembershipString != null) {
								allUsersOrgMemberships.add(orgMembershipString);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	class UserListHandler implements SelectionChangeEvent.Handler {
		AdminUserListDialogBox usersDialogBox = null;

		public UserListHandler(AdminUserListDialogBox usersDialogBox) {
			this.usersDialogBox = usersDialogBox;
		}

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			selectedUser = usersSelectionModel.getSelectedObject();
		}
	}

	public void killDialogBox(DialogBox dialogBox) {
		if (dialogBox != null) {
			dialogBox.hide();
			dialogBox = null;
		}
	}

//	public boolean userInfoIsValid(UserInfo userInfo, boolean ssoActive) {
//
////		if (!Validate.isValidUserName(userInfo.getUserName())) {
////			dialog.userIdTextBox.setFocus(true);
////			showMessageDialog("Account Setting Error", "Invalid username", true);
////			return false;
////		}
//
////		if (!Validate.isAlpha(userInfo.getLastName())) {
////			dialog.lastNameTextBox.setFocus(true);
////			showMessageDialog("Account Setting Error", "Invalid last name",
////					true);
////			return false;
////		}
//
////		if (!Validate.isAlpha(userInfo.getFirstName())) {
////			dialog.firstNameTextBox.setFocus(true);
////			showMessageDialog("Account Setting Error", "Invalid first name",
////					true);
////			return false;
////		}
//
////		if (!Validate.isValidEmail(userInfo.getEmail())) {
////			dialog.emailTextBox.setFocus(true);
////			showMessageDialog("Account Setting Error", "Invalid email", true);
////			return false;
////		}
//
////		if (!ssoActive) {
////			// Password is required for NON-SSO mode
////			String password = userInfo.getPassword();
////			String passwordAgain = userInfo.getPasswordAgain();
////			if (password != null && !password.isEmpty()
////					&& passwordAgain != null && !passwordAgain.isEmpty()) {
////				if (!Validate.isValidPassword(password)) {
////					dialog.password1TextBox.setFocus(true);
////					showMessageDialog("Account Setting Error",
////							"Invalid password", true);
////					return false;
////				}
////				if (!password.equals(passwordAgain)) {
////					dialog.password1TextBox.setFocus(true);
////					showMessageDialog("Account Setting Error",
////							"Passwords do not match", true);
////					return false;
////				}
////			} else {
////				if (userInfo.isNewUser()) {
////					dialog.password1TextBox.setFocus(true);
////					showMessageDialog("Account Setting Error",
////							"Password is empty or null", true);
////					return false;
////				} else {
////					return true;
////				}
////			}
////		} else {
////			// SSO is active so we ignore password fields (since passwords
////			// are handled by the organization's SSO environment).
////		}
////		dialog.submitButton.setFocus(true);
////		return true;
//	}

	public void showMessageDialog(String windowTitle, String message,
			boolean isError) {
		messageDialogBox = new MessageDialogBox(message, isError);
		messageDialogBox.setText(windowTitle);
		messageDialogBox.center();
		messageDialogBox.closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				messageDialogBox.hide();
				messageDialogBox = null;
				
			    Scheduler.get().scheduleDeferred(new Command() {
			        public void execute() {
			        	if (badField == null) {
			        		searchTextBox.setFocus(true);
			        		return;
			        	}
			        	
			        	switch (badField) {
			        	case last_name: 
			        		userAcctAdminDialogBox.lastNameTextBox.setFocus(true);
			        		break;
			        	case first_name:
			        		userAcctAdminDialogBox.firstNameTextBox.setFocus(true);
			        		break;
			        	case username:
			        		userAcctAdminDialogBox.userIdTextBox.setFocus(true);
			        		break;	
			        	case email:
			        		userAcctAdminDialogBox.emailTextBox.setFocus(true);
			        		break;	
			        	case org:
			        		userAcctAdminDialogBox.orgMembershipTextBox.setFocus(true);
			        		break;	
			        	case role_memb:
			        		userAcctAdminDialogBox.adminRadioButton.setFocus(true);
			        		break;	 		
			        	case password:
			        		userAcctAdminDialogBox.password1TextBox.setFocus(true);
			        		break;	
			        	default:
			        		searchTextBox.setFocus(true);
							break;
			        	}
			        }
			    });
			}
		});
	}
	
	/** This fixes focus for dialog boxes in Firefox and IE browsers */
	@Override
	public void show() {
	    super.show();
	    Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	searchTextBox.setFocus(true);
	        }
	    });
	}
}
