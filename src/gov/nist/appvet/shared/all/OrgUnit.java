package gov.nist.appvet.shared.all;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.user.client.rpc.IsSerializable;

/** This class defines an org unit associated with a user and the role the user plays in this org unit.
 * @author steveq@nist.gov
 */
public class OrgUnit implements IsSerializable {
	
    public enum Role {
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
	
	/** Only USER and ANALYST are permitted for orgUnitRole.*/
    private Role orgUnitRole = null;
	/** A hierarchy is an array of top-down organizational levels that form an
	 * organizational unit (e.g., agency, department, group, and team).
	 */
    private ArrayList<String> hierarchy = null;
	
	/** Empty constructor required by GWT compiler but should not be used by AppVet.*/
	public OrgUnit() {
	}
	
	public OrgUnit(Role orgUnitRole) {
		this.orgUnitRole = orgUnitRole;
		hierarchy = new ArrayList<String>();
	}
	
	public OrgUnit(Role orgUnitRole, ArrayList<String> hierarchy) {
		this.orgUnitRole = orgUnitRole;
		this.hierarchy = hierarchy;
	}
	
	/** This method is used to create an OrgUnit from the stored database
	 * representation.*/
	public OrgUnit(String databaseStr) throws Exception {
		if (databaseStr == null || databaseStr.isEmpty()) {
			throw new Exception("OrgUnit cannot be created with null or empty org unit string.");
		}
		String[] roleAndHiearchy = databaseStr.split(":");
		if (roleAndHiearchy == null) {
			throw new Exception("Role and hierarchy string must contain colon to separate role and hierarchy.");
		} else if (roleAndHiearchy.length != 2) {
			throw new Exception("Role and hierarchy string must contain exactly two elements.");
		}
		String role = roleAndHiearchy[0];
		String hierarchyStr = roleAndHiearchy[1];
		String[] orgLevels = hierarchyStr.split(",");
		if (role.equals(Role.USER.name())) {
			orgUnitRole = Role.USER;
			if (orgLevels.length != 4) {
				throw new Exception("Org unit hierarchy must have four defined levels for USER role.");
			}
		} else if (role.equals(Role.ANALYST.name())) {
			orgUnitRole = Role.ANALYST;

			if (orgLevels.length == 0 && orgLevels.length > 4) {
				throw new Exception("Org unit hierarchy must have between 1 and 4 levels for ANALYST role.");
			}
		} else {
			throw new Exception("OrgUnit can only have role type of USER or ANALYST");
		}
		hierarchy = new ArrayList<String>(Arrays.asList(orgLevels));
//		for (int i = 0; i < hierarchy.size(); i++) {
//			System.out.println("level: " + hierarchy.get(i));
//		}
	}
	
	public Role getOrgUnitRole() {
		return orgUnitRole;
	}
	
	public ArrayList<String> getHierarchy() {
		return hierarchy;
	} 
	
	/** This method gets the database representation of this org unit.*/
	public String getDbString() {
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
	
	public String getDisplayString() {
		String displayStr = orgUnitRole.name() + ": ";
		for (int i = 0; i < hierarchy.size(); i++) {
			displayStr += hierarchy.get(i);
			if (i < hierarchy.size()-1) {
				displayStr += ", ";
			}
		}
		return displayStr;
	}
}

