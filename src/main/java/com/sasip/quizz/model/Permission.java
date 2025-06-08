package com.sasip.quizz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long permissionId;

    @Column(name = "permission_name", nullable = false, unique = true)
    private String permissionName;

    // Getter for permissionId
    public Long getPermissionId() {
        return permissionId;
    }

    // Setter for permissionId
    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    // Getter for permissionName
    public String getPermissionName() {
        return permissionName;
    }

    // Setter for permissionName
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
}
