package com.fpoly.thainv.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customer_roles", schema = "dbo")
public class CustomerRoles implements Serializable {

    @EmbeddedId
    private CustomerRolesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Roles role;

    public CustomerRoles() {}

    public CustomerRoles(Users user, Roles role) {
        this.user = user;
        this.role = role;
        this.id = new CustomerRolesId(user.getUserId(), role.getRoleId());
    }

    public CustomerRolesId getId() {
        return id;
    }

    public void setId(CustomerRolesId id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
}
