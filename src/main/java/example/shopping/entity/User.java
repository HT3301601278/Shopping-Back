package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String name;  // 用户真实姓名

    @Column(nullable = false, unique = true)
    private String phone;

    private String avatar;

    @Column(nullable = false)
    private String role;  // 角色(ROLE_ADMIN/ROLE_MERCHANT/ROLE_USER)

    @Column(nullable = false)
    private Integer status; // 状态(0-禁用, 1-启用)

    @Column(columnDefinition = "TEXT")
    private String addresses; // JSON格式存储收货地址

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
