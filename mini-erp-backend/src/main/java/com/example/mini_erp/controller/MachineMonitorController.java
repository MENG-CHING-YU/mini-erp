package com.example.mini_erp.controller;

import com.example.mini_erp.dto.MachineRealtimeDTO;
import com.example.mini_erp.entity.MachineData;
import com.example.mini_erp.service.MachineMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/machines")
public class MachineMonitorController {

    @Autowired
    private MachineMonitorService machineMonitorService;

    /**
     * 獲取所有機台的即時狀態
     */
    @GetMapping("/realtime")
    public ResponseEntity<List<MachineRealtimeDTO>> getAllMachinesRealtimeData() {
        List<MachineRealtimeDTO> data = machineMonitorService.getAllMachinesRealtimeData();
        return ResponseEntity.ok(data);
    }

    /**
     * 獲取特定機台的即時狀態
     */
    @GetMapping("/{machineId}/realtime")
    public ResponseEntity<MachineRealtimeDTO> getMachineRealtimeData(@PathVariable Integer machineId) {
        MachineRealtimeDTO data = machineMonitorService.getMachineRealtimeData(machineId);
        if (data != null) {
            return ResponseEntity.ok(data);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 獲取機台的歷史數據
     */
    @GetMapping("/{machineId}/history")
    public ResponseEntity<List<MachineData>> getMachineHistoryData(
            @PathVariable Integer machineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<MachineData> data = machineMonitorService.getMachineHistoryData(machineId, start, end);
        return ResponseEntity.ok(data);
    }
}