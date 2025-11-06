package com.example.mini_erp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MachineRealtimeDTO {
    private Integer machineId;
    private String machineName;
    private Double temperature;
    private String status;
    private Double speed;
    private Double powerConsumption;
    private Double vibration;
    private String errorCode;
    private String message;
    private LocalDateTime recordedAt;
    private String alertLevel; // normal, warning, critical
}