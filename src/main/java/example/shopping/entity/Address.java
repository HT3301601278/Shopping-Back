package example.shopping.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户地址实体类
 */
@Data
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverPhone;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String detailAddress;

    @Column(nullable = false)
    private Boolean isDefault;     // 是否默认地址

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (isDefault == null) isDefault = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }

    /**
     * 统一API响应结果封装
     * @param <T> 数据类型
     */
    @Data
    public static class Result<T> {
        private Integer code;
        private String msg;
        private T data;

        private Result(Integer code, String msg, T data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        /**
         * 成功返回结果
         * @param data 获取的数据
         */
        public static <T> Result<T> success(T data) {
            return new Result<>(200, "操作成功", data);
        }

        /**
         * 成功返回结果
         * @param data 获取的数据
         * @param msg 提示信息
         */
        public static <T> Result<T> success(T data, String msg) {
            return new Result<>(200, msg, data);
        }

        /**
         * 失败返回结果
         * @param msg 提示信息
         */
        public static <T> Result<T> error(String msg) {
            return new Result<>(500, msg, null);
        }

        /**
         * 失败返回结果
         * @param code 错误码
         * @param msg 提示信息
         */
        public static <T> Result<T> error(Integer code, String msg) {
            return new Result<>(code, msg, null);
        }
    }
}
