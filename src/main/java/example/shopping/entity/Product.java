package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品实体类
 */
@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private String description;

    @Column(columnDefinition = "TEXT")
    private String images;        // JSON格式存储图片URLs

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(columnDefinition = "TEXT")
    private String specifications; // JSON格式存储商品规格

    @Column(nullable = false)
    private Integer status;        // 状态(0-下架, 1-上架)

    @Column(nullable = false)
    private Integer sales;

    private Double rating;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (sales == null) sales = 0;
        if (rating == null) rating = 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
