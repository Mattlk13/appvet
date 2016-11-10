package gov.nist.appvet.gwt.client.gui.dialog;

import gov.nist.appvet.gwt.shared.ToolInfoGwt;

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

public class ToolAdapterEnableDialogBox extends DialogBox {

	public PushButton cancelButton = null;
	public PushButton editButton = null;
	public PushButton okButton = null;
	public ToolAdapterEnabledEditDialogBox toolAdapterEnabledDialog = null;
	private Logger log = Logger.getLogger("ToolAdapterEnabledEditDialogBox");
	public ListBox listBox = null;
	private MessageDialogBox messageDialogBox = null;

	public ToolAdapterEnableDialogBox(final List<ToolInfoGwt> testTools) {
		super(false, true);

		DockPanel dockPanel = new DockPanel();
		setWidget(dockPanel);
		dockPanel.setSize("374px", "182px");

		Label lblNewLabel = new Label("Select tool to enable/disable:");
		lblNewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dockPanel.add(lblNewLabel, DockPanel.NORTH);
		lblNewLabel.setHeight("23px");
		listBox = new ListBox();
		listBox.setStyleName("h1");
		listBox.setFocus(true);

		dockPanel.add(listBox, DockPanel.CENTER);
		dockPanel.setCellHorizontalAlignment(listBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		listBox.setSize("361px", "102px");
		listBox.setVisibleItemCount(7);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		dockPanel.add(horizontalPanel, DockPanel.SOUTH);
		horizontalPanel.setSize("373px", "28px");

		cancelButton = new PushButton("Cancel");
		cancelButton.setStyleName("grayButton shadow");
		horizontalPanel.add(cancelButton);
		cancelButton.setSize("70px", "18px");
		horizontalPanel.setCellVerticalAlignment(cancelButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(cancelButton,
				HasHorizontalAlignment.ALIGN_CENTER);

		editButton = new PushButton("Edit");
		editButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				final int selectedIndex = listBox.getSelectedIndex();
				final ToolInfoGwt selectedTestTool = testTools.get(selectedIndex);
				String selectedToolString = listBox.getItemText(selectedIndex);
				String[] selectedToolStringArray = selectedToolString.split(": ");
				String selectedToolEnabledString = selectedToolStringArray[1];
				toolAdapterEnabledDialog = new ToolAdapterEnabledEditDialogBox(
						selectedTestTool, selectedToolEnabledString);
				toolAdapterEnabledDialog.setText("Tool Enable/Disable");
				toolAdapterEnabledDialog.center();

				toolAdapterEnabledDialog.cancelButton
				.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						killDialogBox(toolAdapterEnabledDialog);
					}
				});

				toolAdapterEnabledDialog.okButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						// Get value
						boolean selectedEnabled = 
								toolAdapterEnabledDialog.enableRadioButton.getValue();
						if (selectedEnabled) {
							listBox.setItemText(selectedIndex, 
									selectedTestTool.getId() + " (" + 
											selectedTestTool.getOs() + "): enabled");
						} else {
							listBox.setItemText(selectedIndex, 
									selectedTestTool.getId() + " (" + 
											selectedTestTool.getOs() + "): disabled");
						}

						killDialogBox(toolAdapterEnabledDialog);
						
						// Enable this OK button
						okButton.setEnabled(true);
					}
				});
			}
		});
		editButton.setStyleName("grayButton shadow");
		horizontalPanel.add(editButton);
		editButton.setSize("70px", "18px");
		horizontalPanel.setCellVerticalAlignment(editButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(editButton,
				HasHorizontalAlignment.ALIGN_CENTER);

		okButton = new PushButton("Ok");
		okButton.setEnabled(false);
		okButton.setStyleName("greenButton shadow");
		okButton.setHTML("Ok");
		horizontalPanel.add(okButton);
		horizontalPanel.setCellVerticalAlignment(okButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(okButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		okButton.setSize("70px", "18px");

		try {
			for (int i = 0; i < testTools.size(); i++) {
				ToolInfoGwt testTool = testTools.get(i);
				if (testTool.isDisabled()) {
					listBox.addItem(testTool.getId() + " (" + 
							testTool.getOs() + "): disabled");
				} else {
					listBox.addItem(testTool.getId() + " (" + 
							testTool.getOs() + "): enabled");
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
