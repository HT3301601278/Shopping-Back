package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类
 */
@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNo;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long storeId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String items;            // JSON格式存储订单项

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String addressInfo;      // JSON格式存储收货地址信息

    private String paymentType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date shippingTime;

    @Column(nullable = false)
    private Integer status;          // 订单状态

    private Integer refundStatus;    // 退款状态

    private String refundReason;

    @Column(columnDefinition = "TEXT")
    private String remark;           // 订单备注

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (refundStatus == null) refundStatus = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
