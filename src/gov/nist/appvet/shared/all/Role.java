package gov.nist.appvet.shared.all;

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
	USER_ANALYST,
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
