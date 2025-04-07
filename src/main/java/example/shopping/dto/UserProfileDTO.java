package example.shopping.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户个人信息数据传输对象
 */
@Data
public class UserProfileDTO {
    
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private String avatar;
    
    @Size(max = 500, message = "地址长度不能超过500个字符")
    private String addresses;
} 