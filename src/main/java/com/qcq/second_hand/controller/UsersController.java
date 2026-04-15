package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.FileUploadService;
import com.qcq.second_hand.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
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

    @Operation(summary = "更新用户", description = "根据用户ID更新用户信息，支持Base64头像")
    @PutMapping("/{userId}")
    public response<Users> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "用户信息", required = true)
            @RequestBody Users user) {
        try {
            // 验证用户是否存在
            Users existingUser = usersService.getUserById(userId);

            // 处理 Base64 格式的头像
            if (user.getAvatar() != null && user.getAvatar().startsWith("data:image")) {
                try {
                    // 提取 Base64 数据
                    String[] parts = user.getAvatar().split(",");
                    if (parts.length == 2) {
                        String base64Data = parts[1];
                        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                        // 生成文件名
                        String extension = ".png";
                        if (user.getAvatar().contains("image/jpeg")) {
                            extension = ".jpg";
                        } else if (user.getAvatar().contains("image/webp")) {
                            extension = ".webp";
                        }

                        String fileName = "avatar_" + userId + "_" + System.currentTimeMillis() + extension;

                        // 创建头像目录
                        String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                        File dir = new File(uploadDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        // 保存文件
                        String filePath = uploadDir + "/" + fileName;
                        Files.write(Paths.get(filePath), imageBytes);

                        // 设置相对路径（会自动通过 Getter 添加 /api 前缀）
                        user.setAvatar("/uploads/avatars/" + fileName);
                    }
                } catch (Exception e) {
                    System.err.println("Base64头像处理失败: " + e.getMessage());
                    e.printStackTrace();
                    // Base64 处理失败，保持原头像不变
                    user.setAvatar(existingUser.getAvatar());
                }
            }

            user.setUserId(userId);
            return response.success(usersService.updateUser(user));
        } catch (Exception e) {
            System.err.println("更新用户失败: " + e.getMessage());
            e.printStackTrace();
            return new response<>(500, "更新用户失败: " + e.getMessage(), null);
        }
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
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    @GetMapping("/info")
    public Map<String, Object> getCurrentUserInfo(HttpServletRequest request) {
        // 从请求属性中获取用户ID（由TokenInterceptor设置）
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", 401);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("success", false);
            errorData.put("data", null);
            errorResponse.put("data", errorData);
            return errorResponse;
        }

        Users user = usersService.getUserById(userId);
        if (user == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", 404);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("success", false);
            errorData.put("data", null);
            errorResponse.put("data", errorData);
            return errorResponse;
        }

        // 构建符合标准格式的响应
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);

        Map<String, Object> dataWrapper = new HashMap<>();
        dataWrapper.put("success", true);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getUserId());
        userData.put("username", user.getUsername());
        userData.put("nickname", user.getNickname());
        userData.put("avatarUrl", user.getAvatar());
        userData.put("isLogin", true);

        dataWrapper.put("data", userData);
        response.put("data", dataWrapper);

        return response;
    }
    @Operation(summary = "获取用户正在出售的商品", description = "获取指定用户正在出售的所有商品列表")
    @GetMapping("/{userId}/selling-products")
    public response<Object> getUserSellingProducts(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        try {
            Object sellingProducts = usersService.getUserSellingProducts(userId);
            return response.success(sellingProducts);
        } catch (Exception e) {
            return new response<>(500, "获取用户商品列表失败: " + e.getMessage(), null);
        }
    }



}
