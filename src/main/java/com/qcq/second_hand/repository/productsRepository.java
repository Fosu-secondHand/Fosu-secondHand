package com.qcq.second_hand.repository;

import com.qcq.second_hand.entity.other.ProductType;
import com.qcq.second_hand.entity.other.productStatus;
import com.qcq.second_hand.entity.products;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface productsRepository extends JpaRepository<products,Long> {

    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p WHERE p.status != 'SOLD'")
    List<products> findAll();

    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p WHERE (p.title LIKE %:keyword% OR p.description LIKE %:keyword%) AND p.status != 'SOLD'")
    List<products> findByKeyword(@Param("keyword") String keyword);

    @EntityGraph(attributePaths = {"seller", "category"})
    products findByProductId(Long productId);

    @Modifying
    @EntityGraph(attributePaths = {"seller", "category"})
    @Transactional
    @Query("UPDATE products p SET p.favoriteCount = p.favoriteCount + 1 WHERE p.productId = :productId")
    void addFavoriteCount(@Param("productId") Long productId);

    @Modifying
    @EntityGraph(attributePaths = {"seller", "category"})
    @Transactional
    @Query("UPDATE products p SET p.favoriteCount = p.favoriteCount - 1 WHERE p.productId = :productId")
    void reduceFavoriteCount(@Param("productId") Long productId);

    @Modifying
    @EntityGraph(attributePaths = {"seller", "category"})
    @Transactional
    @Query("UPDATE products p SET p.wantToBuy = p.wantToBuy + 1 WHERE p.productId = :productId")
    void addWishCount(@Param("productId") Long productId);

    @Modifying
    @EntityGraph(attributePaths = {"seller", "category"})
    @Transactional
    @Query("UPDATE products p SET p.wantToBuy = p.wantToBuy - 1 WHERE p.productId = :productId")
    void reduceWishCount(@Param("productId") Long productId);

    @Modifying
    @EntityGraph(attributePaths = {"seller", "category"})
    @Transactional
    @Query("UPDATE products p SET p.viewCount = p.viewCount + 1 WHERE p.productId = :productId")
    void updateViewCount(@Param("productId") Long productId);

    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p LEFT JOIN p.category c WHERE " +
            "p.status != 'SOLD' AND " +
            "(:campuses IS NULL OR p.campus IN :campuses) AND " +
            "(:categoryNames IS NULL OR c.name IN :categoryNames) " +
            "ORDER BY p.postTime DESC")
    List<products> findByCampusInAndCategoryNameInOrderByPostTimeDesc(
            @Param("campuses") List<String> campuses,
            @Param("categoryNames") List<String> categoryNames);

    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p WHERE p.productType = :productType AND p.status != 'SOLD'")
    List<products> findByProductType(@Param("productType") ProductType productType);

    @Query("SELECT p FROM products p WHERE p.status != 'SOLD' ORDER BY FUNCTION('RAND')") // MySQL
    @EntityGraph(attributePaths = {"seller", "category"})
    Page<products> findRandomProducts(Pageable pageable);

    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p WHERE p.productType = 'SELL' AND p.status != 'SOLD'")
    Page<products> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p WHERE p.price = :price AND p.productType = 'SELL' AND p.status != 'SOLD'")
    Page<products> findByPrice(@Param("price") BigDecimal price, Pageable pageable);

    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p WHERE p.productType = :productType AND p.status != 'SOLD'")
    Page<products> findByProductType(@Param("productType") ProductType productType, Pageable pageable);


    @Query("SELECT p FROM products p WHERE p.productType = :productType AND p.status != 'SOLD' ORDER BY FUNCTION('RAND')") // MySQL
    @EntityGraph(attributePaths = {"seller", "category"})
    Page<products> findRandomProductsByProductType(@Param("productType") ProductType productType, Pageable pageable);

    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p WHERE p.productType = :productType AND p.status = 'ON_SALE'")
    Page<products> findByProductTypeAndStatus(@Param("productType") ProductType productType, Pageable pageable);


    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p WHERE p.price = :price AND p.productType = :productType AND p.status != 'SOLD'")
    Page<products> findByPriceAndProductType(@Param("price") BigDecimal price, @Param("productType") ProductType productType, Pageable pageable);

    @EntityGraph(attributePaths = {"seller", "category"})
    List<products> findBySellerId(Long sellerId);

    @EntityGraph(attributePaths = {"seller", "category"})
    List<products> findBySellerIdAndStatus(Long sellerId, productStatus status);

}
