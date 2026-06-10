package org.securehub.userservice.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.securehub.userservice.dto.UserSearchRequest;
import org.securehub.userservice.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> search(UserSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.keyword() != null && !request.keyword().isBlank()) {
                String pattern = "%" + request.keyword().trim().toLowerCase() + "%";

                Predicate firstName = cb.like(cb.lower(root.get("firstName")), pattern);
                Predicate lastName = cb.like(cb.lower(root.get("lastName")), pattern);
                Predicate email = cb.like(cb.lower(root.get("email")), pattern);

                predicates.add(cb.or(firstName, lastName, email));
            }

            if (request.isActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.isActive()));
            }

//            if (request.organizationIds() != null && !request.organizationIds().isEmpty()) {
//                Join<User, OrganizationMembership> memberships = root.join("memberships", JoinType.LEFT);
//
//                predicates.add(memberships.get("organization").get("id").in(request.organizationIds()));
//
//                query.distinct(true);
//            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
