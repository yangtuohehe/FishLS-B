package com.example.fishlsb.service;

import com.example.fishlsb.entity.TwinEntity;
import com.example.fishlsb.mapper.TwinEntityMapper;
import com.example.fishlsb.websocket.WebSocketServer;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class DigitalTwinService implements MqttCallback {

    private final TwinEntityMapper twinEntityMapper;
    private final MqttClient mqttClient;
    private final ConcurrentHashMap<String, LinkedBlockingQueue<Object>> dataCache = new ConcurrentHashMap<>();
    private final int CACHE_LIMIT = 48;

    public DigitalTwinService(TwinEntityMapper twinEntityMapper, MqttClient mqttClient) {
        this.twinEntityMapper = twinEntityMapper;
        this.mqttClient = mqttClient;
        // 设置回调，使该 Service 能够接收 MQTT 消息
        this.mqttClient.setCallback(this);
        try {
            // 订阅响应主题以接收来自 Python 端的数据
            this.mqttClient.subscribe("digital_twin/handshake/response");
        } catch (MqttException e) {
            e.printStackTrace();
        }
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

    // MQTT 回调方法：处理接收到的响应
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        if ("digital_twin/handshake/response".equals(topic)) {
            String payload = new String(message.getPayload());
            System.out.println("后端收到 MQTT 响应: " + payload);
            // 通过 WebSocket 实时广播给前端页面
            WebSocketServer.broadcast(payload);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("MQTT 连接丢失: " + cause.getMessage());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 发送完成回调
    }
}