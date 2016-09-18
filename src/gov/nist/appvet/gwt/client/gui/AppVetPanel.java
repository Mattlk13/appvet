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
package gov.nist.appvet.gwt.client.gui;

import gov.nist.appvet.gwt.client.GWTService;
import gov.nist.appvet.gwt.client.GWTServiceAsync;
import gov.nist.appvet.gwt.client.gui.dialog.AboutDialogBox;
import gov.nist.appvet.gwt.client.gui.dialog.AppUploadDialogBox;
import gov.nist.appvet.gwt.client.gui.dialog.MessageDialogBox;
import gov.nist.appvet.gwt.client.gui.dialog.ReportUploadDialogBox;
import gov.nist.appvet.gwt.client.gui.dialog.SetAlertDialogBox;
import gov.nist.appvet.gwt.client.gui.dialog.ToolAuthParamDialogBox;
import gov.nist.appvet.gwt.client.gui.dialog.UserAcctDialogBox;
import gov.nist.appvet.gwt.client.gui.dialog.AdminUserListDialogBox;
import gov.nist.appvet.gwt.client.gui.dialog.YesNoConfirmDialog;
import gov.nist.appvet.gwt.client.gui.table.appslist.AppsListPagingDataGrid;
import gov.nist.appvet.gwt.shared.AppInfoGwt;
import gov.nist.appvet.gwt.shared.AppsListGwt;
import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.gwt.shared.SystemAlert;
import gov.nist.appvet.gwt.shared.SystemAlertType;
import gov.nist.appvet.gwt.shared.ToolInfoGwt;
import gov.nist.appvet.gwt.shared.ToolStatusGwt;
import gov.nist.appvet.shared.all.AppStatus;
import gov.nist.appvet.shared.all.AppVetParameter;
import gov.nist.appvet.shared.all.AppVetServletCommand;
import gov.nist.appvet.shared.all.DeviceOS;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.ToolType;
import gov.nist.appvet.shared.all.UserInfo;
import gov.nist.appvet.shared.all.Validate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * @author steveq@nist.gov
 */
@SuppressWarnings("deprecation")
public class AppVetPanel extends DockLayoutPanel {

	// See appvet.gwt.xml
	private final int NUM_APPS_SHOW_REFRESH_WARNING = 0;
	private Logger log = Logger.getLogger("AppVetPanel");
	private SingleSelectionModel<AppInfoGwt> appSelectionModel = null;
	private long MAX_SESSION_IDLE_DURATION = 0;
	private int POLLING_INTERVAL = 0;
	private final GWTServiceAsync appVetServiceAsync = GWT
			.create(GWTService.class);
	private HTML appInfoName = null;
	private HTML appInfoPackage = null;
	private HTML appInfoVersion = null;
	private Image appInfoIcon = null;
	private HTML toolResultsHtml = null;
	private AppsListPagingDataGrid<AppInfoGwt> appsListTable = null;
	private Date lastAppsListUpdate = null;
	private UserInfo userInfo = null;
	private String userName = null;
	private PushButton viewAllButton = null;
	private PushButton deleteButton = null;
	private PushButton downloadReportsButton = null;
	private PushButton uploadReportButton = null;
	private PushButton downloadAppButton = null;
	private PushButton logButton = null;
	private List<AppInfoGwt> allApps = null;
	private TextBox searchTextBox = null;
	private String sessionId = null;
	private Date sessionExpiration = null;
	private Timer pollingTimer = null;
	private Timer warningTimer = null;
	private HorizontalPanel appsListButtonPanel = null;
	private SimplePanel rightCenterPanel = null;
	private AppUploadDialogBox appUploadDialogBox = null;
	private MessageDialogBox messageDialogBox = null;
	private AboutDialogBox aboutDialogBox = null;
	private AdminUserListDialogBox usersDialogBox = null;
	private YesNoConfirmDialog deleteConfirmDialogBox = null;
	private ReportUploadDialogBox reportUploadDialogBox = null;
	private UserAcctDialogBox userAcctDialogBox = null;
	public final InlineHTML statusMessageHtml = new InlineHTML("");
	private String SERVLET_URL = null;
	private ArrayList<ToolInfoGwt> tools = null;
	private HTML appsLabelHtml = null;
	private SimplePanel centerPanel = null;
	private double NORTH_PANEL_HEIGHT = 65.0;
	private double SOUTH_PANEL_HEIGHT = 47.0;
	private boolean searchMode = false;
	private MenuItem userMenuItem = null;
	public boolean timeoutWarningMessage = false;
	public String documentationURL = null;
	public boolean ssoActive = false;
	public String ssoLogoutURL = null;

	class AppListHandler implements SelectionChangeEvent.Handler {
		ConfigInfoGwt configInfo = null;
		AppVetPanel appVetPanel = null;

		public AppListHandler(AppVetPanel appVetPanel, ConfigInfoGwt configInfo) {
			this.appVetPanel = appVetPanel;
			this.configInfo = configInfo;
		}

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			final AppInfoGwt selectedApp = appSelectionModel
					.getSelectedObject();
			displaySelectedAppInfo(selectedApp);
		}
	}

	class AppUploadFormHandler implements FormHandler {
		AppUploadDialogBox appUploadDialog = null;
		String apkFileName = null;

		public AppUploadFormHandler(AppUploadDialogBox appUploadDialog) {
			this.appUploadDialog = appUploadDialog;
		}

		@Override
		@Deprecated
		public void onSubmit(FormSubmitEvent event) {
		}

		@Override
		@Deprecated
		public void onSubmitComplete(FormSubmitCompleteEvent event) {
			appUploadDialog.mainLabel.setText("");
			appUploadDialog.statusLabel.setText("");

			killDialogBox(appUploadDialog);
		}
	}

	class ReportUploadFormHandler implements FormHandler {
		ReportUploadDialogBox reportUploadDialogBox = null;
		String username = null;
		String appid = null;
		DeviceOS appOs = null;
		AppInfoGwt selected = null;

		public ReportUploadFormHandler(
				ReportUploadDialogBox reportUploadDialogBox, String username,
				AppInfoGwt selectedApp) {
			this.reportUploadDialogBox = reportUploadDialogBox;
			this.selected = selectedApp;
			this.username = username;
			this.appid = selectedApp.appId;
			this.appOs = selectedApp.os;
		}

		@Override
		@Deprecated
		public void onSubmit(FormSubmitEvent event) {
			String reportFileName = reportUploadDialogBox.fileUpload
					.getFilename();
			int selectedToolIndex = reportUploadDialogBox.toolNamesComboBox
					.getSelectedIndex();
			String selectedToolName = reportUploadDialogBox.toolNamesComboBox
					.getValue(selectedToolIndex);
			if (reportFileName.length() == 0) {
				showMessageDialog("Report Submission Error",
						"No file selected", true);
				event.setCancelled(true);
			} else if (!Validate.isLegalFileName(reportFileName)) {
				showMessageDialog("App Submission Error", "File \""
						+ reportFileName + "\" contains an illegal character.",
						true);
				event.setCancelled(true);
			} else if (!validReportFileName(selectedToolName, reportFileName,
					tools, appOs)) {
				event.setCancelled(true);
			} else {
				reportUploadDialogBox.cancelButton.setEnabled(false);
				reportUploadDialogBox.submitButton.setEnabled(false);
				reportUploadDialogBox.statusLabel.setText("Uploading "
						+ reportFileName + "...");
			}
		}

		@Override
		@Deprecated
		public void onSubmitComplete(FormSubmitCompleteEvent event) {
			reportUploadDialogBox.statusLabel.setText("");
			killDialogBox(reportUploadDialogBox);
		}
	}

	//	public static int[] getCenterPosition(
	//			com.google.gwt.user.client.ui.UIObject object) {
	//		final int windowWidth = Window.getClientWidth();
	//		final int windowHeight = Window.getClientHeight();
	//		final int xposition = (windowWidth / 2)
	//				- (object.getOffsetHeight() / 2);
	//		final int yposition = (windowHeight / 2)
	//				- (object.getOffsetWidth() / 2);
	//		final int[] position = { xposition, yposition };
	//		return position;
	//	}

	public void killDialogBox(DialogBox dialogBox) {
		if (dialogBox != null) {
			dialogBox.hide();
			dialogBox = null;
		}
	}

	public void logoutSSO() {
		// Cancel poller
		pollingTimer.cancel();

		// Close any open dialog boxes
		killDialogBox(appUploadDialogBox);
		killDialogBox(messageDialogBox);
		killDialogBox(aboutDialogBox);
		killDialogBox(usersDialogBox);
		killDialogBox(deleteConfirmDialogBox);
		killDialogBox(reportUploadDialogBox);
		killDialogBox(userAcctDialogBox);

		// Redirect to the SSO logout URL
		Window.Location.assign(ssoLogoutURL);
		System.gc();
	}

	public void logoutNonSSO() {
		// Cancel poller
		pollingTimer.cancel();

		// Close any open dialog boxes
		killDialogBox(appUploadDialogBox);
		killDialogBox(messageDialogBox);
		killDialogBox(aboutDialogBox);
		killDialogBox(usersDialogBox);
		killDialogBox(deleteConfirmDialogBox);
		killDialogBox(reportUploadDialogBox);
		killDialogBox(userAcctDialogBox);

		// Go back to AppVet login screen
		final LoginPanel loginPanel = new LoginPanel();
		// loginPanel.setTitle("Login panel");
		final RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
		// rootLayoutPanel.setTitle("Root panel");
		rootLayoutPanel.clear();
		rootLayoutPanel.add(loginPanel);
		// Clean up
		System.gc();
	}

	public boolean userInfoIsValid(UserInfo userInfo, boolean ssoActive) {

		if (!Validate.isValidUserName(userInfo.getUserName())) {
			showMessageDialog("Account Setting Error", "Invalid username", true);
			return false;
		}

		if (!Validate.isAlpha(userInfo.getLastName())) {
			showMessageDialog("Account Setting Error", "Invalid last name",
					true);
			return false;
		}

		if (!Validate.isAlpha(userInfo.getFirstName())) {
			showMessageDialog("Account Setting Error", "Invalid first name",
					true);
			return false;
		}

		if (!Validate.isValidEmail(userInfo.getEmail())) {
			showMessageDialog("Account Setting Error", "Invalid email", true);
			return false;
		}

		// We validate role in the calling program.
		// if (!Validate.isValidRole(roleStr)) {
		// log.info("Validating roleStr: " + roleStr);
		// AppVetPanel.showMessageDialog("Account Setting Error",
		// "Invalid role or org hierarchy", true);
		// return false;
		// }

		if (!ssoActive) {
			// Password is required for NON-SSO mode
			String password = userInfo.getPassword();
			String passwordAgain = userInfo.getPasswordAgain();
			if (password != null && !password.isEmpty()
					&& passwordAgain != null && !passwordAgain.isEmpty()) {
				if (!Validate.isValidPassword(password)) {
					showMessageDialog("Account Setting Error",
							"Invalid password", true);
					return false;
				}
				if (!password.equals(passwordAgain)) {
					showMessageDialog("Account Setting Error",
							"Passwords do not match", true);
					return false;
				}
			} else {
				showMessageDialog("Account Setting Error",
						"Password is empty or null", true);
				return false;
			}
		} else {
			// SSO is active so we ignore password fields (since passwords
			// are handled by the organization's SSO environment. Do nothing.
		}
		return true;
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
				killDialogBox(messageDialogBox);
			}
		});
	}

	public void showTimeoutDialog(final long diff) {
		killDialogBox(messageDialogBox);
		timeoutWarningMessage = true;
		messageDialogBox = new MessageDialogBox(
				"Your AppVet session will expire in less than 60 seconds. Please select OK to continue using AppVet.",
				false);
		messageDialogBox.setText("AppVet Timeout Warning");
		messageDialogBox.center();
		messageDialogBox.closeButton.setFocus(true);
		messageDialogBox.closeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				killDialogBox(messageDialogBox);

				if (diff <= 0) {
					// Didn't click within the 60s alert period, so expire
					// pollingTimer.cancel();
					removeSession(true);
				} else {
					sessionExpiration = new Date(System.currentTimeMillis()
							+ MAX_SESSION_IDLE_DURATION);
					timeoutWarningMessage = false;
				}
			}
		});
	}

	public boolean validReportFileName(String selectedToolName,
			String uploadedReportFileName, ArrayList<ToolInfoGwt> tools,
			DeviceOS appOs) {
		String selectedToolRequiredFileType = null;
		for (int i = 0; i < tools.size(); i++) {
			ToolInfoGwt tool = tools.get(i);
			String toolOs = tool.getOs();
			String toolName = tool.getName();
			if (selectedToolName.equals(toolName)
					&& toolOs.equals(appOs.name())) {
				selectedToolRequiredFileType = tool.getReportFileType();
				break;
			} else {
				selectedToolRequiredFileType = "";
			}
		}

		final String uploadedReportFileNameLowercase = uploadedReportFileName
				.toLowerCase();
		if (selectedToolRequiredFileType == null) {
			log.severe("selectedToolRequiredFileType is null");
			return false;
		}
		final String selectedToolRequiredFileTypeLowercase = selectedToolRequiredFileType
				.toLowerCase();

		if (selectedToolRequiredFileTypeLowercase.endsWith("html")) {
			if (!uploadedReportFileNameLowercase.endsWith("html")) {
				showMessageDialog("Report Submission Error", selectedToolName
						+ " reports must be HTML files.", true);
				return false;
			}
		} else if (selectedToolRequiredFileTypeLowercase.endsWith("pdf")) {
			if (!uploadedReportFileNameLowercase.endsWith("pdf")) {
				showMessageDialog("Report Submission Error", selectedToolName
						+ " reports must be PDF files.", true);
				return false;
			}
		} else if (selectedToolRequiredFileTypeLowercase.endsWith("txt")) {
			if (!uploadedReportFileNameLowercase.endsWith("txt")) {
				showMessageDialog("Report Submission Error", selectedToolName
						+ " reports must be TXT files.", true);
				return false;
			}
		} else if (selectedToolRequiredFileTypeLowercase.endsWith("rtf")) {
			if (!uploadedReportFileNameLowercase.endsWith("rtf")) {
				showMessageDialog("Report Submission Error", selectedToolName
						+ " reports must be RTF files.", true);
				return false;
			}
		} else if (selectedToolRequiredFileTypeLowercase.endsWith("xml")) {
			if (!uploadedReportFileNameLowercase.endsWith("xml")) {
				showMessageDialog("Report Submission Error", selectedToolName
						+ " reports must be XML files.", true);
				return false;
			}
		}

		return true;
	}

	public AppVetPanel(final ConfigInfoGwt configInfo,
			AppsListGwt initialApps) {
		super(Unit.PX);

		log.info("Trace 1");
		Window.addResizeHandler(new ResizeHandler() {
			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					adjustComponentSizes();
				}
			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule(250);
			}
		});


		userInfo = configInfo.getUserInfo();
		userName = userInfo.getUserName();
		lastAppsListUpdate = initialApps.appsLastChecked;
		allApps = initialApps.apps;

		sinkEvents(Event.ONCLICK);
		sessionId = configInfo.getSessionId();
		sessionExpiration = configInfo.getSessionExpiration();
		MAX_SESSION_IDLE_DURATION = configInfo.getMaxIdleTime();
		POLLING_INTERVAL = configInfo.getUpdatesDelay();
		setSize("", "");
		setStyleName("mainDockPanel");
		SERVLET_URL = configInfo.getAppVetServletUrl();
		appSelectionModel = new SingleSelectionModel<AppInfoGwt>();
		appSelectionModel.addSelectionChangeHandler(new AppListHandler(this,
				configInfo));

		tools = configInfo.getTools();
		documentationURL = configInfo.getDocumentationURL();
		ssoActive = configInfo.getSSOActive();
		ssoLogoutURL = configInfo.getSsoLogoutURL();
		String orgLogoAltText = configInfo.getOrgLogoAltText();

		final MenuBar adminMenuBar = new MenuBar(true);
		adminMenuBar.setStyleName("adminMenuBar");

		// Set tab-able for 508 compliance
		Roles.getMenubarRole().setTabindexExtraAttribute(adminMenuBar.getElement(), -1);
		adminMenuBar.setFocusOnHoverEnabled(true);

		// Admin menubar
		final MenuItem adminMenuItem = new MenuItem("Admin", true, adminMenuBar);
		adminMenuItem.setTitle("Admin");
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(adminMenuItem.getElement(), 0);

		final MenuItem usersMenuItem = new MenuItem("Add/Edit Users", false,
				new Command() {
			@Override
			public void execute() {
				usersDialogBox = new AdminUserListDialogBox(configInfo,
						ssoActive);
				usersDialogBox.setText("Users");
				usersDialogBox.center();
				usersDialogBox.doneButton.setFocus(true);
				usersDialogBox.doneButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(usersDialogBox);
					}
				});
			}
		});
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(usersMenuItem.getElement(), 0);
		usersMenuItem.setHTML("Add/Edit Users");
		adminMenuBar.addItem(usersMenuItem);
		usersMenuItem.setStyleName("adminSubMenuItem");

		MenuItemSeparator separator_1 = new MenuItemSeparator();
		adminMenuBar.addSeparator(separator_1);
		separator_1.setSize("100%", "1px");

		final MenuItem clearAlertMessageMenuItem = new MenuItem("Clear Status Message", false,
				new Command() {
			@Override
			public void execute() {
				clearAlertMessage(userInfo.getUserName());
			}
		});
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(clearAlertMessageMenuItem.getElement(), 0);
		clearAlertMessageMenuItem.setHTML("Clear Status Message");
		adminMenuBar.addItem(clearAlertMessageMenuItem);
		clearAlertMessageMenuItem.setStyleName("adminSubMenuItem");

		final MenuItem setAlertMenuItem = new MenuItem("Set Status Message", false,
				new Command() {
			@Override
			public void execute() {
				final SetAlertDialogBox setAlertDialogBox = new SetAlertDialogBox();
				setAlertDialogBox.setText("Set Alert Message");
				setAlertDialogBox.center();
				setAlertDialogBox.cancelButton.setFocus(true);
				setAlertDialogBox.cancelButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(setAlertDialogBox);
						return;
					}
				});
				setAlertDialogBox.okButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(setAlertDialogBox);
						SystemAlertType alertType = null;
						if (setAlertDialogBox.alertNormalRadioButton
								.getValue())
							alertType = SystemAlertType.NORMAL;
						else if (setAlertDialogBox.alertWarningRadioButton
								.getValue())
							alertType = SystemAlertType.WARNING;
						else if (setAlertDialogBox.alertCriticalRadioButton
								.getValue())
							alertType = SystemAlertType.CRITICAL;

						String alertMessage = setAlertDialogBox.alertTextArea
								.getText();
						if (alertMessage == null
								|| alertMessage.isEmpty()) {
							showMessageDialog(
									"AppVet Error",
									"Alert message cannot be empty.",
									true);
						}

						setAlertMessage(userInfo.getUserName(),
								alertType, alertMessage);
					}
				});
			}
		});
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(setAlertMenuItem.getElement(), 0);

		adminMenuBar.addItem(setAlertMenuItem);
		setAlertMenuItem.setStyleName("adminSubMenuItem");

		MenuItemSeparator separator_2 = new MenuItemSeparator();
		adminMenuBar.addSeparator(separator_2);
		separator_2.setSize("100%", "1px");

		final MenuItem mntmAppVetLog = new MenuItem("View Log", false,
				new Command() {
			@Override
			public void execute() {
				final String dateString = "?nocache"
						+ new Date().getTime();
				final String url = SERVLET_URL + dateString + "&"
						+ AppVetParameter.COMMAND.value + "="
						+ AppVetServletCommand.GET_APPVET_LOG.name()
						+ "&" + AppVetParameter.SESSIONID.value + "="
						+ sessionId;
				Window.open(url, "_blank", "");
			}
		});
		mntmAppVetLog.setHTML("View Log");
		adminMenuBar.addItem(mntmAppVetLog);
		mntmAppVetLog.setStyleName("adminSubMenuItem");

		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(mntmAppVetLog.getElement(), 0);

		final MenuItem clearAppVetLogMenuItem = new MenuItem("Clear Log", false,
				new Command() {
			@Override
			public void execute() {
				final YesNoConfirmDialog clearLogDialogBox = new YesNoConfirmDialog(
						"<p align=\"center\">\r\nAre you sure you want to clear the AppVet log?\r\n</p>");
				clearLogDialogBox.setText("Confirm Clear");
				clearLogDialogBox.center();
				clearLogDialogBox.cancelButton.setFocus(true);
				clearLogDialogBox.cancelButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(clearLogDialogBox);
						return;
					}
				});
				clearLogDialogBox.okButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(clearLogDialogBox);
						clearLog();
					}
				});
			}
		});
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(clearAppVetLogMenuItem.getElement(), 0);
		adminMenuBar.addItem(clearAppVetLogMenuItem);
		clearAppVetLogMenuItem.setStyleName("adminSubMenuItem");

		final MenuItem downloadAppVetLogMenuItem = new MenuItem("Download Log",
				false, new Command() {
			@Override
			public void execute() {
				final String dateString = "?nocache"
						+ new Date().getTime();
				final String url = SERVLET_URL + dateString + "&"
						+ AppVetParameter.COMMAND.value + "="
						+ AppVetServletCommand.DOWNLOAD_LOG.name()
						+ "&" + AppVetParameter.SESSIONID.value + "="
						+ sessionId;
				Window.open(url, "_self", "");
			}
		});
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(downloadAppVetLogMenuItem.getElement(), 0);
		downloadAppVetLogMenuItem.setHTML("Download Log");
		adminMenuBar.addItem(downloadAppVetLogMenuItem);
		downloadAppVetLogMenuItem.setStyleName("adminSubMenuItem");

		adminMenuItem.setStyleName("adminMenuItem");

		adminMenuItem.setHTML("<img src=\"images/icon-gear.png\" width=\"16px\" height=\"16px\" alt=\"Admin\">");

		final MenuBar appVetMenuBar = new MenuBar(false);

		// TODO REMOVE FOR TESTING ONLY
		//appVetMenuBar.addItem(adminMenuItem);
		log.info("Trace 2");

		Role role;
		try {
			role = Role.getRole(userInfo.getRoleAndOrgMembership());
			if (role == Role.ADMIN) {
				appVetMenuBar.addItem(adminMenuItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		downloadAppButton = new PushButton("Download App");
		downloadAppButton.setHTML("<div><img style=\"vertical-align:middle\" width=\"16px\" height=\"16px\" src=\"images/download-black.png\" alt=\"Download App\" /> <span style=\"vertical-align:middle\">APP</span></div>");

		if (!configInfo.isKeepApps()) {
			// Hide download app button if KEEP_APPS is false
			downloadAppButton.setVisible(false);
		}

		/* The appsListTable must be set to a height in pixels (not percent) and must be 
		 * adjusted during run-time using the resizeComponent() method.
		 */
		appsListTable = new AppsListPagingDataGrid<AppInfoGwt>();
		appsListTable.pager.setHeight("");
		appsListTable.dataGrid
		.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		appsListTable.dataGrid.setFocus(false);
		appsListTable.setPageSize(configInfo.getNumRowsAppsList());
		appsListTable.dataGrid.setStyleName("dataGrid");
		appsListTable.dataGrid.setSize("100%", "");
		appsListTable.setDataList(initialApps.apps);
		appsListTable.setSize("100%", "200px");
		appsListTable.dataGrid.setSelectionModel(appSelectionModel);

		log.info("Trace 3");

		log.info("Trace 3a");

		SimplePanel southPanel = new SimplePanel();
		southPanel.setSize("100%", "");

		HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		horizontalPanel_2
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		southPanel.setWidget(horizontalPanel_2);
		horizontalPanel_2.setSize("100%", "");
		log.info("Trace 3b");

		Image nistLogo = new Image("images/nist_logo_darkgrey.png");
		log.info("Trace 3b1");

		nistLogo.setAltText("NIST logo");
		log.info("Trace 3b2");

		// nistLogo.setTitle("NIST logo");
		horizontalPanel_2.add(nistLogo);
		log.info("Trace 3b3");

		horizontalPanel_2.setCellVerticalAlignment(nistLogo,
				HasVerticalAlignment.ALIGN_MIDDLE);
		log.info("Trace 3b4");

		horizontalPanel_2.setCellHorizontalAlignment(nistLogo,
				HasHorizontalAlignment.ALIGN_RIGHT);
		log.info("Trace 3b5");

		nistLogo.setSize("50px", "13px");
		log.info("Trace 3b6");


		log.info("Trace 3c");

		pollServer(userName);
		log.info("Trace 3d");

		SimplePanel northPanel = new SimplePanel();
		addNorth(northPanel, NORTH_PANEL_HEIGHT);
		northPanel.setHeight("");
		final VerticalPanel northAppVetPanel = new VerticalPanel();
		northPanel.setWidget(northAppVetPanel);
		northAppVetPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		northAppVetPanel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		northAppVetPanel.setSize("100%", "");
		final HorizontalPanel horizontalPanel_5 = new HorizontalPanel();
		horizontalPanel_5.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_5
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setStyleName("mainBanner");
		northAppVetPanel.add(horizontalPanel_5);
		northAppVetPanel.setCellVerticalAlignment(horizontalPanel_5,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setSize("100%", "");
		northAppVetPanel.setCellWidth(horizontalPanel_5, "100%");
		log.info("Trace 3e");

		Image image = new Image("images/appvet_logo2.png");
		image.setAltText("AppVet");
		horizontalPanel_5.add(image);
		image.setWidth("200px");
		horizontalPanel_5.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);
		final HorizontalPanel horizontalPanel_6 = new HorizontalPanel();
		horizontalPanel_6
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.add(horizontalPanel_6);
		horizontalPanel_6.setWidth("");
		horizontalPanel_5.setCellWidth(horizontalPanel_6, "100%");
		horizontalPanel_5.setCellHorizontalAlignment(horizontalPanel_6,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_5.setCellVerticalAlignment(horizontalPanel_6,
				HasVerticalAlignment.ALIGN_MIDDLE);
		searchTextBox = new TextBox();
		searchTextBox.setText("Search");
		searchTextBox.setStyleName("searchTextBox");
		searchTextBox.setTitle("Search by app ID, name, release kit, etc.");
		searchTextBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchTextBox.setText("");
			}
		});
		log.info("Trace 3f");

		searchTextBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event_) {
				final boolean enterPressed = KeyCodes.KEY_ENTER == event_
						.getNativeEvent().getKeyCode();
				final String searchString = searchTextBox.getText();
				if (enterPressed) {
					final int numFound = search();
					if (numFound > 0) {
						final SafeHtmlBuilder sb = new SafeHtmlBuilder();
						sb.appendHtmlConstant("<h3>Found " + numFound + " results for \"" + searchString + "\"</h3>");						
						appsLabelHtml.setHTML(sb.toSafeHtml());
					}
				}
			}
		});
		log.info("Trace 3g");

		searchTextBox.setSize("240px", "15px");
		horizontalPanel_6.add(searchTextBox);
		horizontalPanel_6.setCellVerticalAlignment(searchTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		final PushButton searchButton = new PushButton("Search");
		searchButton.setStyleName("searchButton");
		searchButton.setTitle("Search by app ID, name, release kit, etc.");
		searchButton.setSize("", "");
		searchButton
		.setHTML("<img width=\"18px\" height=\"18px\" src=\"images/icon-search.png\" alt=\"search\" />");
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final String searchString = searchTextBox.getText();
				final int numFound = search();
				if (numFound > 0) {
					final SafeHtmlBuilder sb = new SafeHtmlBuilder();
					sb.appendHtmlConstant("<h3>Found " + numFound + " results for \"" + searchString + "\"</h3>");						
					appsLabelHtml.setHTML(sb.toSafeHtml());
				}
			}
		});
		horizontalPanel_6.add(searchButton);
		horizontalPanel_6.setCellHorizontalAlignment(searchButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6.setCellVerticalAlignment(searchButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel horizontalPanel_4 = new HorizontalPanel();
		horizontalPanel_4.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_4.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalPanel_5.add(horizontalPanel_4);
		horizontalPanel_5.setCellVerticalAlignment(horizontalPanel_4, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setCellHorizontalAlignment(horizontalPanel_4, HasHorizontalAlignment.ALIGN_RIGHT);
		log.info("Trace 3h");

		Roles.getMenubarRole().setTabindexExtraAttribute(appVetMenuBar.getElement(), -1);
		horizontalPanel_4.add(appVetMenuBar);
		horizontalPanel_4.setCellVerticalAlignment(appVetMenuBar, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_4.setCellHorizontalAlignment(appVetMenuBar, HasHorizontalAlignment.ALIGN_RIGHT);
		appVetMenuBar.setStyleName("gwt-MenuBar");
		appVetMenuBar.setAutoOpen(false);
		appVetMenuBar.setSize("250px", "");
		appVetMenuBar.setAnimationEnabled(false);
		appVetMenuBar.setFocusOnHoverEnabled(false);
		log.info("Trace 3i");

		log.info("Trace 4");

		final MenuBar userMenuBar = new MenuBar(true);
		userMenuBar.setStyleName("userMenuBar");
		userMenuBar.setFocusOnHoverEnabled(false);
		// Set tab-able for 508 compliance
		Roles.getMenubarRole().setTabindexExtraAttribute(userMenuBar.getElement(), -1);

		userMenuItem = new MenuItem("User", true,
				userMenuBar);
		userMenuItem.setStyleName("userMenuItem");
		userMenuItem.setTitle("User Preferences");
		userMenuItem.setHTML("<img src=\"images/icon-user.png\" width=\"16px\" height=\"16px\" alt=\"User Preferences\">");
		userMenuBar.setHeight("");
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(userMenuItem.getElement(), 0);

		final MenuItem accountSettingsMenuItem = new MenuItem(
				"Account Settings", false, new Command() {
					@Override
					public void execute() {
						openUserAccount(configInfo);
					}
				});
		accountSettingsMenuItem.setStyleName("userSubMenuItem");
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(accountSettingsMenuItem.getElement(), 0);
		userMenuBar.addItem(accountSettingsMenuItem);

		final MenuItem toolCredentialsMenuItem = new MenuItem(
				"Tool Credentials", false, new Command() {
					@Override
					public void execute() {
						openToolCredentials(configInfo);
					}
				});
		toolCredentialsMenuItem.setStyleName("userSubMenuItem");
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(toolCredentialsMenuItem.getElement(), 0);

		userMenuBar.addItem(toolCredentialsMenuItem);
		accountSettingsMenuItem.setHeight("");

		final MenuItem myAppsMenuItem = new MenuItem("My Apps", false,
				new Command() {
			@Override
			public void execute() {
				searchTextBox.setText(userInfo.getUserName());
				final int numFound = search();
				if (numFound > 0) {
					final SafeHtmlBuilder sb = new SafeHtmlBuilder();
					sb.appendHtmlConstant("<h3>My Apps</h3>");						
					appsLabelHtml.setHTML(sb.toSafeHtml());
				}
			}
		});
		myAppsMenuItem.setStyleName("userSubMenuItem");
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(myAppsMenuItem.getElement(), 0);
		userMenuBar.addItem(myAppsMenuItem);
		myAppsMenuItem.setHeight("");

		final MenuItemSeparator separator = new MenuItemSeparator();
		userMenuBar.addSeparator(separator);
		separator.setSize("100%", "1px");
		final MenuItem logoutMenuItem = new MenuItem("Logout", false,
				new Command() {
			@Override
			public void execute() {
				removeSession(false);
			}
		});
		logoutMenuItem.setStyleName("userSubMenuItem");
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(logoutMenuItem.getElement(), 0);
		userMenuBar.addItem(logoutMenuItem);
		logoutMenuItem.setHeight("");

		appVetMenuBar.addItem(userMenuItem);
		userMenuItem.setHeight("");


		final MenuBar helpMenuBar = new MenuBar(true);
		helpMenuBar.setStyleName("helpMenuBar");
		// Set tab-able for 508 compliance
		Roles.getMenubarRole().setTabindexExtraAttribute(helpMenuBar.getElement(), -1);
		helpMenuBar.setFocusOnHoverEnabled(false);

		final MenuItem helpMenuItem = new MenuItem("Help", true, helpMenuBar);
		helpMenuItem.setTitle("Help");
		helpMenuItem.setHTML("<img src=\"images/icon-white-question-mark.png\"  width=\"16px\" height=\"16px\" alt=\"Settings\">");
		helpMenuItem.setStyleName("helpMenuItem");
		helpMenuBar.setHeight("");
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(helpMenuItem.getElement(), 0);

		final MenuItem aboutMenuItem = new MenuItem("About", false,
				new Command() {
			@Override
			public void execute() {
				aboutDialogBox = new AboutDialogBox(configInfo
						.getAppVetVersion());
				aboutDialogBox.setText("About");
				aboutDialogBox.center();
				aboutDialogBox.closeButton.setFocus(true);
				aboutDialogBox.closeButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(aboutDialogBox);
					}
				});
			}
		});
		aboutMenuItem.setStyleName("helpSubMenuItem");
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(aboutMenuItem.getElement(), 0);

		final MenuItem documentationMenuItem = new MenuItem("Documentation",
				false, new Command() {
			@Override
			public void execute() {
				Window.open(documentationURL, "_blank", null);
			}
		});
		documentationMenuItem.setStyleName("helpSubMenuItem");
		// Set tab-able for 508 compliance
		Roles.getMenuitemRole().setTabindexExtraAttribute(documentationMenuItem.getElement(), 0);

		helpMenuBar.addItem(documentationMenuItem);
		documentationMenuItem.setHeight("");

		appVetMenuBar.addItem(helpMenuItem);
		helpMenuItem.setHeight("");
		helpMenuBar.addItem(aboutMenuItem);
		aboutMenuItem.setHeight("");
		log.info("Trace 5");

		final HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		horizontalPanel_3.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		northAppVetPanel.add(horizontalPanel_3);
		northAppVetPanel.setCellVerticalAlignment(horizontalPanel_3,
				HasVerticalAlignment.ALIGN_MIDDLE);
		northAppVetPanel.setCellHorizontalAlignment(horizontalPanel_3,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3.setSize("100%", "");
		northAppVetPanel.setCellWidth(horizontalPanel_3, "100%");

		horizontalPanel_3.add(statusMessageHtml);
		horizontalPanel_3.setCellHeight(statusMessageHtml, "30px");
		horizontalPanel_3.setCellVerticalAlignment(statusMessageHtml,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHorizontalAlignment(statusMessageHtml,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3.setCellWidth(statusMessageHtml, "100%");
		statusMessageHtml.setStyleName("statusMessage");
		statusMessageHtml
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		statusMessageHtml.setSize("90%", "20px");

		centerPanel = new SimplePanel();
		add(centerPanel);
		centerPanel.setHeight("");

		final HorizontalSplitPanel centerAppVetSplitPanel = new HorizontalSplitPanel();
		centerPanel.setWidget(centerAppVetSplitPanel);
		// centerAppVetSplitPanel.setTitle("AppVet split pane");
		centerAppVetSplitPanel.setSplitPosition("65%");
		centerAppVetSplitPanel.setSize("100%", "100%");
		final SimplePanel leftCenterPanel = new SimplePanel();
		// leftCenterPanel.setTitle("AppVet apps list pane");

		centerAppVetSplitPanel.setLeftWidget(leftCenterPanel);
		leftCenterPanel.setSize("100%", "232px");
		final DockPanel dockPanel_1 = new DockPanel();
		dockPanel_1.add(appsListTable, DockPanel.CENTER);
		dockPanel_1.setCellWidth(appsListTable, "100%");
		dockPanel_1.setCellHorizontalAlignment(appsListTable,
				HasHorizontalAlignment.ALIGN_CENTER);
		dockPanel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dockPanel_1.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		leftCenterPanel.setWidget(dockPanel_1);
		dockPanel_1.setSize("100%", "");
		rightCenterPanel = new SimplePanel();
		// rightCenterPanel.setTitle("AppVet app info panel");

		centerAppVetSplitPanel.setRightWidget(rightCenterPanel);
		rightCenterPanel.setSize("", "");
		final VerticalPanel appInfoVerticalPanel = new VerticalPanel();
		rightCenterPanel.setWidget(appInfoVerticalPanel);
		appInfoVerticalPanel.setSize("", "");
		final HorizontalPanel appInfoPanel = new HorizontalPanel();
		appInfoPanel.setStyleName("iconPanel");
		appInfoVerticalPanel.add(appInfoPanel);
		appInfoVerticalPanel.setCellWidth(appInfoPanel, "100%");
		appInfoPanel.setSize("", "");
		appInfoIcon = new Image("");
		appInfoIcon.setVisible(false);
		appInfoIcon.setAltText("");
		appInfoPanel.add(appInfoIcon);
		appInfoPanel.setCellVerticalAlignment(appInfoIcon,
				HasVerticalAlignment.ALIGN_MIDDLE);
		appInfoIcon.setSize("78px", "78px");
		final VerticalPanel verticalPanel = new VerticalPanel();
		appInfoPanel.add(verticalPanel);
		appInfoName = new HTML("", false);
		appInfoName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		verticalPanel.add(appInfoName);
		appInfoName.setStyleName("appInfoName");
		appInfoName.setSize("", "33px");
		appInfoPanel.setCellVerticalAlignment(appInfoName,
				HasVerticalAlignment.ALIGN_MIDDLE);
		appInfoPackage = new HTML("", true);
		appInfoPackage
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		appInfoPackage.setStyleName("appInfoVersion");
		verticalPanel.add(appInfoPackage);
		appInfoVersion = new HTML("", true);
		appInfoVersion
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		appInfoVersion.setStyleName("appInfoVersion");
		verticalPanel.add(appInfoVersion);
		appsListButtonPanel = new HorizontalPanel();
		appsListButtonPanel
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel_1.add(appsListButtonPanel, DockPanel.NORTH);
		dockPanel_1.setCellHorizontalAlignment(appsListButtonPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		dockPanel_1.setCellWidth(appsListButtonPanel, "100%");
		dockPanel_1.setCellVerticalAlignment(appsListButtonPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		appsListButtonPanel.setStyleName("appListButtonPanel");
		appsListButtonPanel.setSize("100%", "");

		appsLabelHtml = new HTML("<h3>Apps</h3>", true);
		appsListButtonPanel.add(appsLabelHtml);
		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setStyleName("appFunctionButtonPanel");
		appsListButtonPanel.add(horizontalPanel);
		appsListButtonPanel.setCellWidth(horizontalPanel, "50%");
		appsListButtonPanel.setCellVerticalAlignment(horizontalPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		appsListButtonPanel.setCellHorizontalAlignment(horizontalPanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalPanel.setSize("", "");
		final PushButton submitButton = new PushButton("UPLOAD APP");
		submitButton.setHTML("<div><img style=\"vertical-align:middle\" width=\"16px\" height=\"16px\" src=\"images/upload-white.png\" alt=\"Upload APP\"/> <span style=\"vertical-align:middle\">UPLOAD APP\r\n</span></div>");
		submitButton.setStyleName("greenButton shadow");
		submitButton.setTitle("Upload app");

		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				appUploadDialogBox = new AppUploadDialogBox(sessionId,
						SERVLET_URL);
				appUploadDialogBox.setText("Submit App");
				appUploadDialogBox.center();
				appUploadDialogBox.cancelButton.setFocus(true);
				appUploadDialogBox.cancelButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(appUploadDialogBox);
					}
				});
				appUploadDialogBox.uploadAppFileForm
				.addFormHandler(new AppUploadFormHandler(
						appUploadDialogBox));
				appUploadDialogBox.submitButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (appUploadDialogBox.fileUpload.getFilename()
								.isEmpty()) {
							showMessageDialog("Submit App File",
									"No app file selected.", true);
							return;
						}
						appUploadDialogBox.cancelButton
						.setEnabled(false);
						appUploadDialogBox.submitButton
						.setEnabled(false);
						String fileName = appUploadDialogBox.fileUpload
								.getFilename();
						appUploadDialogBox.statusLabel
						.setText("Uploading " + fileName
								+ "...");
						appUploadDialogBox.uploadAppFileForm.submit();
					}
				});
			}
		});
		viewAllButton = new PushButton("VIEW ALL");
		viewAllButton.setStyleName("grayButton shadow");
//		viewAllButton.setHTML("<img width=\"100px\" src=\"images/icon-view-all.png\" alt=\"View All Apps\" />");
		viewAllButton.setHTML("VIEW ALL");
		viewAllButton.setTitle("View All Apps");
		viewAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchMode = false;
				setAllApps();
				viewAllButton.setVisible(false);
			}
		});
		horizontalPanel.add(viewAllButton);
		horizontalPanel.setCellHorizontalAlignment(viewAllButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(viewAllButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		viewAllButton.setSize("100px", "18px");
		viewAllButton.setVisible(false);
		horizontalPanel.add(submitButton);
		horizontalPanel.setCellVerticalAlignment(submitButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(submitButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		submitButton.setSize("120px", "20px");

		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		appInfoVerticalPanel.add(horizontalPanel_1);

		Label label = new Label("");
		horizontalPanel_1.add(label);
		horizontalPanel_1.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
		label.setSize("0", "0");
		uploadReportButton = new PushButton("Upload Report");
		uploadReportButton.setHTML("<div><img style=\"vertical-align:middle\" width=\"16px\" height=\"16px\" src=\"images/upload-black.png\" alt=\"Upload Report\" /> <span style=\"vertical-align:middle\">REPORT\r\n</span></div>");
		uploadReportButton.setStyleName("grayButton shadow");
		horizontalPanel_1.add(uploadReportButton);
		horizontalPanel_1.setCellVerticalAlignment(uploadReportButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellHorizontalAlignment(uploadReportButton, HasHorizontalAlignment.ALIGN_CENTER);

		horizontalPanel.setCellVerticalAlignment(uploadReportButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		uploadReportButton.setTitle("Upload Report");
		uploadReportButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final AppInfoGwt selected = appSelectionModel
						.getSelectedObject();
				if (selected == null) {
					showMessageDialog("AppVet Error", "No app is selected",
							true);
				} else {

					reportUploadDialogBox = new ReportUploadDialogBox(userInfo,
							sessionId, selected.appId, SERVLET_URL,
							selected.os, tools);
					reportUploadDialogBox.setText("Upload Report for "
							+ selected.appName);
					reportUploadDialogBox.center();
					reportUploadDialogBox.toolNamesComboBox.setFocus(true);

					reportUploadDialogBox.cancelButton
					.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							killDialogBox(reportUploadDialogBox);
						}
					});
					reportUploadDialogBox.uploadReportForm
					.addFormHandler(new ReportUploadFormHandler(
							reportUploadDialogBox, userName, selected));

				}
			}
		});
		uploadReportButton.setSize("82px", "20px");
		logButton = new PushButton("View Log");
		logButton.setHTML("<div><img style=\"vertical-align:middle\" width=\"16px\" height=\"16px\" src=\"images/magnifying-glass-black.png\" alt=\"Upload Report\" /> <span style=\"vertical-align:middle\">LOG\r\n</span></div>");
		logButton.setStyleName("grayButton shadow");
		horizontalPanel_1.add(logButton);
		horizontalPanel_1.setCellVerticalAlignment(logButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellHorizontalAlignment(logButton, HasHorizontalAlignment.ALIGN_CENTER);

		horizontalPanel.setCellVerticalAlignment(logButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		logButton.setTitle("View Log");
		logButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final AppInfoGwt selected = appSelectionModel
						.getSelectedObject();
				if (selected != null) {
					final String appId = selected.appId;
					final String dateString = "?nocache" + new Date().getTime();
					final String url = SERVLET_URL + dateString + "&"
							+ AppVetParameter.COMMAND.value + "="
							+ AppVetServletCommand.GET_APP_LOG.name() + "&"
							+ AppVetParameter.APPID.value + "=" + appId + "&"
							+ AppVetParameter.SESSIONID.value + "=" + sessionId;
					Window.open(url, "_blank", "");
				}
			}
		});
		logButton.setSize("82px", "20px");
		deleteButton = new PushButton("Delete App");
		deleteButton.setHTML("Delete Appp");
		deleteButton.setStyleName("grayButton shadow");
		horizontalPanel_1.add(deleteButton);
		horizontalPanel_1.setCellVerticalAlignment(deleteButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellHorizontalAlignment(deleteButton, HasHorizontalAlignment.ALIGN_CENTER);

		horizontalPanel.setCellVerticalAlignment(deleteButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		deleteButton.setTitle("Delete App");
		deleteButton.setVisible(true);
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final AppInfoGwt selected = appSelectionModel
						.getSelectedObject();

				deleteConfirmDialogBox = new YesNoConfirmDialog(
						"<p align=\"center\">\r\nAre you sure you want to delete app #"
								+ selected.appId + "?\r\n</p>");
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
							deleteApp(selected.os, selected.appId,
									userName);
						}
					}
				});
			}
		});
		deleteButton.setSize("82px", "20px");
		downloadReportsButton = new PushButton("Download Reports");
		downloadReportsButton.setHTML("<div><img style=\"vertical-align:middle\" width=\"16px\" height=\"16px\" src=\"images/download-black.png\" alt=\"Download Reports\" /> <span style=\"vertical-align:middle\">REPORTS</span></div>");
		downloadReportsButton.setStyleName("grayButton shadow");
		horizontalPanel_1.add(downloadReportsButton);
		horizontalPanel_1.setCellVerticalAlignment(downloadReportsButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellHorizontalAlignment(downloadReportsButton, HasHorizontalAlignment.ALIGN_CENTER);

		downloadReportsButton.setTitle("Download Reports");
		downloadReportsButton.setEnabled(true);
		downloadReportsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final AppInfoGwt selected = appSelectionModel
						.getSelectedObject();
				if (selected == null) {
					showMessageDialog("AppVet Error", "No app is selected",
							true);
				} else {
					final String appId = selected.appId;
					final String dateString = "?nocache" + new Date().getTime();
					final String url = SERVLET_URL + dateString + "&"
							+ AppVetParameter.COMMAND.value + "="
							+ AppVetServletCommand.DOWNLOAD_REPORTS.name()
							+ "&" + AppVetParameter.APPID.value + "=" + appId
							+ "&" + AppVetParameter.SESSIONID.value + "="
							+ sessionId;
					// TODO
					Window.open(url, "_self", "");
				}
			}
		});
		horizontalPanel.setCellHorizontalAlignment(downloadReportsButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(downloadReportsButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		appsListButtonPanel.setCellHorizontalAlignment(downloadReportsButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		downloadReportsButton.setSize("82px", "20px");

		downloadAppButton.setStyleName("grayButton shadow");
		horizontalPanel_1.add(downloadAppButton);
		horizontalPanel_1.setCellVerticalAlignment(downloadAppButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellHorizontalAlignment(downloadAppButton, HasHorizontalAlignment.ALIGN_CENTER);

		downloadAppButton.setTitle("Download App");
		downloadAppButton.setSize("82px", "20px");
		toolResultsHtml = new HTML("", true);
		
				//TODO
				//		appInfoVerticalPanel.setCellWidth(toolResultsHtml, "100%");
				toolResultsHtml.setSize("100%", "");
				toolResultsHtml
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				toolResultsHtml.setStyleName("toolResultsHtml");
				appInfoVerticalPanel.add(toolResultsHtml);
		downloadAppButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final AppInfoGwt selected = appSelectionModel
						.getSelectedObject();
				if (selected == null) {
					showMessageDialog("AppVet Error", "No app is selected",
							true);
				} else {
					final String appId = selected.appId;
					final String dateString = "?nocache" + new Date().getTime();
					final String url = SERVLET_URL + dateString + "&"
							+ AppVetParameter.COMMAND.value + "="
							+ AppVetServletCommand.DOWNLOAD_APP.name() + "&"
							+ AppVetParameter.APPID.value + "=" + appId + "&"
							+ AppVetParameter.SESSIONID.value + "=" + sessionId;
					// TODO
					Window.open(url, "_blank", "");
				}
			}
		});
		logButton.setVisible(true);

		uploadReportButton.setVisible(true);

		addSouth(southPanel, SOUTH_PANEL_HEIGHT);
		log.info("Trace 6");

		if ((initialApps != null) && (initialApps.apps.size() > 0)) {
			log.info("Trace 3b7");

			appSelectionModel.setSelected(initialApps.apps.get(0), true);
			log.info("Trace 3b8");

		} else {
			log.info("Trace 3b9");

			logButton.setEnabled(false);
			log.info("Trace 3b9a");

			uploadReportButton.setEnabled(false);
			log.info("Trace 3b9b");

			deleteButton.setEnabled(false);
			log.info("Trace 3b9c");

			downloadReportsButton.setEnabled(false);
			log.info("Trace 3b9d");

			downloadAppButton.setEnabled(false);
			log.info("Trace 3b10");

		}
		
		scheduleResize();
		log.info("Trace 7");

		// Note that SSO users can manually refresh the page but non-SSO users
		// will
		// return to AppVet login page upon a manual refresh of the page.
		if (!ssoActive) {
			showDontRefreshWarning();
		}
		log.info("Trace 8");

	}

	public void removeSession(final boolean sessionExpired) {
		// First stop polling the server for data
		pollingTimer.cancel();
		killDialogBox(messageDialogBox);

		appVetServiceAsync.removeSession(sessionId,
				new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				showMessageDialog("AppVet Error",
						"App list retrieval error", true);
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result == false) {
					showMessageDialog("AppVet Error",
							"Could not remove session", true);
				} else {
					if (sessionExpired) {
						// Show session expired message
						showMessageDialog("AppVet Session",
								"AppVet session has expired", true);
						messageDialogBox.closeButton.setFocus(true);
						messageDialogBox.closeButton
						.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								killDialogBox(messageDialogBox);
							}
						});
					}
					if (ssoActive) {
						logoutSSO();
					} else {
						logoutNonSSO();
					}

				}
			}

		});
	}

	public void setAlertMessage(String username, SystemAlertType alertType,
			String alertMessage) {
		SystemAlert alert = new SystemAlert();
		alert.type = alertType;
		alert.message = alertMessage;

		appVetServiceAsync.setAlertMessage(username, alert,
				new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				showMessageDialog("AppVet Error",
						"Could not set alert message", true);
			}

			@Override
			public void onSuccess(Boolean setAlert) {
				if (!setAlert) {
					showMessageDialog("AppVet Error",
							"Could not set alert message", true);
				} else {
					// log.info("Alert message set");
				}
			}
		});
	}

	public void clearAlertMessage(String username) {
		appVetServiceAsync.clearAlertMessage(username,
				new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				showMessageDialog("AppVet Error",
						"Could not clear alert message", true);
			}

			@Override
			public void onSuccess(Boolean setAlert) {
				if (!setAlert) {
					showMessageDialog("AppVet Error",
							"Could not clear alert message", true);
				} else {
					// log.info("Alert message cleared");
				}
			}
		});
	}

	public void clearLog() {
		appVetServiceAsync.clearLog(new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				showMessageDialog("AppVet Error",
						"AppVet log could not be cleared", true);
			}

			@Override
			public void onSuccess(Boolean logCleared) {
				if (logCleared == false) {
					showMessageDialog("AppVet Error",
							"AppVet log could not be cleared", true);
				} else {
					showMessageDialog("AppVet", "AppVet log cleared", false);
				}
			}
		});
	}

	public void deleteApp(final DeviceOS os, final String appid,
			final String username) {
		appVetServiceAsync.deleteApp(os, appid, username,
				new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				showMessageDialog("AppVet Error",
						"App list retrieval error", true);
			}

			@Override
			public void onSuccess(Boolean deleted) {
				if (deleted == false) {
					showMessageDialog("AppVet Error",
							"Could not delete app", true);
				} else {
					final AppInfoGwt currentlySelectedApp = appSelectionModel
							.getSelectedObject();
					final int currentlySelectedIndex = getAppsListIndex(
							currentlySelectedApp, allApps);
					for (int i = 0; i < allApps.size(); i++) {
						final AppInfoGwt appInfoGwt = allApps.get(i);
						if (appInfoGwt.appId.equals(appid)) {
							allApps.remove(i);
							if (!searchMode) {
								appsListTable.remove(i);
							} else {
								appsListTable.remove(appid);
							}
							break;
						}
					}
					if (!searchMode) {
						if (allApps.size() > 0) {
							appSelectionModel.setSelected(
									allApps.get(currentlySelectedIndex),
									true);
						} else {
							appInfoVersion.setHTML("");
							appInfoIcon.setVisible(false);
							appInfoName.setText("");
							toolResultsHtml.setText("");
							logButton.setEnabled(false);
							uploadReportButton.setEnabled(false);
							deleteButton.setEnabled(false);
							downloadAppButton.setEnabled(false);
							downloadReportsButton.setEnabled(false);
						}
					}
				}
			}
		});
	}

	public int getAppsListIndex(AppInfoGwt item, List<AppInfoGwt> appsList) {
		if (item != null) {
			for (int i = 0; i < appsList.size(); i++) {
				if (item.appId.equals(appsList.get(i).appId)) {
					return i;
				}
			}
		}
		return 0;
	}

	public void getUpdatedApps(String username) {
		appVetServiceAsync.getUpdatedApps(lastAppsListUpdate, username,
				new AsyncCallback<AppsListGwt>() {
			@Override
			public void onFailure(Throwable caught) {
				log.severe("Error retrieving updated apps. Server might be down: "
						+ caught.toString());
				pollingTimer.cancel();
			}

			@Override
			public void onSuccess(AppsListGwt updatedAppsList) {
				if (updatedAppsList == null) {
					showMessageDialog("AppVet Database Error",
							"Could not retrieve updated apps", true);
				} else {
					// log.info("Update time: " +
					// updatedAppsList.appsLastChecked.toString());
					lastAppsListUpdate = updatedAppsList.appsLastChecked;
					if (updatedAppsList.apps.size() > 0) {
						setUpdatedApps(updatedAppsList.apps);

					}
				}
			}
		});
	}

	@Override
	public void onBrowserEvent(Event event) {
		sessionExpiration = new Date(System.currentTimeMillis()
				+ MAX_SESSION_IDLE_DURATION);
	}

	public synchronized void displaySelectedAppInfo(final AppInfoGwt selectedApp) {
		// log.info("Updating appinfopanel for: " + selectedApp.appId);
		// Show selected app info results
		if (selectedApp != null) {
			appVetServiceAsync.getToolsResults(selectedApp.os, sessionId,
					selectedApp.appId,
					new AsyncCallback<List<ToolStatusGwt>>() {

				@Override
				public void onFailure(Throwable caught) {
					showMessageDialog("AppVet Error",
							"System error retrieving app info", true);
				}

				@Override
				public void onSuccess(List<ToolStatusGwt> toolsResults) {

					if ((toolsResults == null)
							|| toolsResults.isEmpty()) {
						showMessageDialog("AppVet Error: ",
								"Could not retrieve app info.", true);
					} else {
						// Display selected app information
						String iconPath = null;
						String altText = null;
						if (selectedApp.iconURL == null) {
							// Icon has not yet been generated for this
							// app
							if (selectedApp.os == DeviceOS.ANDROID) {
								iconPath = "images/android-icon-gray.png";
								altText = "Android app";
							} else if (selectedApp.os == DeviceOS.IOS) {
								iconPath = "images/apple-icon-gray.png";
								altText = "iOS app";
							}
						} else {
							// Icon has been generated for this app
							iconPath = selectedApp.iconURL;
							altText = selectedApp.appName;
						}

						appInfoIcon.setVisible(true);
						appInfoIcon.setUrl(iconPath);
						appInfoIcon.setAltText(altText);

						// Set app name in right info panel
						String appNameHtml = null;
						if ((selectedApp.appStatus == AppStatus.NA)
								|| (selectedApp.appStatus == AppStatus.ERROR)
								|| (selectedApp.appStatus == AppStatus.HIGH)
								|| (selectedApp.appStatus == AppStatus.MODERATE)
								|| (selectedApp.appStatus == AppStatus.LOW)) {
							appNameHtml = "<div id=\"appNameInfo\">"
									+ selectedApp.appName + "</div>";
							uploadReportButton.setEnabled(true);
							deleteButton.setEnabled(true);
							downloadReportsButton.setEnabled(true);
							downloadAppButton.setEnabled(true);
						} else {
							appNameHtml = "<div id=\"appNameInfo\">"
									+ selectedApp.appName + "</div>";
							downloadReportsButton.setEnabled(false);
						}

						// Set app package in right info panel
						appInfoName.setHTML(appNameHtml);
						if ((selectedApp.packageName == null)
								|| selectedApp.packageName.equals("")) {
							appInfoPackage
							.setHTML("<b>Package: </b>N/A");
						} else {
							appInfoPackage.setHTML("<b>Package: </b>"
									+ selectedApp.packageName);
						}

						// Set version in right info panel
						if ((selectedApp.versionName == null)
								|| selectedApp.versionName.equals("")) {
							appInfoVersion
							.setHTML("<b>Version: </b>N/A");
						} else {
							appInfoVersion.setHTML("<b>Version: </b>"
									+ selectedApp.versionName);
						}

						// Get tool results
						final String htmlToolResults = getHtmlToolResults(
								selectedApp.appId, toolsResults);
						toolResultsHtml.setHTML(htmlToolResults);
						logButton.setEnabled(true);
					}
				}

				// Display all reports
				public String getHtmlToolResults(String appId,
						List<ToolStatusGwt> toolResults) {
					// Get summary report
					String statuses = "<hr><h3 title=\"Overview\" id=\"appInfoSectionHeader\">OVERVIEW</h3>\n";
					int summaryCount = 0;

					for (int i = 0; i < toolResults.size(); i++) {
						ToolType analysisType = toolResults.get(i)
								.getToolType();

						if (analysisType == ToolType.SUMMARY) {  // TODO: For AV3, SUMMARY was removed (now uses only REPORT)
							summaryCount++;
							statuses += getToolStatusHtmlDisplay(toolResults
									.get(i));
						}
					}

					if (summaryCount == 0) {
						statuses += getNAStatus();
					}

					// Get pre-processing analysis results
					statuses += "<h3 title=\"App Metadata\" id=\"appInfoSectionHeader\">APP METADATA</h3>\n";
					int preprocessorToolCount = 0;

					for (int i = 0; i < toolResults.size(); i++) {
						ToolType toolType = toolResults.get(i)
								.getToolType();

						if (toolType == ToolType.PREPROCESSOR) {
							preprocessorToolCount++;
							statuses += getPreprocessorStatusHtmlDisplay(toolResults
									.get(i));
						}
					}

					if (preprocessorToolCount == 0) {
						statuses += getNAStatus();
					}

					// Get tool and manually-uploaded results.
					statuses += "<h3 title=\"Tool Analyses\"  id=\"appInfoSectionHeader\">TOOL ANALYSES</h3>\n";
					int analysisToolCount = 0;

					for (int i = 0; i < toolResults.size(); i++) {
						ToolType toolType = toolResults.get(i)
								.getToolType();

						if (toolType == ToolType.TESTTOOL
								|| toolType == ToolType.REPORT) {
							analysisToolCount++;
							statuses += getToolStatusHtmlDisplay(toolResults
									.get(i));
						}
					}

					if (analysisToolCount == 0) {
						statuses += getNAStatus();
					}

					/* Get audit results */
					statuses += "<h3 title=\"Final Organization Determination\" id=\"appInfoSectionHeader\">FINAL ORGANIZATION DETERMINATION</h3>\n";
					int auditCount = 0;

					for (int i = 0; i < toolResults.size(); i++) {
						ToolType toolType = toolResults.get(i)
								.getToolType();

						if (toolType == ToolType.AUDIT) {  // TODO: For AV3, AUDIT was removed (now uses only REPORT)
							auditCount++;
							statuses += getToolStatusHtmlDisplay(toolResults
									.get(i));
						}
					}

					if (auditCount == 0) {
						statuses += getNAStatus();
					}

					return statuses;
				}

				public String getNAStatus() {
					return "<table>\n"
							+ "<tr>\n"
							+ "<td title=\"NA status\" align=\"left\" style='color: dimgray; size:18; weight: bold'width=\"185\">"
							+ "N/A" + "</td>\n" + "</tr>\n"
							+ "</table>\n";
				}

				public String getPreprocessorStatusHtmlDisplay(
						ToolStatusGwt toolStatus) {
					String status = null;

					if (toolStatus.getStatusHtml().indexOf("LOW") > -1) {
						// Pre-processor status of LOW is displayed as
						// "COMPLETED"
						status = "<div id=\"tabledim\" style='color: black'>COMPLETED</div>";
					} else {
						status = toolStatus.getStatusHtml();
					}

					String toolIconURL = null;
					String toolIconAltText = null;
					if (toolStatus.getIconURL() != null) {
						// Check for custom icon URL defined in tool
						// adapter config file
						toolIconURL = toolStatus.getIconURL();
						toolIconAltText = toolStatus.getIconAltText();
					} else {
						// Use default icons
						toolIconURL = toolStatus.getToolType()
								.getDefaultIconURL();
						toolIconAltText = toolStatus.getToolType()
								.getDefaultAltText();
					}

					// To over on table, add 'class=\"hovertable\"
					return getToolRowHtml(toolIconURL, toolIconAltText,
							toolStatus.getToolDisplayName(), status,
							toolStatus.getReport());
				}

				public String getToolStatusHtmlDisplay(
						ToolStatusGwt toolStatus) {
					String toolIconURL = null;
					String toolIconAltText = null;

					if (toolStatus.getIconURL() != null) {
						toolIconURL = toolStatus.getIconURL();
						toolIconAltText = toolStatus.getIconAltText();
					} else {
						toolIconURL = toolStatus.getToolType()
								.getDefaultIconURL();
						toolIconAltText = toolStatus.getToolType()
								.getDefaultAltText();
					}

					return getToolRowHtml(toolIconURL, toolIconAltText,
							toolStatus.getToolDisplayName(),
							toolStatus.getStatusHtml(),
							toolStatus.getReport());
				}

				private String getToolRowHtml(String toolIconURL,
						String toolIconAltText, String toolDisplayName,
						String toolStatus, String toolReport) {
					return "<table>" + "<tr>\n"
							+ "<td>"
							+ "<img class=\"toolimages\" src=\""
							+ toolIconURL
							+ "\" alt=\""
							+ toolIconAltText
							+ "\"> "
							+ "</td>\n"
							// Removed title="mytitle" from following td
							+ "<td align=\"left\" width=\"200\">"
							+ toolDisplayName
							+ "</td>\n"
							// Removed title="mytitle" from following td
							+ "<td align=\"left\" width=\"140\">"
							+ toolStatus
							+ "</td>\n"
							// Removed title="mytitle" from following td
							+ "<td align=\"left\" width=\"45\">"
							+ toolReport + "</td>\n" + "</tr>\n"
							+ "</table>";
				}

			});
		}
	}

	public void pollServer(String username) {
		final String user = username;
		pollingTimer = new Timer() {
			@Override
			public void run() {
				// The following methods hit the database. To increase
				// performance,
				// it might be good to combine the functionality of these three
				// methods into a single method call to the server (and
				// database).
				updateSessionExpiration();
				getUpdatedApps(user);
				getAlertMessage();
			}
		};
		pollingTimer.scheduleRepeating(POLLING_INTERVAL);
	}

	public void showDontRefreshWarning() {
		warningTimer = new Timer() {
			@Override
			public void run() {
				if (allApps.size() <= NUM_APPS_SHOW_REFRESH_WARNING) {
					showMessageDialog(
							"AppVet Info",
							"AppVet is a dynamic web application that automatically updates "
									+ " in real-time. Do not refresh or reload this page while using AppVet.",
									true);
				}
			}
		};
		warningTimer.schedule(1000);
	}

	/** This method resizes the center panel and appsListTable.
	 */
	public void adjustComponentSizes() {
		/**
		 * The following variable is the main variable to adjust when changing
		 * the size of the org_logo.png image. The larger this image, the larger
		 * MARGIN_HEIGHT should be. Note these parameters will be rendered
		 * differently on Firefox, Chrome and IE, with Firefox being the most
		 * problematic, so check all three browsers!
		 */
		final int appVetPanelHeight = this.getOffsetHeight(); // Total height including decoration and padding, but not margin
		
		// Set center panel height
		final int MARGIN_HEIGHT = 0;
		final int centerPanelHeight = appVetPanelHeight - (int) NORTH_PANEL_HEIGHT - (int) SOUTH_PANEL_HEIGHT - MARGIN_HEIGHT;
		centerPanel.setHeight(centerPanelHeight + "px");
		
		// Set appsListTable height inside center panel
		int PAGER_HEIGHT = 80;
		final int appsListTableHeight = centerPanelHeight - PAGER_HEIGHT;
		appsListTable.setHeight(appsListTableHeight + "px");
		
		appsListTable.dataGrid.redraw();
	}

	// The size of the AppVet panel is 0 until displayed in rootlayoutpanel.
	public void scheduleResize() {
		final Timer resizeTimer = new Timer() {
			@Override
			public void run() {
				adjustComponentSizes();
			}
		};
		resizeTimer.schedule(250);
	}

	public int search() {
		searchMode = true;
		final String[] tokens = searchTextBox.getValue().split("\\s+");
		if (tokens == null) {
			return 0;
		}
		final ArrayList<AppInfoGwt> searchList = new ArrayList<AppInfoGwt>();
		for (int i = 0; i < tokens.length; i++) {
			if (Validate.isLegalSearchString(tokens[i])) {
				for (int j = 0; j < allApps.size(); j++) {
					final AppInfoGwt appInfoSummary = allApps.get(j);
					if (appInfoSummary.tokenMatch(tokens[i])) {
						searchList.add(appInfoSummary);
					}
				}
			} else {
				log.warning("Search token: " + tokens[i] + " is not valid");
			}
		}
		searchTextBox.setText("Search");
		if (searchList.size() == 0) {
			showMessageDialog("Search Results", "No search results were found",
					true);
			return 0;
		} else {
			appsListTable.setDataList(searchList);
			appSelectionModel.setSelected(searchList.get(0), true);
			// Set View All to visible
			viewAllButton.setVisible(true);
			return searchList.size();
		}
	}

	public void setAllApps() {
		final SafeHtmlBuilder sb = new SafeHtmlBuilder();
		sb.appendHtmlConstant("<h3>Apps</h3>");						
		appsLabelHtml.setHTML(sb.toSafeHtml());
		appsListTable.setDataList(allApps);
	}

	public void setUpdatedApps(List<AppInfoGwt> updatedAppsList) {
		for (int i = 0; i < updatedAppsList.size(); i++) {
			final AppInfoGwt updatedAppInfo = updatedAppsList.get(i);
			int matchIndex = -1;
			for (int j = 0; j < allApps.size(); j++) {
				final AppInfoGwt appInList = allApps.get(j);
				if (updatedAppInfo.appId.equals(appInList.appId)) {
					matchIndex = j;
					break;
				}
			}
			if (matchIndex > -1) {
				// overwrites existing app
				allApps.set(matchIndex, updatedAppInfo);
				if (!searchMode) {
					appsListTable.set(matchIndex, updatedAppInfo);
				}
			} else {
				// adds new app
				allApps.add(0, updatedAppInfo);
				if (!searchMode) {
					appsListTable.add(0, updatedAppInfo);
				}
			}
		}

		final AppInfoGwt currentlySelectedApp = appSelectionModel
				.getSelectedObject();

		if (currentlySelectedApp == null) {
			return;
		}

		final int currentlySelectedIndex = getAppsListIndex(
				currentlySelectedApp, allApps);

		if (currentlySelectedIndex < 0) {
			return;
		}

		if (!searchMode) {
			if (allApps.size() > 0) {
				appSelectionModel.setSelected(
						allApps.get(currentlySelectedIndex), true);
			} else {
				appInfoIcon.setVisible(false);
				appInfoName.setText("");
				toolResultsHtml.setText("");
				logButton.setEnabled(false);
				uploadReportButton.setEnabled(false);
				deleteButton.setEnabled(false);
				downloadAppButton.setEnabled(false);
			}
		}
	}

	public void getAlertMessage() {

		appVetServiceAsync.getAlertMessage(new AsyncCallback<SystemAlert>() {

			@Override
			public void onFailure(Throwable caught) {
				log.severe("Could not update session: " + caught.getMessage());
			}

			@Override
			public void onSuccess(SystemAlert systemAlert) {

				if (systemAlert != null) {
					// log.info("system alert is not null. Setting message: " +
					// systemAlert.message);	
					String systemMessage = systemAlert.message;

					if (systemAlert.type == SystemAlertType.NORMAL) {
						statusMessageHtml.setHTML("<div><img style=\"vertical-align:bottom\" width=\"18px\" height=\"18px\" src=\"images/icon-metadata.png\" alt=\"System Message\" /> <span style=\"\">" + systemMessage + "</span></div>");						
					} else if (systemAlert.type == SystemAlertType.WARNING) {
						statusMessageHtml.setHTML("<div><img style=\"vertical-align:bottom\" width=\"18px\" height=\"18px\" src=\"images/icon-warning.png\" alt=\"Warning\" /> <span style=\"\">"  + systemMessage + "</span></div>");						
					} else if (systemAlert.type == SystemAlertType.CRITICAL) {
						statusMessageHtml.setHTML("<div><img style=\"vertical-align:bottom\" width=\"18px\" height=\"18px\" src=\"images/icon-error.png\" alt=\"Error\" /> <span style=\"\">"  + systemMessage + "</span></div>");						
					}

				} else {
					// log.info("system alert is null. Setting message to ''");
					statusMessageHtml.setHTML("");						
				}
			}
		});
	}

	public void updateSessionExpiration() {
		appVetServiceAsync.updateSessionExpiration(sessionId,
				sessionExpiration, new AsyncCallback<Date>() {

			@Override
			public void onFailure(Throwable caught) {
				log.severe("Could not update session: "
						+ caught.getMessage());
			}

			@Override
			public void onSuccess(Date expirationTime) {
				if (expirationTime == null) {
					log.severe("Error updating session expiration. Session probably expired.");
					removeSession(true);
				} else {
					sessionTimeLeft(expirationTime);
				}
			}
		});
	}

	public void sessionTimeLeft(Date expirationTime) {
		Date currentDate = new Date();
		long diff = expirationTime.getTime() - currentDate.getTime();
		// log.info("diff: " + diff);
		if (diff <= 0) {
			// Session timed-out
			removeSession(true);
		} else if (diff <= 60000 && timeoutWarningMessage == false) {
			// 60 seconds left before timeout, alert user
			// Close current message if its exists
			killDialogBox(messageDialogBox);
			// Now show timeout dialog
			showTimeoutDialog(diff);
		} else if (diff <= 6000 && timeoutWarningMessage == true) {
			// Timeout warning already displayed. Do nothing.
		} else {
			// Do nothing
		}
	}

	public void openToolCredentials(final ConfigInfoGwt configInfoGwt) {
		final ToolAuthParamDialogBox toolAuthParamDialogBox = new ToolAuthParamDialogBox(
				configInfoGwt);
		toolAuthParamDialogBox.setText("Tool Account Information");
		toolAuthParamDialogBox.center();

		toolAuthParamDialogBox.okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toolAuthParamDialogBox.hide();
			}
		});
	}

	/**
	 * If configuration information for a user is changed by an ADMIN during the
	 * time the user is logged in then the change is not visible to the user
	 * until the user's next log in.
	 */
	public void openUserAccount(final ConfigInfoGwt configInfo) {

		if (configInfo.getUserInfo().isDefaultAdmin()) {
			showMessageDialog("Account Info", "Cannot change info for "
					+ "default AppVet administrator", false);
			return;
		}

		userAcctDialogBox = new UserAcctDialogBox(configInfo, ssoActive);
		userAcctDialogBox.setText("Account Settings");
		userAcctDialogBox.center();
		userAcctDialogBox.password1TextBox.setFocus(true);
		userAcctDialogBox.cancelButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				killDialogBox(userAcctDialogBox);
			}
		});
		userAcctDialogBox.okButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// killDialogBox(userAcctDialogBox);
				final String newLastName = userAcctDialogBox.lastNameTextBox
						.getText();
				final String newFirstName = userAcctDialogBox.firstNameTextBox
						.getText();
				final String newEmail = userAcctDialogBox.emailTextBox
						.getText();
				final String newPassword1 = userAcctDialogBox.password1TextBox
						.getValue();
				final String newPassword2 = userAcctDialogBox.password2TextBox
						.getValue();
				final UserInfo updatedUserInfo = new UserInfo();
				updatedUserInfo.setUserName(userInfo.getUserName());
				updatedUserInfo.setLastName(newLastName);
				updatedUserInfo.setFirstName(newFirstName);
				updatedUserInfo.setEmail(newEmail);
				updatedUserInfo.setPasswords(newPassword1, newPassword2);
				updatedUserInfo.setRoleAndOrgMembership(userInfo.getRoleAndOrgMembership());
				// Validate updated user info
				if (!userInfoIsValid(updatedUserInfo, ssoActive)) {
					return;
				}

				appVetServiceAsync.selfUpdatePassword(updatedUserInfo,
						new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						showMessageDialog("Update Error",
								"Could not update user information",
								true);
						killDialogBox(userAcctDialogBox);
					}

					@Override
					public void onSuccess(Boolean result) {
						final boolean updated = result.booleanValue();
						if (updated) {
							userMenuItem.setText(userInfo
									.getNameWithLastNameInitial());
							userInfo.setUserName(userInfo.getUserName());
							userInfo.setLastName(updatedUserInfo
									.getLastName());
							userInfo.setFirstName(updatedUserInfo
									.getFirstName());
							userInfo.setEmail(updatedUserInfo
									.getEmail());
							updatedUserInfo.setPassword("");

							killDialogBox(userAcctDialogBox);
							showMessageDialog("Account Update",
									"Password updated successfully.",
									false);
						} else {
							showMessageDialog(
									"Update Error",
									"Could not update user information",
									true);
							killDialogBox(userAcctDialogBox);
						}
					}
				});

			}
		});

	}
}
