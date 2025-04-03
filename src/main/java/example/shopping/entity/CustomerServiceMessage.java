package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 客服消息实体类
 */
@Data
@Entity
@Table(name = "customer_service_messages")
public class CustomerServiceMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Integer fromType;    // 发送方类型(0-用户, 1-商家)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String contentType;  // 内容类型(text-文本, image-图片, file-文件)

    @Column(nullable = false)
    private Boolean readStatus;  // 已读状态

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        if (readStatus == null) readStatus = false;
        if (contentType == null) contentType = "text";
    }
}
