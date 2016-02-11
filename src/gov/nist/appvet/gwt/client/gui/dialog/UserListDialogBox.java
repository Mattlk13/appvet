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
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.all.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * @author steveq@nist.gov
 */
public class UserListDialogBox extends DialogBox {
	private static Logger log = Logger.getLogger("UserListDialogBox");

	class UserListHandler implements SelectionChangeEvent.Handler {
		UserListDialogBox usersDialogBox = null;

		public UserListHandler(UserListDialogBox usersDialogBox) {
			this.usersDialogBox = usersDialogBox;
		}

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			user = usersSelectionModel.getSelectedObject();
		}
	}

	public PushButton doneButton = null;
	private final static GWTServiceAsync appVetServiceAsync = GWT
			.create(GWTService.class);
	public static MessageDialogBox messageDialogBox = null;
	public UserAcctAdminDialogBox userInfoDialogBox = null;

	public static void killDialogBox(DialogBox dialogBox) {
		if (dialogBox != null) {
			dialogBox.hide();
			dialogBox = null;
		}
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

	public List<UserInfo> allUsers = null;
	public SingleSelectionModel<UserInfo> usersSelectionModel = null;
	public UsersListPagingDataGrid<UserInfo> usersListTable = null;
	public UserInfo user = null;
	public boolean searchMode = true;
	public TextBox searchTextBox = null;
	public PushButton addButton = null;

	public UserListDialogBox() {
		super(false, true);
		setSize("", "450px");
		setAnimationEnabled(false);
		usersSelectionModel = new SingleSelectionModel<UserInfo>();
		usersSelectionModel
				.addSelectionChangeHandler(new UserListHandler(this));
		final DockPanel dockPanel = new DockPanel();
		dockPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		setWidget(dockPanel);
		dockPanel.setSize("", "417px");
		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setStyleName("usersCenterPanel");
		dockPanel.add(verticalPanel, DockPanel.CENTER);
		dockPanel.setCellVerticalAlignment(verticalPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel.setCellHorizontalAlignment(verticalPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setSize("", "416px");
		final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel_1.setStyleName("usersHorizPanel");
		horizontalPanel_1
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.add(horizontalPanel_1);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_1,
				HasHorizontalAlignment.ALIGN_CENTER);
		searchTextBox = new TextBox();
		searchTextBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					searchMode = true;
					search();
				}
			}
		});
		horizontalPanel_1.add(searchTextBox);
		horizontalPanel_1.setCellVerticalAlignment(searchTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		searchTextBox.setSize("260px", "18px");
		final PushButton searchButton = new PushButton("Search");
		searchButton.setTitle("Search Users");
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
			}
		});
		searchButton
				.setHTML("<img width=\"18px\" src=\"images/icon-search-up.png\" alt=\"Search\" />");
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchMode = true;
				search();
			}
		});
		horizontalPanel_1.add(searchButton);
		horizontalPanel_1.setCellVerticalAlignment(searchButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellHorizontalAlignment(searchButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		searchButton.setSize("18px", "18px");
		final PushButton viewAllButton = new PushButton("View All");
		viewAllButton.setTitle("View All Users");
		viewAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchMode = false;
				getAllUsers(allUsers);
			}
		});
		viewAllButton
				.setHTML("<img width=\"18px\" src=\"images/icon-view-all-up.png\" alt=\"View All\" />");
		horizontalPanel_1.add(viewAllButton);
		horizontalPanel_1.setCellHorizontalAlignment(viewAllButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellVerticalAlignment(viewAllButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		viewAllButton.setSize("18px", "18px");
		final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		dockLayoutPanel.setStyleName("usersDockPanel");
		verticalPanel.add(dockLayoutPanel);
		verticalPanel.setCellVerticalAlignment(dockLayoutPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(dockLayoutPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		dockLayoutPanel.setSize("", "380px");
		usersListTable = new UsersListPagingDataGrid<UserInfo>();
		usersListTable.dataGrid.setSize("342px", "342px");
		usersListTable.dataGrid
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
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
		verticalPanel.setCellVerticalAlignment(horizontalPanel_2,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_2,
				HasHorizontalAlignment.ALIGN_CENTER);
		addButton = new PushButton("Add");
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				editUser(true);
			}
		});
		addButton.setHTML("Add");
		horizontalPanel_2.add(addButton);
		horizontalPanel_2.setCellHorizontalAlignment(addButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(addButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		addButton.setSize("70px", "18px");
		final PushButton editButton = new PushButton("Edit");
		editButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				editUser(false);
			}
		});
		final PushButton pshbtnNewButton = new PushButton("Delete");
		pshbtnNewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final UserInfo selected = usersSelectionModel
						.getSelectedObject();
				final YesNoConfirmDialog deleteConfirmDialogBox = new YesNoConfirmDialog(
						"<p align=\"center\">\r\nAre you sure you want to delete user"
								+ " '" + selected.getUserName() + "'?\r\n</p>");
				deleteConfirmDialogBox.setText("Confirm Delete");
				deleteConfirmDialogBox.center();
				deleteConfirmDialogBox.cancelButton.setFocus(true);
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
									deleteUser(user.getUserName());
								}
							}
						});
			}
		});
		pshbtnNewButton.setHTML("Delete");
		horizontalPanel_2.add(pshbtnNewButton);
		horizontalPanel_2.setCellVerticalAlignment(pshbtnNewButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setCellHorizontalAlignment(pshbtnNewButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		pshbtnNewButton.setSize("70px", "18px");
		editButton.setHTML("Edit");
		horizontalPanel_2.add(editButton);
		horizontalPanel_2.setCellHorizontalAlignment(editButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(editButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		editButton.setSize("70px", "18px");
		doneButton = new PushButton("Done");
		doneButton.setHTML("Done");
		horizontalPanel_2.add(doneButton);
		horizontalPanel_2.setCellHorizontalAlignment(doneButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(doneButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		doneButton.setSize("70px", "18px");
		getUsersList();
		//getOrgDeptList();
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

	@SuppressWarnings("deprecation")
	public void editUser(final boolean newUser) {

		if (newUser) {
			userInfoDialogBox = new UserAcctAdminDialogBox(null,
					usersListTable, allUsers);
			userInfoDialogBox.setText("Add User");
			userInfoDialogBox.changePasswordCheckBox.setChecked(true);
			userInfoDialogBox.changePasswordCheckBox.setEnabled(false);
			userInfoDialogBox.password1TextBox.setEnabled(true);
			userInfoDialogBox.password2TextBox.setEnabled(true);
			userInfoDialogBox.lastNameTextBox.setFocus(true);
		} else {
			user = usersSelectionModel.getSelectedObject();
			userInfoDialogBox = new UserAcctAdminDialogBox(user,
					usersListTable, allUsers);
			userInfoDialogBox.changePasswordCheckBox.setChecked(false);
			userInfoDialogBox.password1TextBox.setEnabled(false);
			userInfoDialogBox.password2TextBox.setEnabled(false);
			userInfoDialogBox.lastNameTextBox.setFocus(true);

		}
		userInfoDialogBox.center();
		userInfoDialogBox.cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				userInfoDialogBox.hide();
				userInfoDialogBox = null;
			}
		});
		userInfoDialogBox.okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final String newPassword1 = userInfoDialogBox.password1TextBox
						.getValue();
				final String newPassword2 = userInfoDialogBox.password2TextBox
						.getValue();
				final UserInfo userInfo = new UserInfo();
				userInfo.setUserName(userInfoDialogBox.userIdTextBox.getText());
				userInfo.setChangePassword(userInfoDialogBox.changePasswordCheckBox
						.getValue().booleanValue());
				userInfo.setPasswords(newPassword1, newPassword2);
				userInfo.setLastName(userInfoDialogBox.lastNameTextBox
						.getText());
				userInfo.setFirstName(userInfoDialogBox.firstNameTextBox
						.getText());
				userInfo.setOrganization(userInfoDialogBox.orgSuggestBox
						.getText());
				userInfo.setDepartment(userInfoDialogBox.deptSuggestBox
						.getText());				
				userInfo.setEmail(userInfoDialogBox.emailTextBox.getText());
				final int selectedRoleIndex = userInfoDialogBox.roleComboBox
						.getSelectedIndex();
				userInfo.setRole(userInfoDialogBox.roleComboBox
						.getValue(selectedRoleIndex));
				if (userInfoDialogBox.newUser) {
					userInfo.setNewUser(true);
				}
				if (!userInfo.isValid()) {
					return;
				}
				submitUserInfo(userInfo);
				userInfoDialogBox.hide();
				userInfoDialogBox = null;
			}
		});
	}

	public boolean fieldsValid(UserAcctAdminDialogBox userInfoDialogBox) {
		final String userName = userInfoDialogBox.userIdTextBox.getText();
		if (!Validate.isValidUserName(userName)) {
			showMessageDialog("AppVet Error", "Invalid User ID", true);
			return false;
		} else if (userInfoDialogBox.newUser) {
			for (int i = 0; i < allUsers.size(); i++) {
				final UserInfo userInfo = allUsers.get(i);
				final String dbUserName = userInfo.getUserName();
				if (userName.equals(dbUserName)) {
					showMessageDialog("AppVet Error", "User " + dbUserName
							+ " already exists.", true);
					return false;
				}
			}
		}
		final boolean changePassword = userInfoDialogBox.changePasswordCheckBox
				.getValue();
		final String password1 = userInfoDialogBox.password1TextBox.getText();
		final String password2 = userInfoDialogBox.password2TextBox.getText();
		if (changePassword) {
			if (!Validate.isValidPassword(password1)) {
				showMessageDialog("AppVet Error", "Invalid password", true);
				return false;
			} else if (!password1.equals(password2)) {
				showMessageDialog("AppVet Error", "Passwords do not match",
						true);
				return false;
			}
		}
//		if (!Validate.isPrintable(userInfoDialogBox.organizationTextBox
//				.getText())) {
//			showMessageDialog("AppVet Error", "Invalid Organization", true);
//			return false;
//		}
//		
//		if (!Validate.isPrintable(userInfoDialogBox.departmentTextBox
//				.getText())) {
//			showMessageDialog("AppVet Error", "Invalid Department", true);
//			return false;
//		}
		
		if (!Validate.isValidEmail(userInfoDialogBox.emailTextBox.getText())) {
			showMessageDialog("AppVet Error", "Invalid Email", true);
			return false;
		}
		return true;
	}

	public void getAllUsers(List<UserInfo> allUsers) {
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
		appVetServiceAsync.getUsersList(new AsyncCallback<List<UserInfo>>() {
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
				} else if ((usersList != null) && (usersList.size() > 0)) {
					getAllUsers(usersList);
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
		final String[] tokens = searchTextBox.getValue().split("\\s+");
		if (tokens != null) {
			final ArrayList<UserInfo> searchList = new ArrayList<UserInfo>();
			for (int i = 0; i < tokens.length; i++) {
				if (Validate.isLegalSearchString(tokens[i])) {
					for (int j = 0; j < allUsers.size(); j++) {
						final UserInfo userInfo = allUsers.get(j);
						if (userInfo.tokenMatch(tokens[i])) {
							searchList.add(userInfo);
						}
					}
				}
			}
			usersListTable.setDataList(searchList);
			if (searchList.size() > 0) {
				usersSelectionModel.setSelected(searchList.get(0), true);
			}
		}
	}

	public void submitUserInfo(UserInfo userInfo) {
		appVetServiceAsync.adminSetUser(userInfo,
				new AsyncCallback<List<UserInfo>>() {
			
					@Override
					public void onFailure(Throwable caught) {
						showMessageDialog("AppVet Error",
								"App list retrieval error", true);
					}

					@Override
					public void onSuccess(List<UserInfo> result) {
						if (result.size() == 0) {
							showMessageDialog("Update Error",
									"Error adding or updating user", true);
						} else {
							showMessageDialog("Update Status",
									"User added or updated successfully", false);
							allUsers = result;
							getAllUsers(allUsers);
						}
					}
				});
	}
}
