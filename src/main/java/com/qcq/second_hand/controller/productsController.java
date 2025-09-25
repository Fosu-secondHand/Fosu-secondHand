package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.categories;
import com.qcq.second_hand.entity.products;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.Impl.productsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理", description = "商品相关接口")
@RestController
@RequestMapping("/products")
public class productsController {

    @Autowired
    productsServiceImpl productsServiceImpl;

    @Operation(summary = "获取商品列表", description = "获取所有商品列表")
    @GetMapping("/list")
    public response getGoodList() {
        try {
            System.out.println("开始获取商品列表...");
            Object result = productsServiceImpl.getProductsList();
            System.out.println("获取商品列表成功，结果: " + result);
            return response.success(result);
        } catch (Exception e) {
            System.err.println("获取商品列表时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "关键字模糊查询商品", description = "根据关键字模糊查询商品")
    @GetMapping("/search")
    public response searchGoods(
            @Parameter(description = "搜索关键字", required = true)
            @RequestParam(name = "keyword") String keyword) {
        try {
            System.out.println("开始搜索商品，关键词: " + keyword);
            Object result = productsServiceImpl.searchProductList(keyword);
            System.out.println("搜索商品成功，结果: " + result);
            return response.success(result);
        } catch (Exception e) {
            System.err.println("搜索商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "获取商品详细信息", description = "根据商品ID获取商品详细信息")
    @GetMapping("/detail")
    public response getGoodDetail(
            @Parameter(description = "商品ID", required = true)
            @RequestParam(name = "productId") Long productId) {
        try {
            System.out.println("开始获取商品详情，商品ID: " + productId);
            Object result = productsServiceImpl.getGoodDetail(productId);
            System.out.println("获取商品详情成功，结果: " + result);
            return response.success(result);
        } catch (Exception e) {
            System.err.println("获取商品详情时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "更新收藏", description = "更新商品收藏状态")
    @GetMapping("/detail/toggleStar")
    public response toggleStar(
            @Parameter(description = "用户ID", required = true)
            @RequestParam(name = "userId") Long userId,
            @Parameter(description = "商品ID", required = true)
            @RequestParam(name = "productId") Long productId,
            @Parameter(description = "操作方法(add/reduce)", required = true)
            @RequestParam(name = "reduceOrAdd") String method) {
        try {
            System.out.println("更新收藏，用户ID: " + userId + ", 商品ID: " + productId + ", 方法: " + method);
            productsServiceImpl.toggleStar(userId, productId, method);
            return response.success("更新收藏成功");
        } catch (Exception e) {
            System.err.println("更新收藏时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "更新想要", description = "更新商品想要状态")
    @GetMapping("/detail/toggleWant")
    public response toggleWant(
            @Parameter(description = "用户ID", required = true)
            @RequestParam(name = "userId") Long userId,
            @Parameter(description = "商品ID", required = true)
            @RequestParam(name = "productId") Long productId,
            @Parameter(description = "操作方法(add/reduce)", required = true)
            @RequestParam(name = "reduceOrAdd") String method) {
        try {
            System.out.println("更新想要，用户ID: " + userId + ", 商品ID: " + productId + ", 方法: " + method);
            productsServiceImpl.toggleWant(userId, productId, method);
            return response.success("更新想要成功");
        } catch (Exception e) {
            System.err.println("更新想要时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "上传商品", description = "发布新商品")
    @PostMapping("/publish")
    public response postProducts(
            @Parameter(description = "商品信息", required = true)
            @RequestBody products product) {
        try {
            System.out.println("发布商品: " + product);
            Object result = productsServiceImpl.saveProducts(product);
            return response.success(result);
        } catch (Exception e) {
            System.err.println("发布商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "上传新的商品分类", description = "创建新的商品分类")
    @PostMapping("/postCategories")
    public response postCategories(
            @Parameter(description = "分类信息", required = true)
            @RequestBody categories categories) {
        try {
            System.out.println("创建分类: " + categories);
            Object result = productsServiceImpl.saveCategories(categories);
            return response.success(result);
        } catch (Exception e) {
            System.err.println("创建分类时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "更新商品信息", description = "更新商品信息")
    @PostMapping("/edit")
    public response updateProducts(
            @Parameter(description = "商品信息", required = true)
            @RequestBody products product) {
        try {
            System.out.println("更新商品: " + product);
            Object result = productsServiceImpl.updateProducts(product);
            return response.success(result);
        } catch (Exception e) {
            System.err.println("更新商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }
}
