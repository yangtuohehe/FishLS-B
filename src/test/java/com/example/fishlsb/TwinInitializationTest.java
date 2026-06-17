package com.example.fishlsb;

import com.example.fishlsb.dto.InitRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class TwinInitializationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testInitAndHandshakeProcess() throws Exception {
        System.out.println("========== 测试开始：构造前端初始化请求 ==========");

        InitRequest request = new InitRequest();
        List<String> deviceIds = Arrays.asList(
                "STATION_A", "STATION_B", "STATION_C",
                "STATION_D", "STATION_E", "MAIN_VALVE_1"
        );
        request.setDeviceIds(deviceIds);

        String requestJson = objectMapper.writeValueAsString(request);
        System.out.println("前端发送的JSON数据: " + requestJson);

        System.out.println("========== 发送HTTP请求至Controller ==========");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/twin/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andReturn();

        int statusCode = mvcResult.getResponse().getStatus();
        String responseBody = mvcResult.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);

        System.out.println("HTTP响应状态码: " + statusCode);
        System.out.println("Controller返回的实体坐标数据: " + responseBody);

        System.out.println("========== 等待物联网设备的MQTT异步握手响应 ==========");
        System.out.println("（此时后端服务应已发送握手请求，等待5秒以接收Python端的返回数据）");

        Thread.sleep(5000);

        System.out.println("========== 测试结束 ==========");
    }
}