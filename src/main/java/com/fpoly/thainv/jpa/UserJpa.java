package com.fpoly.thainv.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fpoly.thainv.entities.Users;

public interface UserJpa extends JpaRepository<Users, String>, JpaSpecificationExecutor <Users> {
	Optional<Users> findByEmail(String email);
}
