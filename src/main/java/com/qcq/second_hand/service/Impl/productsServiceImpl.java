package com.qcq.second_hand.service.Impl;

import com.qcq.second_hand.entity.*;
import com.qcq.second_hand.entity.other.ProductType;
import com.qcq.second_hand.entity.other.productStatus;
import com.qcq.second_hand.mapper.ProductsMapper;
import com.qcq.second_hand.repository.categoriesRepository;
import com.qcq.second_hand.repository.favoriteRepository;
import com.qcq.second_hand.repository.productsRepository;
import com.qcq.second_hand.repository.wishItemRepository;
import com.qcq.second_hand.repository.usersRepository;
import com.qcq.second_hand.service.UsersService;
import com.qcq.second_hand.service.productsService;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @Autowired
    private UsersService usersService;

    @Autowired
    ProductsMapper productsMapper;
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

    // 替换 productsServiceImpl.java 中的 updateProducts 方法
    public products updateProducts(products product) {
        // 获取数据库中现有的商品信息
        products existingProduct = productsRepository.findByProductId(product.getProductId());
        if (existingProduct == null) {
            throw new RuntimeException("要更新的商品id不存在");
        }

        // 保留关键字段的原值，防止被null覆盖
        if (product.getSellerId() == null) {
            product.setSellerId(existingProduct.getSellerId());
        }
        if (product.getTitle() == null) {
            product.setTitle(existingProduct.getTitle());
        }
        if (product.getTransactionMethod() == null) {
            product.setTransactionMethod(existingProduct.getTransactionMethod());
        }
        if (product.getCategoryId() == null) {
            product.setCategoryId(existingProduct.getCategoryId());
        }
        if (product.getPrice() == null) {
            product.setPrice(existingProduct.getPrice());
        }
        if (product.getImage() == null) {
            product.setImage(existingProduct.getImage());
        }
        if (product.getPostTime() == null) {
            product.setPostTime(existingProduct.getPostTime());
        }
        if (product.getViewCount() == null) {
            product.setViewCount(existingProduct.getViewCount());
        }
        if (product.getCampus() == null) {
            product.setCampus(existingProduct.getCampus());
        }
        if (product.getWantToBuy() == null) {
            product.setWantToBuy(existingProduct.getWantToBuy());
        }
        if (product.getFavoriteCount() == null) {
            product.setFavoriteCount(existingProduct.getFavoriteCount());
        }
        if (product.getProductType() == null) {
            product.setProductType(existingProduct.getProductType());
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
    public List<products> getUserFavoriteProducts(Long userId) {
        Users user = usersService.getUserById(userId); // 修改这行
        List<favorite> favorites = favoriteRepository.findByUser(user);
        return favorites.stream()
                .map(favorite::getProduct)
                .collect(Collectors.toList());
    }

    public List<products> getProductsByType(ProductType productType) {
        return productsRepository.findByProductType(productType);
    }
    public List<products> getRandomProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<products> pageResult = productsRepository.findRandomProductsByProductType(ProductType.SELL, pageable);
        return pageResult.getContent();
    }


    public List<products> getLatestProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "postTime"));
        Page<products> pageResult = productsRepository.findByProductTypeAndStatus(ProductType.SELL, pageable);
        return pageResult.getContent();
    }

    public List<products> getFreeProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "postTime"));
        Page<products> pageResult = productsRepository.findByPriceAndProductType(BigDecimal.ZERO, ProductType.SELL, pageable);
        return pageResult.getContent();
    }

    public List<products> getWantedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "postTime"));
        Page<products> pageResult = productsRepository.findByProductType(ProductType.WANT, pageable);
        return pageResult.getContent();
    }
    // 实现商品推荐方法
    public List<products> getRecommendedProducts(Long productId, int limit) {
        // 获取当前商品信息
        products currentProduct = productsRepository.findByProductId(productId);
        if (currentProduct == null) {
            throw new RuntimeException("商品不存在");
        }

        // 限制推荐数量在4-8之间
        int actualLimit = Math.max(4, Math.min(8, limit));

        // 调用Mapper查询推荐商品
        return productsMapper.selectRecommendedProductsByCategoryId(
                currentProduct.getCategoryId(),
                productId,
                actualLimit
        );
    }
    // 实现获取用户卖出的所有商品
    // 实现获取用户已售出的商品
    public List<products> getSoldProductsByUserId(Long userId) {
        return productsRepository.findBySellerIdAndStatus(userId, productStatus.SOLD);
    }


    // 实现获取用户买到的所有商品
    public List<products> getPurchasedProductsByUserId(Long userId) {
        return productsMapper.selectAllProductsByBuyerId(userId);
    }
    // 实现获取用户发布的所有商品
public List<products> getPublishedProductsByUserId(Long userId) {
    return productsRepository.findBySellerId(userId);
}
    public void deleteProductByUserId(Long userId, Long productId) {
        products product = productsRepository.findByProductId(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        if (!product.getSellerId().equals(userId)) {
            throw new RuntimeException("无权限删除该商品");
        }

        // 检查商品是否已被购买（状态为SOLD）
        if (product.getStatus() == productStatus.SOLD) {
            throw new RuntimeException("已售出的商品不能删除");
        }

        try {
            productsRepository.deleteById(productId);
        } catch (Exception e) {
            throw new RuntimeException("删除商品失败: " + e.getMessage());
        }
    }

public products getProductById(Long productId) {
    return productsRepository.findByProductId(productId);
}

}
