package com.fpoly.thainv.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fpoly.thainv.entities.Categories;

public interface CategoryJPA extends JpaRepository<Categories, String> {
	@Query("SELECT o FROM Categories o WHERE o.categoryName LIKE:catName")
	public Categories findCategoryByName(String catName);
}
