package gov.nist.appvet.shared.all;

import java.util.ArrayList;

/**
 * @author steveq@nist.gov
 */
public class UserRoleInfo {

	private Role role;
	private ArrayList<OrgUnit> orgUnits = null;
	
	public UserRoleInfo(Role role, ArrayList<OrgUnit> orgUnits) {
		this.role = role;
		this.orgUnits = orgUnits;
	}
	
	public UserRoleInfo(String userRoleInfoStr) {
		if (userRoleInfoStr != null && !userRoleInfoStr.isEmpty()) {
			if (userRoleInfoStr.equals(Role.ADMIN.name())) {
				role = Role.ADMIN;
				return;
			} else if (userRoleInfoStr.equals(Role.TOOL_PROVIDER.name())) {
				role = Role.TOOL_PROVIDER;
				return;
			} else if (userRoleInfoStr.indexOf(Role.USER.name()) > -1 ||
					userRoleInfoStr.indexOf(Role.ANALYST.name()) > -1) {
				role = Role.USER_ANALYST;
			} else {
				return;
			}
		}
		String[] orgUnitsArray = userRoleInfoStr.split(";");
		if (orgUnitsArray != null) {
			orgUnits = new ArrayList<OrgUnit>();
		}
		for (int i = 0; i < orgUnitsArray.length; i++) {
			OrgUnit orgUnit = new OrgUnit(orgUnitsArray[i]);
			if (orgUnit != null) {
				orgUnits.add(orgUnit);
			}
		}
	}
	
	public Role getRole() {
		return role;
	}
	
	public ArrayList<OrgUnit> getOrgUnits() {
		return orgUnits;
	}

//	public UserRoleInfo(Role role) {
//		this.role = role;
//	}

//	public UserRoleInfo(String roleStr) {
//		setRoles(roleStr);
//	}
//
//	public void setRole(Role role) {
//		this.role = role;
//		if (role == Role.ADMIN || role == Role.TOOL_PROVIDER) {
//			userGroups = null;
//			analystGroups = null;
//		}
//	}
//
//	public Role getRole() {
//		return role;
//	}
//
//	public boolean removeUserGroup(String group) {
//		if (role == Role.USER_ANALYST) {
//			if (userGroups == null || userGroups.isEmpty()) {
//				return false;
//			} else {
//				for (int i = 0; i < userGroups.size(); i++) {
//					String selectedGroup = userGroups.get(i);
//					if (group.equals(selectedGroup)) {
//						userGroups.remove(i);
//						return true;
//					}
//				}
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}
//
//	public boolean removeAnalystGroup(String group) {
//		if (role == Role.USER_ANALYST) {
//			if (analystGroups == null || analystGroups.isEmpty()) {
//				return false;
//			} else {
//				for (int i = 0; i < analystGroups.size(); i++) {
//					String selectedGroup = analystGroups.get(i);
//					if (group.equals(selectedGroup)) {
//						analystGroups.remove(i);
//						return true;
//					}
//				}
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}
//
//	/**
//	 * A user may have one or more USER roles depending on the group. A USER
//	 * role is denoted by a string with the syntax: USER: level1Name,
//	 * level2Name, level3Name, level4Name Note that all four levels must be
//	 * defined for a USER.
//	 */
//	public boolean addUserGroup(String groupStr) {
//		if (role == Role.USER_ANALYST) {
//			if (userGroups == null) {
//				userGroups = new ArrayList<String>();
//			}
//			if (isValidGroup(UserRoleInfo.USER_ROLE_STR, groupStr)) {
//				userGroups.add(groupStr);
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}
//	
//
//	/**
//	 * A user may have one or more ANALYST roles depending on the group. An
//	 * ANALYST role is denoted by the string with the syntax: ANALYST:
//	 * level1Name [, level2Name] [, level3Name] [, level4Name] Note that only
//	 * level1Name is required, all other levels are optional depending on what
//	 * level the ANALYST role resides.
//	 */
//	public void addAnalystGroup(String analystStr) {
//		if (role == Role.USER_ANALYST) {
//			if (analystGroups == null) {
//				analystGroups = new ArrayList<String>();
//			}
//			analystGroups.add(analystStr);
//		}
//	}
//
//	/**
//	 * Set the roles for a user. For a USER_ANALYST, a roleStr comprises a
//	 * comma-delimitted set of roles and n-level groups.
//	 * 
//	 * @param rolesStr
//	 */
//	public void setRoles(String rolesStr) {
//		if (rolesStr.equals(Role.ADMIN.name())) {
//			setRole(Role.ADMIN);
//		} else if (rolesStr.equals(Role.TOOL_PROVIDER.name())) {
//			setRole(Role.TOOL_PROVIDER);
//		} else {
//			String[] groups = rolesStr.split(";");
//			for (int i = 0; i < groups.length; i++) {
//				String[] roleAndGroup = groups[i].split(":");
//				String role = roleAndGroup[0];
//				String group = roleAndGroup[1];
//				if (role.equals(USER_ROLE_STR)) {
//					addUserGroup(group);
//				} else if (role.equals(ANALYST_ROLE_STR)) {
//					addAnalystGroup(group);
//				}
//			}
//		}
//	}
//
//	@Override
//	public String toString() {
//		if (role == Role.ADMIN) {
//			return Role.ADMIN.name();
//		} else if (role == Role.TOOL_PROVIDER) {
//			return Role.TOOL_PROVIDER.name();
//		} else {
//			String allGroups = "";
//			allGroups += getUserGroupsStr();
//
//			if (!allGroups.isEmpty() && analystGroups != null
//					&& analystGroups.size() > 0) {
//				allGroups += ";";
//			}
//
//			allGroups += getAnalystGroupsStr();
//
//			return allGroups;
//		}
//	}
//
//	public String getUserGroupsStr() {
//		if (userGroups == null || userGroups.isEmpty()) {
//			return null;
//		}
//		String allGroups = "";
//		for (int i = 0; i < userGroups.size(); i++) {
//			if (i < userGroups.size() - 1) {
//				allGroups += USER_ROLE_STR + ":" + userGroups.get(i) + ";";
//			} else {
//				allGroups += USER_ROLE_STR + ":" + userGroups.get(i);
//			}
//		}
//		return allGroups;
//	}
//	
//
//
//	public String getAnalystGroupsStr() {
//		if (analystGroups == null || analystGroups.isEmpty()) {
//			return null;
//		}
//		String allGroups = "";
//		for (int i = 0; i < analystGroups.size(); i++) {
//			if (i < analystGroups.size() - 1) {
//				allGroups += ANALYST_ROLE_STR + ":" + analystGroups.get(i) + ";";
//			} else {
//				allGroups += ANALYST_ROLE_STR + ":" + analystGroups.get(i);
//			}
//		}
//		return allGroups;
//	}
//	
//	public ArrayList<String> getAllGroups() {
//		ArrayList<String> allGroups = new ArrayList<String>();
//		allGroups.addAll(getUserGroups());
//		allGroups.addAll(getAnalystGroups());
//		return allGroups;
//	}
//	
//	public ArrayList<String> getUserGroups() {
//		return userGroups;
//	}
//	
//	public ArrayList<String> getAnalystGroups() {
//		return analystGroups;
//	}
//
//	/** Gets all groups that can be analyzed for the given group. */
//	public String getGroupsUnderAnalystGroup(String analystGroupStr) {
//		String allGroups = "";
//		// First, check all user groups that this user belongs to
//		if (userGroups != null || !userGroups.isEmpty()) {
//			for (int i = 0; i < userGroups.size(); i++) {
//				String userGroup = userGroups.get(i);
//				if (userGroup.indexOf(analystGroupStr, 0) > -1) {
//					// Analyst can access this group
//					allGroups += userGroups.get(i);
//				}
//				if (i < userGroups.size() - 1) {
//					allGroups += ";";
//				}
//			}
//		}
//		// Next, check all analyst groups that this user belongs to
//		if (analystGroups != null || !analystGroups.isEmpty()) {
//			for (int j = 0; j < analystGroups.size(); j++) {
//				String analystGroup = analystGroups.get(j);
//				if (analystGroup.indexOf(analystGroupStr, 0) > -1) {
//					// Analyst can access this group
//					allGroups += analystGroups.get(j);
//				}
//				if (j < userGroups.size() - 1) {
//					allGroups += ";";
//				}
//			}
//		}
//		return allGroups;
//	}
}
