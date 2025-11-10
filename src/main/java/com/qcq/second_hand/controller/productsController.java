package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.categories;
import com.qcq.second_hand.entity.other.ProductType;
import com.qcq.second_hand.entity.products;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.FileUploadService;
import com.qcq.second_hand.service.Impl.productsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Tag(name = "商品管理", description = "商品相关接口")
@RestController
@RequestMapping("/products")
public class productsController {

    @Autowired
    productsServiceImpl productsServiceImpl;

//    @Operation(summary = "获取商品列表", description = "获取所有商品列表")
//    @GetMapping("/list")
//    public response getGoodList() {
//        try {
//            System.out.println("开始获取商品列表...");
//            Object result = productsServiceImpl.getProductsList();
//            System.out.println("获取商品列表成功，结果: " + result);
//            return response.success(result);
//        } catch (Exception e) {
//            System.err.println("获取商品列表时发生错误: " + e.getMessage());
//            e.printStackTrace();
//            return new response(500, "服务器内部错误: " + e.getMessage(), null);
//        }
//    }
@Operation(summary = "商品筛选接口", description = "根据不同类型获取所有商品列表")
@GetMapping({"/list", "/filter"})
public response filterProducts(
        @Parameter(description = "校区列表(南区/北区)")
        @RequestParam(name = "campus", required = false) List<String> campuses,
        @Parameter(description = "分类名称列表")
        @RequestParam(name = "categories", required = false) List<String> categoryNames,
        @Parameter(description = "筛选类型: recommend(随机推荐), latest(最新发布), free(免费赠送), wanted(求购专区)")
        @RequestParam(name = "type", required = false, defaultValue = "recommend") String type,
        @Parameter(description = "页码", example = "1")
        @RequestParam(name = "page", defaultValue = "1") int page,
        @Parameter(description = "每页数量", example = "20")
        @RequestParam(name = "size", defaultValue = "20") int size) {
    try {
        List<products> result;

        // 如果提供了校区或分类筛选条件，优先使用这些条件
        if (campuses != null || categoryNames != null) {
            result = productsServiceImpl.filterProductsByCampusAndCategory(campuses, categoryNames);
        } else {
            // 否则按类型筛选
            switch (type.toLowerCase()) {
                case "recommend":
                    result = productsServiceImpl.getRandomProducts(page, size);
                    break;
                case "latest":
                    result = productsServiceImpl.getLatestProducts(page, size);
                    break;
                case "free":
                    result = productsServiceImpl.getFreeProducts(page, size);
                    break;
                case "wanted":
                    result = productsServiceImpl.getWantedProducts(page, size);
                    break;
                default:
                    // 默认返回随机排列商品
                    result = productsServiceImpl.getRandomProducts(page, size);
                    break;
            }
        }

        return response.success(result);
    } catch (Exception e) {
        System.err.println("筛选商品时发生错误: " + e.getMessage());
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

            // 设置默认产品类型（如果未设置）
            if (product.getProductType() == null) {
                product.setProductType(ProductType.SELL); // 默认为卖闲置
            }

            // 验证产品类型
            if (product.getProductType() != ProductType.SELL && product.getProductType() != ProductType.WANT) {
                return new response(400, "无效的产品类型", null);
            }

            Object result = productsServiceImpl.saveProducts(product);
            return response.success(result);
        } catch (Exception e) {
            System.err.println("发布商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }


    @Autowired
    private FileUploadService fileUploadService; // 需要创建此服务

    @Operation(summary = "上传商品图片", description = "上传商品图片并返回访问URL")
    @PostMapping("/upload/image")
    public response uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new response(400, "文件不能为空", null);
            }

            // 上传文件并获取访问URL
            String imageUrl = fileUploadService.uploadFile(file);
            return response.success(imageUrl);
        } catch (Exception e) {
            System.err.println("图片上传失败: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "图片上传失败: " + e.getMessage(), null);
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

    // 替换 productsController.java 中的 updateProducts 方法
    @Operation(summary = "更新商品信息", description = "更新商品信息")
    @PostMapping("/edit")
    public response updateProducts(
            @Parameter(description = "商品信息", required = true)
            @RequestBody products product) {
        try {
            System.out.println("更新商品: " + product);

            // 验证商品是否存在
            if (productsServiceImpl.getProductById(product.getProductId()) == null) {
                return new response(404, "商品不存在", null);
            }

            // 验证必填字段
            if (product.getTitle() != null && product.getTitle().trim().isEmpty()) {
                return new response(400, "商品标题不能为空", null);
            }

            if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                return new response(400, "商品价格不能为负数", null);
            }

            // 图片数据验证
            if (product.getImage() != null) {
                List<String> validImages = product.getImage().stream()
                        .filter(Objects::nonNull)
                        .filter(image -> !image.trim().isEmpty())
                        .collect(Collectors.toList());
                product.setImage(validImages);
            }

            Object result = productsServiceImpl.updateProducts(product);
            return response.success(result);
        } catch (Exception e) {
            System.err.println("更新商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }


    //    @Operation(summary = "按校区和分类筛选商品", description = "根据校区和分类筛选商品列表")
//    @GetMapping("/filter")
//    public response filterProducts(
//            @Parameter(description = "校区列表(南区/北区)")
//            @RequestParam(name = "campus", required = false) List<String> campuses,
//            @Parameter(description = "分类名称列表")
//            @RequestParam(name = "categories", required = false) List<String> categoryNames) {
//        try {
//            System.out.println("筛选商品，校区: " + campuses + ", 分类: " + categoryNames);
//            Object result = productsServiceImpl.filterProductsByCampusAndCategory(campuses, categoryNames);
//            System.out.println("筛选商品成功，结果数量: " + (result instanceof List ? ((List<?>) result).size() : 0));
//            return response.success(result);
//        } catch (Exception e) {
//            System.err.println("筛选商品时发生错误: " + e.getMessage());
//            e.printStackTrace();
//            return new response(500, "服务器内部错误: " + e.getMessage(), null);
//        }
//    }
@Operation(summary = "获取用户收藏商品列表", description = "获取指定用户收藏的所有商品")
@GetMapping("/favorites")
public response getUserFavorites(
        @Parameter(description = "用户ID", required = true)
        @RequestParam(name = "userId") Long userId) {
    try {
        List<products> favorites = productsServiceImpl.getUserFavoriteProducts(userId);
        return response.success(favorites);
    } catch (Exception e) {
        return new response(500, "获取收藏列表失败: " + e.getMessage(), null);
    }
}
// 在 productsController.java 中添加新接口
@Operation(summary = "按产品类型筛选商品", description = "根据产品类型筛选商品列表")
@GetMapping("/by-type")
public response getProductsByType(
        @Parameter(description = "产品类型(SELL/WANT)", required = true)
        @RequestParam(name = "type") String type) {
    try {
        ProductType productType;
        if ("SELL".equalsIgnoreCase(type)) {
            productType = ProductType.SELL;
        } else if ("WANT".equalsIgnoreCase(type)) {
            productType = ProductType.WANT;
        } else {
            return new response(400, "无效的产品类型，仅支持 SELL 或 WANT", null);
        }

        List<products> result = productsServiceImpl.getProductsByType(productType);
        return response.success(result);
    } catch (Exception e) {
        System.err.println("按类型筛选商品时发生错误: " + e.getMessage());
        e.printStackTrace();
        return new response(500, "服务器内部错误: " + e.getMessage(), null);
    }
}
    @Operation(summary = "获取推荐商品", description = "根据当前商品分类推荐4-8个相似商品")
    @GetMapping("/recommend")
    public response getRecommendedProducts(
            @Parameter(description = "当前商品ID", required = true)
            @RequestParam(name = "productId") Long productId,
            @Parameter(description = "推荐数量(4-8之间，默认随机)")
            @RequestParam(name = "limit", required = false) Integer limit) {
        try {
            // 如果没有指定数量，则随机生成4-8之间的数量
            int actualLimit = 4;
            if (limit == null) {
                Random random = new Random();
                actualLimit = 4 + random.nextInt(5); // 4-8之间随机
            } else {
                actualLimit = Math.max(4, Math.min(8, limit));
            }

            System.out.println("获取推荐商品，当前商品ID: " + productId + ", 推荐数量: " + actualLimit);
            List<products> result = productsServiceImpl.getRecommendedProducts(productId, actualLimit);
            System.out.println("获取推荐商品成功，结果数量: " + result.size());
            return response.success(result);
        } catch (Exception e) {
            System.err.println("获取推荐商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }
    @Operation(summary = "获取用户已售出的商品", description = "获取指定用户已售出的商品")
    @GetMapping("/sold-by-user")
    public response getSoldProductsByUser(
            @Parameter(description = "用户ID", required = true)
            @RequestParam(name = "userId") Long userId) {
        try {
            System.out.println("获取用户已售出的商品，用户ID: " + userId);
            List<products> result = productsServiceImpl.getSoldProductsByUserId(userId);
            System.out.println("获取用户已售出的商品成功，结果数量: " + result.size());
            return response.success(result);
        } catch (Exception e) {
            System.err.println("获取用户已售出的商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }


    @Operation(summary = "获取用户购买的商品", description = "获取指定用户购买的所有商品")
    @GetMapping("/purchased-by-user")
    public response getPurchasedProductsByUser(
            @Parameter(description = "用户ID", required = true)
            @RequestParam(name = "userId") Long userId) {
        try {
            System.out.println("获取用户购买的商品，用户ID: " + userId);
            List<products> result = productsServiceImpl.getPurchasedProductsByUserId(userId);
            System.out.println("获取用户购买的商品成功，结果数量: " + result.size());
            return response.success(result);
        } catch (Exception e) {
            System.err.println("获取用户购买的商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }
    @Operation(summary = "获取用户发布的商品", description = "获取指定用户发布的所有商品（包括已售出和未售出）")
    @GetMapping("/published-by-user")
    public response getPublishedProductsByUser(
            @Parameter(description = "用户ID", required = true)
            @RequestParam(name = "userId") Long userId) {
        try {
            System.out.println("获取用户发布的商品，用户ID: " + userId);
            List<products> result = productsServiceImpl.getPublishedProductsByUserId(userId);
            System.out.println("获取用户发布的商品成功，结果数量: " + result.size());
            return response.success(result);
        } catch (Exception e) {
            System.err.println("获取用户发布的商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }
    @Operation(summary = "删除用户发布的商品", description = "删除指定用户发布的商品（仅限未售出的商品）")
    @PostMapping("/delete")
    public response deleteProductByUser(
            @Parameter(description = "用户ID", required = true)
            @RequestParam(name = "userId") Long userId,
            @Parameter(description = "商品ID", required = true)
            @RequestParam(name = "productId") Long productId) {
        try {
            System.out.println("删除用户发布的商品，用户ID: " + userId + ", 商品ID: " + productId);
            productsServiceImpl.deleteProductByUserId(userId, productId);
            System.out.println("删除用户发布的商品成功");
            return response.success("删除成功");
        } catch (Exception e) {
            System.err.println("删除用户发布的商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "删除失败: " + e.getMessage(), null);
        }
    }


}
