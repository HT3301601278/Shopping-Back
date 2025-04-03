package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private String role;  // 角色(ROLE_ADMIN/ROLE_MERCHANT/ROLE_USER)
    private Integer status; // 状态(0-禁用, 1-启用)
    private String addresses; // JSON格式存储收货地址
    private Date createTime;
    private Date updateTime;
} 