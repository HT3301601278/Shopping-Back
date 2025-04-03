package example.shopping.mapper;

import example.shopping.entity.Store;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 店铺Mapper接口
 */
@Mapper
public interface StoreMapper {
    
    /**
     * 查询所有店铺
     * @return 店铺列表
     */
    @Select("SELECT * FROM stores")
    List<Store> findAll();
    
    /**
     * 根据ID查询店铺
     * @param id 店铺ID
     * @return 店铺信息
     */
    @Select("SELECT * FROM stores WHERE id = #{id}")
    Store findById(Long id);
    
    /**
     * 根据店主ID查询店铺
     * @param userId 用户ID
     * @return 店铺信息
     */
    @Select("SELECT * FROM stores WHERE user_id = #{userId}")
    Store findByUserId(Long userId);
    
    /**
     * 根据状态查询店铺
     * @param status 状态
     * @return 店铺列表
     */
    @Select("SELECT * FROM stores WHERE status = #{status}")
    List<Store> findByStatus(Integer status);
    
    /**
     * 分页查询店铺
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 店铺列表
     */
    @Select("SELECT * FROM stores LIMIT #{offset}, #{limit}")
    List<Store> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 插入店铺
     * @param store 店铺信息
     * @return 影响行数
     */
    @Insert("INSERT INTO stores(user_id, name, logo, description, contact_info, license, status, create_time, update_time) " +
            "VALUES(#{userId}, #{name}, #{logo}, #{description}, #{contactInfo}, #{license}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Store store);
    
    /**
     * 更新店铺
     * @param store 店铺信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE stores " +
            "<set>" +
            "<if test='name != null'>name = #{name},</if>" +
            "<if test='logo != null'>logo = #{logo},</if>" +
            "<if test='description != null'>description = #{description},</if>" +
            "<if test='contactInfo != null'>contact_info = #{contactInfo},</if>" +
            "<if test='license != null'>license = #{license},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Store store);
    
    /**
     * 删除店铺
     * @param id 店铺ID
     * @return 影响行数
     */
    @Delete("DELETE FROM stores WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 更新店铺状态
     * @param id 店铺ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE stores SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 搜索店铺
     * @param keyword 关键字
     * @return 店铺列表
     */
    @Select("SELECT * FROM stores WHERE name LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%')")
    List<Store> search(String keyword);
} 