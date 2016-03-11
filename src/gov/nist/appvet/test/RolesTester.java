package gov.nist.appvet.test;

import gov.nist.appvet.shared.all.Role;
import gov.nist.appvet.shared.all.UserRoleInfo;

public class RolesTester {

	public void setRoles() {
		UserRoleInfo roles = new UserRoleInfo(Role.USER_ANALYST);
		System.out.println("roles.getRole(): " + roles.getRole());
		roles.addUserGroup("Dept of Homeland Security, OCIO, DDD");
		
	}

	public static void main(String[] args) {
		RolesTester test = new RolesTester();
		test.setRoles();
		
	}
}
