// src/main/java/com/qcq/second_hand/service/Impl/WechatLoginServiceImpl.java
package com.qcq.second_hand.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.service.TokenService;
import com.qcq.second_hand.service.UsersService;
import com.qcq.second_hand.service.WechatLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WechatLoginServiceImpl implements WechatLoginService {

    private final UsersService usersService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> loginWithCode(String code, String appid, String secret) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 调用微信接口获取openid和session_key
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid +
                    "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            if (response == null) {
                result.put("code", 500);
                result.put("message", "微信接口调用失败");
                return result;
            }

            Map<String, Object> wechatResponse = objectMapper.readValue(response, Map.class);

            // 检查微信接口返回错误
            if (wechatResponse.containsKey("errcode")) {
                log.error("微信登录失败: errcode={}, errmsg={}",
                        wechatResponse.get("errcode"), wechatResponse.get("errmsg"));
                result.put("code", 500);
                result.put("message", "微信登录失败: " + wechatResponse.get("errmsg"));
                return result;
            }

            String openid = (String) wechatResponse.get("openid");
            String unionid = (String) wechatResponse.get("unionid");
            String sessionKey = (String) wechatResponse.get("session_key");

            // 查找或创建用户
            Users user;
            try {
                user = usersService.getUserByOpenid(openid);
            } catch (Exception e) {
                // 用户不存在，创建新用户
                user = new Users();
                user.setOpenid(openid);
                user.setUsername("微信用户_" + System.currentTimeMillis());
                user.setDatetime(LocalDateTime.now());
                user.setLastLogin(LocalDateTime.now());
                user.setCreditScore(100);
                user.setStatus(com.qcq.second_hand.entity.other.Status.ACTIVE);
                user.setPhone("");
                user = usersService.saveUser(user);
            }

            // 更新用户最后登录时间
            user.setLastLogin(LocalDateTime.now());
            usersService.updateUser(user);

            // 使用TokenService生成自定义登录态token
            String token = tokenService.generateToken(user.getUserId());

            result.put("code", 200);
            result.put("message", "登录成功");

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getUserId());
            data.put("openid", openid);
            data.put("unionid", unionid);
            data.put("sessionKey", sessionKey);

            result.put("data", data);

        } catch (Exception e) {
            log.error("微信登录异常", e);
            result.put("code", 500);
            result.put("message", "登录异常: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Users updateUserInfo(Long userId, Map<String, Object> userInfo) {
        try {
            // 根据用户ID获取用户
            Users user = usersService.getUserById(userId);

            // 更新用户信息
            if (userInfo.containsKey("nickName")) {
                user.setUsername((String) userInfo.get("nickName"));
            }

            if (userInfo.containsKey("avatarUrl")) {
                user.setAvatar((String) userInfo.get("avatarUrl"));
            }

            if (userInfo.containsKey("gender")) {
                // 可以根据需要添加性别字段到 Users 实体
            }

            if (userInfo.containsKey("province")) {
                // 可以根据需要添加省份字段到 Users 实体
            }

            if (userInfo.containsKey("city")) {
                // 可以根据需要添加城市字段到 Users 实体
            }

            if (userInfo.containsKey("country")) {
                // 可以根据需要添加国家字段到 Users 实体
            }

            // 更新用户信息
            usersService.updateUser(user);

            return user;
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
            throw new RuntimeException("更新用户信息失败: " + e.getMessage());
        }
    }
}
