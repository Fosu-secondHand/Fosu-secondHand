// 新建文件: src/main/java/com/qcq/second_hand/entity/ChatSessionId.java
package com.qcq.second_hand.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class ChatSessionId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "target_id")
    private Long targetId;

    public ChatSessionId() {}

    public ChatSessionId(Long userId, Long targetId) {
        this.userId = userId;
        this.targetId = targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatSessionId)) return false;
        ChatSessionId that = (ChatSessionId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(targetId, that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, targetId);
    }
}
