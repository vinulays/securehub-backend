package org.securehub.userservice.enums;

import java.util.List;
import java.util.Map;

public class RolePermissionMapping {

    public static final Map<String, List<Permission>> ROLE_PERMISSIONS = Map.of(
            "ADMIN", List.of(
                    Permission.USER_READ,
                    Permission.USER_CREATE,
                    Permission.USER_UPDATE,
                    Permission.USER_DELETE
            ),

            "USER", List.of(
                    Permission.USER_READ
            )
    );
}
