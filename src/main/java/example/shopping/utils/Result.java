package example.shopping.utils;

import lombok.Data;

/**
 * 统一响应结果类
 */
@Data
public class Result<T> {
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 提示信息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 是否成功标志
     */
    private Boolean success;
    
    /**
     * 私有构造器
     */
    private Result() {}
    
    /**
     * 成功返回结果
     * @param <T> 数据类型
     * @return 包含默认成功信息的结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 成功返回结果
     * @param data 获取的数据
     * @param <T> 数据类型
     * @return 包含获取数据的结果
     */
    public static <T> Result<T> success(T data) {
        return success(data, "操作成功");
    }
    
    /**
     * 成功返回结果
     * @param data 获取的数据
     * @param message 提示信息
     * @param <T> 数据类型
     * @return 包含获取数据和自定义消息的结果
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setData(data);
        result.setMessage(message);
        result.setSuccess(true);
        return result;
    }
    
    /**
     * 失败返回结果
     * @param <T> 数据类型
     * @return 带有错误信息的结果
     */
    public static <T> Result<T> error() {
        return error("操作失败");
    }
    
    /**
     * 失败返回结果
     * @param message 提示信息
     * @param <T> 数据类型
     * @return 带有自定义错误信息的结果
     */
    public static <T> Result<T> error(String message) {
        return error(400, message);
    }
    
    /**
     * 失败返回结果
     * @param code 状态码
     * @param message 提示信息
     * @param <T> 数据类型
     * @return 带有自定义错误信息和状态码的结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }
} 