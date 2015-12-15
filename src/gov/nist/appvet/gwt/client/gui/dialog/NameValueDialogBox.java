package gov.nist.appvet.gwt.client.gui.dialog;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author steveq@nist.gov
 */
public class NameValueDialogBox extends DialogBox {
	public PushButton okButton = null;
	private TextBox nameTextBox = null;
	private TextBox valueTextBox = null;
	private String previousValue = null;
	private static Logger log = Logger.getLogger("NameValueDialogBox");

	public NameValueDialogBox() {
		setWidth("");
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		setWidget(dockLayoutPanel);
		dockLayoutPanel.setSize("372px", "125px");
		SimplePanel simplePanel = new SimplePanel();
		dockLayoutPanel.addSouth(simplePanel, 2.5);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		simplePanel.setWidget(horizontalPanel);
		horizontalPanel.setSize("100%", "100%");
		okButton = new PushButton("Ok");
		okButton.setHTML("Ok");
		horizontalPanel.add(okButton);
		okButton.setWidth("70px");
		horizontalPanel.setCellHorizontalAlignment(okButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		Grid grid = new Grid(2, 2);
		dockLayoutPanel.add(grid);
		grid.setWidth("372px\r\n");
		Label lblNewLabel_1 = new Label("Name: ");
		grid.setWidget(0, 0, lblNewLabel_1);
		lblNewLabel_1.setSize("70px", "100%");
		nameTextBox = new TextBox();
		grid.setWidget(0, 1, nameTextBox);
		nameTextBox.setWidth("250px");
		nameTextBox.setReadOnly(true);
		Label lblNewLabel = new Label("Value:");
		grid.setWidget(1, 0, lblNewLabel);
		lblNewLabel.setSize("70px", "100%");
		valueTextBox = new TextBox();
		grid.setWidget(1, 1, valueTextBox);
		valueTextBox.setWidth("250px");
		grid.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setVerticalAlignment(1, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setHorizontalAlignment(1, 0,
				HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_LEFT);
		grid.getCellFormatter().setHorizontalAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_RIGHT);
		grid.getCellFormatter().setHorizontalAlignment(1, 1,
				HasHorizontalAlignment.ALIGN_RIGHT);
	}

	public void setParameter(String name, String value) {
		this.nameTextBox.setText(name);
		this.valueTextBox.setText(value);
		previousValue = value;
	}

	public String getPreviousValue() {
		return previousValue;
	}

	public String getParameterName() {
		return this.nameTextBox.getText();
	}

	public String getParameterValue() {
		return this.valueTextBox.getText();
	}

	public boolean valueChanged() {
		log.log(Level.INFO, "value: " + valueTextBox.getText() + ", previous: "
				+ previousValue);
		if (this.valueTextBox.getText().equals(previousValue)) {
			return false;
		} else {
			return true;
		}
	}
}