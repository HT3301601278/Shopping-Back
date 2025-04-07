package example.shopping.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 搜索历史数据传输对象
 */
@Data
public class SearchHistoryDTO {
    private Long id;
    
    @NotBlank(message = "搜索关键词不能为空")
    private String keyword;
    
    private Integer resultCount;
} 