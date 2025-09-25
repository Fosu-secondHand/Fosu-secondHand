// 文件路径: src/main/java/com/qcq/second_hand/entity/other/StatusConverter.java
package com.qcq.second_hand.entity.other;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        // 处理大小写不敏感的枚举转换
        try {
            return Status.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 如果找不到匹配的枚举值，可以返回默认值或抛出异常
            switch (dbData.toLowerCase()) {
                case "active":
                    return Status.ACTIVE;
                case "frozen":
                    return Status.FROZEN;
                case "banned":
                    return Status.BANNED;
                default:
                    throw e;
            }
        }
    }
}
