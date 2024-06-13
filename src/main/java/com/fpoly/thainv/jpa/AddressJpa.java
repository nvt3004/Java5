package com.fpoly.thainv.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.Addresses;

public interface AddressJpa extends JpaRepository<Addresses, String>{
	
}
