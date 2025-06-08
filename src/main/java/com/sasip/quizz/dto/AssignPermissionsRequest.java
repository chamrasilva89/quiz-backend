package com.sasip.quizz.dto;

import java.util.List;

public class AssignPermissionsRequest {

    private List<Long> permissionIds;

    public AssignPermissionsRequest() {}

    public AssignPermissionsRequest(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
