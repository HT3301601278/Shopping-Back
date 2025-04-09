package example.shopping.service.impl;

import example.shopping.dto.LoginDTO;
import example.shopping.dto.RegisterDTO;
import example.shopping.dto.UserProfileDTO;
import example.shopping.entity.User;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.UserMapper;
import example.shopping.service.UserService;
import example.shopping.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        // 执行认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成JWT令牌
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);

        // 获取用户信息
        User user = userMapper.findByUsername(loginDTO.getUsername());

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        user.setPassword(null); // 不返回密码
        result.put("user", user);

        return result;
    }

    @Override
    @Transactional
    public User register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已注册
        if (userMapper.findByPhone(registerDTO.getPhone()) != null) {
            throw new BusinessException("该手机号已注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setPhone(registerDTO.getPhone());
        user.setRole("ROLE_USER"); // 默认为普通用户角色
        user.setStatus(1); // 1-启用
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 保存用户
        userMapper.insert(user);

        // 不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public User findByPhone(String phone) {
        return userMapper.findByPhone(phone);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        user.setUpdateTime(new Date());
        userMapper.update(user);
        return userMapper.findById(user.getId());
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public List<User> findByRole(String role) {
        return userMapper.findByRole(role);
    }

    @Override
    @Transactional
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!oldPassword.equals(user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(newPassword);
        updateUser.setUpdateTime(new Date());
        
        return userMapper.update(updateUser) > 0;
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, UserProfileDTO userProfileDTO) {
        User existingUser = userMapper.findById(userId);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查手机号是否已被其他用户使用
        if (!existingUser.getPhone().equals(userProfileDTO.getPhone())) {
            User userByPhone = userMapper.findByPhone(userProfileDTO.getPhone());
            if (userByPhone != null && !userByPhone.getId().equals(userId)) {
                throw new BusinessException("手机号已被使用");
            }
        }

        // 更新用户信息
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPhone(userProfileDTO.getPhone());
        updateUser.setName(userProfileDTO.getName());
        updateUser.setAvatar(userProfileDTO.getAvatar());
        updateUser.setAddresses(userProfileDTO.getAddresses());
        updateUser.setUpdateTime(new Date());

        userMapper.update(updateUser);
        
        return userMapper.findById(userId);
    }

    @Override
    public boolean updateStatus(Long userId, Integer status) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        user.setStatus(status);
        userMapper.update(user);
        return true;
    }
} 