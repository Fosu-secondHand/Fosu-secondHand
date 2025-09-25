package com.qcq.second_hand.service;


import com.qcq.second_hand.entity.categories;
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

    public void toggleStar(Long userId,Long productId,String method);

    public void toggleWant(Long userId,Long productId,String method);

    public products updateProducts(products product);
}
