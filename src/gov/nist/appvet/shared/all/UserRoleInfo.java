package gov.nist.appvet.shared.all;

import java.util.ArrayList;

/**
 * @author steveq@nist.gov
 */
public class UserRoleInfo {

	private Role role;
	private ArrayList<OrgUnit> orgUnits = null;
	
	public UserRoleInfo(Role role) throws Exception {
		if (role == Role.ADMIN || role == Role.TOOL_PROVIDER || role == Role.USER_ANALYST) {
			this.role = role;
			orgUnits = new ArrayList<OrgUnit>();
		} else {
			throw new Exception("UserRoleInfo can only be set to role type ADMIN, TOOL_PROVIDER, or USER_ANALYST");
		}
	}
	
	public UserRoleInfo(Role role, ArrayList<OrgUnit> orgUnits) throws Exception {
		if (role == Role.ADMIN || role == Role.TOOL_PROVIDER || role == Role.USER_ANALYST) {
			this.role = role;
			this.orgUnits = orgUnits;
		} else {
			throw new Exception("UserRoleInfo can only be set to role type ADMIN, TOOL_PROVIDER, or USER_ANALYST");
		}
	}
	
	public UserRoleInfo(String userRoleInfoStr) throws Exception {
		if (userRoleInfoStr != null && !userRoleInfoStr.isEmpty()) {
			if (userRoleInfoStr.equals(Role.ADMIN.name())) {
				role = Role.ADMIN;
			} else if (userRoleInfoStr.equals(Role.TOOL_PROVIDER.name())) {
				role = Role.TOOL_PROVIDER;
			} else if (userRoleInfoStr.indexOf(Role.USER.name()) > -1 ||
					userRoleInfoStr.indexOf(Role.ANALYST.name()) > -1) {
				role = Role.USER_ANALYST;
				orgUnits = new ArrayList<OrgUnit>();
				System.out.println("UserRoleStr: " + userRoleInfoStr);
				String[] orgUnitsArray = userRoleInfoStr.split(";");
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
				throw new Exception("UserRoleInfo can only be set to role type ADMIN, TOOL_PROVIDER, or USER_ANALYST");
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
	
	@Override
	public String toString() {
		if (role == Role.ADMIN || role == Role.TOOL_PROVIDER) {
			return role.name();
		}
		// For role USER_ADMIN, we do not include the role in the returned
		// string. Instead, we includ the USER and ANALYST roles for each
		// org unit in orgUnits.
		String userInfoStr = "";
		for (int i = 0; i < orgUnits.size(); i++) {
			OrgUnit orgUnit = orgUnits.get(i);
			userInfoStr += orgUnit.getOrgUnitStr();
			if (i < orgUnits.size()-1) {
				userInfoStr += ";";
			}
		}
		return userInfoStr;
	}
	

}
