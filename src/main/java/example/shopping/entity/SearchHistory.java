package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 搜索历史实体类
 */
@Data
public class SearchHistory {
    private Long id;
    private Long userId;
    private String keyword;
    private Integer resultCount;
    private Date createTime;
} 