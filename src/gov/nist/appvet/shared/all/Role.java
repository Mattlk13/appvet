package gov.nist.appvet.shared.all;

import java.util.ArrayList;
import java.util.logging.Logger;



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
	USER,
	// A temporary NEW role is assigned to all users when converting the updated 
	// AppVet 2.1 database schema to AppVet 2.2.	
	NEW;

	private static Logger log = Logger.getLogger("Role");

	private Role() {
	}
	
	/** Get a role given a roleStr. A roleStr is a String that defines a 
	 * role and its related org hierarchy (if available) and has the following 
	 * forms: 
	 * <ul>
	 * <li>ADMIN
	 * <li>TOOL_PROVIDER
	 * <li>ANALYST:level1,level2[,level3[,level4]]
	 * <li>USER:level1,level2[,level3[,level4]]
	 * </ul>
	 */
	public static Role getRole(String roleStr) throws Exception {
		if (roleStr != null && !roleStr.isEmpty()) {
			if (roleStr.equals(Role.ADMIN.name())) {
				return Role.ADMIN;
			} else if (roleStr.equals(Role.TOOL_PROVIDER.name())) {
				return Role.TOOL_PROVIDER;
			} else if (roleStr.equals("NEW")) {
				// A temporary role 'NEW' will be assigned when converting from AppVet 2.1 to AppVet 2.2
				return Role.NEW;
			} else {
				String[] roleAndHierarchy = roleStr.split(":");
				if (roleAndHierarchy == null) {
					throw new Exception("Invalid roleAndHierarchy format.");
				} else if (roleAndHierarchy.length != 2) {
					throw new Exception(
							"Role and hierarchy string must contain exactly two elements.");
				}
				String rolePart = roleAndHierarchy[0];
				if (rolePart.equals(Role.USER.name())) {
					return Role.USER;
				} else if (rolePart.equals(Role.ANALYST.name())) {
					return Role.ANALYST;
				} else {
					throw new Exception("Unknown role: " + rolePart);
				}
			}
		} else {
			throw new Exception("roleStr is null");
		}
	}
	
	/** Get an array of org hierarchies given a roleStr. A roleStr is a String 
	 * that defines a role and its related org hierarchy (if available) and 
	 * has the following 
	 * forms: 
	 * <ul>
	 * <li>ADMIN
	 * <li>TOOL_PROVIDER
	 * <li>ANALYST:level1,level2[,level3[,level4]]
	 * <li>USER:level1,level2[,level3[,level4]]
	 * </ul>
	 */
	public static ArrayList<String> getOrgHierarchy(String roleStr) throws Exception {
		if (roleStr != null && !roleStr.isEmpty()) {
			if (roleStr.equals(Role.ADMIN.name())) {
				return null;
			} else if (roleStr.equals(Role.TOOL_PROVIDER.name())) {
				return null;
			} else if (roleStr.equals(Role.NEW.name())) {
				return null;
			} else {
				String[] roleAndHierarchy = roleStr.split(":");
				if (roleAndHierarchy == null) {
					throw new Exception("Invalid roleAndHierarchy format.");
				} else if (roleAndHierarchy.length != 2) {
					throw new Exception(
							"Role and hierarchy string must contain exactly two elements.");
				}
				String rolePart = roleAndHierarchy[0];
				if (!rolePart.equals(Role.USER.name()) && !rolePart.equals(Role.ANALYST.name())) {
					throw new Exception("Invalid role: " + rolePart);
				} 
				String hierarchyPart = roleAndHierarchy[1];
				if (hierarchyPart == null) {
					throw new Exception("hierarchyPart is null");
				}
				String[] orgLevels = hierarchyPart.split(",");
				if (orgLevels.length <= 0 || orgLevels.length > 4) {
					throw new Exception("Invalid number of org levels" + orgLevels.length);
				}
				ArrayList<String> orgHierarchy = new ArrayList<String>();
				for (int i = 0; i < orgLevels.length; i++) {
					String orgLevel = orgLevels[i];
					String trimmedOrgLevel = orgLevel.trim();
					orgHierarchy.add(trimmedOrgLevel);
				}
				return orgHierarchy;
			}
		} else {
			throw new Exception("Emptry roleStr");
		}
	}
	
	/** Get a String representation of org hierarchies given a roleStr. 
	 * A roleStr is a String 
	 * that defines a role and its related org hierarchy (if available) and 
	 * has the following 
	 * forms: 
	 * <ul>
	 * <li>ADMIN
	 * <li>TOOL_PROVIDER
	 * <li>ANALYST:level1,level2[,level3[,level4]]
	 * <li>USER:level1,level2[,level3[,level4]]
	 * </ul>
	 */
	public static String getOrgHierarchyStr(String roleStr) throws Exception {
		if (roleStr != null && !roleStr.isEmpty()) {
			if (roleStr.equals(Role.ADMIN.name())) {
				return null;
			} else if (roleStr.equals(Role.TOOL_PROVIDER.name())) {
				return null;
			} else if (roleStr.equals(Role.NEW.name())) {
				return null;
			} else {
				String[] roleAndHierarchy = roleStr.split(":");
				if (roleAndHierarchy == null) {
					throw new Exception("Invalid roleAndHierarchy format.");
				} else if (roleAndHierarchy.length != 2) {
					throw new Exception(
							"Role and hierarchy string must contain exactly two elements.");
				}
				String rolePart = roleAndHierarchy[0];
				if (!rolePart.equals(Role.USER.name()) && !rolePart.equals(Role.ANALYST.name())) {
					throw new Exception("Invalid role");
				} 
				String hierarchyPart = roleAndHierarchy[1];
				if (hierarchyPart == null) {
					throw new Exception("hierarchyPart is null");
				}
				String[] orgLevels = hierarchyPart.split(",");
				if (orgLevels.length <= 0 || orgLevels.length > 4) {
					throw new Exception("Invalid number of org levels" + orgLevels.length);
				}
				String orgHierarchyStr = "";
				for (int i = 0; i < orgLevels.length; i++) {
					String orgLevel = orgLevels[i];
					String trimmedOrgLevel = orgLevel.trim();
					orgHierarchyStr += trimmedOrgLevel;
					if (i < orgLevels.length - 1) {
						orgHierarchyStr += ",";
					}
				}
				return orgHierarchyStr;
			}
		} else {
			throw new Exception("Emptry roleStr");
		}
	}
	
	/** Get a String representation of org hierarchies for display purposes
	 * given a roleStr. A roleStr is a String that defines a role and its 
	 * related org hierarchy (if available) and has the following forms: 
	 * <ul>
	 * <li>ADMIN
	 * <li>TOOL_PROVIDER
	 * <li>ANALYST:level1,level2[,level3[,level4]]
	 * <li>USER:level1,level2[,level3[,level4]]
	 * </ul>
	 */
	public static String getOrgHierarchyDisplayStr(String roleStr) throws Exception {
		if (roleStr != null && !roleStr.isEmpty()) {
			if (roleStr.equals(Role.ADMIN.name())) {
				return null;
			} else if (roleStr.equals(Role.TOOL_PROVIDER.name())) {
				return null;
			} else if (roleStr.equals(Role.NEW.name())) {
				return null;
			} else {
				String[] roleAndHierarchy = roleStr.split(":");
				if (roleAndHierarchy == null) {
					throw new Exception("Invalid roleAndHierarchy format.");
				} else if (roleAndHierarchy.length != 2) {
					throw new Exception(
							"Role and hierarchy string must contain exactly two elements.");
				}
				String rolePart = roleAndHierarchy[0];
				if (!rolePart.equals(Role.USER.name()) && !rolePart.equals(Role.ANALYST.name())) {
					throw new Exception("Invalid role");
				} 
				String hierarchyPart = roleAndHierarchy[1];
				if (hierarchyPart == null) {
					throw new Exception("hierarchyPart is null");
				}
				String[] orgLevels = hierarchyPart.split(",");
				if (orgLevels.length <= 0 || orgLevels.length > 4) {
					throw new Exception("Invalid number of org levels" + orgLevels.length);
				}
				String orgHierarchyStr = "";
				for (int i = 0; i < orgLevels.length; i++) {
					String orgLevel = orgLevels[i];
					String trimmedOrgLevel = orgLevel.trim();
					orgHierarchyStr += trimmedOrgLevel;
					if (i < orgLevels.length - 1) {
						orgHierarchyStr += "/";
					}
				}
				return orgHierarchyStr;
			}
		} else {
			throw new Exception("Emptry roleStr");
		}
	}
	
//	/** Get display string using role and slash ('/') delimitter for hierarchy levels.*/
//	public static String getDbDisplayStr(Role role, ArrayList<String> hierarchy) throws Exception {
//		if (role == Role.ADMIN || 
//				role == Role.TOOL_PROVIDER ||
//				role == Role.NEW) {
//			return role.name();
//		}
//		if (role == Role.ANALYST || role == Role.USER) {
//			String dbStr = role.name() + ": ";
//			for (int i = 0; i < hierarchy.size(); i++) {
//				String level = hierarchy.get(i);
//				String trimmedLevel = level.trim();
//				dbStr += trimmedLevel;
//				if (i < hierarchy.size() - 1) {
//					dbStr += "/";
//				}
//			}
//			return dbStr;
//		} else {
//			throw new Exception("Unknown role");
//		}
//	}
	
//	public static String getDbStr(Role role, ArrayList<String> hierarchy) throws Exception {
//		if (role == Role.ADMIN || 
//				role == Role.TOOL_PROVIDER ||
//				role == Role.NEW) {
//			return role.name();
//		}
//		if (role == Role.ANALYST || role == Role.USER) {
//			String dbStr = role.name() + ":";
//			for (int i = 0; i < hierarchy.size(); i++) {
//				String level = hierarchy.get(i);
//				String trimmedLevel = level.trim();
//				dbStr += trimmedLevel;
//				if (i < hierarchy.size() - 1) {
//					dbStr += ",";
//				}
//			}
//			return dbStr;
//		} else {
//			throw new Exception("Unknown role");
//		}
//	}
}
