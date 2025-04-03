package example.shopping.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 客服相关DTO类
 */
public class CustomerServiceDTO {
    
    /**
     * 会话DTO
     */
    @Data
    public static class SessionDTO {
        
        /**
         * 店铺ID
         */
        @NotNull(message = "店铺ID不能为空")
        private Long storeId;
    }
    
    /**
     * 消息DTO
     */
    @Data
    public static class MessageDTO {
        
        /**
         * 会话ID
         */
        @NotNull(message = "会话ID不能为空")
        private Long sessionId;
        
        /**
         * 消息内容
         */
        @NotBlank(message = "消息内容不能为空")
        private String content;
        
        /**
         * 内容类型
         */
        @NotBlank(message = "内容类型不能为空")
        private String contentType;
    }
    
    /**
     * 评价DTO
     */
    @Data
    public static class EvaluationDTO {
        
        /**
         * 会话ID
         */
        @NotNull(message = "会话ID不能为空")
        private Long sessionId;
        
        /**
         * 评价(1-5星)
         */
        @NotNull(message = "评价不能为空")
        @Min(value = 1, message = "评价最低为1星")
        @Max(value = 5, message = "评价最高为5星")
        private Integer evaluation;
        
        /**
         * 评价备注
         */
        private String remark;
    }
} 