package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 店铺实体类
 */
@Data
@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;       // 店主ID

    @Column(nullable = false)
    private String name;       // 店铺名称

    private String logo;       // 店铺LOGO

    private String description; // 店铺描述

    @Column(columnDefinition = "TEXT")
    private String contactInfo; // 联系方式(JSON格式)

    private String license;     // 营业执照URL

    @Column(nullable = false)
    private Integer status;     // 状态(0-审核中, 1-正常, 2-关闭)

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (status == null) status = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
