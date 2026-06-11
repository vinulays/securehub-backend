package org.securehub.organizationservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.securehub.organizationservice.dto.MembershipSearchRequest;
import org.securehub.organizationservice.entity.OrganizationMembership;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MembershipSpecification {

    public static Specification<OrganizationMembership> search(
            UUID organizationId, MembershipSearchRequest request, List<UUID> userIds
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("organization").get("id"), organizationId));

            if (userIds != null) {
                if (userIds.isEmpty()) {
                    // Force no results
                    predicates.add(cb.disjunction());
                } else {
                    predicates.add(root.get("userId").in(userIds));
                }
            }

            if (request.role() != null) {
                predicates.add(cb.equal(root.get("role"), request.role()));
            }

            if (request.isActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.isActive()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
