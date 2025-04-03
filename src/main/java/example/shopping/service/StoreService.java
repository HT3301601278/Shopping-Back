package example.shopping.service;

import example.shopping.entity.Store;

import java.util.List;
import java.util.Map;

/**
 * 店铺服务接口
 */
public interface StoreService {
    
    /**
     * 创建店铺
     * @param userId 用户ID
     * @param store 店铺信息
     * @return 创建的店铺
     */
    Store create(Long userId, Store store);
    
    /**
     * 更新店铺信息
     * @param id 店铺ID
     * @param store 店铺信息
     * @return 更新后的店铺
     */
    Store update(Long id, Store store);
    
    /**
     * 根据ID查询店铺
     * @param id 店铺ID
     * @return 店铺信息
     */
    Store findById(Long id);
    
    /**
     * 根据用户ID查询店铺
     * @param userId 用户ID
     * @return 店铺信息
     */
    Store findByUserId(Long userId);
    
    /**
     * 查询所有店铺
     * @return 店铺列表
     */
    List<Store> findAll();
    
    /**
     * 分页查询店铺
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 包含分页信息的店铺列表
     */
    Map<String, Object> findByPage(int pageNum, int pageSize);
    
    /**
     * 搜索店铺
     * @param keyword 关键字
     * @return 店铺列表
     */
    List<Store> search(String keyword);
    
    /**
     * 更新店铺状态
     * @param id 店铺ID
     * @param status 状态(0-审核中, 1-正常, 2-关闭)
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 删除店铺
     * @param id 店铺ID
     * @return 是否删除成功
     */
    boolean delete(Long id);
    
    /**
     * 获取店铺的客服满意度
     * @param id 店铺ID
     * @return 客服满意度评分
     */
    Double getCustomerServiceRating(Long id);
} 