package gov.nist.appvet.gwt.client.gui.dialog;

import java.util.logging.Logger;

import gov.nist.appvet.shared.all.OrgUnit;
import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserRoleInfo;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class OrgUnitDialogGox extends DialogBox {
	private static Logger log = Logger.getLogger("GroupAcctDialogBox");
	public RadioButton userRadioButton = null;
	public RadioButton level1RadioButton = null;
	public RadioButton level2RadioButton = null;
	public RadioButton level3RadioButton = null;
	public RadioButton level4RadioButton = null;
	public SuggestBox level1SuggestionBox = null;
	public SuggestBox level2SuggestionBox = null;
	public SuggestBox level3SuggestionBox = null;
	public SuggestBox level4SuggestionBox = null;
	public PushButton cancelButton = null;
	public PushButton okButton = null;

	public OrgUnitDialogGox(OrgUnit orgUnit) {
		DockPanel dockPanel = new DockPanel();
		setWidget(dockPanel);
		dockPanel.setSize("394px", "219px");
		
		Grid grid = new Grid(5, 3);
		dockPanel.add(grid, DockPanel.CENTER);
		grid.setWidth("386px");
		
		userRadioButton = new RadioButton("radiogroup", "User (Non-Analyst)");
		userRadioButton.setHTML("User (Non-Analyst)");
		userRadioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> arg0) {
				okButton.setEnabled(true);
				if (userRadioButton.getValue()) {
					level1SuggestionBox.setEnabled(true);
					level2SuggestionBox.setEnabled(true);
					level3SuggestionBox.setEnabled(true);
					level4SuggestionBox.setEnabled(true);
				}
			}
		});
		grid.setWidget(0, 0, userRadioButton);
		
		Label lblAnalyst = new Label("Analyst");
		grid.setWidget(0, 2, lblAnalyst);
		
		Label level1Label = new Label("Organization: ");
		level1Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(1, 0, level1Label);
		level1Label.setWidth("131px");
		
		level1SuggestionBox = new SuggestBox();
		level1SuggestionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> arg0) {
				okButton.setEnabled(true);
			}
		});
		grid.setWidget(1, 1, level1SuggestionBox);
		level1SuggestionBox.setWidth("180px");
		
		level1RadioButton = new RadioButton("radiogroup", "");
		level1RadioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> arg0) {
				okButton.setEnabled(true);
				if (level1RadioButton.getValue()) {
					level1SuggestionBox.setEnabled(true);
					level2SuggestionBox.setText("");
					level2SuggestionBox.setEnabled(false);
					level3SuggestionBox.setText("");
					level3SuggestionBox.setEnabled(false);
					level4SuggestionBox.setText("");
					level4SuggestionBox.setEnabled(false);
				}
			}
		});
		grid.setWidget(1, 2, level1RadioButton);
		level1RadioButton.setWidth("50px");
		
		Label level2Label = new Label("Level2");
		level2Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(2, 0, level2Label);
		
		level2SuggestionBox = new SuggestBox();
		level2SuggestionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> arg0) {
				okButton.setEnabled(true);
			}
		});
		grid.setWidget(2, 1, level2SuggestionBox);
		level2SuggestionBox.setWidth("180px");
		
		level2RadioButton = new RadioButton("radiogroup", "");
		level2RadioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> arg0) {
				okButton.setEnabled(true);
				if (level2RadioButton.getValue()) {
					level1SuggestionBox.setEnabled(true);
					level2SuggestionBox.setEnabled(true);
					level3SuggestionBox.setText("");
					level3SuggestionBox.setEnabled(false);
					level4SuggestionBox.setText("");
					level4SuggestionBox.setEnabled(false);
				}
			}
		});
		level2RadioButton.setHTML("");
		grid.setWidget(2, 2, level2RadioButton);
		
		Label level3Label = new Label("Level3");
		level3Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(3, 0, level3Label);
		log.info("trace bbb");

		level3SuggestionBox = new SuggestBox();
		level3SuggestionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> arg0) {
				okButton.setEnabled(true);
			}
		});
		grid.setWidget(3, 1, level3SuggestionBox);
		level3SuggestionBox.setWidth("180px");
		grid.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(3, 1, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_CENTER);
		log.info("trace ccc");

		level3RadioButton = new RadioButton("radiogroup", "");
		level3RadioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> arg0) {
				okButton.setEnabled(true);
				if (level3RadioButton.getValue()) {
					level1SuggestionBox.setEnabled(true);
					level2SuggestionBox.setEnabled(true);
					level3SuggestionBox.setEnabled(true);
					level4SuggestionBox.setText("");
					level4SuggestionBox.setEnabled(false);
				}
			}
		});
		level3RadioButton.setHTML("");
		grid.setWidget(3, 2, level3RadioButton);
		
		Label level4Label = new Label("Level4");
		level4Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		grid.setWidget(4, 0, level4Label);
		
		level4SuggestionBox = new SuggestBox();
		level4SuggestionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> arg0) {
				okButton.setEnabled(true);
			}
		});
		grid.setWidget(4, 1, level4SuggestionBox);
		level4SuggestionBox.setWidth("180px");
		log.info("trace ddd");

		level4RadioButton = new RadioButton("radiogroup", "");
		level4RadioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> arg0) {
				okButton.setEnabled(true);
				if (level4RadioButton.getValue()) {
					level1SuggestionBox.setEnabled(true);
					level2SuggestionBox.setEnabled(true);
					level3SuggestionBox.setEnabled(true);
					level4SuggestionBox.setEnabled(true);
				}
			}
		});
		grid.setWidget(4, 2, level4RadioButton);
		grid.getCellFormatter().setHorizontalAlignment(1, 2, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(2, 2, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(3, 2, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(4, 2, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel.add(buttonPanel, DockPanel.SOUTH);
		dockPanel.setCellVerticalAlignment(buttonPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.setWidth("375px");
		
		cancelButton = new PushButton("Cancel");
		cancelButton.setHTML("Cancel");
		buttonPanel.add(cancelButton);
		buttonPanel.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);
		cancelButton.setWidth("70px");
		
		okButton = new PushButton("Ok");
		okButton.setHTML("Ok");
		okButton.setEnabled(false);
		buttonPanel.add(okButton);
		buttonPanel.setCellHorizontalAlignment(okButton, HasHorizontalAlignment.ALIGN_CENTER);
		okButton.setWidth("70px");
		
		log.info("trace fff");

		// Set group data if it exists 
		if (orgUnit == null) {
			userRadioButton.setValue(true);
		} else {
			// User
			if (orgUnit.orgUnitRole == Role.USER) {
				userRadioButton.setValue(true);
				
				
			} else if (orgUnit.orgUnitRole == Role.ANALYST) {
				int analystLevel = orgUnit.getHierarchy().size();
				if (analystLevel == 1) {
					level1RadioButton.setValue(true);
				} else if (analystLevel == 2) {
					level2RadioButton.setValue(true);
				} else if (analystLevel == 3) {
					level3RadioButton.setValue(true);
				} else if (analystLevel == 4) {
					level4RadioButton.setValue(true);
				}
				for (int i = 0; i < orgUnit.getHierarchy().size(); i++) {
					String levelName = orgUnit.getHierarchy().get(i);
					if (i == 0) {
						level1SuggestionBox.setText(levelName);
					} else if (i == 1) {
						level2SuggestionBox.setText(levelName);
					} else if (i == 2) {
						level3SuggestionBox.setText(levelName);
					} else if (i == 3) {
						level4SuggestionBox.setText(levelName);
					}
				}
			}
			
		}
		log.info("trace ggg");

	}

}
