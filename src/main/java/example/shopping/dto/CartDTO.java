package example.shopping.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 购物车数据传输对象
 */
@Data
public class CartDTO {
    
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    private Map<String, String> specInfo;
    
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;
} 