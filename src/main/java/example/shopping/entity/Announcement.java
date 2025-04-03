package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 公告实体类
 */
@Data
public class Announcement {
    private Long id;
    private String title;
    private String content;
    private Long publisherId;
    private Integer status;    // 状态(0-隐藏, 1-显示)
    private String readUsers;  // JSON格式存储已读用户ID列表
    private Date createTime;
} 