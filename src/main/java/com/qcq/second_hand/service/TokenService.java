// src/main/java/com/qcq/second_hand/service/TokenService.java
package com.qcq.second_hand.service;

import java.util.Map;

public interface TokenService {
    String generateToken(Long userId);
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
    void removeToken(String token);
}
