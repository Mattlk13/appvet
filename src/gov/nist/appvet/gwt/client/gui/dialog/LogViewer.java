package gov.nist.appvet.gwt.client.gui.dialog;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;

public class LogViewer extends DialogBox {
	public PushButton closeButton = null;
	
	public LogViewer(String log) {
		DockPanel dockPanel = new DockPanel();
		dockPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		setWidget(dockPanel);
		dockPanel.setSize("100%", "100%");
		
		TextArea textArea = new TextArea();
		textArea.setText(log);
		dockPanel.add(textArea, DockPanel.CENTER);
		textArea.setSize("650px", "450px");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dockPanel.add(horizontalPanel, DockPanel.SOUTH);
		horizontalPanel.setHeight("40px");
		dockPanel.setCellWidth(horizontalPanel, "100%");
		dockPanel.setHeight("28px");
		
		closeButton = new PushButton("Close");
		closeButton.setStyleName("greenButton shadow");
		closeButton.setHTML("Close");
		horizontalPanel.add(closeButton);
		closeButton.setSize("70px", "18px");
	}

}
