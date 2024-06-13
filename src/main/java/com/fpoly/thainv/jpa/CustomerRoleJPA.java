package com.fpoly.thainv.jpa;

import com.fpoly.thainv.entities.CustomerRoles;
import com.fpoly.thainv.entities.CustomerRolesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRoleJPA extends JpaRepository<CustomerRoles, CustomerRolesId> {
}
