package com.fpoly.thainv.tholh.JPA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.thainv.entities.AttributeProduct;

public interface AttributeProductJPATho extends JpaRepository<AttributeProduct, String>{

}
