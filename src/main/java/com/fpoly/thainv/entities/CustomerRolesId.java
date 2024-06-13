package com.fpoly.thainv.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CustomerRolesId implements Serializable {

	private int userId;
	private int roleId;

	public CustomerRolesId() {
	}

	public CustomerRolesId(int userId, int roleId) {
		this.userId = userId;
		this.roleId = roleId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CustomerRolesId that = (CustomerRolesId) o;
		return userId == that.userId && roleId == that.roleId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, roleId);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
}
