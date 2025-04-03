package example.shopping.entity;

import lombok.Data;

/**
 * 商品分类实体类
 */
@Data
public class Category {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer status;    // 状态(0-禁用, 1-启用)
    private Integer sortOrder; // 排序值
} 