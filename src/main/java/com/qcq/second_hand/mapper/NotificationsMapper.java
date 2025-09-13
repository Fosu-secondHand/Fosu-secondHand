package com.qcq.second_hand.mapper;

import com.qcq.second_hand.entity.Notifications;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface NotificationsMapper extends BaseMapper<Notifications> {
    List<Notifications> findByUserIdOrderByCreateTimeDesc(Long userId);
    List<Notifications> findByUserIdAndIsRead(Long userId, boolean isRead);
    Long countByUserIdAndIsRead(Long userId, boolean isRead);
    void updateAllUnreadToReadByUserId(Long userId);
}
