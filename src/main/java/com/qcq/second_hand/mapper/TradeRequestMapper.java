// TradeRequestMapper.java
package com.qcq.second_hand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qcq.second_hand.entity.TradeRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TradeRequestMapper extends BaseMapper<TradeRequest> {
    List<TradeRequest> selectReceivedRequests(@Param("userId") Long userId);
    List<TradeRequest> selectSentRequests(@Param("userId") Long userId);
}
