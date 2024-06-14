package com.fpoly.thainv.filters;

import org.springframework.data.jpa.domain.Specification;

import com.fpoly.thainv.entities.Addresses;
import com.fpoly.thainv.entities.CustomerRoles;
import com.fpoly.thainv.entities.Users;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class UserSpecification {

    public static Specification<Users> filterUsers(String keyword, Integer roleId, Boolean isDeleted) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (keyword != null && !keyword.isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                // Filter by keyword in user fields
                Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likePattern);
                Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likePattern);
                Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern);
                Predicate phonePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern);

                // Join with Addresses and filter by address fields
                Join<Users, Addresses> addressJoin = root.join("address");
                Predicate cityPredicate = criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("city")), likePattern);
                Predicate countryPredicate = criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("country")), likePattern);

                // Combine all predicates
                Predicate combinedPredicate = criteriaBuilder.or(firstNamePredicate, lastNamePredicate, emailPredicate, phonePredicate, cityPredicate, countryPredicate);
                predicate = criteriaBuilder.and(predicate, combinedPredicate);
            }

            // Filter by role
            if (roleId != null) {
                Join<Users, CustomerRoles> customerRolesJoin = root.join("customerRoles");
                Predicate rolePredicate = criteriaBuilder.equal(customerRolesJoin.get("role").get("roleId"), roleId);
                predicate = criteriaBuilder.and(predicate, rolePredicate);
            }

            // Filter by isDeleted
            if (isDeleted != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isDeleted"), isDeleted));
            }

            return predicate;
        };
    }
}
