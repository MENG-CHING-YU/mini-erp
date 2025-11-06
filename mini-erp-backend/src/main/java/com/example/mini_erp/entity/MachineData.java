package com.example.mini_erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "machine_data")
public class MachineData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "machine_id", nullable = false)
    private Integer machineId;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "status")
    private String status; // running, stopped, warning, error

    @Column(name = "speed")
    private Double speed;

    @Column(name = "power_consumption")
    private Double powerConsumption;

    @Column(name = "vibration")
    private Double vibration;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "message")
    private String message;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}