package gov.nist.appvet.test;

import java.util.ArrayList;

import gov.nist.appvet.shared.all.OrgUnit;
import gov.nist.appvet.shared.all.UserRoleInfo;

public class RoleTester {

	public void setRoles() {
		
		//----------------------------------------------------------
		UserRoleInfo userRoleInfo1 = null;
		try {
			userRoleInfo1 = new UserRoleInfo(UserRoleInfo.Role.ADMIN);
			System.out.println("roles.getRole(): " + userRoleInfo1.getRole());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//----------------------------------------------------------
		ArrayList<OrgUnit> orgUnits = userRoleInfo1.getOrgUnits();
		if (orgUnits.isEmpty()) 
			System.out.println("orgUnits is empty");
		else
			System.out.println("orgUnits is good");
		
		//----------------------------------------------------------
		OrgUnit orgUnit1;
		try {
			orgUnit1 = new OrgUnit(OrgUnit.Role.ANALYST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//----------------------------------------------------------
		try {
			orgUnit1 = new OrgUnit(OrgUnit.Role.USER);
			if (orgUnits == null) {
				orgUnits = new ArrayList<OrgUnit>();
			}
			orgUnits.add(orgUnit1);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//----------------------------------------------------------
		ArrayList<OrgUnit> orgUnits1 = userRoleInfo1.getOrgUnits();
		if (orgUnits1 == null) 
			System.out.println("orgUnits1 is null");
		else {
			System.out.println("orgUnits1 is good");
			for (int i = 0; i < orgUnits1.size(); i++) {
				System.out.println(i + ": " + orgUnits1.get(i));
			}
		}
		
		//----------------------------------------------------------
		ArrayList<OrgUnit> orgUnits2 = userRoleInfo1.getOrgUnits();
		try {
			OrgUnit orgUnit2 = new OrgUnit(OrgUnit.Role.USER);
			orgUnits2.add(orgUnit2);
			ArrayList<OrgUnit> orgUnits3 = userRoleInfo1.getOrgUnits();
			if (orgUnits3 == null) 
				System.out.println("orgUnits3 is empty");
			else {
				System.out.println("orgUnits3 is good");
				for (int i = 0; i < orgUnits3.size(); i++) {
					System.out.println("orgUnits3 role: " + orgUnits3.get(i).getOrgUnitRole());
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//----------------------------------------------------------
		UserRoleInfo userRoleInfo2 = null;
		try {
			userRoleInfo2 = new UserRoleInfo("ADMIN");
			System.out.println("userRoleInfo2.getRole(): " + userRoleInfo2.getRole());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		UserRoleInfo userRoleInfo3 = null;
		try {
			userRoleInfo3 = new UserRoleInfo("USER:My Org,My-Dept,MyLab,My Project;ANALYST:My Org2,MyDept2,My Lab2,My Proj2");
			System.out.println("userRoleInfo3.getRole(): " + userRoleInfo3.getRole());
			ArrayList<OrgUnit> orgUnits4 = userRoleInfo3.getOrgUnits();
			for (int i = 0; i < orgUnits4.size(); i++) {
				OrgUnit orgUnit4 = orgUnits4.get(i);
				System.out.println("orgUnit4 role " + i + ": " + orgUnit4.getOrgUnitRole());
				for (int j = 0; j < orgUnit4.getHierarchy().size(); j++) {
					String levelA = orgUnit4.getHierarchy().get(j);
					System.out.println("levelA: " + levelA);
				}
			}
			
			//----------------------------------------------------------
			// Add new org unit
			ArrayList<String> levels5 = new ArrayList<String>();
			levels5.add("Fun Org");
			levels5.add("Fun Dept");
			levels5.add("Fun Lab");
			levels5.add("Fun Division");
			
			OrgUnit orgUnit5 = new OrgUnit(OrgUnit.Role.USER, levels5);
			
			userRoleInfo3.addOrgUnit(orgUnit5);
			
			ArrayList<OrgUnit> orgUnits5 = userRoleInfo3.getOrgUnits();
			for (int i = 0; i < orgUnits5.size(); i++) {
				OrgUnit orgUnit6 = orgUnits5.get(i);
				System.out.println("orgUnit6 role " + i + ": " + orgUnit6.getOrgUnitRole());
				for (int j = 0; j < orgUnit6.getHierarchy().size(); j++) {
					String levelB = orgUnit6.getHierarchy().get(j);
					System.out.println("levelB : " + j + ": " + levelB);
				}
			}
			
			//----------------------------------------------------------
			// Remove an org unit
			userRoleInfo3.removeOrgUnit(1);
			ArrayList<OrgUnit> orgUnits6 = userRoleInfo3.getOrgUnits();
			for (int i = 0; i < orgUnits6.size(); i++) {
				OrgUnit orgUnit7 = orgUnits6.get(i);
				System.out.println("orgUnit6 role " + i + ": " + orgUnit7.getOrgUnitRole());
				for (int j = 0; j < orgUnit7.getHierarchy().size(); j++) {
					String levelC = orgUnit7.getHierarchy().get(j);
					System.out.println("levelC : " + j + ": " + levelC);
				}
			}
			
			//----------------------------------------------------------
			// Overwrite an org unit
			ArrayList<String> levels6 = new ArrayList<String>();
			levels6.add("Very Fun Org");
			levels6.add("Very Fun Dept");
			levels6.add("Very Fun Lab");
			levels6.add("Very Fun Division");
			
			OrgUnit orgUnit8 = new OrgUnit(OrgUnit.Role.USER, levels6);
			
			userRoleInfo3.setOrgUnit(0, orgUnit8);
			
			ArrayList<OrgUnit> orgUnits8 = userRoleInfo3.getOrgUnits();
			for (int i = 0; i < orgUnits8.size(); i++) {
				OrgUnit orgUnit9 = orgUnits8.get(i);
				System.out.println("orgUnit9 role " + i + ": " + orgUnit9.getOrgUnitRole());
				for (int j = 0; j < orgUnit9.getHierarchy().size(); j++) {
					String levelD = orgUnit9.getHierarchy().get(j);
					System.out.println("levelD : " + j + ": " + levelD);
				}
			}
			
			
			// Get string to store in database
			String userRoleString1 = userRoleInfo3.getDbString();
			System.out.println("userRoleString1: " + userRoleString1);
			
			UserRoleInfo userRoleInfo4 = new UserRoleInfo(userRoleString1);
			System.out.println("userRoleInfo4.getRole(): " + userRoleInfo4.getRole());
			ArrayList<OrgUnit> orgUnits10 = userRoleInfo4.getOrgUnits();
			for (int i = 0; i < orgUnits10.size(); i++) {
				OrgUnit orgUnit10 = orgUnits10.get(i);
				System.out.println("orgUnit10 role " + i + ": " + orgUnit10.getOrgUnitRole());
				for (int j = 0; j < orgUnit10.getHierarchy().size(); j++) {
					String levelE = orgUnit10.getHierarchy().get(j);
					System.out.println("levelE: " + levelE);
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//----------------------------------------------------------

	}

	public static void main(String[] args) {
		RoleTester test = new RoleTester();
		test.setRoles();
		
	}
}
