package com.example.fishlsb.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class MqttConfig {

    @Bean
    public MqttClient mqttClient() {
        try {
            // 这里对应你 Python 脚本中的 MQTT_BROKER = "broker.emqx.io", PORT = 1883
            String broker = "tcp://broker.emqx.io:1883";
            // 客户端ID必须唯一，所以加上 UUID 随机后缀防止冲突
            String clientId = "spring_boot_backend_" + UUID.randomUUID().toString();

            MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true); // 开启断线自动重连
            options.setCleanSession(true);       // 不保持之前的会话
            options.setConnectionTimeout(10);    // 连接超时时间
            options.setKeepAliveInterval(20);    // 心跳间隔

            // 执行连接
            client.connect(options);
            System.out.println("========== MQTT Broker 连接成功 ==========");

            return client;
        } catch (MqttException e) {
            System.err.println("MQTT 连接失败: " + e.getMessage());
            throw new RuntimeException("无法初始化 MQTT 客户端", e);
        }
    }
}