package example.shopping.service;

import example.shopping.dto.SearchHistoryDTO;
import example.shopping.entity.SearchHistory;

import java.util.List;
import java.util.Map;

/**
 * 搜索历史服务接口
 */
public interface SearchHistoryService {

    /**
     * 获取用户的搜索历史记录
     *
     * @param userId 用户ID
     * @return 搜索历史列表
     */
    List<SearchHistory> findByUserId(Long userId);

    /**
     * 获取用户的搜索历史记录（按时间倒序）
     *
     * @param userId 用户ID
     * @param limit  返回数量
     * @return 搜索历史列表
     */
    List<SearchHistory> findLatestByUserId(Long userId, Integer limit);

    /**
     * 添加搜索历史
     *
     * @param userId           用户ID
     * @param searchHistoryDTO 搜索历史信息
     * @return 添加的搜索历史
     */
    SearchHistory add(Long userId, SearchHistoryDTO searchHistoryDTO);

    /**
     * 删除单条搜索历史
     *
     * @param userId 用户ID
     * @param id     搜索历史ID
     * @return 是否删除成功
     */
    boolean delete(Long userId, Long id);

    /**
     * 清空用户的搜索历史
     *
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clear(Long userId);

    /**
     * 获取用户的热门搜索关键词
     *
     * @param userId 用户ID
     * @param limit  返回数量
     * @return 热门关键词列表
     */
    List<Map<String, Object>> getHotKeywords(Long userId, Integer limit);

    /**
     * 获取全站热门搜索关键词
     *
     * @param limit 返回数量
     * @return 热门关键词列表
     */
    List<Map<String, Object>> getGlobalHotKeywords(Integer limit);
}
