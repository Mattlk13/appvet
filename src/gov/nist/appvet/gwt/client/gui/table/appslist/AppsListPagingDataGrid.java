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
import gov.nist.appvet.gwt.shared.AppInfoGwt;
import gov.nist.appvet.shared.all.AppStatus;
import gov.nist.appvet.shared.all.DeviceOS;

import java.util.Comparator;
import java.util.logging.Logger;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * @author steveq@nist.gov
 */
public class AppsListPagingDataGrid<T> extends PagingDataGrid<T> {

	// Turn off sorting of columns for 508 Compliance
	private final boolean SORTING_ON = false;
	private final DateTimeFormat dateTimeFormat = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm:ss");
	private Logger log = Logger.getLogger("AppsListPagingDataGrid");

	@Override
	public void initTableColumns(DataGrid<T> dataGrid,
			ListHandler<T> sortHandler) {
		
		// TEST GETTING WIDGET ITEMS
		this.getElement();
		
		
		
		// App ID
		final Column<T, String> appIdColumn = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				return ((AppInfoGwt) object).appId;
			}
		};
		appIdColumn.setSortable(SORTING_ON);
		sortHandler.setComparator(appIdColumn, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((AppInfoGwt) o1).appId
						.compareTo(((AppInfoGwt) o2).appId);
			}
		});
		appIdColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(appIdColumn, "ID");
		dataGrid.setColumnWidth(appIdColumn, "43px");
		//dataGrid.setTitle("Apps list");

		// Platform/OS Icon 
		final SafeHtmlCell osIconCell = new SafeHtmlCell();
		final Column<T, SafeHtml> osIconColumn = new Column<T, SafeHtml>(
				osIconCell) {
			@Override
			public SafeHtml getValue(T object) {
				final SafeHtmlBuilder sb = new SafeHtmlBuilder();
				final DeviceOS os = ((AppInfoGwt) object).os;
				if (os == null) {
					log.warning("OS is null");
					return sb.toSafeHtml();
				}
				if (os == DeviceOS.ANDROID) {
					sb.appendHtmlConstant("<img width=\"10\" src=\"images/android_logo_green.png\"  alt=\"Android\" />");
				} else if (os == DeviceOS.IOS) {
					sb.appendHtmlConstant("<img width=\"10\" src=\"images/ios_logo_blue.png\"  alt=\"iOS\" />");
				}
				return sb.toSafeHtml();
			}
		};

		osIconColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		osIconColumn.setSortable(false);
		dataGrid.addColumn(osIconColumn, "OS");
		dataGrid.setColumnWidth(osIconColumn, "15px");

		// App Icon
		final SafeHtmlCell iconCell = new SafeHtmlCell();
		final Column<T, SafeHtml> iconColumn = new Column<T, SafeHtml>(iconCell) {
			@Override
			public SafeHtml getValue(T object) {
				final String selectedIconURL = ((AppInfoGwt) object).iconURL;
				final DeviceOS os = ((AppInfoGwt) object).os;
				String iconURL = null;
				String altText = null;
				//log.info("selectedIconURL: " + selectedIconURL);
				if (selectedIconURL == null) {
					if (os == DeviceOS.ANDROID) {
						iconURL = "images/android-icon-gray.png";
						altText = "Android app";
					} else if (os == DeviceOS.IOS) {
						iconURL = "images/apple-icon-gray.png";
						altText = "iOS app";
					}
				} else {
					iconURL = selectedIconURL;
					altText = ((AppInfoGwt) object).appName;
				}
				final SafeHtmlBuilder sb = new SafeHtmlBuilder();
				sb.appendHtmlConstant("<img width=\"20\" src=\"" + iconURL
						+ "\" alt=\"" + altText + "\" />");
				return sb.toSafeHtml();
			}
		};
		
		iconColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		iconColumn.setSortable(SORTING_ON);
		dataGrid.addColumn(iconColumn, "App");
		dataGrid.setColumnWidth(iconColumn, "20px");
		
		// App Name
		final Column<T, String> appNameColumn = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				return ((AppInfoGwt) object).appName;
			}
		};
		
		appNameColumn.setSortable(SORTING_ON);
		sortHandler.setComparator(appNameColumn, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((AppInfoGwt) o1).appName
						.compareTo(((AppInfoGwt) o2).appName);
			}
		});
		
		appNameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(appNameColumn, "");
		dataGrid.setColumnWidth(appNameColumn, "80px");

		// App Version
		final Column<T, String> appVersionColumn = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				return ((AppInfoGwt) object).versionName;
			}
		};
		
		appVersionColumn.setSortable(SORTING_ON);
		sortHandler.setComparator(appVersionColumn, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((AppInfoGwt) o1).versionName
						.compareTo(((AppInfoGwt) o2).versionName);
			}
		});
		
		appVersionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(appVersionColumn, "Version");
		dataGrid.setColumnWidth(appVersionColumn, "35px");		

		// Status
		final SafeHtmlCell statusCell = new SafeHtmlCell();
		final Column<T, SafeHtml> statusColumn = new Column<T, SafeHtml>(
				statusCell) {
			@Override
			public SafeHtml getValue(T object) {
				final SafeHtmlBuilder sb = new SafeHtmlBuilder();
				final AppStatus appStatus = ((AppInfoGwt) object).appStatus;
				String statusHtml = null;
				
				if (appStatus == AppStatus.ERROR) {
					statusHtml = "<div id=\"error\" style='color: black'>ERROR</div>";
				} else if (appStatus == AppStatus.MODERATE) {
					statusHtml = "<div id=\"warning\" style='color: orange'>"
							+ "MODERATE" + "</div>";					
				} else if (appStatus == AppStatus.LOW) {
					statusHtml = "<div id=\"endorsed\" style='color: green'>"
							+ "LOW" + "</div>";					
				} else if (appStatus == AppStatus.HIGH) {
					statusHtml = "<div id=\"error\" style='color: red'>HIGH</div>";
				} else if (appStatus == AppStatus.NA) {
					statusHtml = "<div id=\"error\">N/A</div>";
				} else {
					statusHtml = "<div id=\"error\">"
							+ appStatus.name() + "</div>";
				}
				sb.appendHtmlConstant(statusHtml);
				return sb.toSafeHtml();
			}
		};
		
		statusColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		statusColumn.setSortable(SORTING_ON);
		sortHandler.setComparator(statusColumn, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((AppInfoGwt) o1).appStatus
						.compareTo(((AppInfoGwt) o2).appStatus);
			}
		});
		
		dataGrid.addColumn(statusColumn, "Status/Risk");
		dataGrid.setColumnWidth(statusColumn, "45px");

		// Submitter 
		final Column<T, String> submitterColumn = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				return ((AppInfoGwt) object).ownerName;
			}
		};
		
		submitterColumn.setSortable(SORTING_ON);
		sortHandler.setComparator(submitterColumn, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((AppInfoGwt) o1).ownerName
						.compareTo(((AppInfoGwt) o2).ownerName);
			}
		});
		
		submitterColumn
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(submitterColumn, "User");
		dataGrid.setColumnWidth(submitterColumn, "60px");

		// Submit Time
		final Column<T, String> submitTimeColumn = new Column<T, String>(
				new TextCell()) {
			@Override
			public String getValue(T object) {
				final AppInfoGwt appInfo = (AppInfoGwt) object;
				final String dateString = dateTimeFormat.format(appInfo.submitTime);
				return dateString;
			}
		};
		submitTimeColumn.setSortable(SORTING_ON);
		sortHandler.setComparator(submitTimeColumn, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				final AppInfoGwt appInfo1 = (AppInfoGwt) o1;
				final String dateString1 = dateTimeFormat.format(appInfo1.submitTime);
				final AppInfoGwt appInfo2 = (AppInfoGwt) o2;
				final String dateString2 = dateTimeFormat.format(appInfo2.submitTime);
				return dateString1.compareTo(dateString2);
			}
		});
		
		submitTimeColumn
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(submitTimeColumn, "Date/Time");
		dataGrid.setColumnWidth(submitTimeColumn, "75px");
	}

	
	/** Set the number of rows shown on each page. */
	public void setPageSize(int pageSize) {
		this.dataGrid.setPageSize(pageSize);
	}
	
}
