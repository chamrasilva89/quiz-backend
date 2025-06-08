package com.sasip.quizz.dto;

public class UpdatePermissionRequest {

    private Long permissionId;
    private String permissionName;

    public UpdatePermissionRequest() {
    }

    public UpdatePermissionRequest(Long permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }

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
