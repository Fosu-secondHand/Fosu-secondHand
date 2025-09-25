package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.categories;
import com.qcq.second_hand.entity.other.Condition;
import com.qcq.second_hand.entity.other.TransactionMethod;
import com.qcq.second_hand.entity.other.productStatus;
import com.qcq.second_hand.entity.products;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.Impl.productsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class productsController {

    @Autowired
    productsServiceImpl productsServiceImpl;


    @Value("${app.cover.permanent-path}")
    private String coverPermanentDir; // 图片永久存储目录

    /*
    * 获取商品列表
    * */
    @GetMapping("/list")
    public response getGoodList()
    {
        return response.success(productsServiceImpl.getProductsList());
    }

    /*
    * 关键字模糊查询商品
    * */
    @GetMapping("/search")
    public response searchGoods(@RequestParam(name = "keyword") String keyword)
    {
        return response.success(productsServiceImpl.searchProductList(keyword));
    }

    /*
    * 获取商品详细信息
    * */
    @GetMapping("/detail")
    public response getGoodDetail(@RequestParam(name = "productId") Long productId)
    {
        return response.success(productsServiceImpl.getGoodDetail(productId));
    }

    /*
    * 更新收藏
    * */
    @GetMapping("/detail/toggleStar")
    public response toggleStar(@RequestParam(name = "userId") Long userId,@RequestParam(name = "productId") Long productId ,@RequestParam(name = "reduceOrAdd") String method)
    {
        productsServiceImpl.toggleStar(userId,productId,method);
        return response.success("更新收藏成功");
    }

    /*
    * 更新想要
    * */
    @GetMapping("/detail/toggleWant")
    public response toggleWant(@RequestParam(name = "userId") Long userId,@RequestParam(name = "productId") Long productId ,@RequestParam(name = "reduceOrAdd") String method)
    {
        productsServiceImpl.toggleWant(userId,productId,method);
        return response.success("更新想要成功");
    }


    /*
    * 上传商品
    * */
    @PostMapping("/publish")
    public response postProducts(@RequestParam("sellerId") Long sellerId,
                                 @RequestParam("coverImage") List<MultipartFile> coverImages,
                                 @RequestParam("title") String title,
                                 @RequestParam("transactionMethod") TransactionMethod transactionMethod,
                                 @RequestParam("description") String description,
                                 @RequestParam("categoryId") Long categoryId,
                                 @RequestParam("price") BigDecimal price,
                                 @RequestParam("condition") Condition condition,
                                 @RequestParam("image") List<MultipartFile> image,
                                 @RequestParam("postTime") LocalDateTime postTime,
                                 @RequestParam("updateTime") LocalDateTime updateTime,
                                 @RequestParam("viewCount") Integer viewCount,
                                 @RequestParam("campus") String campus,
                                 @RequestParam("status") productStatus status,
                                 @RequestParam("favoriteCount") Long favoriteCount,
                                 @RequestParam("wantToBuy") Long wantToBuy
                                 ) throws Exception
    {
        List<String> img_list=new ArrayList<>();
        for(MultipartFile x:image)
        {
            String coverOriginalName = x.getOriginalFilename();
            String coverSuffix = coverOriginalName.substring(coverOriginalName.lastIndexOf("."));
            String coverUniqueName = UUID.randomUUID().toString() + coverSuffix;
            Path coverSavePath = Paths.get(coverPermanentDir + coverUniqueName);
            x.transferTo(coverSavePath);
            img_list.add(coverSavePath.toString());
        }

        products p=new products(sellerId,  title,  description,  transactionMethod,
             categoryId,  price,  condition,  img_list,
             status,  postTime,  updateTime,
             viewCount,  campus, favoriteCount, wantToBuy);

        return response.success(productsServiceImpl.saveProducts(p));
    }

    /*
    * 上传一个新的种类
    * */
    @PostMapping("/postCategories")
    public response postCategories(@RequestBody categories categories)
    {
        return response.success(productsServiceImpl.saveCategories(categories));
    }

    /*
    * 更新商品的信息
    * */
    @PostMapping("/edit")
    public response updateProducts(@RequestBody products product)
    {
        return response.success(productsServiceImpl.updateProducts(product));
    }


}
