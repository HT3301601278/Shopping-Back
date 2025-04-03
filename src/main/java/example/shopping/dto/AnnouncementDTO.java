package example.shopping.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 公告数据传输对象
 */
@Data
public class AnnouncementDTO {
    
    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    private String title;
    
    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    private String content;
    
    /**
     * 状态(0-隐藏, 1-显示)
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
} 