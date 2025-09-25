package com.qcq.second_hand.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "categories")
public class categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_Id")
    private Long categoryId;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "icon", nullable = true, length = 255)
    private String icon;

    @Column(name = "sort_Order", nullable = false)
    private Integer sortOrder;

    @Column(name = "parent_Id", nullable = true)
    private Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_Id", referencedColumnName = "category_Id", insertable = false, updatable = false)
    private categories parentCategory;

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<categories> childCategories;

    public categories(){}

    public categories(String name, String icon, Integer sortOrder, Long parentId) {
        this.name = name;
        this.icon = icon;
        this.sortOrder = sortOrder;
        this.parentId = parentId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public categories getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(categories parentCategory) {
        this.parentCategory = parentCategory;
    }

    public List<categories> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(List<categories> childCategories) {
        this.childCategories = childCategories;
    }


    //    @ManyToOne：表示这是一个多对一的关系，即多个子类别可以引用同一个父类别。
//    @JoinColumn：定义了外键列的名称（parent_Id）和引用的列名称（category_Id）。insertable = false, updatable = false表示在插入或更新操作时，不会操作这个字段。这通常用于避免在JPA层面重复设置外键值，因为外键值通常由数据库层面维护。
//    @OneToMany：表示这是一个一对多的关系，mappedBy属性指定了在子类别实体类中，是通过哪个字段来维护这种关系的。
}