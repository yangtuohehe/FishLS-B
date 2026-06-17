package com.example.fishlsb.dto;

import java.util.List;

public class ModeSwitchRequest {
    private String deviceId;
    private String mode;

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
}
