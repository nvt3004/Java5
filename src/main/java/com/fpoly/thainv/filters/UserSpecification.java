package com.fpoly.thainv.filters;

import org.springframework.data.jpa.domain.Specification;

import com.fpoly.thainv.entities.CustomerRoles;
import com.fpoly.thainv.entities.Users;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class UserSpecification {

    public static Specification<Users> filterUsers(String name, String email, String phone, Integer roleId, Boolean isDeleted) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            // Filter by name
            if (name != null && !name.isEmpty()) {
                Predicate firstNamePredicate = criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("firstName")), "%" + name.toLowerCase() + "%");
                Predicate lastNamePredicate = criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("lastName")), "%" + name.toLowerCase() + "%");
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(firstNamePredicate, lastNamePredicate));
            }

            // Filter by email
            if (email != null && !email.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            // Filter by phone
            if (phone != null && !phone.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("phone")), "%" + phone.toLowerCase() + "%"));
            }

            // Filter by role
            if (roleId != null) {
                Join<Users, CustomerRoles> customerRolesJoin = root.join("customerRoles");
                Predicate rolePredicate = criteriaBuilder.equal(customerRolesJoin.get("role").get("roleId"), roleId);
                predicate = criteriaBuilder.and(predicate, rolePredicate);
            }

            // Filter by isDeleted
            if (isDeleted != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder
                        .equal(root.get("isDeleted"), isDeleted));
            }

            return predicate;
        };
    }
}
