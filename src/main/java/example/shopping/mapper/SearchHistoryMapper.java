package example.shopping.mapper;

import example.shopping.entity.SearchHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 搜索历史Mapper接口
 */
@Mapper
public interface SearchHistoryMapper {

    /**
     * 根据用户ID查询搜索历史
     *
     * @param userId 用户ID
     * @return 搜索历史列表
     */
    @Select("SELECT * FROM search_histories WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<SearchHistory> findByUserId(Long userId);

    /**
     * 分页查询用户搜索历史
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit  数量限制
     * @return 搜索历史列表
     */
    @Select("SELECT * FROM search_histories WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<SearchHistory> findByUserIdWithPage(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询用户搜索过的关键词
     *
     * @param userId 用户ID
     * @return 关键词列表
     */
    @Select("SELECT DISTINCT keyword FROM search_histories WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<String> findKeywordsByUserId(Long userId);

    /**
     * 插入搜索历史
     *
     * @param searchHistory 搜索历史信息
     * @return 影响行数
     */
    @Insert("INSERT INTO search_histories(user_id, keyword, result_count, create_time) VALUES(#{userId}, #{keyword}, #{resultCount}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SearchHistory searchHistory);

    /**
     * 删除搜索历史
     *
     * @param id 搜索历史ID
     * @return 影响行数
     */
    @Delete("DELETE FROM search_histories WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 清空用户搜索历史
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM search_histories WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);

    /**
     * 获取热门搜索词
     *
     * @param limit 数量限制
     * @return 热门搜索词列表
     */
    @Select("SELECT keyword, COUNT(*) as count FROM search_histories GROUP BY keyword ORDER BY count DESC LIMIT #{limit}")
    List<Map<String, Object>> findHotKeywords(int limit);
}
