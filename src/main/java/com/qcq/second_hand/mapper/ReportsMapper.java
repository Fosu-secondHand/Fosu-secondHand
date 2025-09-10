package com.qcq.second_hand.mapper;

import com.qcq.second_hand.entity.Reports;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportsMapper extends BaseMapper<Reports> {
    List<Reports> findByReporterId(Long reporterId);
    List<Reports> findByReportedProductId(Long productId);
    List<Reports> findByReportedUserId(Long userId);
}
