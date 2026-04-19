package com.qcq.second_hand.entity.other;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "消息元数据")
public class MessageMetadata {

    @Schema(description = "图片URL列表（msgType=1时使用）")
    private List<String> images;

    @Schema(description = "链接URL（msgType=3时使用）")
    private String linkUrl;

    @Schema(description = "链接标题（msgType=3时使用）")
    private String linkTitle;

    @Schema(description = "链接描述（msgType=3时使用）")
    private String linkDescription;

    @Schema(description = "链接封面图（msgType=3时使用）")
    private String linkCover;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件名称")
    private String fileName;
}
