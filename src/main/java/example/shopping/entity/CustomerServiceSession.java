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
    private Date createTime;
    private Date updateTime;
} 