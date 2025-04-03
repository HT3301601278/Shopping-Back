package example.shopping.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品实体类
 */
@Data
public class Product {
    private Long id;
    private String name;
    private Long storeId;
    private Long categoryId;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String images;        // JSON格式存储图片URLs
    private String detail;
    private String specifications; // JSON格式存储商品规格
    private Integer status;        // 状态(0-下架, 1-上架)
    private Integer sales;
    private Double rating;
    private Date createTime;
    private Date updateTime;
} 