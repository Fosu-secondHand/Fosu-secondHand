package com.qcq.second_hand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qcq.second_hand.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface ChatSessionMapper{

    List<ChatSession> selectByUserId(Long id);


}
