package com.example.fishlsb.controller;

import com.example.fishlsb.dto.InitRequest;
import com.example.fishlsb.dto.Result;
import com.example.fishlsb.dto.DeviceInitResponse;
import com.example.fishlsb.service.DigitalTwinService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/twin")
public class TwinController {

    private final DigitalTwinService digitalTwinService;

    public TwinController(DigitalTwinService digitalTwinService) {
        this.digitalTwinService = digitalTwinService;
    }

    @PostMapping("/init")
    public Result<List<DeviceInitResponse>> initTwinEntities(@RequestBody InitRequest request) {
        try {
            List<DeviceInitResponse> responses = digitalTwinService.initializeTwins(request.getDeviceIds());
            return new Result<>(200, "初始化数据获取成功", responses);
        } catch (RuntimeException e) {
            return new Result<>(500, e.getMessage(), null);
        }
    }
}