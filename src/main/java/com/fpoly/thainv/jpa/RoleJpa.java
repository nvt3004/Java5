package com.fpoly.thainv.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.Roles;

public interface RoleJpa extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByRoleName(String roleName);
}