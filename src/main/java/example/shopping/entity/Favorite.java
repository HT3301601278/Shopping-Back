package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 收藏实体类
 */
@Data
public class Favorite {
    private Long id;
    private Long userId;
    private Long productId;
    private Date createTime;
} 