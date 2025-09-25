// src/main/java/com/qcq/second_hand/service/Impl/TokenServiceImpl.java
package com.qcq.second_hand.service.Impl;

import com.qcq.second_hand.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    // 使用内存存储token，生产环境建议使用Redis
    private static final Map<String, TokenInfo> TOKEN_STORE = new ConcurrentHashMap<>();

    @Override
    public String generateToken(Long userId) {
        // 生成UUID格式的token
        String token = java.util.UUID.randomUUID().toString().replace("-", "");

        // 存储token信息，设置30分钟过期时间
        TokenInfo tokenInfo = new TokenInfo(userId, LocalDateTime.now().plusMinutes(30));
        TOKEN_STORE.put(token, tokenInfo);

        log.info("为用户{}生成token: {}", userId, token);
        return token;
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        TokenInfo tokenInfo = TOKEN_STORE.get(token);
        if (tokenInfo == null) {
            return false;
        }

        // 检查是否过期
        if (LocalDateTime.now().isAfter(tokenInfo.getExpireTime())) {
            // 过期则删除
            TOKEN_STORE.remove(token);
            return false;
        }

        return true;
    }

    @Override
    public Long getUserIdFromToken(String token) {
        if (!validateToken(token)) {
            return null;
        }

        TokenInfo tokenInfo = TOKEN_STORE.get(token);
        return tokenInfo.getUserId();
    }

    @Override
    public void removeToken(String token) {
        TOKEN_STORE.remove(token);
        log.info("移除token: {}", token);
    }

    // Token信息内部类
    private static class TokenInfo {
        private final Long userId;
        private final LocalDateTime expireTime;

        public TokenInfo(Long userId, LocalDateTime expireTime) {
            this.userId = userId;
            this.expireTime = expireTime;
        }

        public Long getUserId() {
            return userId;
        }

        public LocalDateTime getExpireTime() {
            return expireTime;
        }
    }
}
