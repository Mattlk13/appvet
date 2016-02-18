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
import gov.nist.appvet.gwt.client.gui.dialog.UserAcctDialogBox;
import gov.nist.appvet.gwt.client.gui.dialog.UserListDialogBox;
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * @author steveq@nist.gov
 */
@SuppressWarnings("deprecation")
public class AppVetPanel extends DockLayoutPanel {

	// See appvet.gwt.xml
	private static Logger log = Logger.getLogger("AppVetPanel");
	private SingleSelectionModel<AppInfoGwt> appSelectionModel = null;
	private static long MAX_SESSION_IDLE_DURATION = 0;
	private static int POLLING_INTERVAL = 0;
	private final static GWTServiceAsync appVetServiceAsync = GWT
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
	private PushButton deleteButton = null;
	private PushButton downloadReportsButton = null;
	private PushButton uploadReportButton = null;
	private PushButton downloadAppButton = null;
	private PushButton logButton = null;
	private List<AppInfoGwt> allApps = null;
	private TextBox searchTextBox = null;
	private String sessionId = null;
	private static Date sessionExpiration = null;
	private static Timer pollingTimer = null;
	private HorizontalPanel appsListButtonPanel = null;
	private SimplePanel rightCenterPanel = null;
	private static AppUploadDialogBox appUploadDialogBox = null;
	private static MessageDialogBox errorDialogBox = null;
	private static MessageDialogBox messageDialogBox = null;
	private static AboutDialogBox aboutDialogBox = null;
	private static UserListDialogBox usersDialogBox = null;
	private static YesNoConfirmDialog deleteConfirmDialogBox = null;
	private static ReportUploadDialogBox reportUploadDialogBox = null;
	private static UserAcctDialogBox userAcctDialogBox = null;
	public final Label statusMessageLabel = new Label("");
	private String SERVLET_URL = null;
	private String HOST_URL = null;
	private ArrayList<ToolInfoGwt> tools = null;
	private InlineLabel appsLabel = null;
	private int iconVersion = 0;
	private static double NORTH_PANEL_HEIGHT = 110.0;
	private static double SOUTH_PANEL_HEIGHT = 47.0;
	private static boolean searchMode = false;
	private MenuItem accountMenuItem = null;
	public static boolean timeoutWarningMessage = false;

	
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
			displayAppInfo(selectedApp);
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
		AppInfoGwt selected = null;

		public ReportUploadFormHandler(
				ReportUploadDialogBox reportUploadDialogBox, String username,
				AppInfoGwt selected) {
			this.reportUploadDialogBox = reportUploadDialogBox;
			this.selected = selected;
			this.username = username;
			this.appid = selected.appId;
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
					tools)) {
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

	
	public static int[] getCenterPosition(
			com.google.gwt.user.client.ui.UIObject object) {
		final int windowWidth = Window.getClientWidth();
		final int windowHeight = Window.getClientHeight();
		final int xposition = (windowWidth / 2)
				- (object.getOffsetHeight() / 2);
		final int yposition = (windowHeight / 2)
				- (object.getOffsetWidth() / 2);
		final int[] position = { xposition, yposition };
		return position;
	}

	
	public static void killDialogBox(DialogBox dialogBox) {
		if (dialogBox != null) {
			dialogBox.hide();
			dialogBox = null;
		} 
	}

	
	public static void showExpiredSessionMessage() {
		// Close any open dialog boxes
		killDialogBox(appUploadDialogBox);
		killDialogBox(errorDialogBox);
		killDialogBox(messageDialogBox);
		killDialogBox(aboutDialogBox);
		killDialogBox(usersDialogBox);
		killDialogBox(deleteConfirmDialogBox);
		killDialogBox(reportUploadDialogBox);
		killDialogBox(userAcctDialogBox);
		
		// Go back to AppVet login screen
		final LoginPanel loginPanel = new LoginPanel(Unit.PX);
		//loginPanel.setTitle("Login panel");
		final RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
		//rootLayoutPanel.setTitle("Root panel");
		rootLayoutPanel.clear();
		rootLayoutPanel.add(loginPanel);
		
		// Show expired session dialog box
		AppVetPanel.showMessageDialog("AppVet Session",
				"AppVet session has expired", true);
		messageDialogBox.closeButton.setFocus(true);
		messageDialogBox.closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				killDialogBox(messageDialogBox);
			}
		});
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
				killDialogBox(messageDialogBox);
			}
		});
	}

	
	public static void showTimeoutDialog(final long diff) {
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
				
				if (diff <= 0) {
					// Didn't click within the 60s alert period, so expire
					pollingTimer.cancel();
					showExpiredSessionMessage();
				} else {
					sessionExpiration = 
							new Date(System.currentTimeMillis() + 
									MAX_SESSION_IDLE_DURATION);
					killDialogBox(messageDialogBox);
					timeoutWarningMessage = false;
				}
			}
		});
	}

	
	public static boolean validReportFileName(String selectedToolName,
			String uploadedReportFileName, ArrayList<ToolInfoGwt> tools) {
		String selectedToolRequiredFileType = null;
		
		for (int i = 0; i < tools.size(); i++) {
			ToolInfoGwt tool = tools.get(i);
			String toolName = tool.getName();
			if (selectedToolName.equals(toolName)) {
				selectedToolRequiredFileType = tool.getReportFileType();
				break;
			}
		}
		
		final String uploadedReportFileNameLowercase = uploadedReportFileName
				.toLowerCase();
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

	
	public AppVetPanel(Unit unit, final ConfigInfoGwt configInfo,
			AppsListGwt initialApps) {
		super(Unit.PX);
		Window.addResizeHandler(new ResizeHandler() {
			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					resizeComponents();
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
		allApps = initialApps.apps;
		lastAppsListUpdate = initialApps.appsLastChecked;
		
		sinkEvents(Event.ONCLICK);
		sessionId = configInfo.getSessionId();
		sessionExpiration = configInfo.getSessionExpiration();
		MAX_SESSION_IDLE_DURATION = configInfo.getMaxIdleTime();
		POLLING_INTERVAL = configInfo.getUpdatesDelay();
		setSize("", "");
		setStyleName("mainDockPanel");
		SERVLET_URL = configInfo.getAppVetServletUrl();
		HOST_URL = configInfo.getAppVetHostUrl();
		appSelectionModel = new SingleSelectionModel<AppInfoGwt>();
		appSelectionModel.addSelectionChangeHandler(new AppListHandler(this,
				configInfo));
		tools = configInfo.getTools();
		final VerticalPanel northAppVetPanel = new VerticalPanel();
		northAppVetPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		northAppVetPanel.setStyleName("northAppVetPanel");
		addNorth(northAppVetPanel, 155.0);
		northAppVetPanel.setSize("100%", "");
		final HorizontalPanel horizontalPanel_5 = new HorizontalPanel();
		horizontalPanel_5
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setStyleName("appVetHeaderPanel");
		northAppVetPanel.add(horizontalPanel_5);
		northAppVetPanel.setCellVerticalAlignment(horizontalPanel_5,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setSize("100%", "");
		northAppVetPanel.setCellWidth(horizontalPanel_5, "100%");

		Image image = new Image("images/appvet_logo.png");
		image.setAltText("AppVet");
		horizontalPanel_5.add(image);
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
		searchTextBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event_) {
				final boolean enterPressed = KeyCodes.KEY_ENTER == event_
						.getNativeEvent().getKeyCode();
				final String searchString = searchTextBox.getText();
				if (enterPressed) {
					final int numFound = search();
					if (numFound > 0) {
						appsLabel.setText("Search Results for \""
								+ searchString + "\"");
					}
				}
			}
		});
		searchTextBox.setSize("300px", "22px");
		horizontalPanel_6.add(searchTextBox);
		horizontalPanel_6.setCellVerticalAlignment(searchTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		final PushButton searchButton = new PushButton("Search");
		searchButton.setTitle("Search by app ID, name, release kit, etc.");
		searchButton.setSize("18px", "18px");
		searchButton
				.setHTML("<img width=\"18px\" src=\"images/icon-search-up.png\" alt=\"search\" />");
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final String searchString = searchTextBox.getText();
				final int numFound = search();
				if (numFound > 0) {
					appsLabel.setText("Search Results for \"" + searchString
							+ "\"");
				}
			}
		});
		horizontalPanel_6.add(searchButton);
		horizontalPanel_6.setCellHorizontalAlignment(searchButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6.setCellVerticalAlignment(searchButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		// Your org_logo.png should be placed in
		// $CATALINA_HOME/webapps/appvet-images directory.
		Image orgLogo = new Image("../appvet_images/org_logo.png");
		orgLogo.setSize("120px", "120px");
		String orgLogoAltText = configInfo.getOrgLogoAltText();
		orgLogo.setAltText(orgLogoAltText);
		//orgLogo.setTitle(orgLogoAltText);
		horizontalPanel_5.add(orgLogo);
		horizontalPanel_5.setCellVerticalAlignment(orgLogo,
				HasVerticalAlignment.ALIGN_BOTTOM);
		horizontalPanel_5.setCellHorizontalAlignment(orgLogo,
				HasHorizontalAlignment.ALIGN_RIGHT);
		final HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		northAppVetPanel.add(horizontalPanel_3);
		northAppVetPanel.setCellVerticalAlignment(horizontalPanel_3,
				HasVerticalAlignment.ALIGN_MIDDLE);
		northAppVetPanel.setCellHorizontalAlignment(horizontalPanel_3,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3.setSize("100%", "");
		northAppVetPanel.setCellWidth(horizontalPanel_3, "100%");
		final MenuBar appVetMenuBar = new MenuBar(false);
		horizontalPanel_3.add(appVetMenuBar);
		horizontalPanel_3.setCellWidth(appVetMenuBar, "25%");
		horizontalPanel_3.setCellVerticalAlignment(appVetMenuBar,
				HasVerticalAlignment.ALIGN_MIDDLE);
		appVetMenuBar.setStyleName("appVetMenuBar");
		appVetMenuBar.setAutoOpen(false);
		appVetMenuBar.setSize("250px", "");
		appVetMenuBar.setAnimationEnabled(false);
		appVetMenuBar.setFocusOnHoverEnabled(true);

		final MenuBar userMenuBar = new MenuBar(true);
		userMenuBar.setFocusOnHoverEnabled(false);

		accountMenuItem = new MenuItem(userInfo.getFirstName(), true,
				userMenuBar);
		userMenuBar.setHeight("");
		accountMenuItem.setStyleName("AccountMenuItem");
		final MenuItem accountSettingsMenuItem = new MenuItem(
				"Account Settings", false, new Command() {
					@Override
					public void execute() {
						openUserAccount(configInfo);
					}
				});
		userMenuBar.addItem(accountSettingsMenuItem);
		accountSettingsMenuItem.setHeight("");
		final MenuItem myAppsMenuItem = new MenuItem("My Apps", false,
				new Command() {
					@Override
					public void execute() {
						searchTextBox.setText(userInfo.getUserName());
						final int numFound = search();
						if (numFound > 0) {
							appsLabel.setText("My Apps");
						}
					}
				});
		userMenuBar.addItem(myAppsMenuItem);
		myAppsMenuItem.setHeight("");
		final MenuItemSeparator separator = new MenuItemSeparator();
		userMenuBar.addSeparator(separator);
		separator.setHeight("");
		final MenuItem logoutMenuItem = new MenuItem("Logout", false,
				new Command() {
					@Override
					public void execute() {
						appVetServiceAsync.removeSession(sessionId,
								new AsyncCallback<Boolean>() {
									@Override
									public void onFailure(Throwable caught) {
										AppVetPanel.showMessageDialog(
												"AppVet Error",
												"App list retrieval error",
												true);
										errorDialogBox.closeButton
												.setFocus(true);
										errorDialogBox.closeButton
												.addClickHandler(new ClickHandler() {
													@Override
													public void onClick(
															ClickEvent event) {
														killDialogBox(errorDialogBox);
													}
												});
									}

									@Override
									public void onSuccess(Boolean result) {
										if (result == false) {
											AppVetPanel.showMessageDialog(
													"AppVet Error",
													"Could not remove session",
													true);
											errorDialogBox.closeButton
													.setFocus(true);
											errorDialogBox.closeButton
													.addClickHandler(new ClickHandler() {
														@Override
														public void onClick(
																ClickEvent event) {
															killDialogBox(errorDialogBox);
														}
													});
										} else {
											pollingTimer.cancel();
											final LoginPanel loginPanel = new LoginPanel(
													Unit.PX);
											//loginPanel.setTitle("Login panel");
											final RootLayoutPanel rootLayoutPanel = RootLayoutPanel
													.get();
											//rootLayoutPanel.setTitle("Root panel");
											rootLayoutPanel.clear();
											rootLayoutPanel.add(loginPanel);
											System.gc();
										}
									}
								});
					}
				});
		userMenuBar.addItem(logoutMenuItem);
		logoutMenuItem.setHeight("");
		appVetMenuBar.addItem(accountMenuItem);
		accountMenuItem.setHeight("");
		final MenuBar helpMenuBar = new MenuBar(true);
		helpMenuBar.setFocusOnHoverEnabled(false);

		final MenuItem helpMenuItem = new MenuItem("Help", true, helpMenuBar);
		helpMenuBar.setHeight("");
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
		final MenuItem documentationMenuItem = new MenuItem("Documentation",
				false, new Command() {
					@Override
					public void execute() {
						Window.open("http://appvet.github.io/appvet", "_blank",
								null);
					}
				});
		helpMenuBar.addItem(documentationMenuItem);
		documentationMenuItem.setHeight("");
		appVetMenuBar.addItem(helpMenuItem);
		helpMenuItem.setHeight("");
		helpMenuBar.addItem(aboutMenuItem);
		aboutMenuItem.setHeight("");
		horizontalPanel_3.add(statusMessageLabel);
		horizontalPanel_3.setCellVerticalAlignment(statusMessageLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHorizontalAlignment(statusMessageLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3.setCellWidth(statusMessageLabel, "50%");
		statusMessageLabel.setStyleName("statusError");
		statusMessageLabel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		statusMessageLabel.setSize("420px", "18px");
		//statusMessageLabel.setTitle("Status message");
		
		Label lblNewLabel = new Label("");
		horizontalPanel_3.add(lblNewLabel);
		horizontalPanel_3.setCellWidth(lblNewLabel, "25%");
		lblNewLabel.setHeight("");
		final MenuBar adminMenuBar = new MenuBar(true);
		adminMenuBar.setFocusOnHoverEnabled(true);

		// Log menu
		final MenuBar logMenubar = new MenuBar(true);
		logMenubar.setFocusOnHoverEnabled(true);
		final MenuItem mntmAppVetLog = new MenuItem("View", false,
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
		logMenubar.addItem(mntmAppVetLog);
		final MenuItem clearAppVetLogMenuItem = new MenuItem("Clear", false,
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
		logMenubar.addItem(clearAppVetLogMenuItem);
		final MenuItem downloadAppVetLogMenuItem = new MenuItem("Download",
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
		logMenubar.addItem(downloadAppVetLogMenuItem);
		
		// Alert menu
		final MenuBar alertMenubar = new MenuBar(true);
		alertMenubar.setFocusOnHoverEnabled(true);
		
		final MenuItem setAlertMenuItem = new MenuItem("Set", false,
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
										if (setAlertDialogBox.alertNormalRadioButton.isChecked())
											alertType = SystemAlertType.NORMAL;
										else if (setAlertDialogBox.alertWarningRadioButton.isChecked())
											alertType = SystemAlertType.WARNING;
										else if (setAlertDialogBox.alertCriticalRadioButton.isChecked())
											alertType = SystemAlertType.CRITICAL;
										
										String alertMessage = setAlertDialogBox.alertTextArea.getText();
										if (alertMessage == null || alertMessage.isEmpty()) {
											showMessageDialog("AppVet Error",
													"Alert message cannot be empty.",
													true);
										}
										
										setAlertMessage(userInfo.getUserName(), alertType, alertMessage);
									}
								});
					}
				});
		alertMenubar.addItem(setAlertMenuItem);
		
		final MenuItem clearAlertMessageMenuItem = new MenuItem("Clear", false,
				new Command() {
					@Override
					public void execute() {
						clearAlertMessage(userInfo.getUserName());
					}
				});
		alertMenubar.addItem(clearAlertMessageMenuItem);
		

		// Admin menubar
		final MenuItem adminMenuItem = new MenuItem("Admin", true, adminMenuBar);
		adminMenuBar.addItem("Log", logMenubar);

		final MenuItem usersMenuItem = new MenuItem("Users", false,
				new Command() {
					@Override
					public void execute() {
						usersDialogBox = new UserListDialogBox(configInfo.getNumRowsUsersList());
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
		adminMenuBar.addItem(usersMenuItem);
		
		adminMenuBar.addItem("Alerts", alertMenubar);
		
		
		if (userInfo.getRole().equals(Role.ADMIN.name())) {
			appVetMenuBar.addItem(adminMenuItem);
		}

		final HorizontalSplitPanel centerAppVetSplitPanel = new HorizontalSplitPanel();
		//centerAppVetSplitPanel.setTitle("AppVet split pane");
		centerAppVetSplitPanel.setSplitPosition("65%");
		centerAppVetSplitPanel.setSize("100%", "");
		final SimplePanel leftCenterPanel = new SimplePanel();
		//leftCenterPanel.setTitle("AppVet apps list pane");

		centerAppVetSplitPanel.setLeftWidget(leftCenterPanel);
		leftCenterPanel.setSize("100%", "");
		final DockPanel dockPanel_1 = new DockPanel();
		dockPanel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dockPanel_1.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		leftCenterPanel.setWidget(dockPanel_1);
		dockPanel_1.setSize("100%", "");
		rightCenterPanel = new SimplePanel();
		//rightCenterPanel.setTitle("AppVet app info panel");

		centerAppVetSplitPanel.setRightWidget(rightCenterPanel);
		rightCenterPanel.setSize("", "");
		final VerticalPanel appInfoVerticalPanel = new VerticalPanel();
		appInfoVerticalPanel
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		rightCenterPanel.setWidget(appInfoVerticalPanel);
		appInfoVerticalPanel.setSize("", "");
		final HorizontalPanel appInfoPanel = new HorizontalPanel();
		appInfoPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
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
		appInfoIcon.setSize("75px", "75px");
		final VerticalPanel verticalPanel = new VerticalPanel();
		appInfoPanel.add(verticalPanel);
		appInfoName = new HTML("", true);
		appInfoName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		verticalPanel.add(appInfoName);
		appInfoName.setStyleName("appInfoName");
		appInfoName.setWidth("");
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
		appsLabel = new InlineLabel("Apps");
		//appsLabel.setTitle("Apps");
		appsLabel.setText("Apps");
		appsLabel.setStyleName("AppsLabel");
		appsListButtonPanel.add(appsLabel);
		appsListButtonPanel.setCellWidth(appsLabel, "60px");
		appsListButtonPanel.setCellVerticalAlignment(appsLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		appsLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		appsLabel.setSize("", "");
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
		final PushButton submitButton = new PushButton("Upload App");
		submitButton.setTitle("Upload App");
		submitButton
				.setHTML("<img width=\"80px\" src=\"images/upload-app-up.png\" alt=\"Upload App\" />");
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
		final PushButton viewAllButton = new PushButton("View All Apps");
		viewAllButton
				.setHTML("<img width=\"80px\" src=\"images/view-all-up.png\" alt=\"View All Apps\" />");
		viewAllButton.setTitle("View All Apps");
		viewAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchMode = false;
				setAllApps();
			}
		});
		horizontalPanel.add(viewAllButton);
		horizontalPanel.setCellHorizontalAlignment(viewAllButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellVerticalAlignment(viewAllButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		viewAllButton.setSize("80px", "");
		horizontalPanel.add(submitButton);
		horizontalPanel.setCellVerticalAlignment(submitButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(submitButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		submitButton.setSize("80px", "");
		appsListTable = new AppsListPagingDataGrid<AppInfoGwt>();
		appsListTable.dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		appsListTable.dataGrid.setFocus(false);
		appsListTable.setPageSize(configInfo.getNumRowsAppsList());
		appsListTable.dataGrid.setStyleName("dataGrid");
		dockPanel_1.add(appsListTable, DockPanel.CENTER);
		dockPanel_1.setCellWidth(appsListTable, "100%");
		dockPanel_1.setCellHorizontalAlignment(appsListTable,
				HasHorizontalAlignment.ALIGN_CENTER);
		dockPanel_1.setCellVerticalAlignment(appsListTable,
				HasVerticalAlignment.ALIGN_MIDDLE);
		appsListTable.setAppVetHostUrl(HOST_URL);
		//appsListTable.setAppVetProxyUrl(PROXY_URL);
		appsListTable.dataGrid.setSize("100%", "");
		appsListTable.setDataList(initialApps.apps);
		appsListTable.setSize("100%", "");
		appsListTable.dataGrid.setSelectionModel(appSelectionModel);

		SimplePanel simplePanel = new SimplePanel();
		addSouth(simplePanel, 21.0);
		simplePanel.setSize("100%", "");

		HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		horizontalPanel_2
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		simplePanel.setWidget(horizontalPanel_2);
		horizontalPanel_2.setSize("100%", "100%");

		Image nistLogo = new Image("images/nist_logo_darkgrey.png");
		nistLogo.setAltText("NIST logo");
		//nistLogo.setTitle("NIST logo");
		horizontalPanel_2.add(nistLogo);
		horizontalPanel_2.setCellVerticalAlignment(nistLogo,
				HasVerticalAlignment.ALIGN_BOTTOM);
		horizontalPanel_2.setCellHorizontalAlignment(nistLogo,
				HasHorizontalAlignment.ALIGN_RIGHT);
		nistLogo.setSize("50px", "13px");
		
		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		appInfoVerticalPanel.add(horizontalPanel_1);
		
		Label label = new Label("");
		horizontalPanel_1.add(label);
		label.setSize("0", "0");
		uploadReportButton = new PushButton("Upload Report");
		horizontalPanel_1.add(uploadReportButton);
		uploadReportButton
				.setHTML("<img width=\"80px\" src=\"images/upload-report-up.png\" alt=\"Upload App\" />");
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
					reportUploadDialogBox = new ReportUploadDialogBox(
							userInfo, sessionId, selected.appId,
							SERVLET_URL, selected.os, tools);
					reportUploadDialogBox.setText("Upload Report for "
							+ selected.appName);
					reportUploadDialogBox.center();
					// reportUploadDialogBox.cancelButton.setFocus(true);
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
									reportUploadDialogBox, userName,
									selected));

				}
			}
		});
		uploadReportButton.setSize("80px", "");
		logButton = new PushButton("View Log");
		horizontalPanel_1.add(logButton);
		logButton
				.setHTML("<img width=\"80px\" src=\"images/view-log-up.png\" alt=\"Upload App\" />");
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
		logButton.setSize("80px", "");
		deleteButton = new PushButton("Delete App");
		horizontalPanel_1.add(deleteButton);
		deleteButton
				.setHTML("<img width=\"80px\" src=\"images/delete-app-up.png\" alt=\"Upload App\" />");
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
								+ selected.appId
								+ "?\r\n</p>");
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
		deleteButton.setSize("80px", "");
		downloadReportsButton = new PushButton("Download Reports");
		horizontalPanel_1.add(downloadReportsButton);
		downloadReportsButton
				.setHTML("<img width=\"80px\" src=\"images/download-reports-up.png\" alt=\"Download Reports\" />");
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
		downloadReportsButton.setSize("80px", "");
		
				downloadAppButton = new PushButton("Download App");
				horizontalPanel_1.add(downloadAppButton);
				downloadAppButton
						.setHTML("<img width=\"80px\" src=\"images/download-app-up.png\" alt=\"Download App\" />");
				downloadAppButton.setTitle("Download App");
				downloadAppButton.setSize("80px", "");
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
							Window.open(url, "_self", "");
						}
					}
				});
		logButton.setVisible(true);
		
				uploadReportButton.setVisible(true);
		toolResultsHtml = new HTML("", true);
		appInfoVerticalPanel.add(toolResultsHtml);
		appInfoVerticalPanel.setCellWidth(toolResultsHtml, "100%");
		toolResultsHtml.setWidth("100%");
		toolResultsHtml
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		toolResultsHtml.setStyleName("toolResultsHtml");
		add(centerAppVetSplitPanel);
		if ((initialApps != null) && (initialApps.apps.size() > 0)) {
			appSelectionModel.setSelected(initialApps.apps.get(0), true);
		} else {
			logButton.setEnabled(false);
			uploadReportButton.setEnabled(false);
			deleteButton.setEnabled(false);
			downloadReportsButton.setEnabled(false);
			downloadAppButton.setEnabled(false);
		}
		pollServer(userName);
		scheduleResize();
	}
	
	
	public void setAlertMessage(String username, SystemAlertType alertType, String alertMessage) {
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
					//log.info("Alert message set");
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
					//log.info("Alert message cleared");
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
//						log.severe("Error retrieving updated apps. Server might be down: "
//								+ caught.toString());
						pollingTimer.cancel();
					}

					@Override
					public void onSuccess(AppsListGwt updatedAppsList) {
						if (updatedAppsList == null) {
							showMessageDialog("AppVet Database Error",
									"Could not retrieve updated apps", true);
						} else {
							//log.info("Update time: " + updatedAppsList.appsLastChecked.toString());
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
		sessionExpiration = 
				new Date(System.currentTimeMillis() + MAX_SESSION_IDLE_DURATION);
	}
	
	
	public synchronized void displayAppInfo(final AppInfoGwt selectedApp) {
		//log.info("Updating appinfopanel for: " + selectedApp.appId);
		// Show selected app info results
		if (selectedApp != null) {
			appVetServiceAsync.getToolsResults(selectedApp.os, sessionId,
					selectedApp.appId,
					new AsyncCallback<List<ToolStatusGwt>>() {

						@Override
						public void onFailure(Throwable caught) {
							showMessageDialog("AppVet Error",
									"System error retrieving app info",
									true);
						}

						@Override
						public void onSuccess(
								List<ToolStatusGwt> toolsResults) {

							if ((toolsResults == null)
									|| toolsResults.isEmpty()) {
								showMessageDialog("AppVet Error: ",
										"Could not retrieve app info.",
										true);
							} else {
								String defaultIcon = null;
								String altText = null;
								
								if (selectedApp.os == DeviceOS.ANDROID) {
									defaultIcon = "android-icon-gray.png";
									altText = "Android app";
								} else if (selectedApp.os == DeviceOS.IOS) {
									defaultIcon = "apple-icon-gray.png";
									altText = "iOS app";
								}

								String appNameHtml = null;
								appInfoIcon.setVisible(true);
								appInfoIcon.setAltText("App Icon");

								// Set app icon in right app info panel
								if (selectedApp.appStatus == AppStatus.REGISTERING) {
									//log.info("Displaying REGISTERING");
									iconVersion++;
									final String iconPath = "images/"
											+ defaultIcon + "?v"
											+ iconVersion;
									appInfoIcon.setUrl(iconPath);
									appInfoIcon.setAltText(altText);
									appNameHtml = "<div id=\"appNameInfo\">"
											+ "Unknown" + "</div>";
									appInfoName.setHTML(appNameHtml);
									toolResultsHtml
											.setHTML("Waiting for data...");
									appInfoPackage
											.setHTML("<b>Package: </b>N/A");
									appInfoVersion
											.setHTML("<b>Version: </b>N/A");
									return;
								} else if (selectedApp.appStatus == AppStatus.PENDING) {
									//log.info("Displaying PENDING");
									final String iconPath = "images/"
											+ defaultIcon;
									appInfoIcon.setUrl(iconPath);
									appInfoIcon.setAltText(altText);
								} else if (selectedApp.appStatus == AppStatus.PROCESSING) {
									//log.info("Displaying PROCESSING");
									iconVersion++;
									final String iconPath = "images/"
											+ defaultIcon;
									appInfoIcon.setUrl(iconPath);
									appInfoIcon.setAltText(altText);
								} else {
									//log.info("Displaying OTHER STATUS: " + selectedApp.appStatus.name());
									String URL = HOST_URL;

									final String iconPath = URL
											+ "/appvet_images/"
											+ selectedApp.appId + ".png";
									appInfoIcon.setUrl(iconPath);
									appInfoIcon.setAltText(selectedApp.appName);
								}

								
								String appName = null;
								if (selectedApp.appName == null) {
									appName = "Retrieving...";
								} else {
									appName = selectedApp.appName;
								}

								// Set app name in right info panel
								if ((selectedApp.appStatus == AppStatus.NA)
										|| (selectedApp.appStatus == AppStatus.ERROR)
										|| (selectedApp.appStatus == AppStatus.HIGH)
										|| (selectedApp.appStatus == AppStatus.MODERATE)
										|| (selectedApp.appStatus == AppStatus.LOW)) {
									appNameHtml = "<div id=\"appNameInfo\">"
											+ appName + "</div>";
									uploadReportButton.setEnabled(true);
									deleteButton.setEnabled(true);
									downloadReportsButton.setEnabled(true);
									downloadAppButton.setEnabled(true);
								} else {
									appNameHtml = "<div id=\"appNameInfo\">"
											+ appName + "</div>";
									downloadReportsButton.setEnabled(false);
								}

								// Set app package in right info panel
								appInfoName.setHTML(appNameHtml);
								if ((selectedApp.packageName == null)
										|| selectedApp.packageName
												.equals("")) {
									appInfoPackage
											.setHTML("<b>Package: </b>N/A");
								} else {
									appInfoPackage
											.setHTML("<b>Package: </b>"
													+ selectedApp.packageName);
								}

								// Set version in right info panel
								if ((selectedApp.versionName == null)
										|| selectedApp.versionName
												.equals("")) {
									appInfoVersion
											.setHTML("<b>Version: </b>N/A");
								} else {
									appInfoVersion
											.setHTML("<b>Version: </b>"
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
							String statuses = "<hr><h3 title=\"Overview\" id=\"appInfoSectionHeader\">Overview</h3>\n";
							int summaryCount = 0;

							for (int i = 0; i < toolResults.size(); i++) {
								ToolType analysisType = toolResults.get(i)
										.getToolType();

								if (analysisType == ToolType.SUMMARY) {
									summaryCount++;
									statuses += getToolStatusHtmlDisplay(toolResults
											.get(i));
								}
							}

							if (summaryCount == 0) {
								statuses += getNAStatus();
							}

							// Get pre-processing analysis results 
							statuses += "<h3 title=\"App Metadata\" id=\"appInfoSectionHeader\">App Metadata</h3>\n";
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
							statuses += "<h3 title=\"Tool Analyses\"  id=\"appInfoSectionHeader\">Tool Analyses</h3>\n";
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
							statuses += "<h3 title=\"Final Organization Determination\" id=\"appInfoSectionHeader\">Final Organization Determination</h3>\n";
							int auditCount = 0;

							for (int i = 0; i < toolResults.size(); i++) {
								ToolType toolType = toolResults.get(i)
										.getToolType();

								if (toolType == ToolType.AUDIT) {
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
							log.info("Tool : " + toolStatus.getToolDisplayName() + ", status: " 
									+ toolStatus.getStatusHtml());
							
							String status = null;

							if (toolStatus.getStatusHtml().indexOf("LOW") > -1) {
								status = "<div id=\"tabledim\" style='color: black'>COMPLETED</div>";
							} else {
								status = toolStatus.getStatusHtml();
							}
							
							String toolIconURL = null;
							String toolIconAltText = null;
							if (toolStatus.getIconURL() != null) {
								// Check for custom icon URL defined in tool adapter config file
								toolIconURL = toolStatus.getIconURL();
								toolIconAltText = toolStatus.getIconAltText();
							} else {
								// Use default icons
								toolIconURL = toolStatus.getToolType().getDefaultIconURL();
								toolIconAltText = toolStatus.getToolType().getDefaultAltText();
							}

							// To over on table, add 'class=\"hovertable\"
							return getToolRowHtml(toolIconURL, toolIconAltText, 
									toolStatus.getToolDisplayName(), status, toolStatus.getReport());
						}

						public String getToolStatusHtmlDisplay(
								ToolStatusGwt toolStatus) {
							String toolIconURL = null;
							String toolIconAltText = null;
							
							if (toolStatus.getIconURL() != null) {
								toolIconURL = toolStatus.getIconURL();
								toolIconAltText = toolStatus.getIconAltText();
							} else {
								toolIconURL = toolStatus.getToolType().getDefaultIconURL();
								toolIconAltText = toolStatus.getToolType().getDefaultAltText();
							}
							
							return getToolRowHtml(toolIconURL, toolIconAltText, toolStatus.getToolDisplayName(),
									toolStatus.getStatusHtml(), toolStatus.getReport());
						}
						
						private String getToolRowHtml(String toolIconURL, String toolIconAltText, 
								String toolDisplayName, String toolStatus, String toolReport) {
							return "<table>"
									+ "<tr>\n"
									+ "<td>"
									+ "<img class=\"toolimages\" src=\"" + toolIconURL + "\" alt=\"" + toolIconAltText + "\"> "
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
									+ toolReport + "</td>\n"
									+ "</tr>\n" + "</table>";
						}
						
					});
		}
		//log.info("Done updating appinfopanel for: " + selectedApp.appId);
	
	}

	
	public void pollServer(String username) {
		final String user = username;
		pollingTimer = new Timer() {
			@Override
			public void run() {
				// The following methods hit the database. To increase performance,
				// it might be good to combine the functionality of these three
				// methods into a single method call to the server (and database).
				updateSessionExpiration();
				getUpdatedApps(user);
				getAlertMessage();
			}
		};
		pollingTimer.scheduleRepeating(POLLING_INTERVAL);
	}

	
	public void resizeComponents() {
		/**
		 * The following variable is the main variable to adjust when changing
		 * the size of the org_logo.png image. The larger this image, the larger
		 * MARGIN_HEIGHT should be. Note these parameters will be rendered
		 * differently on Firefox, Chrome and IE, with Firefox being the most
		 * problematic, so check all three browsers!
		 */
		final int MARGIN_HEIGHT = 75;
		final int appVetPanelHeight = getOffsetHeight();

		// First adjust apps list table
		final int appsListButtonPanelHeight = appsListButtonPanel
				.getOffsetHeight();

		final int appsListTableHeight = appVetPanelHeight
				- (int) NORTH_PANEL_HEIGHT - (int) SOUTH_PANEL_HEIGHT
				- appsListButtonPanelHeight - MARGIN_HEIGHT;

		appsListTable.setSize("100%", appsListTableHeight + "px");
		appsListTable.dataGrid.redraw();

	}

	
	// The size of the AppVet panel is 0 until displayed in rootlayoutpanel.
	public void scheduleResize() {
		final Timer resizeTimer = new Timer() {
			@Override
			public void run() {
				resizeComponents();
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
		appsListTable.setDataList(searchList);
		if (searchList.size() == 0) {
			showMessageDialog("Search Results", "No search results were found",
					true);
			return 0;
		} else {
			appSelectionModel.setSelected(searchList.get(0), true);
			return searchList.size();
		}
	}

	
	public void setAllApps() {
		appsLabel.setText("Apps");
		appsListTable.setDataList(allApps);
	}

	
	/**
	 * Check to make sure that the displayed app status is reflecting the latest
	 * updated app status. This is needed since a race condition exists where an
	 * app's status will change from PROCESSING to some other status and the
	 * table misses the opportunity to refresh the screen.
	 * 
	 * @param latestApps
	 *            The latest updated apps information.
	 * @param numApps
	 *            The number of apps, starting from the most recent, to check.
	 */
	public void refreshLastApp(List<AppInfoGwt> latestApps, int numAppsToCheck) {

		ListDataProvider<AppInfoGwt> appsTableList = appsListTable
				.getDataProvider();
		Set<HasData<AppInfoGwt>> displayedApps = appsTableList
				.getDataDisplays();
		Iterator<HasData<AppInfoGwt>> displayedAppsIterator = displayedApps
				.iterator();

		int numApps = 0;

		if (numAppsToCheck > latestApps.size()) {
			numApps = latestApps.size();
		}

		for (int i = 0; i < numApps; i++) {
			AppInfoGwt app = latestApps.get(i);
			AppStatus actualAppStatus = app.appStatus;

			// Check this app against the app status that is displayed
			while (displayedAppsIterator.hasNext()) {
				HasData<AppInfoGwt> displayedApp = displayedAppsIterator.next();
				Iterable<AppInfoGwt> displayedAppItems = displayedApp
						.getVisibleItems();
				for (AppInfoGwt displayedAppItem : displayedAppItems) { // Match
																		// on
																		// app
																		// ID
					if (displayedAppItem.appId.equals(app.appId)) {
						AppStatus displayedAppStatus = displayedAppItem.appStatus;
						if (displayedAppStatus != actualAppStatus) {
							appsListTable.set(0, app);

						}
					}
				}
			}
		}
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
						log.severe("Could not update session: "
								+ caught.getMessage());
					}

					@Override
					public void onSuccess(SystemAlert systemAlert) {
						if (systemAlert != null) {
							//log.info("system alert is not null. Setting message: " + systemAlert.message);
							if (systemAlert.type == SystemAlertType.NORMAL) {
								statusMessageLabel.setStyleName("statusOk");
							} else if (systemAlert.type == SystemAlertType.WARNING) {
								statusMessageLabel.setStyleName("statusWarning");
							} else if (systemAlert.type == SystemAlertType.CRITICAL) {
								statusMessageLabel.setStyleName("statusError");
							}
							
							statusMessageLabel.setText(systemAlert.message);
						} else {
							//log.info("system alert is null. Setting message to ''");
							statusMessageLabel.setText("");
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
							// Session has expired
							pollingTimer.cancel();
							showExpiredSessionMessage();
						} else {
							sessionTimeLeft(expirationTime);
						}
					}
				});
	}
	

	
	public static void sessionTimeLeft(Date expirationTime) {
		Date currentDate = new Date();
		long diff = expirationTime.getTime() - currentDate.getTime();
		//log.info("diff: " + diff);
		if (diff <= 0) {
			// Session has expired
			pollingTimer.cancel();
			showExpiredSessionMessage();
		} else if (diff <= 60000 && timeoutWarningMessage == false){
			// 60 seconds left before timeout, alert user
			showTimeoutDialog(diff);
		} else if (diff <= 6000 && timeoutWarningMessage == true) {
			// Timeout warning already displayed. Do nothing.
		} else {
			// Do nothing
		}
	}


	public void openUserAccount(final ConfigInfoGwt configInfo) {
		userAcctDialogBox = new UserAcctDialogBox(configInfo);
		userAcctDialogBox.setText("Account Settings");
		userAcctDialogBox.center();
		userAcctDialogBox.password1TextBox.setFocus(true);
		userAcctDialogBox.okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				killDialogBox(userAcctDialogBox);
			}
		});
		userAcctDialogBox.updateButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final String newLastName = userAcctDialogBox.lastNameTextBox
						.getText();
				final String newFirstName = userAcctDialogBox.firstNameTextBox
						.getText();
				final String newOrganization = userAcctDialogBox.organizationTextBox
						.getText();
				final String newDepartment = userAcctDialogBox.departmentTextBox
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
				updatedUserInfo.setOrganization(newOrganization);
				updatedUserInfo.setDepartment(newDepartment);
				updatedUserInfo.setEmail(newEmail);
				updatedUserInfo.setChangePassword(true);
				updatedUserInfo.setPasswords(newPassword1, newPassword2);
				updatedUserInfo.setRole(userInfo.getRole());
				if (!updatedUserInfo.isValid()) {
					return;
				}
				appVetServiceAsync.updateSelf(updatedUserInfo,
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
									accountMenuItem.setText(userInfo
											.getNameWithLastNameInitial());
									userInfo.setUserName(userInfo.getUserName());
									userInfo.setLastName(updatedUserInfo
											.getLastName());
									userInfo.setFirstName(updatedUserInfo
											.getFirstName());
									userInfo.setOrganization(updatedUserInfo
											.getOrganization());
									userInfo.setDepartment(updatedUserInfo
											.getDepartment());
									userInfo.setEmail(updatedUserInfo
											.getEmail());
									updatedUserInfo.setChangePassword(false);
									updatedUserInfo.setPassword("");
									userInfo.setRole(updatedUserInfo.getRole());
								
									killDialogBox(userAcctDialogBox);
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
