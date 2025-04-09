package example.shopping.mapper;

import example.shopping.entity.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface ReviewMapper {
    
    /**
     * 查询所有评论
     * @return 评论列表
     */
    @Select("SELECT * FROM reviews ORDER BY is_top DESC, create_time DESC")
    List<Review> findAll();
    
    /**
     * 根据ID查询评论
     * @param id 评论ID
     * @return 评论信息
     */
    @Select("SELECT * FROM reviews WHERE id = #{id}")
    Review findById(Long id);
    
    /**
     * 根据商品ID查询评论
     * @param productId 商品ID
     * @return 评论列表
     */
    @Select("SELECT * FROM reviews WHERE product_id = #{productId} ORDER BY is_top DESC, create_time DESC")
    List<Review> findByProductId(Long productId);
    
    /**
     * 根据用户ID查询评论
     * @param userId 用户ID
     * @return 评论列表
     */
    @Select("SELECT * FROM reviews WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Review> findByUserId(Long userId);
    
    /**
     * 根据订单ID查询评论
     * @param orderId 订单ID
     * @return 评论列表
     */
    @Select("SELECT * FROM reviews WHERE order_id = #{orderId}")
    List<Review> findByOrderId(Long orderId);
    
    /**
     * 根据商品ID和用户ID查询评论
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 评论信息
     */
    @Select("SELECT * FROM reviews WHERE product_id = #{productId} AND user_id = #{userId}")
    List<Review> findByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);
    
    /**
     * 根据状态查询评论
     * @param status 状态
     * @return 评论列表
     */
    @Select("SELECT * FROM reviews WHERE status = #{status} ORDER BY create_time DESC")
    List<Review> findByStatus(Integer status);
    
    /**
     * 分页查询评论
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 评论列表
     */
    @Select("SELECT * FROM reviews ORDER BY is_top DESC, create_time DESC LIMIT #{offset}, #{limit}")
    List<Review> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 插入评论
     * @param review 评论信息
     * @return 影响行数
     */
    @Insert("INSERT INTO reviews(product_id, user_id, order_id, content, rating, images, " +
            "status, is_top, create_time, update_time) " +
            "VALUES(#{productId}, #{userId}, #{orderId}, #{content}, #{rating}, #{images}, " +
            "#{status}, #{isTop}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);
    
    /**
     * 更新评论
     * @param review 评论信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE reviews " +
            "<set>" +
            "<if test='content != null'>content = #{content},</if>" +
            "<if test='rating != null'>rating = #{rating},</if>" +
            "<if test='images != null'>images = #{images},</if>" +
            "<if test='reply != null'>reply = #{reply},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='isTop != null'>is_top = #{isTop},</if>" +
            "update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Review review);
    
    /**
     * 删除评论
     * @param id 评论ID
     * @return 影响行数
     */
    @Delete("DELETE FROM reviews WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 更新评论状态
     * @param id 评论ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE reviews SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 更新评论置顶状态
     * @param id 评论ID
     * @param isTop 是否置顶
     * @return 影响行数
     */
    @Update("UPDATE reviews SET is_top = #{isTop}, update_time = NOW() WHERE id = #{id}")
    int updateTopStatus(@Param("id") Long id, @Param("isTop") Boolean isTop);
    
    /**
     * 更新商家回复
     * @param id 评论ID
     * @param reply 回复内容
     * @return 影响行数
     */
    @Update("UPDATE reviews SET reply = #{reply}, update_time = NOW() WHERE id = #{id}")
    int updateReply(@Param("id") Long id, @Param("reply") String reply);
    
    /**
     * 统计商品评论数量
     * @param productId 商品ID
     * @return 评论数量
     */
    @Select("SELECT COUNT(*) FROM reviews WHERE product_id = #{productId} AND status = 1")
    int countByProductId(Long productId);
    
    /**
     * 计算商品平均评分
     * @param productId 商品ID
     * @return 平均评分
     */
    @Select("SELECT AVG(rating) FROM reviews WHERE product_id = #{productId} AND status = 1")
    Double calculateAverageRating(Long productId);
    
    /**
     * 根据商品ID和状态查询评论
     * @param productId 商品ID
     * @param status 评论状态
     * @return 评论列表
     */
    @Select("SELECT * FROM reviews WHERE product_id = #{productId} AND status = #{status} ORDER BY is_top DESC, create_time DESC")
    List<Review> findByProductIdAndStatus(@Param("productId") Long productId, @Param("status") Integer status);
    
    /**
     * 商家查询与自己商品相关的评论
     * @param productId 商品ID
     * @param storeId 店铺ID
     * @return 评论列表
     */
    @Select("SELECT r.* FROM reviews r " +
            "INNER JOIN products p ON r.product_id = p.id " +
            "WHERE r.product_id = #{productId} AND p.store_id = " +
            "(SELECT store_id FROM products WHERE id = #{productId}) " +
            "ORDER BY r.is_top DESC, r.create_time DESC")
    List<Review> findByProductIdForMerchant(@Param("productId") Long productId);
} 