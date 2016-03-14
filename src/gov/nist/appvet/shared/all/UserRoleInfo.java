package gov.nist.appvet.shared.all;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author steveq@nist.gov
 */
public class UserRoleInfo implements IsSerializable {

	public enum Role {
		// An ADMIN is an administrator of the AppVet system. An ADMIN
		// has complete access and control of all accounts and apps.
		ADMIN,
		// A TOOL_PROVIDER is a user that can submit tool reports
		// for any app and access their own account (e.g., for testing their
		// tool).
		TOOL_PROVIDER,
		// An ANALYST is a user that can access all apps in an org unit and
		// all apps in all sub org units. This role is used when there is a need
		// to distinguish the user's role for a specific org unit.
		ANALYST,
		// A USER is a user that can access only their own apps. This role is
		// used when there is a need
		// to distinguish the user's role for a specific org unit.
		USER;

		private Role() {
		}

		public static Role getRole(String roleName) {
			if (roleName != null) {
				for (final Role r : Role.values()) {
					if (roleName.equalsIgnoreCase(r.name())) {
						return r;
					}
				}
			}
			return null;
		}
	}

	private Role role;
	/**
	 * An organizational hierarchy is an array of top-down organizational levels
	 * that form an organizational unit (e.g., agency, department, group, and
	 * team). Note that orgHierarchy is null if role is ADMIN or TOOL_PROVIDER.
	 */
	private ArrayList<String> orgUnitHierarchy = null;

	/** Empty constructor required by GWT but should not be used by AppVet. */
	public UserRoleInfo() {
	}

	public UserRoleInfo(Role role) {
		this.role = role;
		orgUnitHierarchy = new ArrayList<String>();
	}

	public UserRoleInfo(Role role, ArrayList<String> orgUnits) {
		this.role = role;
		this.orgUnitHierarchy = orgUnits;
	}

	/**
	 * This method constructs a user role info object from its database string
	 * representation.
	 */
	public UserRoleInfo(String databaseStr) throws Exception {
		System.out.println("databaseStr: " + databaseStr);
		if (databaseStr != null && !databaseStr.isEmpty()) {
			if (databaseStr.equals(Role.ADMIN.name())) {
				System.out.println("Setting to ADMIN");
				role = Role.ADMIN;
			} else if (databaseStr.equals(Role.TOOL_PROVIDER.name())) {
				System.out.println("Setting to TOOL_PROVIDER");
				role = Role.TOOL_PROVIDER;
			} else {
				System.out.println("GOT ELSE");
				String[] roleAndHierarchy = databaseStr.split(":");
				if (roleAndHierarchy == null) {
					System.err.println("Role and hierarchy string must contain colon "
							+ "to separate role and hierarchy.");
					return;
				} else if (roleAndHierarchy.length != 2) {
					System.out.println(
							"Role and hierarchy string must contain exactly two elements.");
					return;
				}
				String rolePart = roleAndHierarchy[0];
				System.out.println("rolePart: " + rolePart);
				String hierarchyPart = roleAndHierarchy[1];
				System.out.println("hierarchyPart: " + hierarchyPart);
				String[] orgLevels = hierarchyPart.split(",");
				System.out.println("orgLevels size: " + orgLevels.length);
				for (int j = 0; j < orgLevels.length; j++) {
					System.out.println("Value: " + orgLevels[j]);
				}
				
				String roleUSERname = UserRoleInfo.Role.USER.name();
				System.out.println("roleUSERname: " + roleUSERname);

				String roleANALYSTname = UserRoleInfo.Role.ANALYST.name();
				System.out.println("roleANALYSTname: " + roleANALYSTname);

				if (rolePart.equals(roleUSERname)) {
					System.out.println("r = USER");
					role = Role.USER;
				} else if (role.equals(roleANALYSTname)) {
					System.out.println("r = ANALYST");
					role = Role.ANALYST;
				} else {
					System.err.println("Only USER and ANALYST roles permitted here.");
//					throw new Exception(
//							"OrgUnit can only have role type of USER or ANALYST");
				}
				System.out.println("out of if");

				orgUnitHierarchy = new ArrayList<String>(Arrays.asList(orgLevels));
				System.out.println("orgUnitHierarchy size: " + orgUnitHierarchy.size());
				for (int i = 0; i < orgUnitHierarchy.size(); i++) {
					System.out.println("level: " + orgUnitHierarchy.get(i));
				}
			}
		} else {
			throw new Exception(
					"UserRoleInfo cannot be set with null or empty string.");
		}
	}

	public Role getRole() {
		return role;
	}

	public ArrayList<String> getOrgUnitHierarchy() {
		return orgUnitHierarchy;
	}

//	public void addOrgUnit(OrgUnit orgUnit) throws Exception {
//		if (role == Role.USER_ANALYST) {
//			orgHierarchy.add(orgUnit);
//		} else {
//			throw new Exception(
//					"Adding org units can only be invoked for role type USER_ANALYST, not role type "
//							+ role.name());
//		}
//	}
//
//	public void setOrgUnit(int index, OrgUnit orgUnit) throws Exception {
//		if (role == Role.USER_ANALYST) {
//			orgHierarchy.set(index, orgUnit);
//		} else {
//			throw new Exception(
//					"Setting org units can only be invoked for role type USER_ANALYST, not role type "
//							+ role.name());
//		}
//	}
//
//	public void removeOrgUnit(int index) throws Exception {
//		if (role == Role.USER_ANALYST) {
//			orgHierarchy.remove(index);
//		} else {
//			throw new Exception(
//					"Setting org units can only be invoked for role type USER_ANALYST, not role type "
//							+ role.name());
//		}
//	}

	/** This method gets the database representation of a user info object. */
	public String getDbString() {
		if (role == Role.ADMIN || role == Role.TOOL_PROVIDER) {
			return role.name();
		}
		String userInfoStr = role.name() + ":";
		for (int i = 0; i < orgUnitHierarchy.size(); i++) {
			userInfoStr += orgUnitHierarchy.get(i);
			if (i < orgUnitHierarchy.size() - 1) {
				userInfoStr += ",";
			}
		}
		return userInfoStr;
	}
	
	public String getOrgUnitHierarchyStr() {
		String userInfoStr = "";
		for (int i = 0; i < orgUnitHierarchy.size(); i++) {
			userInfoStr += orgUnitHierarchy.get(i);
			if (i < orgUnitHierarchy.size() - 1) {
				userInfoStr += ",";
			}
		}
		return userInfoStr;
	}

}
