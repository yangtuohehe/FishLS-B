package com.example.fishlsb;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MqttHandshakeTest {

    @Test
    public void testDirectMqttHandshake() throws Exception {
        System.out.println("========== 纯 MQTT 握手测试开始 ==========");

        String broker = "tcp://broker.emqx.io:1883";
        String clientId = "java_test_client_" + UUID.randomUUID().toString();
        MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());

        CountDownLatch latch = new CountDownLatch(1);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("连接丢失: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("\n[收到响应] 主题: " + topic);
                System.out.println("[收到响应] 内容: " + new String(message.getPayload()));
                if ("digital_twin/handshake/response".equals(topic)) {
                    latch.countDown();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        System.out.println("正在连接到 Broker: " + broker);
        client.connect(options);
        System.out.println("连接成功，正在订阅主题...");

        String resTopic = "digital_twin/handshake/response";
        client.subscribe(resTopic);
        System.out.println("已订阅主题: " + resTopic);

        String reqTopic = "digital_twin/handshake/request";
        String payload = "{\"request\": \"sync\"}";
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        client.publish(reqTopic, message);
        System.out.println("已发送握手请求至: " + reqTopic + "，内容: " + payload);

        System.out.println("等待 Python 端回复 (最多等待 15 秒)...");
        boolean received = latch.await(15, TimeUnit.SECONDS);

        if (received) {
            System.out.println("\n========== 握手测试成功 ==========");
        } else {
            System.err.println("\n========== 握手测试超时，未收到响应 ==========");
        }

        client.disconnect();
        client.close();
    }
}