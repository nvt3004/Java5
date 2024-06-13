package com.fpoly.thainv.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpoly.thainv.entities.LoginHistory;

@Repository
public interface LoginHistoryJpa extends JpaRepository<LoginHistory, Integer> {
}

