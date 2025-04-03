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
    private String status;     // 会话状态(ongoing-进行中, closed-已结束)
    private Date startTime;
    private Date endTime;
    private String evaluation;
    private Integer rating;    // 满意度评分
} 