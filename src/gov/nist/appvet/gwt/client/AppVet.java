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
package gov.nist.appvet.gwt.client;

import java.util.logging.Logger;

import gov.nist.appvet.gwt.client.gui.AppVetPanel;
import gov.nist.appvet.gwt.client.gui.LoginPanel;
import gov.nist.appvet.gwt.client.gui.dialog.MessageDialogBox;
import gov.nist.appvet.gwt.shared.AppsListGwt;
import gov.nist.appvet.gwt.shared.ConfigInfoGwt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author steveq@nist.gov
 */
public class AppVet implements EntryPoint {

	private static Logger log = Logger.getLogger("AppVet");
	private final GWTServiceAsync appVetService = GWT.create(GWTService.class);
	private static MessageDialogBox messageDialogBox = null;
	
	// SSO parameters
	String username = null;
	String password = null;
	
	@Override
	public void onModuleLoad() {
		
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
		    public void onWindowClosing(Window.ClosingEvent closingEvent) {
		    	log.info("Trying to relog back in");
				if (username == null || password == null) {
					// Send to AppVet GUI
					final LoginPanel loginPanel = new LoginPanel(Unit.PX);
					// loginPanel.setTitle("Login panel");
					final RootLayoutPanel rootPanel = RootLayoutPanel.get();
					// rootPanel.setTitle("Root panel");
					rootPanel.add(loginPanel);
				} else if (username != null && password != null) {
					// Login attempt via SSO
					// log.info("AppVet GWT got: name=" + username);
					// Authenticate SSO username and password
					authenticateSSO(username, password);
				}
		        //closingEvent.setMessage("AppVet is a dynamic web application and should not be refreshed.");
		    }
		});

/*		// disable refresh button
		Event.addNativePreviewHandler(new NativePreviewHandler() {

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				switch (event.getTypeInt()) {
				case Event.ONKEYDOWN:
					NativeEvent nEvent = event.getNativeEvent();
					if (nEvent.getCtrlKey() && nEvent.getKeyCode() == 'R') {
						log.info("Key code R seen");
						nEvent.preventDefault();
					}

					if (nEvent.getKeyCode() == 116) {
						log.info("Key code 116 seen");
						nEvent.preventDefault();
					}
					break;
				}
			}

		});*/

		// If SSO is used, these parameters should not be null. If one or
		// both parameters are null, send to AppVet GUI.
		username = Window.Location.getParameter("ssou");
		password = Window.Location.getParameter("ssop");

		if (username == null || password == null) {
			// Send to AppVet GUI
			final LoginPanel loginPanel = new LoginPanel(Unit.PX);
			// loginPanel.setTitle("Login panel");
			final RootLayoutPanel rootPanel = RootLayoutPanel.get();
			// rootPanel.setTitle("Root panel");
			rootPanel.add(loginPanel);
		} else if (username != null && password != null) {
			// Login attempt via SSO
			// log.info("AppVet GWT got: name=" + username);
			// Authenticate SSO username and password
			authenticateSSO(username, password);
		}
	}

	public void authenticateSSO(final String username, final String password) {
		appVetService.authenticate(username, password, true,
				new AsyncCallback<ConfigInfoGwt>() {

					@Override
					public void onFailure(Throwable caught) {
						showMessageDialog("AppVet Error",
								"Authentication system error", true);
						return;
					}

					@Override
					public void onSuccess(final ConfigInfoGwt result) {

						if (result == null) {
							showMessageDialog("AppVet Login Error",
									"Unknown username or password", true);
							return;
						} else {
							// checkConfigInfo(result);
							startAppVet(result);
						}

					}

				});

	}

	public void startAppVet(final ConfigInfoGwt configInfo) {
		final String userName = configInfo.getUserInfo().getUserName();

		if ((userName == null) || userName.isEmpty()) {
			log.warning("Error retrieving apps list: "
					+ "username is null or empty");
			return;
		}

		appVetService.getAllApps(userName, new AsyncCallback<AppsListGwt>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				showMessageDialog("AppVet Error", "Apps list retrieval error: "
						+ caught.getMessage(), true);
				return;
			}

			@Override
			public void onSuccess(AppsListGwt appsList) {

				if (appsList == null) {
					showMessageDialog("AppVet Error", "No apps are available",
							true);
					return;
				} else {
					final AppVetPanel appVetPanel = new AppVetPanel(Unit.PX,
							configInfo, appsList);
					// appVetPanel.setTitle("AppVet Panel");
					final RootLayoutPanel rootLayoutPanel = RootLayoutPanel
							.get();
					// rootLayoutPanel.setTitle("Root panel");
					rootLayoutPanel.clear();
					rootLayoutPanel.add(appVetPanel);
				}

			}

		});

	}

	private void showMessageDialog(String windowTitle, String message,
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
}