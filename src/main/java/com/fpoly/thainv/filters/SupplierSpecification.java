package com.fpoly.thainv.filters;

import org.springframework.data.jpa.domain.Specification;

import com.fpoly.thainv.entities.Addresses;
import com.fpoly.thainv.entities.Suppliers;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class SupplierSpecification {

	public static Specification<Suppliers> filterByKeyword(String keyword, Boolean isDeleted) {
		return (root, query, criteriaBuilder) -> {
			Predicate predicate = criteriaBuilder.conjunction();

			// Filter by keyword in supplier fields
			if (keyword != null && !keyword.isEmpty()) {
				String likePattern = "%" + keyword.toLowerCase() + "%";

				Predicate supplierNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("supplierName")),
						likePattern);
				Predicate contactNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("contactName")),
						likePattern);
				Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern);
				Predicate phonePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern);

				predicate = criteriaBuilder.or(supplierNamePredicate, contactNamePredicate, emailPredicate,
						phonePredicate);

				// Join with Addresses and filter by address fields
				Join<Suppliers, Addresses> addressJoin = root.join("addresses");
				Predicate addressLine1Predicate = criteriaBuilder
						.like(criteriaBuilder.lower(addressJoin.get("addressLine1")), likePattern);
				Predicate addressLine2Predicate = criteriaBuilder
						.like(criteriaBuilder.lower(addressJoin.get("addressLine2")), likePattern);
				Predicate cityPredicate = criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("city")),
						likePattern);
				Predicate statePredicate = criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("state")),
						likePattern);
				Predicate countryPredicate = criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("country")),
						likePattern);
				Predicate postalCodePredicate = criteriaBuilder
						.like(criteriaBuilder.lower(addressJoin.get("postalCode")), likePattern);

				predicate = criteriaBuilder.or(predicate, addressLine1Predicate, addressLine2Predicate, cityPredicate,
						statePredicate, countryPredicate, postalCodePredicate);
			}

			// Filter by isDeleted
			if (isDeleted != null) {
				predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isDeleted"), isDeleted));
			}

			return predicate;
		};

	}

}
