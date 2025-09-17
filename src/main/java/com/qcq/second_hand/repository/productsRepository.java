package com.qcq.second_hand.repository;

import com.qcq.second_hand.entity.products;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface productsRepository extends JpaRepository<products,Long> {

    @EntityGraph(attributePaths = {"seller", "category"})
    List<products> findAll();

    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT p FROM products p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
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



}
