package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController {

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Operation(summary = "创建用户", description = "创建新的用户")
    @PostMapping
    public response<Users> createUser(
            @Parameter(description = "用户信息", required = true)
            @RequestBody Users user) {
        return response.success(usersService.saveUser(user));
    }

    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详情")
    @GetMapping("/{userId}")
    public response<Users> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        return response.success(usersService.getUserById(userId));
    }

    @Operation(summary = "获取所有用户", description = "获取所有用户列表")
    @GetMapping
    public response<List<Users>> getAllUsers() {
        return response.success(usersService.getAllUsers());
    }

    @Operation(summary = "更新用户", description = "根据用户ID更新用户信息")
    @PutMapping("/{userId}")
    public response<Users> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "用户信息", required = true)
            @RequestBody Users user) {
        user.setUserId(userId);
        return response.success(usersService.updateUser(user));
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @DeleteMapping("/{userId}")
    public response<String> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        usersService.deleteUser(userId);
        return response.success("用户删除成功");
    }

    @Operation(summary = "根据OpenID查询用户", description = "根据OpenID查询用户")
    @GetMapping("/openid/{openid}")
    public response<Users> getUserByOpenid(
            @Parameter(description = "OpenID", required = true)
            @PathVariable String openid) {
        return response.success(usersService.getUserByOpenid(openid));
    }

    @Operation(summary = "根据手机号查询用户", description = "根据手机号查询用户")
    @GetMapping("/phone/{phone}")
    public response<Users> getUserByPhone(
            @Parameter(description = "手机号", required = true)
            @PathVariable String phone) {
        return response.success(usersService.getUserByPhone(phone));
    }
}
