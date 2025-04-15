package example.shopping.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 商品分类实体类
 */
@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Long parentId;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer status;    // 状态(0-禁用, 1-启用)

    private Integer sortOrder; // 排序值

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Category> children;

    @PrePersist
    protected void onCreate() {
        if (parentId == null) parentId = 0L;
        if (level == null) level = 1;
        if (status == null) status = 1;
        if (sortOrder == null) sortOrder = 0;
    }
}
