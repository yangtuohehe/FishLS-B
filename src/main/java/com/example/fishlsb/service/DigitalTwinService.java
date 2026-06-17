package com.example.fishlsb.service;

import com.example.fishlsb.entity.TwinEntity;
import com.example.fishlsb.mapper.TwinEntityMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class DigitalTwinService {

    private final TwinEntityMapper twinEntityMapper;
    private final MqttClient mqttClient;
    private final ConcurrentHashMap<String, LinkedBlockingQueue<Object>> dataCache = new ConcurrentHashMap<>();
    private final int CACHE_LIMIT = 48;

    public DigitalTwinService(TwinEntityMapper twinEntityMapper, MqttClient mqttClient) {
        this.twinEntityMapper = twinEntityMapper;
        this.mqttClient = mqttClient;
    }

    public List<TwinEntity> initializeTwins(List<String> deviceIds) {
        List<TwinEntity> entities = twinEntityMapper.findByDeviceIds(deviceIds);

        for (String deviceId : deviceIds) {
            dataCache.computeIfAbsent(deviceId, k -> new LinkedBlockingQueue<>(CACHE_LIMIT));
        }

        try {
            String handshakeMsg = "{\"request\": \"sync\"}";
            mqttClient.publish("digital_twin/handshake/request", new MqttMessage(handshakeMsg.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return entities;
    }
}