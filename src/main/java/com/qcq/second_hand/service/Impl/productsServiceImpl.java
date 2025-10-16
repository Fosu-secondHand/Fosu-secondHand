package com.qcq.second_hand.service.Impl;

import com.qcq.second_hand.entity.*;
import com.qcq.second_hand.repository.categoriesRepository;
import com.qcq.second_hand.repository.favoriteRepository;
import com.qcq.second_hand.repository.productsRepository;
import com.qcq.second_hand.repository.wishItemRepository;
import com.qcq.second_hand.repository.usersRepository;
import com.qcq.second_hand.service.productsService;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;


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

    @Autowired
    private EntityManager entityManager;


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

    @Override
    public List<products> filterProductsByCampusAndCategory(List<String> campuses, List<String> categoryNames) {
        // 验证校区参数
        if (campuses != null) {
            List<String> validCampuses = Arrays.asList("南区", "北区");
            campuses = campuses.stream()
                    .filter(validCampuses::contains)
                    .collect(Collectors.toList());
        }

        // 验证分类参数
        if (categoryNames != null) {
            List<String> validCategories = Arrays.asList("教材书籍", "服饰鞋包", "生活用品", "数码产品", "美妆个护", "交通出行", "其他闲置");
            categoryNames = categoryNames.stream()
                    .filter(validCategories::contains)
                    .collect(Collectors.toList());
        }

        // 构建查询条件
        StringBuilder jpql = new StringBuilder("SELECT p FROM products p WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (campuses != null && !campuses.isEmpty()) {
            jpql.append(" AND p.campus IN :campuses");
            params.put("campuses", campuses);
        }

        if (categoryNames != null && !categoryNames.isEmpty()) {
            jpql.append(" AND p.category.name IN :categoryNames");
            params.put("categoryNames", categoryNames);
        }

        jpql.append(" ORDER BY p.postTime DESC");

        TypedQuery<products> query = entityManager.createQuery(jpql.toString(), products.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

}
