package gov.nist.appvet.shared.all;

import java.util.ArrayList;
import java.util.Arrays;

/** This class defines an org unit associated with a user and the role the user plays in this org unit.
 * @author steveq@nist.gov
 */
public class OrgUnit {
	
	/** Only Role.USER and Role.ANALYST are permitted for groupRole.*/
	public Role orgUnitRole = null;
	/** A hierarchy is an array of organizational units from a top level 
	 * (level 1) to a depth of up to four levels.
	 */
	public ArrayList<String> hierarchy = null;
	
	public OrgUnit(Role orgUnitRole) throws Exception {
		if (orgUnitRole == Role.USER || orgUnitRole == Role.ANALYST) {
			this.orgUnitRole = orgUnitRole;
			hierarchy = new ArrayList<String>();
		} else {
			throw new Exception("OrgUnit can only have role type of USER or ANALYST");
		}
	}
	
	public OrgUnit(Role orgUnitRole, ArrayList<String> hierarchy) throws Exception {
		if (orgUnitRole == Role.USER || orgUnitRole == Role.ANALYST) {
			this.orgUnitRole = orgUnitRole;
			this.hierarchy = hierarchy;
		} else {
			throw new Exception("OrgUnit can only have role type of USER or ANALYST");
		}
	}
	
	/** Set org unit as a string of the form: 
	 * [USER|ANALYST]:item1,...,item4 (max) 
	 * This method is used to create an OrgUnit from the stored database
	 * representation.*/
	public OrgUnit(String orgUnitStr) throws Exception {
		if (orgUnitStr == null || orgUnitStr.isEmpty()) {
			throw new Exception("OrgUnit cannot be created with null or empty org unit string.");
		}
		String[] roleAndHiearchy = orgUnitStr.split(":");
		if (roleAndHiearchy == null) {
			throw new Exception("Role and hierarchy string must contain colon to separate role and hierarchy.");
		} else if (roleAndHiearchy.length != 2) {
			throw new Exception("Role and hierarchy string must contain exactly two elements.");
		}
		String role = roleAndHiearchy[0];
		System.out.println("Got OrgUnit role from string: " + role);
		if (role.equals(Role.USER.name())) {
			orgUnitRole = Role.USER;
			String hierarchyStr = roleAndHiearchy[1];
			String[] orgLevels = hierarchyStr.split(",");
			if (orgLevels.length != 4) {
				throw new Exception("Org unit hierarchy must have four defined levels for USER role.");
			}
			hierarchy = new ArrayList<String>(Arrays.asList(orgLevels));
			for (int i = 0; i < hierarchy.size(); i++) {
				System.out.println("level: " + hierarchy.get(i));
			}
		} else if (role.equals(Role.ANALYST.name())) {
			orgUnitRole = Role.ANALYST;
			String hierarchyStr = roleAndHiearchy[1];
			String[] orgLevels = hierarchyStr.split(",");
			if (orgLevels.length == 0 && orgLevels.length > 4) {
				throw new Exception("Org unit hierarchy must have between 1 and 4 levels for ANALYST role.");
			}
			hierarchy = new ArrayList<String>(Arrays.asList(orgLevels));
			for (int i = 0; i < hierarchy.size(); i++) {
				System.out.println("level: " + hierarchy.get(i));
			}
		} else {
			throw new Exception("OrgUnit can only have role type of USER or ANALYST");
		}
	}
	
	public void setRole(Role orgUnitRole) throws Exception {
		if (orgUnitRole == Role.USER || orgUnitRole == Role.ANALYST) {
			this.orgUnitRole = orgUnitRole;
		} else {
			throw new Exception("OrgUnit can only have role type of USER or ANALYST");
		}
	}
	
	public void setLevel(int level, String levelName) {
		if (hierarchy != null && hierarchy.isEmpty() && hierarchy.size() <= level) {
			hierarchy.set(level, levelName);
		}
	}
	
	public String getOrgUnitStr() {
		if (hierarchy != null && !hierarchy.isEmpty()) {
			String orgUnitStr = orgUnitRole.name() + ":";
			for (int i = 0; i < hierarchy.size(); i++) {
				orgUnitStr += hierarchy.get(i);
				if (i < hierarchy.size()-1) {
					orgUnitStr += ",";
				}
			}
			return orgUnitStr;
		}
		return null;
	}
	
	public String getHierarchyStr() {
		if (hierarchy != null && !hierarchy.isEmpty()) {
			String orgUnitStr = "";
			for (int i = 0; i < hierarchy.size(); i++) {
				orgUnitStr += hierarchy.get(i);
			}
			return orgUnitStr;
		}
		return null;
	}
	
	public ArrayList<String> getHierarchy() {
		return hierarchy;
	}
	
	

}

