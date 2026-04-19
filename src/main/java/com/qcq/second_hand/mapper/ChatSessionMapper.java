package com.qcq.second_hand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qcq.second_hand.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession>{

    List<ChatSession> selectByUserId(@Param("userId") Long userId);


}
