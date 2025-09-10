package com.qcq.second_hand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qcq.second_hand.entity.Users;
import java.util.List;

public interface UsersService {
    Users saveUser(Users user);
    Users getUserById(Long userId);
    List<Users> getAllUsers();
    Users updateUser(Users user);
    void deleteUser(Long userId);
    Users getUserByOpenid(String openid);
    Users getUserByPhone(String phone);
}