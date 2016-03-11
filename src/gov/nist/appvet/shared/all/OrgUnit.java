package gov.nist.appvet.shared.all;

import java.util.ArrayList;
import java.util.Arrays;

/** This class defines a group as a Role.USER or Role.ANALYST within an 
 * organizational unit.
 * @author steveq@nist.gov
 */
public class OrgUnit {
	
	/** Only Role.USER and Role.ANALYST are permitted for groupRole.*/
	public Role orgUnitRole = null;
	/** A hierarchy is an array of organizational units from a top level 
	 * (level 1) to a depth of up to four levels.
	 */
	public ArrayList<String> hierarchy = null;
	
	public OrgUnit(Role orgUnitRole) {
		if (orgUnitRole == Role.USER || orgUnitRole == Role.ANALYST) {
			this.orgUnitRole = orgUnitRole;
		}
	}
	
	public OrgUnit(Role orgUnitRole, ArrayList<String> hierarchy) {
		if (orgUnitRole == Role.USER || orgUnitRole == Role.ANALYST) {
			this.orgUnitRole = orgUnitRole;
			this.hierarchy = hierarchy;
		}
	}
	
	/** Set group as a string of the form: 
	 * [USER|ANALYST]:item1,...,item4 (max) 
	 * This method is used to create an OrgUnit from the stored database
	 * representation.*/
	public OrgUnit(String orgUnitStr) {
		if (orgUnitStr == null || orgUnitStr.isEmpty()) {
			return;
		}
		String[] roleAndHiearchy = orgUnitStr.split(":");
		String role = roleAndHiearchy[0];
		if (role.equals(Role.USER.name())) {
			orgUnitRole = Role.USER;
			String hierarchyStr = roleAndHiearchy[1];
			String[] orgLevels = hierarchyStr.split(",");
			if (orgLevels.length != 4) {
				return;
			}
			hierarchy = new ArrayList<String>(Arrays.asList(orgLevels));
		} else if (role.equals(Role.ANALYST.name())) {
			orgUnitRole = Role.ANALYST;
			String hierarchyStr = roleAndHiearchy[1];
			String[] orgLevels = hierarchyStr.split(",");
			if (orgLevels.length == 0 && orgLevels.length > 4) {
				return;
			}
			hierarchy = new ArrayList<String>(Arrays.asList(orgLevels));
		} else {
			return;
		}
	}
	
	public String getOrgUnitStr() {
		if (hierarchy != null && !hierarchy.isEmpty()) {
			String orgUnitStr = orgUnitRole.name() + ":";
			for (int i = 0; i < hierarchy.size(); i++) {
				orgUnitStr += hierarchy.get(i);
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

