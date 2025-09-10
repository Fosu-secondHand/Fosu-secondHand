package com.qcq.second_hand.mapper;

import com.qcq.second_hand.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    Users findByOpenid(String openid);
    Users findByPhone(String phone);
}