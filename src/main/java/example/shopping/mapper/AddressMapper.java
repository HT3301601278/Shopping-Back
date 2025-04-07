package example.shopping.mapper;

import example.shopping.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 地址数据访问接口
 */
@Mapper
public interface AddressMapper {
    
    /**
     * 插入地址
     * @param address 地址信息
     * @return 影响行数
     */
    @Insert("INSERT INTO addresses(user_id, receiver_name, receiver_phone, province, city, district, " +
            "detail_address, is_default, create_time, update_time) " +
            "VALUES(#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, " +
            "#{detailAddress}, #{isDefault}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);
    
    /**
     * 更新地址
     * @param address 地址信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE addresses " +
            "<set>" +
            "<if test='receiverName != null'>receiver_name = #{receiverName},</if>" +
            "<if test='receiverPhone != null'>receiver_phone = #{receiverPhone},</if>" +
            "<if test='province != null'>province = #{province},</if>" +
            "<if test='city != null'>city = #{city},</if>" +
            "<if test='district != null'>district = #{district},</if>" +
            "<if test='detailAddress != null'>detail_address = #{detailAddress},</if>" +
            "<if test='isDefault != null'>is_default = #{isDefault},</if>" +
            "update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Address address);
    
    /**
     * 根据ID删除地址
     * @param id 地址ID
     * @return 影响行数
     */
    @Delete("DELETE FROM addresses WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据用户ID删除所有地址
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM addresses WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 根据ID查找地址
     * @param id 地址ID
     * @return 地址信息
     */
    @Select("SELECT * FROM addresses WHERE id = #{id}")
    Address findById(Long id);
    
    /**
     * 根据用户ID查找所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    @Select("SELECT * FROM addresses WHERE user_id = #{userId}")
    List<Address> findByUserId(Long userId);
    
    /**
     * 根据用户ID查找默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    @Select("SELECT * FROM addresses WHERE user_id = #{userId} AND is_default = 1")
    Address findDefaultByUserId(Long userId);
    
    /**
     * 将用户的所有地址设为非默认
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE addresses SET is_default = 0 WHERE user_id = #{userId}")
    int resetDefault(@Param("userId") Long userId);
} 