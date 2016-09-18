package gov.nist.appvet.gwt.client.gui.dialog;

import gov.nist.appvet.gwt.shared.ConfigInfoGwt;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserInfo;

import java.util.List;
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

public class OrgLevelsDialogBox extends DialogBox {
	
	public PushButton cancelButton = null;
	public PushButton editButton = null;
	public PushButton okButton = null;
	public OrgLevelNameEditDialogBox levelNameDialogBox = null;
	private Logger log = Logger.getLogger("OrgLevelsDialogBox");
	public ListBox listBox = null;
	private MessageDialogBox messageDialogBox = null;
	private int minOrgLevelsRequired = -1;
	private int maxOrgLevels = -1;
	public String orgMembership;

	public OrgLevelsDialogBox(final ConfigInfoGwt configInfo, 
			final List<String> allUserOrgLevels, final String orgMembership) {
		super(false, true);
		this.orgMembership = orgMembership;
		this.minOrgLevelsRequired = configInfo.getMinOrgLevelsRequired();
		this.maxOrgLevels = configInfo.getMaxOrgLevels();
		
		DockPanel dockPanel = new DockPanel();
		setWidget(dockPanel);
		dockPanel.setSize("374px", "182px");
		
		Label lblNewLabel = new Label("Select level to edit:");
		lblNewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dockPanel.add(lblNewLabel, DockPanel.NORTH);
		String[] userMembershipLevels;
		listBox = new ListBox();
		listBox.setFocus(true);
		try {
			if (orgMembership.isEmpty()) {
				// New User
				minOrgLevelsRequired = configInfo.getMinOrgLevelsRequired();
				maxOrgLevels = configInfo.getMaxOrgLevels();
				for (int i = 1; i <= maxOrgLevels; i++) {

					String orgLevelDisplay = "Level " + i;
					if (i <= minOrgLevelsRequired) {
						orgLevelDisplay += " (Required): ";
					} else {
						orgLevelDisplay += ": ";
					}

					listBox.addItem(orgLevelDisplay);
				}
			} else {
				//userMembershipLevels = Role.getOrgMembership(selectedUser.getRoleAndOrgMembership());
				userMembershipLevels = orgMembership.split(",");
				int minOrgLevelsRequired = configInfo.getMinOrgLevelsRequired();
				int maxOrgLevels = configInfo.getMaxOrgLevels();
				for (int i = 1; i <= maxOrgLevels; i++) {

					String orgLevelDisplay = "Level " + i;
					if (i <= minOrgLevelsRequired) {
						orgLevelDisplay += " (Required): ";
					} else {
						orgLevelDisplay += ": ";
					}
					if (i <= userMembershipLevels.length) {
						orgLevelDisplay += userMembershipLevels[i-1];
					}
					listBox.addItem(orgLevelDisplay);
				}
			}

		} catch (Exception e) {
			log.severe(e.getMessage());
		}
	    
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
		editButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				log.info("tra 1");
				final int selectedIndex = listBox.getSelectedIndex();
				log.info("tra 2");

				String selectedLevelDisplay = listBox.getItemText(selectedIndex);
				String[] selectedLevelDisplayArray = selectedLevelDisplay.split(": ");
				String selectedLevelName = null;
				if (selectedLevelDisplayArray[1] != null && !selectedLevelDisplayArray[1].isEmpty()) {
					selectedLevelName = selectedLevelDisplayArray[1];
				} else {
					selectedLevelName = "";
				}
				levelNameDialogBox = new OrgLevelNameEditDialogBox(allUserOrgLevels, orgMembership, selectedIndex, selectedLevelName);
				log.info("tra 3");

				levelNameDialogBox.setText("Organization Level");
				log.info("tra 4");

				levelNameDialogBox.center();
				log.info("tra 5");

				levelNameDialogBox.cancelButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(levelNameDialogBox);
					}
				});
				
				levelNameDialogBox.okButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						// Get value
						if (levelNameDialogBox.suggestBox.getText() == null || 
								levelNameDialogBox.suggestBox.getText().isEmpty()) {
							// Can't be null or empty
							showMessageDialog("AppVet Error", "Level name cannot be null or empty",
									true);
						} else {
							// Set in list 
							int index = selectedIndex + 1; // index starts at 1, not 0
							String displayStr = "Level ";
							log.info("index: " + index + ", minOrgLevelsRequired: " + minOrgLevelsRequired);
							if (index >= 0 && index <= minOrgLevelsRequired) {
								displayStr += index + " (Required): ";
							} else {
								displayStr += index + ": ";
							}
							displayStr += levelNameDialogBox.suggestBox.getText();
							listBox.setItemText(selectedIndex, displayStr);
							killDialogBox(levelNameDialogBox);
						}
						
						
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
