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
package gov.nist.appvet.gwt.client.gui.table;

import gov.nist.appvet.gwt.shared.AppInfoGwt;
import gov.nist.appvet.shared.all.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Abstract PaggingDataGrid class to set initial GWT DataGrid and Simple Pager
 * with ListDataProvider
 * 
 * @author Ravi Soni
 * 
 */
public abstract class PagingDataGrid<T> extends Composite {

	private Logger log = Logger.getLogger("PagingDataGrid");

	public DataGrid<T> dataGrid;
	public final SimplePager pager;
	private String height;
	public ListDataProvider<T> dataProvider;
	private final DockPanel dock = new DockPanel();
	public int iconVersion = 0;

	public PagingDataGrid() {
		initWidget(dock);
		dataGrid = new DataGrid<T>();
		dataGrid.setPageSize(3);
		dataGrid.setWidth("100%");
		final SimplePager.Resources pagerResources = GWT
				.create(SimplePager.Resources.class);


		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0,
				true);
		setPagerImageAltAndTitle();

		pager.setDisplay(dataGrid);
		dataProvider = new ListDataProvider<T>();
		dataProvider.setList(new ArrayList<T>());
		dataGrid.setEmptyTableWidget(new HTML("No Data to Display"));
		final ListHandler<T> sortHandler = new ListHandler<T>(
				dataProvider.getList());
		initTableColumns(dataGrid, sortHandler);
		dataGrid.addColumnSortHandler(sortHandler);
		dataProvider.addDataDisplay(dataGrid);
		pager.setVisible(true);
		dataGrid.setVisible(true);
		dock.add(dataGrid, DockPanel.CENTER);
		dock.add(pager, DockPanel.SOUTH);
		dock.setWidth("100%");
		dock.setCellWidth(dataGrid, "100%");
		dock.setCellWidth(pager, "100%");
	}

	public void setPagerImageAltAndTitle() {
		final NodeList<Element> tdElems = pager.getElement().getElementsByTagName("img");

		for (int i = 0; i < tdElems.getLength(); i++) {
			final String altText;
			if (i == 0)
				altText = "First page";
			else if (i == 1)
				altText = "Previous page";
			else if (i == 2)
				altText = "Next page";
			else if (i == 3)
				altText = "Last page";
			else
				continue;

			Element e = tdElems.getItem(i);

			// Set ALT attribute
			e.setAttribute("ALT", altText);
			
			// Set title
			e.setTitle(altText);			
		}
	}

	public void add(int index, T element) {
		final List<T> list = dataProvider.getList();
		list.add(index, element);
	}

	public List<T> deleteUser(String username) {
		final List<T> list = dataProvider.getList();
		for (int i = 0; i < list.size(); i++) {
			final UserInfo userInfoGwt = (UserInfo) list.get(i);
			if (userInfoGwt.getUserName().equals(username)) {
				list.remove(i);
				break;
			}
		}
		return list;
	}

	public ListDataProvider<T> getDataProvider() {
		return dataProvider;
	}

	public String getHeight() {
		return height;
	}

	public abstract void initTableColumns(DataGrid<T> dataGrid,
			ListHandler<T> sortHandler);

	public void remove(int index) {
		final List<T> list = dataProvider.getList();
		list.remove(index);
	}

	public void remove(String appid) {
		final List<T> list = dataProvider.getList();
		for (int i = 0; i < list.size(); i++) {
			final AppInfoGwt appInfoGwt = (AppInfoGwt) list.get(i);
			if (appInfoGwt.appId.equals(appid)) {
				list.remove(i);
				break;
			}
		}
	}

	public void set(int index, T element) {
		final List<T> list = dataProvider.getList();
		list.set(index, element);
	}

	public void clearDataList() {
		final List<T> list = dataProvider.getList();
		list.clear();
		dataProvider.refresh();
	}

	public void setDataList(List<T> dataList) {
		// Note that list can switch between all apps and searched apps
		final List<T> list = dataProvider.getList();
		list.clear();
		list.addAll(dataList);
		dataProvider.refresh();
	}

	public void setDataProvider(ListDataProvider<T> dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void setEmptyTableWidget() {
		dataGrid.setEmptyTableWidget(new HTML(
				"The current request has taken longer than the allowed time limit. Please try your report query again."));
	}

	@Override
	public void setHeight(String height) {
		this.height = height;
		dataGrid.setHeight(height);
	}
}
