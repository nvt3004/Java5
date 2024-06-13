package com.fpoly.thainv.tholh.JPA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fpoly.thainv.entities.Products;

@Repository
public interface ProductJPATho extends JpaRepository<Products, String>{
    
}
