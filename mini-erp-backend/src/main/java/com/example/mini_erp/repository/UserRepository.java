package com.example.mini_erp.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mini_erp.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 依照帳號查詢
    Optional<User> findByUsername(String username);
}
