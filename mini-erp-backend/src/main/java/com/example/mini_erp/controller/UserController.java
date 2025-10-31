package com.example.mini_erp.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mini_erp.entity.User;
import com.example.mini_erp.jwt.JwtUtil;
import com.example.mini_erp.service.UserService;



@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 註冊
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // 根據帳號查詢
    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    // 更新密碼
    @PutMapping("/{username}/password")
    public User updatePassword(@PathVariable String username,
                               @RequestBody String newPassword) {
        return userService.updatePassword(username, newPassword);
    }

    // 登入 (簡單示範，實務建議加 JWT)
@PostMapping("/login")
public Map<String, String> login(@RequestBody User user) {
    User found = userService.findByUsername(user.getUsername());
    if (found != null && found.getPassword().equals(user.getPassword())) {
        String accessToken = JwtUtil.generateAccessToken(found.getUsername());
        String refreshToken = JwtUtil.generateRefreshToken(found.getUsername());

        // 實務上建議回傳標準欄位：accessToken, refreshToken, tokenType, expiresIn
        // 同時為了向後相容，這裡也放一個 token 欄位（等同於 accessToken）
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("token", accessToken); // 向後相容
        result.put("tokenType", "Bearer");
        // 如果需要可以額外回傳使用者資訊（示例）
        result.put("username", found.getUsername());
        return result;
    }
    throw new RuntimeException("帳號或密碼錯誤");
}
@GetMapping
public List<User> getAllUsers() {
    return userService.getAllUsers();
}
    
}
