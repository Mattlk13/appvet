package gov.nist.appvet.gwt.client.gui.dialog;

import gov.nist.appvet.gwt.client.gui.table.appslist.GroupsListPagingDataGrid;
import gov.nist.appvet.shared.all.Group;
import gov.nist.appvet.shared.all.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;

public class GroupListDialogBox extends DialogBox {
	private static Logger log = Logger.getLogger("GroupListDialogBox");

	public PushButton cancelButton = null;
	public PushButton okButton = null;
	public PushButton addButton = null;
	public PushButton deleteButton = null;
	public PushButton editButton = null;
	public GroupAcctDialogBox groupAcctDialogBox = null;
	public GroupsListPagingDataGrid<Group> groupsListTable = null;
	public SingleSelectionModel<Group> groupsSelectionModel = null;
	public Group selectedGroup = null;
	//public ListBox groupsListBox = null;
//	public int selectedIndex = -1;
//	public String selectedGroupStr = null;
	public ArrayList<Group> allGroups = null;
	
	public GroupListDialogBox(final UserInfo userInfo, final ArrayList<Group> groups) {
		log.info("trace aa");
		this.allGroups = groups;

		DockPanel dockPanel = new DockPanel();
		setWidget(dockPanel);
		dockPanel.setSize("376px", "307px");
//		// Set groups in list box
//		if (groups != null && groups.size() > 0) {
//			for (int i = 0; i < groups.size(); i++) {
//				Group group = groups.get(i);
//				String groupStr = group.toString();
//				groupsListBox.addItem(groupStr);
//			}
//		}
		
		groupsSelectionModel = new SingleSelectionModel<Group>();
		groupsSelectionModel
				.addSelectionChangeHandler(new GroupListHandler(this));
		
		groupsListTable = new GroupsListPagingDataGrid<Group>();
		groupsListTable.setStyleName("usersDockPanel");
		groupsListTable.dataGrid.setFocus(false);
		groupsListTable.setPageSize(10);
		groupsListTable.dataGrid.setSize("374px", "342px");
		groupsListTable.dataGrid
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		groupsListTable.dataGrid.setSelectionModel(groupsSelectionModel);
		
		//dockLayoutPanel.add(usersListTable);
		dockPanel.add(groupsListTable, DockPanel.CENTER);
		groupsListTable.setSize("", "200px");
		
//		// FOr testing
//		groupsListBox.addItem("My Org,MyDept,My Groupie,MyProject[ANALYST]");
//		groupsListBox.addItem("My Org,MyDept,My Groupie,MyProject2");
//		groupsListBox.addItem("My Org,MyDept,My Groupie[ANALYST]");
//		groupsListBox.addItem("My Org,MyDept[ANALYST]");
//		groupsListBox.addItem("My Org[ANALYST]");
		log.info("trace bb");
		
		
		// Set groups list
		if (groups != null && groups.size() > 0) {
			groupsListTable.setDataList(allGroups);
		}
		
		VerticalPanel verticalPanel = new VerticalPanel();
		dockPanel.add(verticalPanel, DockPanel.SOUTH);
		log.info("trace cc");

		Label analystLabel = new Label("*Analyst role.");
		analystLabel.setStyleName("footNoteAnnotation");
		analystLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		verticalPanel.add(analystLabel);
		analystLabel.setHeight("26px");
		verticalPanel.setCellHorizontalAlignment(analystLabel, HasHorizontalAlignment.ALIGN_CENTER);
		log.info("trace dd");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		log.info("trace dd1");
		verticalPanel.add(horizontalPanel);
		log.info("trace dd2");
		log.info("trace dd3");
		horizontalPanel.setWidth("375px");
		log.info("trace ee");

		cancelButton = new PushButton("Cancel");
		cancelButton.setHTML("Cancel");
		horizontalPanel.add(cancelButton);
		cancelButton.setWidth("50px");
		horizontalPanel.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);
		log.info("trace ff");

		addButton = new PushButton("Add");
		addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				setGroup(userInfo, true);
			}
		});
		horizontalPanel.add(addButton);
		horizontalPanel.setCellVerticalAlignment(addButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_CENTER);
		addButton.setWidth("50px");
		
		deleteButton = new PushButton("Delete");
		deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				boolean removed = allGroups.remove(selectedGroup);
				if (removed) 
					log.info("Removed element");
				else
					log.warning("Did dnot remove element");
				groupsListTable.setDataList(allGroups);
			}
		});
		horizontalPanel.add(deleteButton);
		horizontalPanel.setCellVerticalAlignment(deleteButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(deleteButton, HasHorizontalAlignment.ALIGN_CENTER);
		deleteButton.setWidth("50px");
		
		editButton = new PushButton("Edit");
		editButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				if (groups == null)
					log.warning("groups is null");
				else
					log.info("Groups is good");
				final Group selectedGroup = groupsSelectionModel
						.getSelectedObject();
				if (selectedGroup == null)
					log.severe("selected group is null");
				else
					log.info("selected group is good");
				setGroup(userInfo, false);
			}
		});
		horizontalPanel.add(editButton);
		horizontalPanel.setCellVerticalAlignment(editButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(editButton, HasHorizontalAlignment.ALIGN_CENTER);
		editButton.setWidth("50px");
		
		okButton = new PushButton("Ok");
		okButton.setHTML("Ok");
		horizontalPanel.add(okButton);
		horizontalPanel.setCellVerticalAlignment(okButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(okButton, HasHorizontalAlignment.ALIGN_CENTER);
		okButton.setWidth("50px");
		deleteButton.setEnabled(false);
		editButton.setEnabled(false);
		okButton.setEnabled(false);
		if (selectedGroup == null) {
			editButton.setEnabled(false);
		} else {
			editButton.setEnabled(true);
		}
		log.info("trace gg");

	}
	
	public void setGroup(UserInfo userInfo, final boolean newGroup) {
		log.info("Starting group acct dialog box");
		if (newGroup) {
			groupAcctDialogBox = new GroupAcctDialogBox(null);
		} else {
			groupAcctDialogBox = new GroupAcctDialogBox(selectedGroup);
		}
		groupAcctDialogBox.setText("Add Group for " + userInfo.getFullName());
		groupAcctDialogBox.center();
		groupAcctDialogBox.cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				killDialogBox(groupAcctDialogBox);
				return;
			}
		});
		groupAcctDialogBox.okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Deselect current selection
				//groupsSelectionModel.clear();
				
				log.info("userRadioButton: " + groupAcctDialogBox.userRadioButton.getValue());
				log.info("level1Name: " + groupAcctDialogBox.level1SuggestionBox.getValue());
				log.info("level1Analyst: " + groupAcctDialogBox.level1RadioButton.getValue());
				log.info("level2Name: " + groupAcctDialogBox.level2SuggestionBox.getValue());
				log.info("level2Analyst: " + groupAcctDialogBox.level2RadioButton.getValue());
				log.info("level3Name: " + groupAcctDialogBox.level3SuggestionBox.getValue());
				log.info("level3Analyst: " + groupAcctDialogBox.level3RadioButton.getValue());
				log.info("level4Name: " + groupAcctDialogBox.level4SuggestionBox.getValue());
				log.info("level4Analyst: " + groupAcctDialogBox.level4RadioButton.getValue());

				if (newGroup) {
					// Add it to the list
					Group group = new Group();
					group.isUser = groupAcctDialogBox.userRadioButton.getValue();
					group.level1Name = groupAcctDialogBox.level1SuggestionBox.getValue();
					group.isLevel1Analyst = groupAcctDialogBox.level1RadioButton.getValue();
					group.level2Name = groupAcctDialogBox.level2SuggestionBox.getValue();
					group.isLevel2Analyst = groupAcctDialogBox.level2RadioButton.getValue();
					group.level3Name = groupAcctDialogBox.level3SuggestionBox.getValue();
					group.isLevel3Analyst = groupAcctDialogBox.level3RadioButton.getValue();
					group.level4Name = groupAcctDialogBox.level4SuggestionBox.getValue();
					group.isLevel4Analyst = groupAcctDialogBox.level4RadioButton.getValue();
					
					if (allGroups == null) {
						allGroups = new ArrayList<Group>();
					}
					allGroups.add(group);
					groupsListTable.setDataList(allGroups);
					deleteButton.setEnabled(true);
					editButton.setEnabled(true);
				} else {
					// Edit the selected group
					if (selectedGroup != null) {
						selectedGroup.isUser = groupAcctDialogBox.userRadioButton.getValue();
						selectedGroup.level1Name = groupAcctDialogBox.level1SuggestionBox.getValue();
						selectedGroup.isLevel1Analyst = groupAcctDialogBox.level1RadioButton.getValue();
						selectedGroup.level2Name = groupAcctDialogBox.level2SuggestionBox.getValue();
						selectedGroup.isLevel2Analyst = groupAcctDialogBox.level2RadioButton.getValue();
						selectedGroup.level3Name = groupAcctDialogBox.level3SuggestionBox.getValue();
						selectedGroup.isLevel3Analyst = groupAcctDialogBox.level3RadioButton.getValue();
						selectedGroup.level4Name = groupAcctDialogBox.level4SuggestionBox.getValue();
						selectedGroup.isLevel4Analyst = groupAcctDialogBox.level4RadioButton.getValue();						
					} else {
						log.severe("Selected group should not be null");
					}
				}

				okButton.setEnabled(true);

				// Kill group acct box only after we have retrieved the data
				killDialogBox(groupAcctDialogBox);
			}
		});
		
	}
	
//	public void setAllGroups(List<Group> allGroups, boolean selectLastIndex) {
//		final Group currentlySelectedGroup = groupsSelectionModel
//				.getSelectedObject();
//		int currentlySelectedIndex = 0;
//		if (currentlySelectedGroup != null) {
//			currentlySelectedIndex = getGroupsListIndex(currentlySelectedGroup,
//					allGroups);
//		}
//		groupsListTable.setDataList(allGroups);
//		if (allGroups.size() > 0) {
//			if (selectLastIndex) {
//				// Set selected to the last group in the list
//				groupsSelectionModel.setSelected(
//						allGroups.get(allGroups.size()-1), true);
//			} else {
//				groupsSelectionModel.setSelected(
//						allGroups.get(currentlySelectedIndex), true);
//			} 
//		}
//	}
	
	public int getGroupsListIndex(Group item, List<Group> groupsList) {
		if (item != null) {
			for (int i = 0; i < groupsList.size(); i++) {
				if (item.level1Name.equals(groupsList.get(i).level1Name)) {
					return i;
				}
			}
		}
		return 0;
	}
	
	public ArrayList<Group> getGroups() {
		return allGroups;
	}
	
	public static void killDialogBox(DialogBox dialogBox) {
		if (dialogBox != null) {
			dialogBox.hide();
			dialogBox = null;
		}
	}
	
	class GroupListHandler implements SelectionChangeEvent.Handler {
		GroupListDialogBox groupsDialogBox = null;

		public GroupListHandler(GroupListDialogBox groupsDialogBox) {
			this.groupsDialogBox = groupsDialogBox;
		}

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			selectedGroup = groupsSelectionModel.getSelectedObject();
		}
	}

}
