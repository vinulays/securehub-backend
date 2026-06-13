package org.securehub.userservice.enums;

import java.util.List;
import java.util.Map;

public class UserRolePermissionMapping {

    public static final Map<String, List<UserPermission>> ROLE_PERMISSIONS = Map.of(
            "ADMIN", List.of(
                    UserPermission.USER_READ,
                    UserPermission.USER_CREATE,
                    UserPermission.USER_UPDATE,
                    UserPermission.USER_DELETE
            ),

            "USER", List.of(
                    UserPermission.USER_READ
            )
    );
}
