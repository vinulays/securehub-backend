package org.securehub.userservice.organization.entity;

import jakarta.persistence.*;
import lombok.*;
import org.securehub.userservice.common.entity.BaseEntity;
import org.securehub.userservice.membership.entity.OrganizationMembership;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<OrganizationMembership> memberships = new ArrayList<>();
}
