package com.fpoly.thainv.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpoly.thainv.entities.Users;

/**
 * @author nhanprogrammer
 */
public interface AuthJpa extends JpaRepository<Users, String> {

	Optional<Users> findByEmail(String email);
    Optional<Users> findByPhone(String phone); 
}