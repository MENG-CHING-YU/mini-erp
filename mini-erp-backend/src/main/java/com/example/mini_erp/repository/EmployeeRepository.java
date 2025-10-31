package com.example.mini_erp.repository;

import com.example.mini_erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // 目前 JpaRepository 已經提供了 findAll(), findById(), save(), deleteById() 等方法
    // 如果之後需要自訂查詢可以再加
}
