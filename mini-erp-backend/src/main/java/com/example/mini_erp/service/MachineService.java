package com.example.mini_erp.service;

import com.example.mini_erp.entity.Machine;
import com.example.mini_erp.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MachineService {

    @Autowired
    private MachineRepository machineRepository;

    // 查全部
    public List<Machine> findAll() {
        return machineRepository.findAll();
    }

    // 查單筆
    public Optional<Machine> findById(Integer id) {
        return machineRepository.findById(id);
    }

    // 新增或修改
    public Machine save(Machine machine) {
        return machineRepository.save(machine);
    }

    // 刪除
    public void deleteById(Integer id) {
        machineRepository.deleteById(id);
    }

    // 狀態查詢
    public List<Machine> findByStatus(String status) {
        return machineRepository.findByStatus(status);
    }

    // 狀態統計
    public long countByStatus(String status) {
        return machineRepository.countByStatus(status);
    }

    // 模糊查詢（名稱）
    public List<Machine> searchByName(String name) {
        return machineRepository.findByNameContaining(name);
    }

    // 檢查出廠編號是否存在
    public boolean existsBySerialNumber(String serialNumber) {
        return machineRepository.findBySerialNumber(serialNumber) != null;
    }

    // 查詢需要保養的機台（下次保養日期 <= 今天）
    public List<Machine> findMaintenanceDue(LocalDate today) {
        return machineRepository.findByNextMaintenanceDateLessThanEqual(today);
    }

    // 查詢保固即將到期的機台
    public List<Machine> findWarrantyExpiring(LocalDate startDate, LocalDate endDate) {
        return machineRepository.findByWarrantyEndDateBetween(startDate, endDate);
    }

    // 根據位置查詢
    public List<Machine> findByLocation(String location) {
        return machineRepository.findByLocationContaining(location);
    }

    // 根據負責人查詢
    public List<Machine> findByResponsiblePerson(String person) {
        return machineRepository.findByResponsiblePerson(person);
    }
}