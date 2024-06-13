package com.fpoly.thainv.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.thainv.entities.Attributes;
import com.fpoly.thainv.jpa.AttributeJPA;

@Service
public class AttributesService {

    @Autowired
    private AttributeJPA attributesJpa;

    public Optional<Attributes> findById(Integer id) {
        return attributesJpa.findById(String.valueOf(id));
    }
}
