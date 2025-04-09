package example.shopping.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 评论数据传输对象
 */
@Getter
@Setter
public class ReviewDTO {
    
    /**
     * 商品ID，发布评论时必填
     */
    private Long productId;
    
    /**
     * 订单ID，发布评论时必填
     */
    private Long orderId;
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    /**
     * 评分(1-5星)，仅用户首次评论时必填
     */
    @Min(value = 1, message = "评分最低为1星")
    @Max(value = 5, message = "评分最高为5星")
    private Integer rating;
    
    /**
     * 图片URLs
     */
    private List<String> images;

    /**
     * 父评论ID，回复评论时必填
     */
    private Long parentId;

    /**
     * 评论类型(0-用户评论, 1-商家回复, 2-用户追评)
     */
    @NotNull(message = "评论类型不能为空")
    private Integer type;
} 