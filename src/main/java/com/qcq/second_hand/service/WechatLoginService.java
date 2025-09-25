// src/main/java/com/qcq/second_hand/service/WechatLoginService.java
package com.qcq.second_hand.service;

import com.qcq.second_hand.entity.Users;

import java.util.Map;

public interface WechatLoginService {
    Map<String, Object> loginWithCode(String code, String appid, String secret);
    Users updateUserInfo(Long userId, Map<String, Object> userInfo);
}