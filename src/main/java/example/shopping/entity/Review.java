package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 评论实体类
 */
@Data
public class Review {
    private Long id;
    private Long productId;
    private Long userId;
    private Long orderId;
    private String content;
    private Integer rating;      // 评分(1-5星)
    private String images;       // JSON格式存储图片URLs
    private String reply;        // 商家回复
    private Integer status;      // 状态(0-审核中, 1-显示, 2-隐藏)
    private Boolean isTop;       // 是否置顶
    private Date createTime;
} 