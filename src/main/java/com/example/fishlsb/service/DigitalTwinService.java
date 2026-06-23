package com.example.fishlsb.service;

import com.example.fishlsb.dto.DeviceInitResponse;
import com.example.fishlsb.mapper.TwinEntityMapper;
import com.example.fishlsb.websocket.WebSocketServer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class DigitalTwinService implements MqttCallback {

    private final TwinEntityMapper twinEntityMapper;
    private MqttClient mqttClient;
    private final ConcurrentHashMap<String, LinkedBlockingQueue<Object>> dataCache = new ConcurrentHashMap<>();
    private final int CACHE_LIMIT = 48;
    private volatile CompletableFuture<String> handshakeFuture;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DigitalTwinService(TwinEntityMapper twinEntityMapper) {
        this.twinEntityMapper = twinEntityMapper;
        this.initializeMqttConnection();
    }

    private void initializeMqttConnection() {
        try {
            String broker = "tcp://broker.emqx.io:1883";
            String clientId = "spring_backend_" + UUID.randomUUID().toString();
            mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            mqttClient.setCallback(this);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);

            mqttClient.connect(options);
            mqttClient.subscribe("digital_twin/handshake/response");
            mqttClient.subscribe("digital_twin/water_quality/data");
            mqttClient.subscribe("digital_twin/valve/state");
        } catch (MqttException e) {
            System.out.println("底层网络通信初始化失败: " + e.getMessage());
        }
    }

    public List<DeviceInitResponse> initializeTwins(List<String> deviceIds) {
        List<DeviceInitResponse> responses = twinEntityMapper.findInitInfoByDeviceIds(deviceIds);

        for (String deviceId : deviceIds) {
            dataCache.computeIfAbsent(deviceId, k -> new LinkedBlockingQueue<>(CACHE_LIMIT));
        }

        handshakeFuture = new CompletableFuture<>();
        try {
            String reqTopic = "digital_twin/handshake/request";
            String payload = "{\"request\": \"sync\"}";
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            mqttClient.publish(reqTopic, message);

            String mqttResponse = handshakeFuture.get(15, TimeUnit.SECONDS);
            JsonNode rootNode = objectMapper.readTree(mqttResponse);

            if (rootNode.has("stations")) {
                JsonNode stationsNode = rootNode.get("stations");
                for (JsonNode station : stationsNode) {
                    String sid = station.get("station_id").asText();
                    double conc = station.has("concentration") ? station.get("concentration").asDouble() : 0.0;
                    for (DeviceInitResponse response : responses) {
                        if (response.getDeviceId().equals(sid)) {
                            response.setInitialValue(conc);
                        }
                    }
                }
            }

            if (rootNode.has("valve_info")) {
                JsonNode valveNode = rootNode.get("valve_info");
                String vid = valveNode.get("device_id").asText();
                String vStatus = valveNode.has("status") ? valveNode.get("status").asText() : "UNKNOWN";
                for (DeviceInitResponse response : responses) {
                    if (response.getDeviceId().equals(vid)) {
                        response.setStatus(vStatus);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("同步握手过程异常中断未能获取设备动态数据");
        } finally {
            handshakeFuture = null;
        }

        return responses;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        if ("digital_twin/handshake/response".equals(topic)) {
            if (handshakeFuture != null && !handshakeFuture.isDone()) {
                handshakeFuture.complete(payload);
            }
        }
        WebSocketServer.broadcast(payload);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("底层网络连接意外断开: " + cause.getMessage());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}