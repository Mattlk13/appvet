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
package gov.nist.appvet.gwt.client.gui.table.appslist;

import gov.nist.appvet.gwt.client.gui.table.PagingDataGrid;
import gov.nist.appvet.shared.all.UserInfo;

import java.util.Comparator;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;

/**
 * @author steveq@nist.gov
 */
public class UsersListPagingDataGrid<T> extends PagingDataGrid<T> {
	
	// Turn off sorting of columns for 508 Compliance
	private final boolean SORTING_ON = false;
	
	@Override
	public void initTableColumns(DataGrid<T> dataGrid,
			ListHandler<T> sortHandler) {
		
		// Last Name
		final Column<T, String> lastNameColumn = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				return ((UserInfo) object).getLastName();
			}
		};
		lastNameColumn.setSortable(SORTING_ON);
		sortHandler.setComparator(lastNameColumn, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((UserInfo) o1).getLastName().compareTo(
						((UserInfo) o2).getLastName());
			}
		});
		SafeHtml lastNameHeader = 
				SafeHtmlUtils.fromTrustedString("<span title=\"Last name\">Last name</span>");
		dataGrid.addColumn(lastNameColumn, lastNameHeader);
		dataGrid.setColumnWidth(lastNameColumn, "50px");
		
		// First Name
		final Column<T, String> firstNameColumn = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				return ((UserInfo) object).getFirstName();
			}
		};
		firstNameColumn.setSortable(SORTING_ON);
		sortHandler.setComparator(firstNameColumn, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((UserInfo) o1).getFirstName().compareTo(
						((UserInfo) o2).getFirstName());
			}
		});
		SafeHtml firstNameHeader = 
				SafeHtmlUtils.fromTrustedString("<span title=\"First name\">First name</span>");
		dataGrid.addColumn(firstNameColumn, firstNameHeader);
		dataGrid.setColumnWidth(firstNameColumn, "50px");
		
		// User ID
		final Column<T, String> userIdColumn = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				String roleStr = ((UserInfo)object).getRoleAndOrgMembership();
				if (roleStr.equals("NEW")) {
					// A role of NEW indicates that a new role and org unit
					// must be set for the user
					return ((UserInfo) object).getUserName() + "*";
				} else {
					return ((UserInfo) object).getUserName();
				}
			}
		};
		userIdColumn.setSortable(SORTING_ON);
		sortHandler.setComparator(userIdColumn, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((UserInfo) o1).getUserName().compareTo(
						((UserInfo) o2).getUserName());
			}
		});
		SafeHtml userIdHeader = 
				SafeHtmlUtils.fromTrustedString("<span title=\"User ID\">User ID</span>");
		dataGrid.addColumn(userIdColumn, userIdHeader);
		dataGrid.setColumnWidth(userIdColumn, "50px");
	}
	
	/** Set the number of rows shown on each page. */
	public void setPageSize(int pageSize) {
		this.dataGrid.setPageSize(pageSize);
	}
}
