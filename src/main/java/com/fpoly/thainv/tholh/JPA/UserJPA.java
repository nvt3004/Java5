package com.fpoly.thainv.tholh.JPA;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpoly.thainv.entities.Users;

public interface UserJPA extends JpaRepository<Users, String>{
	@Query("select u from Users u where u.email = :uEmail")
    public Optional<Users> findUserByEmail(@Param("uEmail") String email);
}
