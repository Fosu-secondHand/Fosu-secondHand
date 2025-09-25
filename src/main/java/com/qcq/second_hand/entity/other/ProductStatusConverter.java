package com.qcq.second_hand.entity.other;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProductStatusConverter implements AttributeConverter<productStatus, String> {

    @Override
    public String convertToDatabaseColumn(productStatus status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    @Override
    public productStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        try {
            // 首先尝试直接匹配（区分大小写）
            return productStatus.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            // 如果直接匹配失败，尝试忽略大小写匹配
            for (productStatus status : productStatus.values()) {
                if (status.name().equalsIgnoreCase(dbData)) {
                    return status;
                }
            }

            // 特殊处理常见的错误格式
            switch (dbData.toLowerCase().trim()) {
                case "on_sale":
                case "onsale":
                    return productStatus.ON_SALE;
                case "sold":
                    return productStatus.SOLD;
                case "off_shelf":
                case "offshelf":
                    return productStatus.OFF_SHELF;
                default:
                    throw new IllegalArgumentException("Unknown product status: " + dbData);
            }
        }
    }
}
