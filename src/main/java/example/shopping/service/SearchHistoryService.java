package example.shopping.service;

import example.shopping.entity.SearchHistory;

import java.util.List;
import java.util.Map;

/**
 * 搜索历史服务接口
 */
public interface SearchHistoryService {
    
    /**
     * 添加搜索历史
     * @param userId 用户ID
     * @param keyword 关键词
     * @param resultCount 结果数量
     * @return 添加的搜索历史
     */
    SearchHistory add(Long userId, String keyword, Integer resultCount);
    
    /**
     * 查询用户搜索历史
     * @param userId 用户ID
     * @return 搜索历史列表
     */
    List<SearchHistory> findByUserId(Long userId);
    
    /**
     * 分页查询用户搜索历史
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 包含分页信息的搜索历史列表
     */
    Map<String, Object> findByUserIdWithPage(Long userId, int pageNum, int pageSize);
    
    /**
     * 查询用户搜索过的关键词
     * @param userId 用户ID
     * @return 关键词列表
     */
    List<String> findKeywordsByUserId(Long userId);
    
    /**
     * 删除搜索历史
     * @param id 搜索历史ID
     * @return 是否删除成功
     */
    boolean delete(Long id);
    
    /**
     * 清空用户搜索历史
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clear(Long userId);
    
    /**
     * 获取热门搜索词
     * @param limit 数量限制
     * @return 热门搜索词列表
     */
    List<Map<String, Object>> findHotKeywords(int limit);
    
    /**
     * 基于用户搜索历史推荐商品
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 推荐商品ID列表
     */
    List<Long> recommendProductsBySearchHistory(Long userId, int limit);
} 