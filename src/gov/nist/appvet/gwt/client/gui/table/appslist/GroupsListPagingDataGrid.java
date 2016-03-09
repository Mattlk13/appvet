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
import gov.nist.appvet.shared.all.Group;

import java.util.Comparator;
import java.util.logging.Logger;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;

/**
 * @author steveq@nist.gov
 */
public class GroupsListPagingDataGrid<T> extends PagingDataGrid<T> {
	private static Logger log = Logger.getLogger("GroupsListPagingDataGrid");

	// Turn off sorting of columns for 508 Compliance
	private final boolean SORTING_ON = false;
	
	@Override
	public void initTableColumns(DataGrid<T> dataGrid,
			ListHandler<T> sortHandler) {
		
		// Level 1 (Top Level)
		final Column<T, String> level1Column = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				if (((Group) object).isLevel1Analyst) {
					return ((Group) object).level1Name + "*";
				} else {
					return ((Group) object).level1Name;
				}
			}
		};
		level1Column.setSortable(SORTING_ON);
		sortHandler.setComparator(level1Column, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((Group) o1).level1Name.compareTo(
						((Group) o2).level1Name);
			}
		});
		dataGrid.addColumn(level1Column, "Level 1");
		dataGrid.setColumnWidth(level1Column, "50px");
		
		// Level 2
		final Column<T, String> level2Column = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				if (((Group) object).isLevel2Analyst) {
					return ((Group) object).level2Name + "*";
				} else {
					return ((Group) object).level2Name;
				}
			}
		};
		level2Column.setSortable(SORTING_ON);
		sortHandler.setComparator(level2Column, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((Group) o1).level2Name.compareTo(
						((Group) o2).level2Name);
			}
		});
		dataGrid.addColumn(level2Column, "Level 2");
		dataGrid.setColumnWidth(level2Column, "50px");
		
		// Level 3
		final Column<T, String> level3Column = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				if (((Group) object).isLevel3Analyst) {
					return ((Group) object).level3Name + "*";
				} else {
					return ((Group) object).level3Name;
				}
			}
		};
		level3Column.setSortable(SORTING_ON);
		sortHandler.setComparator(level3Column, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((Group) o1).level3Name.compareTo(
						((Group) o2).level3Name);
			}
		});
		dataGrid.addColumn(level3Column, "Level 3");
		dataGrid.setColumnWidth(level3Column, "50px");
		
		// Level 4
		final Column<T, String> level4Column = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				if (((Group) object).isLevel4Analyst) {
					return ((Group) object).level4Name + "*";
				} else {
					return ((Group) object).level4Name;
				}
			}
		};
		level4Column.setSortable(SORTING_ON);
		sortHandler.setComparator(level4Column, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((Group) o1).level4Name.compareTo(
						((Group) o2).level4Name);
			}
		});
		dataGrid.addColumn(level4Column, "Level 4");
		dataGrid.setColumnWidth(level4Column, "50px");
	}
	
	/** Set the number of rows shown on each page. */
	public void setPageSize(int pageSize) {
		this.dataGrid.setPageSize(pageSize);
	}
}
