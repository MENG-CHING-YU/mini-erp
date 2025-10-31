package com.example.mini_erp.service;

import com.example.mini_erp.dto.EmployeeDTO;
import com.example.mini_erp.entity.Employee;
import com.example.mini_erp.entity.User;
import com.example.mini_erp.repository.EmployeeRepository;
import com.example.mini_erp.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired UserRepository userRepository;

    // 取得所有 Employee
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // 取得單筆 Employee
    public Optional<Employee> getEmployeeById(Integer id) {
        return employeeRepository.findById(id);
    }

    // 新增或更新 Employee
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // 刪除 Employee
    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    // ====== DTO 相關 ======

    // 將 Employee 轉成 EmployeeDTO
  public EmployeeDTO convertToDTO(Employee emp) {
    if (emp == null) return null;

    EmployeeDTO dto = new EmployeeDTO();
    dto.setId(emp.getId());
    dto.setFullName(emp.getFullName());
    dto.setDepartment(emp.getDepartment());
    dto.setPosition(emp.getPosition());

    // contact 只存 email
    dto.setEmail(emp.getContact());

    // 對應 userId
    dto.setUserId(emp.getUser() != null ? emp.getUser().getId() : null);

    // ✅ LocalDateTime → LocalDate
    dto.setHireDate(emp.getHireDate() != null ? emp.getHireDate().toLocalDate() : null);

    dto.setCreatedAt(emp.getCreatedAt());
    dto.setUpdatedAt(emp.getUpdatedAt());

    return dto;
}


    // 取得所有 EmployeeDTO
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployeesDTO() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
      // 篩選出沒有員工綁定的帳號（User）
    @Transactional(readOnly = true)
    public List<User> getUnassignedUsers() {
        // 1. 查詢所有的用戶
        List<User> allUsers = userRepository.findAll();

        // 2. 查詢所有已經綁定員工的帳號（userId）
        List<Integer> assignedUserIds = employeeRepository.findAll().stream()
                .map(emp -> emp.getUser().getId())  // 取出所有已經綁定的 userId
                .collect(Collectors.toList());

        // 3. 篩選出沒有綁定的用戶
        return allUsers.stream()
                .filter(user -> !assignedUserIds.contains(user.getId()))  // 篩選出 userId 沒有綁定員工的帳號
                .collect(Collectors.toList());
    }
}
