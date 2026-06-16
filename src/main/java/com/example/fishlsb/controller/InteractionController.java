package com.example.fishlsb.controller;

import com.fishls.dto.*;
import com.fishls.service.InteractionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class InteractionController {
    private final DeviceDataService deviceDataService;

    public InteractionController(DeviceDataService deviceDataService) {
        this.deviceDataService = deviceDataService;
    }

    @GetMapping("/entities/locations")
    public Result<Map<String, Location>> getLocations(@RequestParam String ids) {
        Map<String, Location> locations = deviceDataService.getDeviceLocations(ids);
        return new Result<>(locations);
    }

    @GetMapping("/observations/history/batch")
    public Result<Object> getHistoryBatch(@RequestParam String ids) {
        // 由于前端折线图主要依靠WebSocket实时追加数据，该接口可返回空壳对象防止控制台报错
        return new Result<>(new Object());
    }

    @PostMapping("/control/mode")
    public Result<Void> switchControlMode(@RequestBody ModeRequest request) {
        deviceDataService.updateValveMode(request.deviceId, request.mode);
        return new Result<>(null);
    }

    @PostMapping("/control/command")
    public Result<Void> sendControlCommand(@RequestBody CommandRequest request) {
        deviceDataService.executePhysicalCommand(request.deviceId, request.targetValue);
        return new Result<>(null);
    }

    @PutMapping("/rules/declaration")
    public Result<Void> declareRules(@RequestBody RuleDeclarationRequest request) {
        deviceDataService.updateRules(request.rules);
        return new Result<>(null);
    }
}
