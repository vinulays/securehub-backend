package org.securehub.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String keycloakUserId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(columnDefinition = "boolean default true")
    private Boolean isActive;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<OrganizationMembership> memberships = new ArrayList<>();
}
