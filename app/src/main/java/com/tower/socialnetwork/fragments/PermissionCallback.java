package com.tower.socialnetwork.fragments;

public interface PermissionCallback {
    void permissionGrantedCallback(int requestCode, String permissions[], int[] grantResults);
}
