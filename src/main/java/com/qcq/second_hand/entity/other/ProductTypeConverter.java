// 创建 ProductTypeConverter.java
package com.qcq.second_hand.entity.other;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProductTypeConverter implements AttributeConverter<ProductType, String> {

    @Override
    public String convertToDatabaseColumn(ProductType productType) {
        if (productType == null) {
            return null;
        }
        return productType.name();
    }

    @Override
    public ProductType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        try {
            return ProductType.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            // 特殊处理常见的错误格式
            switch (dbData.toLowerCase().trim()) {
                case "sell":
                    return ProductType.SELL;
                case "want":
                    return ProductType.WANT;
                default:
                    throw new IllegalArgumentException("Unknown product type: " + dbData);
            }
        }
    }
}
