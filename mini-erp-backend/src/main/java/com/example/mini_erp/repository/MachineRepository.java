package com.example.mini_erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.mini_erp.entity.Machine;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Integer> {

    // 🔍 根據狀態查詢
    List<Machine> findByStatus(String status);

    // 🔍 根據狀態統計數量
    long countByStatus(String status);

    // 🔍 根據名稱模糊搜尋
    List<Machine> findByNameContaining(String name);

    // 🔍 根據出廠編號查詢（唯一性檢查）
    Machine findBySerialNumber(String serialNumber);

    // 🔍 根據位置模糊搜尋
    List<Machine> findByLocationContaining(String location);

    // 🔍 根據負責人查詢
    List<Machine> findByResponsiblePerson(String responsiblePerson);

    // 🔍 查詢需要保養的機台（下次保養日期 <= 指定日期）
    List<Machine> findByNextMaintenanceDateLessThanEqual(LocalDate date);

    // 🔍 查詢保固期限在指定範圍內的機台
    List<Machine> findByWarrantyEndDateBetween(LocalDate startDate, LocalDate endDate);

    // 🔍 查詢已過保的機台
    List<Machine> findByWarrantyEndDateLessThan(LocalDate date);

    // 🔍 根據製造商查詢
    List<Machine> findByManufacturer(String manufacturer);

    // 🔍 根據型號查詢
    List<Machine> findByModel(String model);
}