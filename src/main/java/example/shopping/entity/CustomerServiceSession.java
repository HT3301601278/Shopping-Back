package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 客服会话实体类
 */
@Data
public class CustomerServiceSession {
    private Long id;
    private Long userId;
    private Long storeId;
    private Integer status;     // 会话状态(0-进行中, 1-已结束)
    private Date startTime;
    private Date endTime;
    private Integer evaluation; // 用户评价(1-5星)
    private String remark;      // 评价备注
    
    // 投诉相关字段
    private Integer complaintStatus;  // 投诉状态(0-待处理, 1-已处理, 2-已驳回)
    private String complaintResult;   // 投诉处理结果
    private Boolean isPenalty;        // 是否处罚商家
    private String penaltyContent;    // 处罚内容
    
    private Date createTime;
    private Date updateTime;
} 