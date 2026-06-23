package com.example.fishlsb.dto;

public class DeviceInitResponse {
    private String deviceId;
    private String entityName;
    private Double longitude;
    private Double latitude;
    private Double initialValue;
    private String status;
    private String dynamicPropertyName;

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getInitialValue() { return initialValue; }
    public void setInitialValue(Double initialValue) { this.initialValue = initialValue; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDynamicPropertyName() { return dynamicPropertyName; }
    public void setDynamicPropertyName(String dynamicPropertyName) { this.dynamicPropertyName = dynamicPropertyName; }
}