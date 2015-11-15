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
import gov.nist.appvet.gwt.shared.UserToolCredentialsGwt;
import gov.nist.appvet.shared.os.DeviceOS;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author steveq@nist.gov
 */
public class ToolAuthParamDialogBox extends DialogBox {
	private static Logger log = Logger.getLogger("ToolAuthParamDialogBox");
	private final static GWTServiceAsync appVetServiceAsync = GWT
			.create(GWTService.class);
	public String username = null;
	public PushButton okButton = null;
	public ListBox toolParametersListBox = null;
	public int selectedParameterIndex = 0;
	public Label toolParamStatusLabel = null;
	public ArrayList<UserToolCredentialsGwt> toolCredentials = null;
	final PushButton editButton = new PushButton("Edit");
	final PushButton saveButton = new PushButton("Save");
	public PushButton doneButton = null;
	public static MessageDialogBox messageDialogBox = null;
	final ListBox toolsListBox = new ListBox();

	public ToolAuthParamDialogBox(final ConfigInfoGwt configInfoGwt) {
		super(false, true);
		setSize("400px", "438px");
		setAnimationEnabled(false);
		saveButton.setEnabled(false);
		editButton.setEnabled(false);
		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		setWidget(verticalPanel);
		verticalPanel.setSize("366px", "383px");
		CaptionPanel toolListPanel = new CaptionPanel("Tool");
		verticalPanel.add(toolListPanel);
		toolListPanel.setWidth("");
		verticalPanel.setCellHorizontalAlignment(toolListPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellVerticalAlignment(toolListPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		// Display tools and user's tool credentials
		username = configInfoGwt.getUserInfo().getUserName();
		toolCredentials = configInfoGwt.getUserInfo().getToolCredentials();
		if (toolCredentials == null)
			log.severe("toolCredentials is null");
		else {
			log.log(Level.INFO,
					"toolCredentials size: " + toolCredentials.size());
		}
		for (int i = 0; i < toolCredentials.size(); i++) {
			UserToolCredentialsGwt toolCreds = toolCredentials.get(i);
			if (toolCreds.os.equals(DeviceOS.ANDROID.name())) {
				toolsListBox.addItem(DeviceOS.ANDROID.name() + ": "
						+ toolCreds.toolName);
			} else if (toolCreds.os.equals(DeviceOS.IOS.name())) {
				toolsListBox.addItem(DeviceOS.IOS.name() + ": "
						+ toolCreds.toolName);
			}
		}
		toolsListBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				int selectedToolIndex = toolsListBox.getSelectedIndex();
				String selectedToolName = toolsListBox
						.getItemText(selectedToolIndex);
				displayToolCredentials(selectedToolName);
			}
		});
		toolListPanel.setContentWidget(toolsListBox);
		toolsListBox.setSize("338px", "100px");
		toolsListBox.setVisibleItemCount(5);
		toolParamStatusLabel = new Label("");
		toolParamStatusLabel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		verticalPanel.add(toolParamStatusLabel);
		CaptionPanel cptnpnlNewPanel = new CaptionPanel(
				"Authentication Parameters");
		verticalPanel.add(cptnpnlNewPanel);
		verticalPanel.setCellVerticalAlignment(cptnpnlNewPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		cptnpnlNewPanel.setSize("", "");
		final VerticalPanel dockLayoutPanel = new VerticalPanel();
		dockLayoutPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		cptnpnlNewPanel.setContentWidget(dockLayoutPanel);
		dockLayoutPanel.setStyleName("usersDockPanel");
		dockLayoutPanel.setSize("", "50px");
		toolParametersListBox = new ListBox();
		toolParametersListBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				selectedParameterIndex = toolParametersListBox
						.getSelectedIndex();
				if (selectedParameterIndex > -1)
					editButton.setEnabled(true);
			}
		});
		dockLayoutPanel.add(toolParametersListBox);
		dockLayoutPanel.setCellVerticalAlignment(toolParametersListBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		toolParametersListBox.setSize("338px", "100px");
		toolParametersListBox.setVisibleItemCount(5);
		final HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		dockLayoutPanel.add(horizontalPanel_2);
		dockLayoutPanel.setCellVerticalAlignment(horizontalPanel_2,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setWidth("338px");
		verticalPanel.setCellVerticalAlignment(horizontalPanel_2,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_2,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setStyleName("buttonPanel");
		editButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				// Get parameter name and value
				int selectedParameterIndex = toolParametersListBox
						.getSelectedIndex();
				log.log(Level.INFO, "selected param index to edit: "
						+ selectedParameterIndex);
				String selectedParameterString = toolParametersListBox
						.getItemText(selectedParameterIndex);
				log.log(Level.INFO, "selectedParameterString to edit: "
						+ selectedParameterString);
				String selectedParameterNoWhitespace = selectedParameterString
						.replaceAll("\\s+", "");
				String[] selectedParameter = selectedParameterNoWhitespace
						.split("=");
				String selectedParameterName = selectedParameter[0];
				log.log(Level.INFO, "selectedParameterName to edit: "
						+ selectedParameterName);
				String selectedParameterValue = selectedParameter[1];
				log.log(Level.INFO, "selectedParameterValue to edit: "
						+ selectedParameterValue);
				editParameterValue(selectedParameterName,
						selectedParameterValue);
			}
		});
		horizontalPanel_2.add(editButton);
		horizontalPanel_2.setCellHorizontalAlignment(editButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(editButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		editButton.setSize("70px", "18px");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				// Save credentials for all tools in database
				int selectedToolIndex = toolsListBox.getSelectedIndex();
				String selectedToolName = toolsListBox
						.getItemText(selectedToolIndex);
				UserToolCredentialsGwt selectedTool = getToolCredentials(selectedToolName);
				for (int i = 0; i < selectedTool.authParamNames.length; i++) {
					String parameterNameAndValue = toolParametersListBox
							.getItemText(i);
					String[] parameterNameValueArray = parameterNameAndValue
							.split("=");
					String paramValue = parameterNameValueArray[1];
					selectedTool.authParamValues[i] = paramValue;
				}
				appVetServiceAsync.updateUserToolCredentials(username,
						toolCredentials, new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								showMessageDialog(
										"Update Error",
										"Tool credentials not updated successfully",
										true);
								return;
							}

							@Override
							public void onSuccess(final Boolean result) {
								if (result == null
										|| result.booleanValue() == false) {
									showMessageDialog(
											"Update Error",
											"Tool credentials not updated successfully",
											true);
									return;
								} else {
									showMessageDialog(
											"Update",
											"Tool credentials updated successfully",
											false);
									saveButton.setEnabled(false);
								}
							}
						});
			}
		});
		horizontalPanel_2.add(saveButton);
		horizontalPanel_2.setCellVerticalAlignment(saveButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setCellHorizontalAlignment(saveButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		saveButton.setWidth("70px");
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setWidth("338px");
		okButton = new PushButton("Ok");
		horizontalPanel.add(okButton);
		okButton.setWidth("70px");
		horizontalPanel.setCellVerticalAlignment(okButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(okButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		String selectedToolName = toolsListBox.getItemText(0);
		displayToolCredentials(selectedToolName);
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

	public void displayAuthenticationRequirements(UserToolCredentialsGwt tool) {
		if (tool == null) {
			log.log(Level.SEVERE, "toolInfo is null");
		}
		if (tool.authRequired) {
			toolParamStatusLabel.setText("'" + tool.toolName
					+ "' requires authentication.");
		} else {
			toolParamStatusLabel.setText("'" + tool.toolName
					+ "' does not require authentication.");
			editButton.setEnabled(false);
		}
	}

	/**
	 * 
	 * @param selectedToolListName
	 *            The name of the selected tool as displayed in the toolListBox
	 *            (i.e., with <ANDROID/IOS> followed by ':' <toolName>)
	 * @return
	 */
	public void displayToolCredentials(String selectedToolListName) {
		UserToolCredentialsGwt selectedTool = getToolCredentials(selectedToolListName);
		if (selectedTool != null) {
			// Display credentials list
			displayAuthenticationRequirements(selectedTool);
			// Clear parameters list
			toolParametersListBox.clear();
			if (selectedTool.authRequired) {
				// Display credentials for selected tool
				String[] names = selectedTool.authParamNames;
				String[] values = selectedTool.authParamValues;
				for (int i = 0; i < names.length; i++) {
					toolParametersListBox.addItem(names[i] + "=" + values[i]);
				}
			}
		}
	}

	public UserToolCredentialsGwt getToolCredentials(String selectedToolListName) {
		// Parse out the tool name
		String[] selectedToolListNameArray = selectedToolListName.split(": ");
		if (selectedToolListNameArray.length == 2) {
			String os = selectedToolListNameArray[0];
			String selectedToolName = selectedToolListNameArray[1];
			for (int i = 0; i < toolCredentials.size(); i++) {
				UserToolCredentialsGwt toolCreds = toolCredentials.get(i);
				if (toolCreds.toolName.equals(selectedToolName)
						&& toolCreds.os.equals(os)) {
					return toolCreds;
				}
			}
		}
		return null;
	}

	private void editParameterValue(final String selectedParameterName,
			final String selectedParameterValue) {
		final NameValueDialogBox nameValueDialogBox = new NameValueDialogBox();
		nameValueDialogBox.setParameter(selectedParameterName,
				selectedParameterValue);
		nameValueDialogBox.setText("Edit Parameter Name and Value");
		nameValueDialogBox.center();
		nameValueDialogBox.show();
		nameValueDialogBox.okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				log.log(Level.INFO, "Clicked OK");
				nameValueDialogBox.hide();
				String paramName = nameValueDialogBox.getParameterName();
				String paramValue = nameValueDialogBox.getParameterValue();
				if (paramValue == null || paramValue.isEmpty()
						|| paramValue.equals("null")) {
					paramValue = "null";
				}
				// Display the changed parameter value
				toolParametersListBox.setItemText(selectedParameterIndex,
						paramName + "=" + paramValue);
				if (nameValueDialogBox.valueChanged()) {
					log.log(Level.INFO, "Value changed");
					saveButton.setEnabled(true);
				} else {
					log.log(Level.INFO, "Value didnt' change");
				}
			}
		});
	}
}
