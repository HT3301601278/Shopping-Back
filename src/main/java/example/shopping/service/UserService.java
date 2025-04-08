package example.shopping.service;

import example.shopping.dto.LoginDTO;
import example.shopping.dto.RegisterDTO;
import example.shopping.dto.UserProfileDTO;
import example.shopping.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 包含用户信息和token的Map
     */
    Map<String, Object> login(LoginDTO loginDTO);
    
    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册成功的用户
     */
    User register(RegisterDTO registerDTO);
    
    /**
     * 通过用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);
    
    /**
     * 通过ID查找用户
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Long id);
    
    /**
     * 通过手机号查找用户
     * @param phone 手机号
     * @return 用户对象
     */
    User findByPhone(String phone);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新后的用户
     */
    User updateUser(User user);
    
    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 按角色查询用户
     * @param role 角色
     * @return 用户列表
     */
    List<User> findByRole(String role);
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否更新成功
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 更新用户个人信息
     * @param userId 用户ID
     * @param userProfileDTO 用户个人信息
     * @return 更新后的用户信息
     */
    User updateProfile(Long userId, UserProfileDTO userProfileDTO);
} 