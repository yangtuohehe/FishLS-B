package com.example.fishlsb;

import com.example.fishlsb.dto.DeviceInitResponse;
import com.example.fishlsb.mapper.TwinEntityMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class IntegratedHandshakeTest {

    @Autowired
    private TwinEntityMapper twinEntityMapper;

    @Test
    public void testMapperAndMqttHandshake() throws Exception {
        System.out.println("========== 阶段一：查询数据库获取设备信息与动态属性名 ==========");
        List<String> deviceIds = Arrays.asList(
                "DEV-ST-001",
                "DEV-ST-002",
                "DEV-ST-003",
                "DEV-ST-004",
                "DEV-ST-005",
                "DEV-VALVE-001"
        );

        List<DeviceInitResponse> responses = twinEntityMapper.findInitInfoByDeviceIds(deviceIds);
        for (DeviceInitResponse response : responses) {
            System.out.println("----------------------------------------");
            System.out.println("设备 ID: " + response.getDeviceId());
            System.out.println("经度坐标: " + response.getLongitude());
            System.out.println("纬度坐标: " + response.getLatitude());
            System.out.println("动态观测属性名称: " + response.getDynamicPropertyName());
        }

        System.out.println("\n========== 阶段二：执行 MQTT 握手并打印原始返回值 ==========");
        String broker = "tcp://broker.emqx.io:1883";
        String clientId = "integrated_test_" + UUID.randomUUID().toString();
        MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
        CountDownLatch latch = new CountDownLatch(1);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("测试端网络连接丢失: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("成功捕获 MQTT 响应报文");
                System.out.println("报文主题: " + topic);
                System.out.println("原始报文内容: " + new String(message.getPayload()));
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

        client.connect(options);
        client.subscribe("digital_twin/handshake/response");

        String reqTopic = "digital_twin/handshake/request";
        String payload = "{\"request\": \"sync\"}";
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        client.publish(reqTopic, message);

        boolean received = latch.await(15, TimeUnit.SECONDS);
        if (!received) {
            System.err.println("等待物联网设备响应超时");
        }

        client.disconnect();
        client.close();
        System.out.println("========== 综合测试结束 ==========");
    }
}