package com.qcq.second_hand.service.Impl;

import com.qcq.second_hand.entity.*;
import com.qcq.second_hand.repository.categoriesRepository;
import com.qcq.second_hand.repository.favoriteRepository;
import com.qcq.second_hand.repository.productsRepository;
import com.qcq.second_hand.repository.wishItemRepository;
import com.qcq.second_hand.repository.usersRepository;
import com.qcq.second_hand.service.productsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class productsServiceImpl implements productsService
{

    @Autowired
    categoriesRepository categoriesRepository;

    @Autowired
    productsRepository productsRepository;

    @Autowired
    usersRepository usersRepository;

    @Autowired
    wishItemRepository wishItemRepository;

    @Autowired
    favoriteRepository favoriteRepository;


    public categories saveCategories(categories categories)
    {
        return categoriesRepository.save(categories);
    }

    public products saveProducts(products products)
    {
        return productsRepository.save(products);
    }

    public List<products> getProductsList()
    {
        return productsRepository.findAll();
    }

    public List<products> searchProductList(String keyword)
    {
        return productsRepository.findByKeyword(keyword);
    }

    public products getGoodDetail(Long productId) {
        productsRepository.updateViewCount(productId);
        products product = productsRepository.findByProductId(productId);
        if (product != null && product.getSeller() != null) {
            // 设置卖家地址
            product.setAddress(product.getSeller().getAddress());
        }
        return product;
    }

    /*
    * 更新收藏
    * */
    public void toggleStar(Long userId,Long productId,String method) {
        Users user = usersRepository.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        products product = productsRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        boolean exists = favoriteRepository.existsByUserAndProduct(user, product);
        if (method.equals("reduce"))
        {
            if (!exists) {
                throw new RuntimeException("This favorite not in the user's want list");
            }

            favorite like = favoriteRepository.findByUserAndProduct(user,product);

            favoriteRepository.deleteById(like.getId());
            productsRepository.reduceFavoriteCount(productId);
        }
        else if (method.equals("add"))
        {
            if (exists) {
                throw new RuntimeException("This favorite is already in the user's want list");
            }

            favorite like = new favorite();
            like.setUser(user);
            like.setProduct(product);
            like.setCreatedAt(LocalDateTime.now());

            favoriteRepository.save(like);
            productsRepository.addFavoriteCount(productId);
        }
    }

    /*
    * 更新想要
    * */
    public void toggleWant(Long userId,Long productId,String method)
    {
        Users user = usersRepository.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        products product = productsRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        boolean exists = wishItemRepository.existsByUserAndProduct(user, product);
        if (method.equals("reduce"))
        {
            if (!exists) {
                throw new RuntimeException("This wish not in the user's want list");
            }

            wishItem want = wishItemRepository.findByUserAndProduct(user,product);

            wishItemRepository.deleteById(want.getId());
            productsRepository.reduceWishCount(productId);
        }
        else if (method.equals("add"))
        {
            if (exists) {
                throw new RuntimeException("This wish is already in the user's want list");
            }

            wishItem want = new wishItem();
            want.setUser(user);
            want.setProduct(product);
            want.setCreatedAt(LocalDateTime.now());

            wishItemRepository.save(want);
            productsRepository.addWishCount(productId);
        }
    }

    public products updateProducts(products product)
    {
        if(productsRepository.findByProductId(product.getProductId())==null)
        {
            throw new RuntimeException("要更新的商品id不存在");
        }

        return productsRepository.save(product);
    }
}
