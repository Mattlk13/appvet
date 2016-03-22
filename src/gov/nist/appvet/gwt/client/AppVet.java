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

import gov.nist.appvet.gwt.client.gui.AppVetPanel;
import gov.nist.appvet.gwt.client.gui.LoginPanel;
import gov.nist.appvet.gwt.client.gui.dialog.MessageDialogBox;
import gov.nist.appvet.gwt.shared.AppsListGwt;
import gov.nist.appvet.gwt.shared.ConfigInfoGwt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author steveq@nist.gov
 */
public class AppVet implements EntryPoint {

	private final GWTServiceAsync appVetService = GWT.create(GWTService.class);
	private MessageDialogBox messageDialogBox = null;

	
	@Override
	public void onModuleLoad() {
		handleServletRequest();
	}
	
	
	public void handleServletRequest() {
		
		appVetService.handleServletRequest( 
				new AsyncCallback<ConfigInfoGwt>() {

			@Override
			public void onFailure(Throwable caught) {
				showMessageDialog("AppVet Error",
						"Authentication error", true);
				return;
			}

			@Override
			public void onSuccess(final ConfigInfoGwt appvetConfig) {

				if (appvetConfig == null) {
					showMessageDialog("AppVet Login Error",
							"Unknown username or password", true);
					return;
				} else {
					if (appvetConfig.getSSOActive()) {
						if (appvetConfig.getUnauthorizedURL() != null) {
							// An authentication error occured so redirect
							Window.Location.assign(appvetConfig.getUnauthorizedURL()); 
						} else {
							// Display user's AppVet account
							displayAppVet(appvetConfig);
						}
					} else {
						// SSO is not active so display AppVet login page
						displayLogin();
					}
				}

			}

		});
		
	}
	
	
	public void displayLogin() {
		// Display main AppVet login page
		final LoginPanel loginPanel = new LoginPanel(Unit.PX);
		// loginPanel.setTitle("Login panel");
		final RootLayoutPanel rootPanel = RootLayoutPanel.get();
		// rootPanel.setTitle("Root panel");
		rootPanel.add(loginPanel);
	}
	
	
	public void displayAppVet(final ConfigInfoGwt configInfoGwt) {
		
		String username = configInfoGwt.getUserInfo().getUserName();
		appVetService.getAllApps(username, new AsyncCallback<AppsListGwt>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				showMessageDialog("AppVet Error", 
						"Error retrieving apps list", true);
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
							configInfoGwt, appsList);
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