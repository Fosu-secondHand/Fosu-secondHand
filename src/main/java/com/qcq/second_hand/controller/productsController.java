package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.categories;
import com.qcq.second_hand.entity.products;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.Impl.productsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class productsController {

    @Autowired
    productsServiceImpl productsServiceImpl;

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
    public response postProducts(@RequestBody products product)
    {
        return response.success(productsServiceImpl.saveProducts(product));

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
