package example.shopping.mapper;

import example.shopping.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 通过用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    /**
     * 通过ID查找用户
     * @param id 用户ID
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);
    
    /**
     * 通过手机号查找用户
     * @param phone 手机号
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User findByPhone(String phone);
    
    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响行数
     */
    @Insert("INSERT INTO users(username, password, phone, name, avatar, role, status, addresses, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{phone}, #{name}, #{avatar}, #{role}, #{status}, #{addresses}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE users " +
            "<set>" +
            "<if test='password != null'>password = #{password},</if>" +
            "<if test='phone != null'>phone = #{phone},</if>" +
            "<if test='name != null'>name = #{name},</if>" +
            "<if test='avatar != null'>avatar = #{avatar},</if>" +
            "<if test='role != null'>role = #{role},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='addresses != null'>addresses = #{addresses},</if>" +
            "update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(User user);
    
    /**
     * 查询所有用户
     * @return 用户列表
     */
    @Select("SELECT * FROM users")
    List<User> findAll();
    
    /**
     * 按角色查询用户
     * @param role 角色
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE role = #{role}")
    List<User> findByRole(String role);
} 