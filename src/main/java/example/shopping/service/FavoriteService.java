package example.shopping.service;

import example.shopping.entity.Favorite;

import java.util.List;
import java.util.Map;

/**
 * 收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 添加收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 添加的收藏
     */
    Favorite add(Long userId, Long productId);
    
    /**
     * 取消收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否取消成功
     */
    boolean cancel(Long userId, Long productId);
    
    /**
     * 查询用户的收藏列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Map<String, Object>> findByUserId(Long userId);
    
    /**
     * 分页查询用户的收藏列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 包含分页信息的收藏列表
     */
    Map<String, Object> findByUserIdWithPage(Long userId, int pageNum, int pageSize);
    
    /**
     * 查询用户是否收藏了某商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否收藏
     */
    boolean isFavorite(Long userId, Long productId);
    
    /**
     * 统计商品被收藏次数
     * @param productId 商品ID
     * @return 收藏次数
     */
    int countByProductId(Long productId);
} 