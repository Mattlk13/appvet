package gov.nist.appvet.shared.all;

import java.util.ArrayList;

/** Defines a group that a user belongs to */
public class Group {
	public boolean isAdmin = false;
	public boolean isTool = false;
	public boolean isUser = false;
	public String level1Name = null;
	public boolean isLevel1Analyst = false;
	public String level2Name = null;
	public boolean isLevel2Analyst = false;
	public String level3Name = null;
	public boolean isLevel3Analyst = false;
	public String level4Name = null;
	public boolean isLevel4Analyst = false;
	
	public Group(){}
	
	/**
	 * Group string should have the format 
	 * "level1 [*], level2 [*], level3 [*], level4 [*]" where '*' represents
	 * an ANALYST role.
	 * This method is used when acquiring group information from the database
	 * as strings.
	 * @param groupStr
	 */
	public Group(String groupStr) {
		String analyst = " (" + Role.ANALYST.name() + ")";
		String[] levels = groupStr.split(",");
		if (levels != null && levels.length > 0) {
			for (int i = 0; i < levels.length; i++) {
				String level = levels[i];
				int indexAnalyst = level.indexOf(analyst);
				if (level.indexOf(analyst) > -1) {
					if (i == 0) {
						level1Name = level.substring(0, indexAnalyst);
						isLevel1Analyst = true;
					} else if (i == 1) {
						level2Name = level.substring(0, indexAnalyst);
						isLevel2Analyst = true;
					} else if (i == 2) {
						level3Name = level.substring(0, indexAnalyst);
						isLevel3Analyst = true;
					} else if (i == 3) {
						level4Name = level.substring(0, indexAnalyst);
						isLevel4Analyst = true;
					}
				} else {
					if (i == 0) {
						level1Name = level;
					} else if (i == 1) {
						level2Name = level;
					} else if (i == 2) {
						level3Name = level;
					} else if (i == 3) {
						level4Name = level;
					}					
				}
			}
		}
	}
	
	/** This method is used when storing group info in the database and
	 * displaying in a list box.
	 */
	@Override
	public String toString() {
		String tmpStr = "";
		if (isAdmin) {
			return Role.ADMIN.name();
		} else if (isTool) {
			return Role.TOOL_PROVIDER.name();
		} else {
			String analyst = " (" + Role.ANALYST.name() + ")";
			if (level1Name != null && !level1Name.isEmpty()) {
				tmpStr += level1Name;
				if (isLevel1Analyst) {
					tmpStr += analyst;
				}
			}
			if (level2Name != null && !level2Name.isEmpty()) {
				tmpStr += "," + level2Name;
				if (isLevel2Analyst) {
					tmpStr += analyst;
				}
			}
			if (level3Name != null && !level3Name.isEmpty()) {
				tmpStr += "," + level3Name;
				if (isLevel3Analyst) {
					tmpStr += analyst;
				}
			}
			if (level4Name != null && !level4Name.isEmpty()) {
				tmpStr += "," + level4Name;
				if (isLevel4Analyst) {
					tmpStr += analyst;
				}
			}
		}
		return tmpStr;
	}
	
	public static String getGroupsString(ArrayList<Group> groups) {
		if (groups == null) {
			return null;
		} else {
			String tmpStr = "";
			for (int i = 0; i < groups.size(); i++) {
				Group group = groups.get(i);
				tmpStr += group.toString();
				if (tmpStr.indexOf(Role.ADMIN.name()) == -1
						&& tmpStr.indexOf(Role.TOOL_PROVIDER.name()) == -1
						&& i < groups.size()-1) {
					// Add semicolon
					tmpStr += ";";
				}
			}
			return tmpStr;
		}
	}
}
