package com.example.fishlsb.dto;

import java.util.List;

public class InitRequest {
    private List<String> deviceIds;

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }
}