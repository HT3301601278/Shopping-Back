package example.shopping.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 公告DTO
 */
@Data
public class AnnouncementDTO {
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "状态不能为空")
    private Integer status;    // 状态(0-隐藏, 1-显示)
}
