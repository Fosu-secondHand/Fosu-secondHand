package com.qcq.second_hand.service;

import com.qcq.second_hand.entity.Reports;

import java.util.List;

public interface ReportsService {
    Reports createReport(Reports reports);
    List<Reports> getReportsByReporterId(Long reporterId);
    List<Reports> getReportsByReportedProductId(Long productId);
    List<Reports> getReportsByReportedUserId(Long userId);
    Reports updateHandleResult(Long reportId, String handleResult);
    void deleteReport(Long reportId);
}
