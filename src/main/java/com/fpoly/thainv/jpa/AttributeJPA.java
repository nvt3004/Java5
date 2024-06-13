package com.fpoly.thainv.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fpoly.thainv.entities.Attributes;

public interface AttributeJPA extends JpaRepository<Attributes, String> {
//	private Integer attributeId;
//	private String attributeKey;
//	private String value;
	@Query("SELECT o FROM Attributes o WHERE o.attributeKey LIKE:attributeKey AND o.value LIKE:attributeValue")
	public Attributes findAttributeByKeyAndValue(String attributeKey, String attributeValue);
}
