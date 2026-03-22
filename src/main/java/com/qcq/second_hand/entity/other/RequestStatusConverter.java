package com.qcq.second_hand.entity.other;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestStatusConverter extends BaseTypeHandler<RequestStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RequestStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public RequestStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String status = rs.getString(columnName);
        return parseStatus(status);
    }

    @Override
    public RequestStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String status = rs.getString(columnIndex);
        return parseStatus(status);
    }

    @Override
    public RequestStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String status = cs.getString(columnIndex);
        return parseStatus(status);
    }

    private RequestStatus parseStatus(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }

        try {
            return RequestStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            // 特殊处理常见的错误格式
            switch (status.toLowerCase().trim()) {
                case "pending":
                    return RequestStatus.PENDING;
                case "accepted":
                    return RequestStatus.ACCEPTED;
                case "rejected":
                    return RequestStatus.REJECTED;
                default:
                    throw new IllegalArgumentException("Unknown request status: " + status);
            }
        }
    }
}
