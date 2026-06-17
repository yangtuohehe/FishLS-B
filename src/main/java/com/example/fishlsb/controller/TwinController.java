package com.example.fishlsb.controller;

import com.example.fishlsb.dto.InitRequest;
import com.example.fishlsb.dto.Result;
import com.example.fishlsb.entity.TwinEntity;
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
    public Result<List<TwinEntity>> initTwinEntities(@RequestBody InitRequest request) {
        List<TwinEntity> entities = digitalTwinService.initializeTwins(request.getDeviceIds());
        return new Result<>(200, "初始化及握手请求发送成功", entities);
    }
}