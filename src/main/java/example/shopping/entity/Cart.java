package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 购物车实体类
 */
@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long productId;
    private String specInfo;   // JSON格式存储规格信息
    private Integer quantity;
    private Boolean selected;
    private Date createTime;
} 