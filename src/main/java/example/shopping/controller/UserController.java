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
import java.util.Map;

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

    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")  // 只有管理员可以修改用户状态
    public Result<Boolean> updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, Integer> statusMap) {
        Integer status = statusMap.get("status");
        if (status == null) {
            return Result.error("状态参数不能为空");
        }
        boolean result = userService.updateStatus(userId, status);
        return Result.success(result, "用户状态更新成功");
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")  // 只有管理员可以查询任意用户信息
    public Result<User> getUserById(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }
} 