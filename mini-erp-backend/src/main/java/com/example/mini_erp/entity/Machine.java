package com.example.mini_erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name; // 機台名稱

    @Column(nullable = false, unique = true)
    private String serialNumber; // 出廠編號

    private String model; // 機台型號

    private String manufacturer; // 製造商

    @Column(nullable = false)
    private String status = "running"; // running / stopped / maintenance

    private String location; // 放置位置

    private LocalDate purchaseDate; // 購買日期

    private LocalDate warrantyEndDate; // 保固到期日

    private Integer maintenanceCycle = 30; // 保養週期(天)

    private LocalDate lastMaintenanceDate; // 上次保養日期

    private LocalDate nextMaintenanceDate; // 下次保養日期

    private BigDecimal price; // 購買價格

    @Column(columnDefinition = "TEXT")
    private String description; // 機台描述/備註

    private String responsiblePerson; // 負責人

    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        // 自動計算下次保養日期
        if (this.lastMaintenanceDate != null && this.maintenanceCycle != null) {
            this.nextMaintenanceDate = this.lastMaintenanceDate.plusDays(this.maintenanceCycle);
        }
    }
}