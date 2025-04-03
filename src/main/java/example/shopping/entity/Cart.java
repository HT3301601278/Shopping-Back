package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 购物车实体类
 */
@Data
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(columnDefinition = "TEXT")
    private String specInfo;   // JSON格式存储规格信息

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Boolean selected;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        if (quantity == null) quantity = 1;
        if (selected == null) selected = true;
    }
}
