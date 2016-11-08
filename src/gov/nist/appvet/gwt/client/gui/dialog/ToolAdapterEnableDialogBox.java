package gov.nist.appvet.gwt.client.gui.dialog;

import gov.nist.appvet.gwt.shared.ToolInfoGwt;
import gov.nist.appvet.shared.all.ToolType;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class ToolAdapterEnableDialogBox extends DialogBox {
	
	public PushButton cancelButton = null;
	public PushButton editButton = null;
	public PushButton okButton = null;
	public ToolAdapterEnabledEditDialogBox toolAdapterEnabledDialog = null;
	private Logger log = Logger.getLogger("ToolAdapterEnabledEditDialogBox");
	public ListBox listBox = null;
	private MessageDialogBox messageDialogBox = null;
//	private int minOrgLevelsRequired = -1;
//	private int maxOrgLevels = -1;
//	public String orgMembership;

	public ToolAdapterEnableDialogBox(ArrayList<ToolInfoGwt> tools) {
		super(false, true);
//		this.orgMembership = orgMembership;
//		this.minOrgLevelsRequired = configInfo.getMinOrgLevelsRequired();
//		this.maxOrgLevels = configInfo.getMaxOrgLevels();
		
		DockPanel dockPanel = new DockPanel();
		setWidget(dockPanel);
		dockPanel.setSize("374px", "182px");
		
		Label lblNewLabel = new Label("Select tool to enable/disable:");
		lblNewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dockPanel.add(lblNewLabel, DockPanel.NORTH);
//		String[] userMembershipLevels;
		listBox = new ListBox();
		listBox.setFocus(true);

	    
		dockPanel.add(listBox, DockPanel.CENTER);
		dockPanel.setCellVerticalAlignment(listBox, HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel.setCellHorizontalAlignment(listBox, HasHorizontalAlignment.ALIGN_CENTER);
		listBox.setSize("372px", "118px");
		listBox.setVisibleItemCount(7);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		dockPanel.add(horizontalPanel, DockPanel.SOUTH);
		horizontalPanel.setWidth("373px");
		
		cancelButton = new PushButton("Cancel");
		cancelButton.setStyleName("grayButton shadow");
		horizontalPanel.add(cancelButton);
		cancelButton.setSize("70px", "18px");
		horizontalPanel.setCellVerticalAlignment(cancelButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);
		
		editButton = new PushButton("Edit");
		editButton.setEnabled(false);
		editButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				final int selectedIndex = listBox.getSelectedIndex();

				String selectedToolInfoString = listBox.getItemText(selectedIndex);
				String[] selectedToolWithOSArray = selectedToolInfoString.split(") enabled: ");
				String selectedToolWithOS = selectedToolWithOSArray[0];
				String[] selectedToolArray = selectedToolWithOS.split(" (");
				String selectedToolIdValue = selectedToolArray[0];
				String selectedToolOSValue = selectedToolArray[1];
				log.info("GOT tool: " + selectedToolIdValue);
				log.info("GOT OS: " + selectedToolOSValue);
				String selectedToolEnabledValue = selectedToolWithOSArray[1];
				boolean selectedToolEnabled = new Boolean(selectedToolEnabledValue).booleanValue();
				log.info("GOT enabled: " + selectedToolEnabled);
				
				toolAdapterEnabledDialog = 
						new ToolAdapterEnabledEditDialogBox(selectedToolIdValue, selectedToolOSValue, selectedToolEnabled);

				toolAdapterEnabledDialog.setText("Organization Level");

				toolAdapterEnabledDialog.center();

				toolAdapterEnabledDialog.cancelButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(toolAdapterEnabledDialog);
					}
				});
				
				toolAdapterEnabledDialog.okButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						// Get value
//						if (toolAdapterEnabledDialog.suggestBox.getText() == null || 
//								toolAdapterEnabledDialog.suggestBox.getText().isEmpty()) {
//							// Can't be null or empty
//							showMessageDialog("AppVet Error", "Level name cannot be null or empty",
//									true);
//						} else {
//							// Set in list 
//							int index = selectedIndex + 1; // index starts at 1, not 0
//							String displayStr = "Level ";
//							if (index >= 0 && index <= minOrgLevelsRequired) {
//								displayStr += index + " (Required): ";
//							} else {
//								displayStr += index + ": ";
//							}
//							displayStr += toolAdapterEnabledDialog.suggestBox.getText();
//							listBox.setItemText(selectedIndex, displayStr);
//							killDialogBox(toolAdapterEnabledDialog);
//						}
						
						
					}
				});
				
			}
		});
		editButton.setStyleName("grayButton shadow");
		horizontalPanel.add(editButton);
		editButton.setSize("70px", "18px");
		horizontalPanel.setCellVerticalAlignment(editButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(editButton, HasHorizontalAlignment.ALIGN_CENTER);
		
		okButton = new PushButton("Ok");
		okButton.setStyleName("greenButton shadow");
		okButton.setHTML("Ok");
		horizontalPanel.add(okButton);
		horizontalPanel.setCellVerticalAlignment(okButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(okButton, HasHorizontalAlignment.ALIGN_CENTER);
		okButton.setSize("70px", "18px");	
		
		
		try {
			for (int i = 0; i < tools.size(); i++) {
				ToolInfoGwt tool = tools.get(i);
				if (tool.getType() == ToolType.TESTTOOL) {
					listBox.addItem(tool.getId() + " (" + tool.getOs() + ") enabled: " + tool.isEnabled());
				}
			}


		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		
	}
	
	public void killDialogBox(DialogBox dialogBox) {
		if (dialogBox != null) {
			dialogBox.hide();
			dialogBox = null;
		}
	}
	
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
			}
		});
	}
}
