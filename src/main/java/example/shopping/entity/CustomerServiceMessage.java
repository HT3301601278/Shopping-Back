package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 客服消息实体类
 */
@Data
public class CustomerServiceMessage {
    private Long id;
    private Long sessionId;
    private Long userId;
    private Long storeId;
    private String fromType;     // 发送方类型(user-用户, merchant-商家)
    private String content;
    private String contentType;  // 内容类型(text-文本, image-图片, file-文件)
    private Boolean readStatus;  // 已读状态
    private Date createTime;
} 