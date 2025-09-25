package com.qcq.second_hand.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.entity.other.Status;
import com.qcq.second_hand.mapper.UsersMapper;
import com.qcq.second_hand.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    private static final Integer DEFAULT_CREDIT_SCORE = 100;

    @Override
    public Users saveUser(Users user) {
        if (user.getDatetime() == null) {
            user.setDatetime(LocalDateTime.now());
        }
        if (user.getLastLogin() == null) {
            user.setLastLogin(LocalDateTime.now());
        }

        if (user.getCreditScore() == null) {
            user.setCreditScore(DEFAULT_CREDIT_SCORE);
        }
        if (user.getStatus() == null) {
            user.setStatus(Status.ACTIVE);
        }
        save(user);
        return user;
    }

    @Override
    public Users getUserById(Long userId) {
        Users user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在: " + userId);
        }
        return user;
    }

    @Override
    public List<Users> getAllUsers() {
        return list();
    }

    @Override
    public Users updateUser(Users user) {
        Users existingUser = getUserById(user.getUserId());

        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getCreditScore() != null) {
            existingUser.setCreditScore(user.getCreditScore());
        }
        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
        }

        existingUser.setLastLogin(LocalDateTime.now());
        updateById(existingUser);
        return existingUser;
    }

    @Override
    public void deleteUser(Long userId) {
        removeById(userId);
    }

    @Override
    public Users getUserByOpenid(String openid) {
        Users user = baseMapper.findByOpenid(openid);
        if (user == null) {
            throw new RuntimeException("用户不存在: " + openid);
        }
        return user;
    }

    @Override
    public Users getUserByPhone(String phone) {
        Users user = baseMapper.findByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在: " + phone);
        }
        return user;
    }
}
