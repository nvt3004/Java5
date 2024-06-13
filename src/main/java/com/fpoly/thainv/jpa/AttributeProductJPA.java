package com.fpoly.thainv.jpa;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.thainv.entities.AttributeProduct;

@Repository
public interface AttributeProductJPA extends JpaRepository<AttributeProduct, String> {

	@Query(value = "SELECT o FROM AttributeProduct o WHERE o.products.productId =:idProduct")
	public List<AttributeProduct> findAttributeByProductId(int idProduct, Sort sort);

	List<AttributeProduct> findByProductsProductId(Integer productId);
	@Query("SELECT ap FROM AttributeProduct ap WHERE ap.products.productId = :productId AND ap.attributes.attributeId IN (:colorId, :sizeId)")
    List<AttributeProduct> findByProductIdAndAttributeIds(@Param("productId") int productId, @Param("colorId") int colorId, @Param("sizeId") int sizeId);
}
