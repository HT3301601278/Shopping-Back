package example.shopping.controller;

import example.shopping.dto.PasswordDTO;
import example.shopping.dto.UserProfileDTO;
import example.shopping.entity.User;
import example.shopping.service.UserService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("isAuthenticated()")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/profile")
    public Result<User> getUserProfile() {
        Long userId = getCurrentUserId();
        User user = userService.findById(userId);
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }

    /**
     * 更新用户个人信息
     * @param userProfileDTO 用户个人信息
     * @return 更新后的用户信息
     */
    @PutMapping("/profile")
    public Result<User> updateUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO) {
        Long userId = getCurrentUserId();
        User updatedUser = userService.updateProfile(userId, userProfileDTO);
        updatedUser.setPassword(null); // 不返回密码
        return Result.success(updatedUser, "个人信息更新成功");
    }

    /**
     * 修改用户密码
     * @param passwordDTO 密码信息
     * @return 是否修改成功
     */
    @PutMapping("/password")
    public Result<Boolean> updatePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        Long userId = getCurrentUserId();
        boolean result = userService.updatePassword(userId, passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        return Result.success(result, "密码修改成功");
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getId();
    }
} 