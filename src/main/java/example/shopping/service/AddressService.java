package example.shopping.service;

import example.shopping.dto.AddressDTO;
import example.shopping.entity.Address;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {
    
    /**
     * 获取用户的所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> findByUserId(Long userId);
    
    /**
     * 根据ID获取地址
     * @param id 地址ID
     * @return 地址信息
     */
    Address findById(Long id);
    
    /**
     * 添加地址
     * @param userId 用户ID
     * @param addressDTO 地址信息
     * @return 添加的地址
     */
    Address add(Long userId, AddressDTO addressDTO);
    
    /**
     * 更新地址
     * @param userId 用户ID
     * @param id 地址ID
     * @param addressDTO 地址信息
     * @return 更新后的地址
     */
    Address update(Long userId, Long id, AddressDTO addressDTO);
    
    /**
     * 删除地址
     * @param userId 用户ID
     * @param id 地址ID
     * @return 是否删除成功
     */
    boolean delete(Long userId, Long id);
    
    /**
     * 设置默认地址
     * @param userId 用户ID
     * @param id 地址ID
     * @return 是否设置成功
     */
    boolean setDefault(Long userId, Long id);
    
    /**
     * 获取用户的默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    Address getDefault(Long userId);
} 