package gov.nist.appvet.test;

import java.util.ArrayList;

import gov.nist.appvet.shared.all.Role;

public class RoleTester {
	
	public void testIt() {
/*		try{
			String roleStr = "ADMIN";
			Role role = Role.getRole(roleStr);
			System.out.println("Test 1: Role role: :" + role.name());
			
//			role = Role.getRole(null);
//			System.out.println("Test 2: Role role: :" + role.name());	

			roleStr = "ANALYST:test";
			role = Role.getRole(roleStr);
			System.out.println("Test 3: Role role: :" + role.name());
			
			roleStr = "USER: test, test2 ,test3 ";
			role = Role.getRole(roleStr);
			ArrayList<String> hierarchy = Role.getUserOrgMembershipLevelsArray(roleStr);
			System.out.println("Test 4: Role role: :" + role.name());
			for (int i = 0; i < hierarchy.size(); i++) {
				System.out.println("level: " + hierarchy.get(i));
			}
			String hierarchyStr = Role.getUserOrgMembershipLevelsStr(roleStr);
			System.out.println("hierarchyStr: " + hierarchyStr);
			
//			String dbStr = Role.getDbStr(Role.USER, hierarchy);
//			System.out.println("dbStr: " + dbStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
	}

	public static void main(String[] args) {
		RoleTester test = new RoleTester();
		test.testIt();
	}
}
