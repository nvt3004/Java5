package com.fpoly.thainv.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions", schema = "dbo")
public class Permissions implements Serializable {

    private Integer permissionId;
    private String permissionName;
    private Set<Users> users = new HashSet<>();

    public Permissions() {}

    public Permissions(String permissionName) {
        this.permissionName = permissionName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id", unique = true, nullable = false)
    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    @Column(name = "permission_name", nullable = false, length = 50)
    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "manage_permissions",
        joinColumns = @JoinColumn(name = "permission_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    public Set<Users> getUsers() {
        return users;
    }

    public void setUsers(Set<Users> users) {
        this.users = users;
    }
}