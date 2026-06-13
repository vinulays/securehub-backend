package org.securehub.organizationservice.enums;

import java.util.List;
import java.util.Map;

public class OrganizationRolePermissionMapping {

    public static final Map<OrganizationRole, List<OrganizationPermission>> ROLE_PERMISSIONS = Map.of(
            OrganizationRole.OWNER,
            List.of(
                    OrganizationPermission.MEMBER_VIEW,
                    OrganizationPermission.MEMBER_CREATE,
                    OrganizationPermission.MEMBER_UPDATE,
                    OrganizationPermission.MEMBER_DELETE
            ),

            OrganizationRole.ADMIN,
            List.of(
                    OrganizationPermission.MEMBER_VIEW,
                    OrganizationPermission.MEMBER_CREATE,
                    OrganizationPermission.MEMBER_UPDATE
            ),

            OrganizationRole.MEMBER,
            List.of(
                    OrganizationPermission.MEMBER_VIEW
            )
    );
}
