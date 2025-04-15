package example.shopping.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 评论实体类
 */
@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long orderId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;      // 评分(1-5星)

    @Column(columnDefinition = "TEXT")
    private String images;       // JSON格式存储图片URLs

    @Column
    private Long parentId;       // 父评论ID，用于关联回复，为null表示是原始评论

    @Column(nullable = false)
    private Integer type;        // 评论类型(0-用户评论, 1-商家回复, 2-用户追评)

    @Column(nullable = false)
    private Integer status;      // 状态(0-正常显示, 1-待审核, 2-隐藏)

    @Column(nullable = false)
    private Boolean isTop;       // 是否置顶

    @Column(columnDefinition = "TEXT")
    private String reason;       // 审核原因

    @Column(updatable = false)
    private LocalDateTime createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = new Date();
        if (status == null) status = 0;  // 默认状态改为0（正常显示）
        if (isTop == null) isTop = false;
        if (type == null) type = 0;      // 默认为用户评论
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
