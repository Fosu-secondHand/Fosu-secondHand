package com.qcq.second_hand.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.entity.products; // 添加导入
import com.qcq.second_hand.entity.other.Status;
import com.qcq.second_hand.mapper.UsersMapper;
import com.qcq.second_hand.mapper.ProductsMapper; // 添加导入
import com.qcq.second_hand.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;


@Service
@RequiredArgsConstructor
@Transactional
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    private static final Integer DEFAULT_CREDIT_SCORE = 100;
    private final ProductsMapper productsMapper;


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

        LambdaUpdateWrapper<Users> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Users::getUserId, user.getUserId());

        if (user.getUsername() != null) {
            updateWrapper.set(Users::getUsername, user.getUsername());
        }
        if (user.getAvatar() != null) {
            updateWrapper.set(Users::getAvatar, user.getAvatar());
        }
        if (user.getPhone() != null) {
            updateWrapper.set(Users::getPhone, user.getPhone());
        }
        if (user.getCreditScore() != null) {
            updateWrapper.set(Users::getCreditScore, user.getCreditScore());
        }
        if (user.getStatus() != null) {
            updateWrapper.set(Users::getStatus, user.getStatus());
        }
        if (user.getAddress() != null) {
            updateWrapper.set(Users::getAddress, user.getAddress());
        }
        if (user.getNickname() != null) {
            updateWrapper.set(Users::getNickname, user.getNickname());
        }
        if (user.getGender() != null) {
            updateWrapper.set(Users::getGender, user.getGender());
        }

        updateWrapper.set(Users::getLastLogin, LocalDateTime.now());
        update(null, updateWrapper);

        // 更新existingUser对象以返回最新数据
        if (user.getUsername() != null) existingUser.setUsername(user.getUsername());
        if (user.getAvatar() != null) existingUser.setAvatar(user.getAvatar());
        if (user.getPhone() != null) existingUser.setPhone(user.getPhone());
        if (user.getCreditScore() != null) existingUser.setCreditScore(user.getCreditScore());
        if (user.getStatus() != null) existingUser.setStatus(user.getStatus());
        if (user.getAddress() != null) existingUser.setAddress(user.getAddress());
        if (user.getNickname() != null) existingUser.setNickname(user.getNickname());
        if (user.getGender() != null) existingUser.setGender(user.getGender());
        existingUser.setLastLogin(LocalDateTime.now());

        return existingUser;
    }





    @Override
    public void deleteUser(Long userId) {
        removeById(userId);
    }

    @Override
    public Users getUserByOpenid(String openid) {
        // 应该在找不到用户时抛出异常，而不是返回null
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
    @Override
    public List<products> getUserSellingProducts(Long userId) {
        // 查询用户所有的商品（不管状态如何）
        return productsMapper.selectAllProductsByUserId(userId);
    }



}
