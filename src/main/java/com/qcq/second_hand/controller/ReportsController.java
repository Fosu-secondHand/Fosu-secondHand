package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.Reports;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportsController {

    private final ReportsService reportsService;

    @Autowired
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    // 创建举报记录
    @PostMapping
    public response<Reports> createReport(@RequestBody Reports reports) {
        return response.success(reportsService.createReport(reports));
    }

    // 根据举报人获取举报记录
    @GetMapping("/reporter/{reporterId}")
    public response<List<Reports>> getReportsByReporterId(@PathVariable Long reporterId) {
        return response.success(reportsService.getReportsByReporterId(reporterId));
    }

    // 根据被举报商品获取举报记录
    @GetMapping("/product/{productId}")
    public response<List<Reports>> getReportsByReportedProductId(@PathVariable Long productId) {
        return response.success(reportsService.getReportsByReportedProductId(productId));
    }

    // 根据被举报用户获取举报记录
    @GetMapping("/user/{userId}")
    public response<List<Reports>> getReportsByReportedUserId(@PathVariable Long userId) {
        return response.success(reportsService.getReportsByReportedUserId(userId));
    }

    // 更新处理结果
    @PutMapping("/{reportId}/handle-result")
    public response<Reports> updateHandleResult(
            @PathVariable Long reportId,
            @RequestParam String handleResult) {
        return response.success(reportsService.updateHandleResult(reportId, handleResult));
    }

    // 删除举报记录
    @DeleteMapping("/{reportId}")
    public response<String> deleteReport(@PathVariable Long reportId) {
        reportsService.deleteReport(reportId);
        return response.success("举报记录删除成功");
    }
}
