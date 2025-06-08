package com.sasip.quizz.dto;

public class CreatePermissionRequest {

    private String permissionName;

    public CreatePermissionRequest() {
    }

    public CreatePermissionRequest(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
}
