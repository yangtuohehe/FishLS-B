package com.example.fishlsb.controller;

import com.fishls.dto.*;
import com.fishls.service.InteractionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping("/entities/locations")
    public Result<Map<String, LocationDto>> getLocations(@RequestParam String ids) {
        Map<String, LocationDto> locations = interactionService.getDeviceLocations(ids);
        return Result.success(locations);
    }

    @GetMapping("/observations/history/batch")
    public Result<Map<String, ChartDataDto>> getHistoryBatch(@RequestParam String ids) {
        Map<String, ChartDataDto> historyData = interactionService.getDeviceHistoryBatch(ids);
        return Result.success(historyData);
    }

    @PostMapping("/control/mode")
    public Result<Void> switchControlMode(@RequestBody ModeSwitchRequest request) {
        interactionService.updateDeviceMode(request.getDeviceId(), request.getMode());
        return Result.success();
    }

    @PostMapping("/control/command")
    public Result<Void> sendControlCommand(@RequestBody CommandRequest request) {
        interactionService.executePhysicalCommand(request.getDeviceId(), request.getTargetValue());
        return Result.success();
    }

    @PutMapping("/rules/declaration")
    public Result<Void> declareRules(@RequestBody RuleDeclarationRequest request) {
        interactionService.syncRules(request.getRules());
        return Result.success();
    }
}
