package example.shopping.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类
 */
@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long storeId;
    private String items;            // JSON格式存储订单项
    private BigDecimal totalAmount;
    private String addressInfo;      // JSON格式存储收货地址信息
    private String paymentType;
    private Date paymentTime;
    private Date shippingTime;
    private Integer status;          // 订单状态
    private Integer refundStatus;    // 退款状态
    private String refundReason;
    private Date createTime;
    private Date updateTime;
} 