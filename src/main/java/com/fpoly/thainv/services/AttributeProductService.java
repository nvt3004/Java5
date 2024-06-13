package com.fpoly.thainv.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.thainv.entities.AttributeProduct;
import com.fpoly.thainv.jpa.AttributeProductJPA;

@Service
public class AttributeProductService {
	 @Autowired
	    private AttributeProductJPA attributeProductJpa;

	    public List<AttributeProduct> findByProductIdAndAttributeIds(int productId, int colorId, int sizeId) {
	        return attributeProductJpa.findByProductIdAndAttributeIds(productId, colorId, sizeId);
	    }

	    public void save(AttributeProduct attributeProduct) {
	    	attributeProductJpa.save(attributeProduct);
	    }
}
