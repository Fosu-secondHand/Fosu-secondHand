package com.qcq.second_hand.entity.other;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ConditionConverter implements AttributeConverter<Condition, String> {

    @Override
    public String convertToDatabaseColumn(Condition condition) {
        if (condition == null) {
            return null;
        }
        return condition.name();
    }

    @Override
    public Condition convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        try {
            // 首先尝试直接匹配（区分大小写）
            return Condition.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            // 如果直接匹配失败，尝试忽略大小写匹配
            for (Condition condition : Condition.values()) {
                if (condition.name().equalsIgnoreCase(dbData)) {
                    return condition;
                }
            }

            // 特殊处理常见的错误格式
            switch (dbData.toLowerCase().trim()) {
                case "new":
                    return Condition.NEW;
                case "like_new":
                    return Condition.LIKE_NEW;
                case "good":
                    return Condition.GOOD;
                case "fair":
                    return Condition.FAIR;
                default:
                    throw new IllegalArgumentException("Unknown product condition: " + dbData);
            }
        }
    }
}
