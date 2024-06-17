package com.fpoly.thainv.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fpoly.thainv.jpa.ProductJPA;

@Service
public class ProductService {
    @Autowired
    private ProductJPA productRepository;

    @Transactional
    public void updateQuantityByProductIdAndAttributeId(int productId, int AttributeId, int newQuantity) {
        productRepository.updateQuantityByProductIdAndAttributeId(productId, AttributeId, newQuantity);
    }

}
