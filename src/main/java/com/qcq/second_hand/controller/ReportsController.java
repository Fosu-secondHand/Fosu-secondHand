package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.Reports;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "举报管理", description = "举报相关接口")
@RestController
@RequestMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportsController {

    private final ReportsService reportsService;

    @Autowired
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @Operation(summary = "创建举报记录", description = "创建新的举报记录")
    @PostMapping
    public response<Reports> createReport(
            @Parameter(description = "举报信息", required = true)
            @RequestBody Reports reports) {
        return response.success(reportsService.createReport(reports));
    }

    @Operation(summary = "根据举报人获取举报记录", description = "根据举报人ID获取举报记录")
    @GetMapping("/reporter/{reporterId}")
    public response<List<Reports>> getReportsByReporterId(
            @Parameter(description = "举报人ID", required = true)
            @PathVariable Long reporterId) {
        return response.success(reportsService.getReportsByReporterId(reporterId));
    }

    @Operation(summary = "根据被举报商品获取举报记录", description = "根据被举报商品ID获取举报记录")
    @GetMapping("/product/{productId}")
    public response<List<Reports>> getReportsByReportedProductId(
            @Parameter(description = "被举报商品ID", required = true)
            @PathVariable Long productId) {
        return response.success(reportsService.getReportsByReportedProductId(productId));
    }

    @Operation(summary = "根据被举报用户获取举报记录", description = "根据被举报用户ID获取举报记录")
    @GetMapping("/user/{userId}")
    public response<List<Reports>> getReportsByReportedUserId(
            @Parameter(description = "被举报用户ID", required = true)
            @PathVariable Long userId) {
        return response.success(reportsService.getReportsByReportedUserId(userId));
    }

    @Operation(summary = "更新处理结果", description = "根据举报ID更新处理结果")
    @PutMapping("/{reportId}/handle-result")
    public response<Reports> updateHandleResult(
            @Parameter(description = "举报ID", required = true)
            @PathVariable Long reportId,
            @Parameter(description = "处理结果", required = true)
            @RequestParam String handleResult) {
        return response.success(reportsService.updateHandleResult(reportId, handleResult));
    }

    @Operation(summary = "删除举报记录", description = "根据举报ID删除举报记录")
    @DeleteMapping("/{reportId}")
    public response<String> deleteReport(
            @Parameter(description = "举报ID", required = true)
            @PathVariable Long reportId) {
        reportsService.deleteReport(reportId);
        return response.success("举报记录删除成功");
    }
}
