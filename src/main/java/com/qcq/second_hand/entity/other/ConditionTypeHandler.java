package com.qcq.second_hand.entity.other;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConditionTypeHandler extends BaseTypeHandler<Condition> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Condition parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public Condition getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToCondition(value);
    }

    @Override
    public Condition getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToCondition(value);
    }

    @Override
    public Condition getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToCondition(value);
    }

    private Condition convertToCondition(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        try {
            return Condition.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 处理大小写不匹配的情况
            for (Condition condition : Condition.values()) {
                if (condition.name().equalsIgnoreCase(dbData)) {
                    return condition;
                }
            }
            throw new IllegalArgumentException("Unknown product condition: " + dbData);
        }
    }
}
