package example.shopping.mapper;

import example.shopping.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    int insert(Address address);
    
    /**
     * 更新地址
     * @param address 地址信息
     * @return 影响行数
     */
    int update(Address address);
    
    /**
     * 根据ID删除地址
     * @param id 地址ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据用户ID删除所有地址
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(Long userId);
    
    /**
     * 根据ID查找地址
     * @param id 地址ID
     * @return 地址信息
     */
    Address findById(Long id);
    
    /**
     * 根据用户ID查找所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> findByUserId(Long userId);
    
    /**
     * 根据用户ID查找默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    Address findDefaultByUserId(Long userId);
    
    /**
     * 将用户的所有地址设为非默认
     * @param userId 用户ID
     * @return 影响行数
     */
    int resetDefault(@Param("userId") Long userId);
} 