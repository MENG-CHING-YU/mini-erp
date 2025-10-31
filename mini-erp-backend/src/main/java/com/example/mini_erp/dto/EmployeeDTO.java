package com.example.mini_erp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeDTO {

    private Integer id;
    private String fullName;      // 對應 Employee.fullName
    private String department;    // 對應 Employee.department
    private String position;      // 對應 Employee.position
    private String email;         // 對應 Employee.contact（Email）
    private Integer userId;       // 對應 Employee.user.id
    private LocalDate hireDate;   // 對應 Employee.hireDate
    private LocalDateTime createdAt;  // 對應 Employee.createdAt
    private LocalDateTime updatedAt;  // 對應 Employee.updatedAt

    // Getter & Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
