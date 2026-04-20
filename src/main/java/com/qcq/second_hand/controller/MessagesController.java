package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.ChatSession;
import com.qcq.second_hand.entity.Messages;
import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.MessagesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.grammars.hql.HqlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "消息管理", description = "消息相关接口")
@RestController
@RequestMapping("/messages")  // 建议统一使用复数形式
public class MessagesController {

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private com.qcq.second_hand.service.UsersService usersService;

    @Autowired
    private com.qcq.second_hand.service.FileUploadService fileUploadService;
    @Operation(summary = "获取聊天记录", description = "获取两个用户之间的所有聊天记录")
    @GetMapping("/history")
    public response getChatHistory(
            @Parameter(description = "用户 ID1", required = true)
            @RequestParam Long userId1,
            @Parameter(description = "用户 ID2", required = true)
            @RequestParam Long userId2) {
        try {
            // 参数验证
            if (userId1 == null || userId2 == null) {
                return new response(400, "用户 ID 不能为空", null);
            }

            List<Messages> messages = messagesService.getChatHistory(userId1, userId2);

            Map<String, Object> result = new HashMap<>();
            result.put("messages", messages);
            result.put("count", messages.size());

            return response.success(result);
        } catch (Exception e) {
            return new response(500, "获取聊天记录失败：" + e.getMessage(), null);
        }
    }

    @Operation(summary = "获取聊天记录（分页）", description = "分页获取两个用户之间的聊天记录")
    @GetMapping("/history/page")
    public response getChatHistoryByPage(
            @Parameter(description = "用户 ID1", required = true)
            @RequestParam Long userId1,
            @Parameter(description = "用户 ID2", required = true)
            @RequestParam Long userId2,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        try {
            if (userId1 == null || userId2 == null) {
                return new response(400, "用户 ID 不能为空", null);
            }

            if (page < 1) page = 1;
            if (size < 1 || size > 100) size = 20;

            List<Messages> messages = messagesService.getChatHistoryByPage(userId1, userId2, page, size);

            // ✅ 核心优化：在后端统一转换消息格式，适配前端
            List<Map<String, Object>> formattedMessages = messages.stream().map(msg -> {
                Map<String, Object> map = new HashMap<>();
                map.put("messageId", msg.getMessageId());
                map.put("senderId", msg.getSenderId());
                map.put("receiverId", msg.getReceiverId());
                map.put("sendTime", msg.getSendTime());
                map.put("isRead", msg.getIsRead());

                Integer msgType = msg.getMsgType() != null ? msg.getMsgType() : 0;
                map.put("msgType", msgType);

                // 根据 msgType 智能组装前端需要的 type 和 content
                if (msgType == 1) {
                    // --- 图片消息 ---
                    map.put("type", "image");
                    String imageUrl = "";

                    if (msg.getMetadata() != null && !msg.getMetadata().isEmpty()) {
                        try {
                            // 解析 metadata JSON
                            com.fasterxml.jackson.databind.JsonNode metaNode =
                                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(msg.getMetadata());

                            if (metaNode.has("images") && metaNode.get("images").isArray() && metaNode.get("images").size() > 0) {
                                String relativePath = metaNode.get("images").get(0).asText();
                                // ✅ 关键：在这里拼接完整 URL，前端直接用即可
                                imageUrl = "http://139.199.87.181:8080" + relativePath;
                            }
                        } catch (Exception e) {
                            System.err.println("解析图片 metadata 失败: " + e.getMessage());
                        }
                    }
                    map.put("content", imageUrl);

                } else if (msgType == 2) {
                    // --- 文件消息 ---
                    map.put("type", "file");
                    map.put("content", msg.getContent() != null ? msg.getContent() : "[文件]");

                } else {
                    // --- 文本消息 (默认) ---
                    map.put("type", "text");
                    map.put("content", msg.getContent() != null ? msg.getContent() : "");
                }

                return map;
            }).collect(java.util.stream.Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("messages", formattedMessages);
            result.put("page", page);
            result.put("size", size);
            result.put("total", formattedMessages.size());

            return response.success(result);
        } catch (Exception e) {
            System.err.println("获取聊天记录失败: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "获取聊天记录失败：" + e.getMessage(), null);
        }
    }

    @Operation(summary = "获取会话列表", description = "获取指定用户的所有聊天会话列表（包含对方用户信息）")
    @GetMapping("/getChatSessionList")
    public response getChatSessionList(
            @Parameter(description = "用户 ID", required = true)
            @RequestParam Long userId) {
        try {
            List<ChatSession> sessions = messagesService.selectListOfChatSession(userId);

            // 🔍 添加日志：打印查询到的会话数量
            System.out.println("=== 查询到会话数量: " + sessions.size() + " ===");
            sessions.forEach(session -> {
                System.out.println("会话 - targetId: " + session.getTargetId()
                        + ", lastMessage: " + session.getLastMessage()
                        + ", lastDate: " + session.getLastDate());
            });


            List<Map<String, Object>> enhancedSessions = sessions.stream().map(session -> {
                Map<String, Object> map = new HashMap<>();
                map.put("targetId", session.getTargetId());
                map.put("userId", session.getUserId());
                map.put("unreadCount", session.getUnreadCount());
                map.put("lastMessage", session.getLastMessage());
                map.put("lastDate", session.getLastDate());

                Users otherUser = usersService.getUserById(session.getTargetId());
                if (otherUser != null) {
                    map.put("nickname", otherUser.getNickname());
                    map.put("avatar", otherUser.getAvatar());
                } else {
                    map.put("nickname", "未知用户");
                    map.put("avatar", "");
                }

                return map;
            }).collect(java.util.stream.Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("sessions", enhancedSessions);
            result.put("count", enhancedSessions.size());

            return response.success(result);
        } catch (Exception e) {
            return new response(500, "获取会话列表失败：" + e.getMessage(), null);
        }
    }
    @Operation(summary = "获取未读消息数量", description = "获取指定用户的未读消息总数")
    @GetMapping("/unread-count")
    public response getUnreadCount(
            @Parameter(description = "用户 ID", required = true)
            @RequestParam Long userId) {
        try {
            Integer count = messagesService.getUnreadCount(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("count", count);

            return response.success(result);
        } catch (Exception e) {
            return new response(500, "获取未读数量失败：" + e.getMessage(), null);
        }
    }

    @Operation(summary = "标记消息为已读", description = "将指定消息标记为已读状态")
    @PostMapping("/mark-read")
    public response markMessageAsRead(
            @Parameter(description = "消息 ID", required = true)
            @RequestParam Long messageId) {
        try {
            boolean success = messagesService.markAsRead(messageId);

            if (success) {
                return response.success("标记成功");
            } else {
                return new response(500, "标记失败", null);
            }
        } catch (Exception e) {
            return new response(500, "标记已读失败：" + e.getMessage(), null);
        }
    }

    @Operation(summary = "批量标记消息为已读", description = "将多个消息批量标记为已读状态")
    @PostMapping("/mark-read-batch")
    public response markMessagesAsReadBatch(
            @Parameter(description = "消息 ID 列表", required = true)
            @RequestBody List<Long> messageIds) {
        try {
            if (messageIds == null || messageIds.isEmpty()) {
                return new response(400, "消息 ID 列表不能为空", null);
            }

            boolean success = messagesService.markMultipleAsRead(messageIds);

            if (success) {
                return response.success("批量标记成功");
            } else {
                return new response(500, "批量标记失败", null);
            }
        } catch (Exception e) {
            return new response(500, "批量标记已读失败：" + e.getMessage(), null);
        }
    }
    @Operation(summary = "上传聊天图片", description = "上传聊天中的图片并返回访问URL")
    @PostMapping("/upload/image")
    public response uploadChatImage(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new response(400, "文件不能为空", null);
            }

            String imageUrl = fileUploadService.uploadFile(file);
            return response.success(imageUrl);
        } catch (Exception e) {
            System.err.println("聊天图片上传失败: " + e.getMessage());
            e.printStackTrace();
            return new response(500, "图片上传失败: " + e.getMessage(), null);
        }
    }

}
