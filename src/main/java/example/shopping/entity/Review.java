package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 评论实体类
 */
@Data
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

    private String reply;        // 商家回复

    @Column(nullable = false)
    private Integer status;      // 状态(0-审核中, 1-显示, 2-隐藏)

    @Column(nullable = false)
    private Boolean isTop;       // 是否置顶

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (status == null) status = 0;
        if (isTop == null) isTop = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
