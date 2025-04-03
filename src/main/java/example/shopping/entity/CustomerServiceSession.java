package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 客服会话实体类
 */
@Data
@Entity
@Table(name = "customer_service_sessions")
public class CustomerServiceSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Integer status;     // 会话状态(0-进行中, 1-已结束)

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    private Integer evaluation; // 用户评价(1-5星)

    private String remark;      // 评价备注

    // 投诉相关字段
    private Integer complaintStatus;  // 投诉状态(0-待处理, 1-已处理, 2-已驳回)

    private String complaintResult;   // 投诉处理结果

    private Boolean isPenalty;        // 是否处罚商家

    private String penaltyContent;    // 处罚内容

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        startTime = new Date();
        status = 0;
        if (complaintStatus == null) complaintStatus = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
