package com.qcq.second_hand.service.Impl;

import com.qcq.second_hand.entity.Reports;
import com.qcq.second_hand.mapper.ReportsMapper;
import com.qcq.second_hand.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportsServiceImpl implements ReportsService {

    private final ReportsMapper reportsMapper;

    @Override
    public Reports createReport(Reports reports) {
        reportsMapper.insert(reports);
        return reports;
    }

    @Override
    public List<Reports> getReportsByReporterId(Long reporterId) {
        return reportsMapper.findByReporterId(reporterId);
    }

    @Override
    public List<Reports> getReportsByReportedProductId(Long productId) {
        return reportsMapper.findByReportedProductId(productId);
    }

    @Override
    public List<Reports> getReportsByReportedUserId(Long userId) {
        return reportsMapper.findByReportedUserId(userId);
    }

    @Override
    public Reports updateHandleResult(Long reportId, String handleResult) {
        Reports report = reportsMapper.selectById(reportId);
        if (report == null) {
            throw new RuntimeException("举报记录不存在: " + reportId);
        }
        report.setHandleResult(handleResult);
        reportsMapper.updateById(report);
        return report;
    }

    @Override
    public void deleteReport(Long reportId) {
        reportsMapper.deleteById(reportId);
    }
}
