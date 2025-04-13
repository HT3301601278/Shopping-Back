package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 公告实体类
 */
@Data
@Entity
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long publisherId;

    @Column(nullable = false)
    private Integer status;    // 状态(0-隐藏, 1-显示)

    @Column(columnDefinition = "TEXT")
    private String readUsers;  // JSON格式存储已读用户ID列表

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (status == null) status = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
