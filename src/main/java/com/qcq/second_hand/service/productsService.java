package com.qcq.second_hand.service;


import com.qcq.second_hand.entity.categories;
import com.qcq.second_hand.entity.other.ProductType;
import com.qcq.second_hand.entity.products;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface productsService {

    public categories saveCategories(categories categories);

    public products saveProducts(products products);

    public List<products> getProductsList();

    public List<products> searchProductList(String keyword);

    public products getGoodDetail(Long productId);

    products getProductById(Long productId);

    public void toggleStar(Long userId,Long productId,String method);

    public void toggleWant(Long userId,Long productId,String method);

    void deleteProductByUserId(Long userId, Long productId);

    public products updateProducts(products product);

    public List<products> filterProductsByCampusAndCategory(List<String> campuses, List<String> categoryNames);
    public List<products> getProductsByType(ProductType productType);
    public List<products> getRandomProducts(int page, int size);
    public List<products> getLatestProducts(int page, int size);
    public List<products> getFreeProducts(int page, int size);
    public List<products> getWantedProducts(int page, int size);
    // 获取用户卖出的所有商品
    List<products> getSoldProductsByUserId(Long userId);
    // 获取用户买到的所有商品
    List<products> getPurchasedProductsByUserId(Long userId);
    List<products> getRecommendedProducts(Long productId, int limit);
    List<products> getPublishedProductsByUserId(Long userId);

}