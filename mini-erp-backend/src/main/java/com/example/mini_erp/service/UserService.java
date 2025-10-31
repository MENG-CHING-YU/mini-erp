package com.example.mini_erp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.mini_erp.entity.User;
import com.example.mini_erp.repository.UserRepository;

import java.util.List;
import java.util.Optional;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 註冊使用者
    public User registerUser(User user) {
        // 可以在這裡加密密碼
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("帳號已存在");
        }
        return userRepository.save(user);
    }

    // 根據帳號查詢
    public User findByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.orElseThrow(() -> new RuntimeException("找不到使用者"));
    }

    // 更新密碼示例
    public User updatePassword(String username, String newPassword) {
        User user = findByUsername(username);
        user.setPassword(newPassword); // 可以加密
        return userRepository.save(user);
    }
    public List<User> getAllUsers() {
    return userRepository.findAll();
}
  public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

}
