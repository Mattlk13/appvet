package gov.nist.appvet.shared.all;

import java.util.ArrayList;

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
		// A USER_ANALYST is a role that indicates multiple USER and/or 
		// ANALYST roles for a user.
		USER_ANALYST;
		
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
	private ArrayList<OrgUnit> orgUnits = null;
	
	/** Empty constructor required by GWT but should not be used by AppVet.*/
	public UserRoleInfo() {
	}
	
	public UserRoleInfo(Role role) {
		this.role = role;
		orgUnits = new ArrayList<OrgUnit>();
	}
	
	public UserRoleInfo(Role role, ArrayList<OrgUnit> orgUnits) {
		this.role = role;
		this.orgUnits = orgUnits;
	}
	
	/** This method constructs a user role info object from its database
	 * string representation.
	 */
	public UserRoleInfo(String databaseStr) throws Exception {
		System.out.println("userRoleInfoStr: " + databaseStr);
		if (databaseStr != null && !databaseStr.isEmpty()) {
			if (databaseStr.equals(Role.ADMIN.name())) {
				role = Role.ADMIN;
			} else if (databaseStr.equals(Role.TOOL_PROVIDER.name())) {
				role = Role.TOOL_PROVIDER;
			} else if (databaseStr.indexOf(OrgUnit.Role.USER.name()) > -1 ||
					databaseStr.indexOf(OrgUnit.Role.ANALYST.name()) > -1) {
				// The database string representation does not contain the 
				// role USER_ANALYST but instead contains one or more
				// OrgUnit.Role.USER and/or OrgUnit.Role.ANALYST roles.
				role = Role.USER_ANALYST;
				orgUnits = new ArrayList<OrgUnit>();
				String[] orgUnitsArray = databaseStr.split(";");
				if (orgUnitsArray != null) {
					for (int i = 0; i < orgUnitsArray.length; i++) {
						OrgUnit orgUnit;
						try {
							orgUnit = new OrgUnit(orgUnitsArray[i]);
							if (orgUnit != null) {
								orgUnits.add(orgUnit);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}	
				} else {
					System.err.println("orgUnitsArray is null");
				}
			} else {
				throw new Exception("UserRoleInfo can only be set to role USER or ANALYST");
			}
		} else {
			throw new Exception("UserRoleInfo cannot be set with null or empty string.");
		}
	}
	
	public Role getRole() {
		return role;
	}
	
	public ArrayList<OrgUnit> getOrgUnits() {
		return orgUnits;
	}
	
	public void addOrgUnit(OrgUnit orgUnit) throws Exception {
		if (role == Role.USER_ANALYST) {
			orgUnits.add(orgUnit);
		} else {
			throw new Exception("Adding org units can only be invoked for role type USER_ANALYST, not role type " + role.name());
		}
	}
	
	public void setOrgUnit(int index, OrgUnit orgUnit) throws Exception {
		if (role == Role.USER_ANALYST) {
			orgUnits.set(index, orgUnit);
		} else {
			throw new Exception("Setting org units can only be invoked for role type USER_ANALYST, not role type " + role.name());
		}
	}
	
	public void removeOrgUnit(int index) throws Exception {
		if (role == Role.USER_ANALYST) {
			orgUnits.remove(index);
		} else {
			throw new Exception("Setting org units can only be invoked for role type USER_ANALYST, not role type " + role.name());
		}
	}
	
	/** This method gets the database representation of a user info object.*/
	public String getDbString() {
		if (role == Role.ADMIN || role == Role.TOOL_PROVIDER) {
			return role.name();
		}
		// For role USER_ADMIN, we do not include the role in the returned
		// string. Instead, we include the USER and/or ANALYST roles for each
		// org unit in orgUnits.
		String userInfoStr = "";
		for (int i = 0; i < orgUnits.size(); i++) {
			OrgUnit orgUnit = orgUnits.get(i);
			userInfoStr += orgUnit.getDbString();
			if (i < orgUnits.size()-1) {
				userInfoStr += ";";
			}
		}
		return userInfoStr;
	}
	

}
