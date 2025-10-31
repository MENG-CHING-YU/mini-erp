package com.example.mini_erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.mini_erp.service.MachineService;
import com.example.mini_erp.entity.Machine;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/machines")
public class MachineController {

    @Autowired
    private MachineService machineService;

    // ✅ 查詢所有機台
    @GetMapping
    public List<Machine> getAllMachines(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        System.out.println("👤 使用者查詢機台列表: " + username);
        return machineService.findAll();
    }

    // ✅ 查詢單一機台（詳情頁）
    @GetMapping("/{id}")
    public ResponseEntity<Machine> getMachine(@PathVariable Integer id) {
        Optional<Machine> machine = machineService.findById(id);
        return machine.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 新增機台（RESTful 標準用 POST /machines）
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMachine(@RequestBody Machine machine) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 檢查出廠編號是否重複
            if (machineService.existsBySerialNumber(machine.getSerialNumber())) {
                response.put("success", false);
                response.put("message", "出廠編號已存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 自動計算下次保養日期
            if (machine.getLastMaintenanceDate() != null && machine.getMaintenanceCycle() != null) {
                LocalDate nextDate = machine.getLastMaintenanceDate().plusDays(machine.getMaintenanceCycle());
                machine.setNextMaintenanceDate(nextDate);
            }
            
            Machine saved = machineService.save(machine);
            response.put("success", true);
            response.put("message", "新增成功");
            response.put("data", saved);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "新增失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 更新機台
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMachine(
            @PathVariable Integer id, 
            @RequestBody Machine updatedMachine) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Machine> existingMachine = machineService.findById(id);
            
            if (existingMachine.isEmpty()) {
                response.put("success", false);
                response.put("message", "機台不存在");
                return ResponseEntity.notFound().build();
            }
            
            // 自動計算下次保養日期
            if (updatedMachine.getLastMaintenanceDate() != null && 
                updatedMachine.getMaintenanceCycle() != null) {
                LocalDate nextDate = updatedMachine.getLastMaintenanceDate()
                    .plusDays(updatedMachine.getMaintenanceCycle());
                updatedMachine.setNextMaintenanceDate(nextDate);
            }
            
            updatedMachine.setId(id);
            Machine saved = machineService.save(updatedMachine);
            
            response.put("success", true);
            response.put("message", "更新成功");
            response.put("data", saved);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 刪除機台
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMachine(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Machine> machine = machineService.findById(id);
        if (machine.isPresent()) {
            machineService.deleteById(id);
            response.put("success", true);
            response.put("message", "刪除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "機台不存在");
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ 根據名稱模糊搜尋
    @GetMapping("/search")
    public List<Machine> searchByName(@RequestParam String name) {
        return machineService.searchByName(name);
    }

    // ✅ 根據狀態查詢
    @GetMapping("/status/{status}")
    public List<Machine> getByStatus(@PathVariable String status) {
        return machineService.findByStatus(status);
    }

    // ✅ 查詢需要保養的機台
    @GetMapping("/maintenance/due")
    public List<Machine> getMaintenanceDueMachines() {
        LocalDate today = LocalDate.now();
        return machineService.findMaintenanceDue(today);
    }

    // ✅ 查詢保固即將到期的機台（30天內）
    @GetMapping("/warranty/expiring")
    public List<Machine> getWarrantyExpiringSoon() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plusDays(30);
        return machineService.findWarrantyExpiring(today, thirtyDaysLater);
    }

    // ✅ 統計資料（儀表板用）
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Machine> allMachines = machineService.findAll();
        stats.put("total", allMachines.size());
        stats.put("running", machineService.countByStatus("running"));
        stats.put("stopped", machineService.countByStatus("stopped"));
        stats.put("maintenance", machineService.countByStatus("maintenance"));
        
        LocalDate today = LocalDate.now();
        stats.put("maintenanceDue", machineService.findMaintenanceDue(today).size());
        
        return ResponseEntity.ok(stats);
    }
}