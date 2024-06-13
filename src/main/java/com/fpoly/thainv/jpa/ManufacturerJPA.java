package com.fpoly.thainv.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.Manufacturers;

public interface ManufacturerJPA extends JpaRepository<Manufacturers, String> {

}
