package com.qcq.second_hand.entity;

import com.qcq.second_hand.entity.other.*;
import com.qcq.second_hand.entity.other.*;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_Id")
    private Long productId;

    @Column(name = "seller_Id", nullable = false)
    private Long sellerId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_method", nullable = false)
    private TransactionMethod transactionMethod;

    @Column(name = "description", nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(name = "favorite_Count", nullable = false)
    private Long favoriteCount;

    @Column(name = "category_Id", nullable = false)
    private Long categoryId;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "itemCondition", nullable = false, length = 20)
    private Condition condition;

    @Convert(converter = JsonConverter.class)
    @Column(name = "image", nullable = false, columnDefinition = "JSON")
    private List<String> image;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private productStatus status;

    @Column(name = "post_time", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime postTime;

    @Column(name = "update_time", nullable = true)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime updateTime;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Column(name = "campus", nullable = false, length = 50)
    private String campus;

    @Column(name = "want_to_buy", nullable = false)
    private Long wantToBuy;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_Id", referencedColumnName = "user_Id", insertable = false, updatable = false)
    private Users seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_Id", referencedColumnName = "category_Id", insertable = false, updatable = false)
    private categories category;

    public products(){}

    public products(Long sellerId, String title, String description,TransactionMethod transactionMethod,
                    Long categoryId, BigDecimal price, Condition condition, List<String> image,
                    productStatus status, LocalDateTime postTime, LocalDateTime updateTime,
                    Integer viewCount, String campus,Long favoriteCount,Long wantToBuy) {
        this.sellerId = sellerId;
        this.title = title;
        this.transactionMethod=transactionMethod;
        this.description = description;
        this.categoryId = categoryId;
        this.price = price;
        this.condition = condition;
        this.image = image;
        this.status = status;
        this.postTime = postTime;
        this.updateTime = updateTime;
        this.viewCount = viewCount;
        this.campus = campus;
        this.favoriteCount = favoriteCount;
        this.wantToBuy=wantToBuy;
    }

    public Long getWantToBuy() {
        return wantToBuy;
    }

    public void setWantToBuy(Long wantToBuy) {
        this.wantToBuy = wantToBuy;
    }

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public TransactionMethod getTransactionMethod() {
        return transactionMethod;
    }

    public void setTransactionMethod(TransactionMethod transactionMethod) {
        this.transactionMethod = transactionMethod;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public productStatus getStatus() {
        return status;
    }

    public void setStatus(productStatus status) {
        this.status = status;
    }

    public LocalDateTime getPostTime() {
        return postTime;
    }

    public void setPostTime(LocalDateTime postTime) {
        this.postTime = postTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Users getSeller() {
        return seller;
    }

    public void setSeller(Users seller) {
        this.seller = seller;
    }

    public categories getCategory() {
        return category;
    }

    public void setCategory(categories category) {
        this.category = category;
    }
}