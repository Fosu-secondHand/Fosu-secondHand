package com.qcq.second_hand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qcq.second_hand.entity.Messages;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessagesMapper extends BaseMapper<Messages> {
    // 如果有自定义查询方法，可以在这里添加
    // 目前继承 BaseMapper 已经提供了基础的 CRUD 方法
}
