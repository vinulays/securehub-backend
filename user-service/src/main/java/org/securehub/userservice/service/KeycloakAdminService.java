package org.securehub.userservice.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.securehub.userservice.enums.UserRole;
import org.securehub.userservice.exception.KeycloakException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final Keycloak keycloak;

    @Value("${keycloak.admin.realm}")
    private String realmName;

    public String createUser(
            String email,
            String firstName,
            String lastName,
            UserRole roleName
    ) {
        UserRepresentation user = new UserRepresentation();

        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        user.setEnabled(true);
        user.setEmailVerified(false);

        Response response = keycloak.realm(realmName).users().create(user);

        if (response.getStatus() != 201) {
            throw new KeycloakException("Failed to create Keycloak User");
        }

        String keycloakUserId = CreatedResponseUtil.getCreatedId(response);

        assignRealmRole(keycloakUserId, roleName.toString());

        return keycloakUserId;
    }

    public void assignRealmRole(
            String userId,
            String roleName
    ) {

        RealmResource realm =
                keycloak.realm(realmName);

        RoleRepresentation role =
                realm.roles()
                        .get(roleName)
                        .toRepresentation();

        realm.users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(role));
    }

    public void setPassword(String keycloakUserId, String password) {

        CredentialRepresentation credential = new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);

        credential.setValue(password);

        credential.setTemporary(false);

        keycloak.realm(realmName)
                .users()
                .get(keycloakUserId)
                .resetPassword(credential);
    }

    public void markEmailAsVerified(String keycloakUserId) {

        UserRepresentation user = new UserRepresentation();

        user.setEmailVerified(true);

        keycloak.realm(realmName)
                .users()
                .get(keycloakUserId)
                .update(user);
    }

    public void updateUserStatus(String keycloakUserId, boolean enable) {

        UserRepresentation user = new UserRepresentation();

        user.setEnabled(enable);

        keycloak.realm(realmName)
                .users()
                .get(keycloakUserId)
                .update(user);
    }

    public void updateUserAttribute(String keycloakUserId, UUID userId, String attributeName) {

        UserResource userResource = keycloak.realm(realmName)
                .users()
                .get(keycloakUserId);

        UserRepresentation user = userResource.toRepresentation();

        Map<String, List<String>> attributes = user.getAttributes();

        if (attributes == null) {
            attributes = new HashMap<>();
        }

        attributes.put(attributeName, List.of(userId.toString()));

        user.setAttributes(attributes);

        userResource.update(user);
    }

}
