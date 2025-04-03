package example.shopping.mapper;

import example.shopping.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 收藏Mapper接口
 */
@Mapper
public interface FavoriteMapper {
    
    /**
     * 查询用户的收藏
     * @param userId 用户ID
     * @return 收藏列表
     */
    @Select("SELECT * FROM favorites WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Favorite> findByUserId(Long userId);
    
    /**
     * 查询商品的收藏
     * @param productId 商品ID
     * @return 收藏列表
     */
    @Select("SELECT * FROM favorites WHERE product_id = #{productId}")
    List<Favorite> findByProductId(Long productId);
    
    /**
     * 查询用户是否收藏了某商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 收藏信息
     */
    @Select("SELECT * FROM favorites WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    Favorite findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 插入收藏
     * @param favorite 收藏信息
     * @return 影响行数
     */
    @Insert("INSERT INTO favorites(user_id, product_id, create_time) VALUES(#{userId}, #{productId}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Favorite favorite);
    
    /**
     * 删除收藏
     * @param id 收藏ID
     * @return 影响行数
     */
    @Delete("DELETE FROM favorites WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 删除用户的收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 影响行数
     */
    @Delete("DELETE FROM favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 统计商品被收藏次数
     * @param productId 商品ID
     * @return 收藏次数
     */
    @Select("SELECT COUNT(*) FROM favorites WHERE product_id = #{productId}")
    int countByProductId(Long productId);
    
    /**
     * 分页查询用户收藏
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 收藏列表
     */
    @Select("SELECT * FROM favorites WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Favorite> findByUserIdWithPage(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
} 