package org.securehub.organizationservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.securehub.organizationservice.dto.OrganizationSearchRequest;
import org.securehub.organizationservice.entity.Organization;
import org.securehub.organizationservice.enums.OrganizationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrganizationSpecification {

    public static Specification<Organization> search(OrganizationSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.keyword() != null && !request.keyword().isBlank()) {
                String pattern = "%" + request.keyword().trim().toLowerCase() + "%";

                Predicate name = cb.like(cb.lower(root.get("name")), pattern);
                Predicate slug = cb.like(cb.lower(root.get("slug")), pattern);

                predicates.add(cb.or(name, slug));
            }

            if (request.isActive() != null) {
                OrganizationStatus status = request.isActive()
                        ? OrganizationStatus.ACTIVE
                        : OrganizationStatus.INACTIVE;

                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };
    }
}
