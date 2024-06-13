package com.fpoly.thainv.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.Images;

public interface ImageJPA extends JpaRepository<Images, String> {

}
