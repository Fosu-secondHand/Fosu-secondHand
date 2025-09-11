package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController {

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    // 创建用户
    @PostMapping
    public response<Users> createUser(@RequestBody Users user) {
        return response.success(usersService.saveUser(user));
    }

    // 获取用户详情
    @GetMapping("/{userId}")
    public response<Users> getUserById(@PathVariable Long userId) {
        return response.success(usersService.getUserById(userId));
    }

    // 获取所有用户
    @GetMapping
    public response<List<Users>> getAllUsers() {
        return response.success(usersService.getAllUsers());
    }

    // 更新用户
    @PutMapping("/{userId}")
    public response<Users> updateUser(@PathVariable Long userId, @RequestBody Users user) {
        user.setUserId(userId);
        return response.success(usersService.updateUser(user));
    }

    // 删除用户
    @DeleteMapping("/{userId}")
    public response<String> deleteUser(@PathVariable Long userId) {
        usersService.deleteUser(userId);
        return response.success("用户删除成功");
    }

    // 根据OpenID查询用户
    @GetMapping("/openid/{openid}")
    public response<Users> getUserByOpenid(@PathVariable String openid) {
        return response.success(usersService.getUserByOpenid(openid));
    }

    // 根据手机号查询用户
    @GetMapping("/phone/{phone}")
    public response<Users> getUserByPhone(@PathVariable String phone) {
        return response.success(usersService.getUserByPhone(phone));
    }
}
