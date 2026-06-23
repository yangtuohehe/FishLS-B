package com.example.fishlsb;

import com.example.fishlsb.dto.DeviceInitResponse;
import com.example.fishlsb.mapper.TwinEntityMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class MapperTest {

    @Autowired
    private TwinEntityMapper twinEntityMapper;

    @Test
    public void testDatabaseQueryOnly() {
        System.out.println("========== 开始执行 MyBatis 数据库直查测试 ==========");

        List<String> deviceIds = Arrays.asList(
                "DEV-ST-001",
                "DEV-ST-002",
                "DEV-ST-003",
                "DEV-ST-004",
                "DEV-ST-005",
                "DEV-VALVE-001"
        );

        List<DeviceInitResponse> responses = twinEntityMapper.findInitInfoByDeviceIds(deviceIds);

        System.out.println("查询命中的实体数量: " + responses.size());

        for (DeviceInitResponse response : responses) {
            System.out.println("----------------------------------------");
            System.out.println("设备 ID: " + response.getDeviceId());
            System.out.println("实体名称: " + response.getEntityName());
            System.out.println("经度 (Longitude): " + response.getLongitude());
            System.out.println("纬度 (Latitude): " + response.getLatitude());
            System.out.println("初始值 (Initial Value): " + response.getInitialValue());
            System.out.println("状态 (Status): " + response.getStatus());
        }

        System.out.println("========== 测试结束 ==========");
    }
}