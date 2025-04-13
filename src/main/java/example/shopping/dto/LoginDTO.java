package example.shopping.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录数据传输对象
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
