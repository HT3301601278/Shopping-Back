package example.shopping.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户地址实体类
 */
@Data
public class Address {
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Boolean isDefault;     // 是否默认地址
    private Date createTime;
    private Date updateTime;
} 