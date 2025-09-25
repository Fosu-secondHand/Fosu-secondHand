// src/main/java/com/qcq/second_hand/controller/WechatLoginController.java
package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.WechatLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@CrossOrigin(origins = {"https://yourdomain.com"}) // 修改为实际允许的域名
@Tag(name = "微信登录", description = "微信小程序登录相关接口")
@RestController
@RequestMapping("/wechat")
@RequiredArgsConstructor
public class WechatLoginController {

    private final WechatLoginService wechatLoginService;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    @Operation(summary = "微信登录", description = "通过code获取用户信息并登录")
    @PostMapping("/login")
    public response login(
            @Parameter(description = "微信登录code", required = true)
            @RequestParam(required = false) String code,
            @RequestBody(required = false) Map<String, String> requestBody) {

        String actualCode = code;
        if (actualCode == null && requestBody != null) {
            actualCode = requestBody.get("code");
        }

        if (actualCode == null || actualCode.isEmpty()) {
            return new response(400, "缺少必要的参数: code", null);
        }

        try {
            Map<String, Object> result = wechatLoginService.loginWithCode(actualCode, appid, secret);
            Integer resultCode = (Integer) result.get("code");
            if (Objects.equals(resultCode, 200)) {
                return response.success(result.get("data"));
            } else {
                String message = (String) result.getOrDefault("message", "未知错误");
                return new response(500, message, null);
            }
        } catch (Exception e) {
            // 记录日志，不要把异常信息暴露给前端
            e.printStackTrace();
            return new response(500, "登录失败，请稍后再试", null);
        }
    }

    @Operation(summary = "更新用户信息", description = "更新微信用户详细信息")
    @PostMapping("/updateUserInfo")
    public response updateUserInfo(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "用户信息", required = true)
            @RequestBody Map<String, Object> userInfo) {

        if (userId == null || userId <= 0) {
            return new response(400, "无效的用户ID", null);
        }

        if (userInfo == null || userInfo.isEmpty()) {
            return new response(400, "用户信息不能为空", null);
        }

        try {
            Users updatedUser = wechatLoginService.updateUserInfo(userId, userInfo);
            return response.success(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return new response(500, "更新用户信息失败，请稍后再试", null);
        }
    }
}
