package com.fpoly.thainv.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.thainv.entities.Products;

@Repository
public interface ProductJPA extends JpaRepository<Products, String> {

	@Query(value = "SELECT * FROM products o WHERE o.is_deleted =:status ORDER BY o.product_id DESC", nativeQuery = true)
	public List<Products> findAllByStatus(int status);

	@Query("SELECT p FROM Products p WHERE p.isDeleted = :status AND p.productName LIKE:pdName ORDER BY p.id DESC")
	Page<Products> selectAllByStatus(@Param("status") boolean status,@Param("pdName") String pdName, Pageable pageable);
	
	@Query("SELECT p FROM Products p WHERE p.isDeleted = :status AND p.productName LIKE:pdName AND p.categories.categoryId =:idCat ORDER BY p.id DESC")
	Page<Products> selectAllByStatus(@Param("status") boolean status,@Param("pdName") String pdName,@Param("idCat") int idCat, Pageable pageable);
}
