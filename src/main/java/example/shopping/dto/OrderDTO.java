package example.shopping.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单数据传输对象
 */
@Data
public class OrderDTO {
    
    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long storeId;
    
    /**
     * 收货地址ID
     */
    @NotNull(message = "收货地址不能为空")
    private Long addressId;
    
    /**
     * 支付方式
     */
    @NotNull(message = "支付方式不能为空")
    private String paymentType;
    
    /**
     * 订单项列表
     */
    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemDTO> items;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 订单项数据传输对象
     */
    @Data
    public static class OrderItemDTO {
        
        /**
         * 商品ID
         */
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        
        /**
         * 商品数量
         */
        @NotNull(message = "商品数量不能为空")
        private Integer quantity;
        
        /**
         * 商品规格信息
         */
        private String specInfo;
    }
} 