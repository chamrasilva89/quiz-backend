package com.sasip.quizz.dto;

public class PermissionDTO {

    private Long permissionId;
    private String permissionName;

    // Constructors
    public PermissionDTO() {}

    public PermissionDTO(Long permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }

    // Getters and Setters
    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
}
