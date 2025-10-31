package com.example.mini_erp.controller;

import com.example.mini_erp.dto.EmployeeDTO;
import com.example.mini_erp.entity.Employee;
import com.example.mini_erp.entity.User;
import com.example.mini_erp.service.EmployeeService;
import com.example.mini_erp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    // GET /employees → 取得所有員工 DTO
    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployeesDTO();
    }

    // GET /employees/{id} → 取得單筆員工 DTO（詳情）
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Integer id) {
        Optional<Employee> empOpt = employeeService.getEmployeeById(id);
        if (empOpt.isPresent()) {
            return ResponseEntity.ok(employeeService.convertToDTO(empOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /employees → 新增或更新員工
    @PostMapping
    public ResponseEntity<EmployeeDTO> save(@RequestBody EmployeeDTO dto) {
        Employee employee;

        if (dto.getId() != null) {
            // 更新
            employee = employeeService.getEmployeeById(dto.getId()).orElse(new Employee());
        } else {
            // 新增
            employee = new Employee();
        }

        // 對應欄位
        employee.setFullName(dto.getFullName());
        employee.setDepartment(dto.getDepartment());
        employee.setPosition(dto.getPosition());

        // contact 只存 Email
        employee.setContact(dto.getEmail());

        // hireDate
        employee.setHireDate(dto.getHireDate() != null ? dto.getHireDate().atStartOfDay() : null);

        // 設定 User 關聯，null 也可以直接存
        User user = dto.getUserId() != null ? userService.findById(dto.getUserId()).orElse(null) : null;
        employee.setUser(user);

        Employee saved = employeeService.saveEmployee(employee);
        return ResponseEntity.ok(employeeService.convertToDTO(saved));
    }

    // DELETE /employees/{id} → 刪除員工
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // GET /employees/unassigned-users → 取得未被指派為員工的使用者清單
    @GetMapping("/unassigned-users")
    public ResponseEntity<List<User>> getUnassignedUsers() {
        // 呼叫服務來獲取未指派為員工的使用者
        List<User> unassignedUsers = employeeService.getUnassignedUsers();
        return ResponseEntity.ok(unassignedUsers);
    }
}
