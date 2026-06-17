package com.example.fishlsb.dto;
import java.util.List;

public class CommandRequest {
    private String deviceId;
    private Double targetValue;

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Double getTargetValue() { return targetValue; }
    public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }
}