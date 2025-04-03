package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 店铺实体类
 */
@Data
public class Store {
    private Long id;
    private Long userId;       // 店主ID
    private String name;       // 店铺名称
    private String logo;       // 店铺LOGO
    private String description; // 店铺描述
    private String contactInfo; // 联系方式(JSON格式)
    private String license;     // 营业执照URL
    private Integer status;     // 状态(0-审核中, 1-正常, 2-关闭)
    private Date createTime;
    private Date updateTime;
} 