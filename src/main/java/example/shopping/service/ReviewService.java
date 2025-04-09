package example.shopping.service;

import example.shopping.dto.ReviewDTO;
import example.shopping.entity.Review;

import java.util.List;
import java.util.Map;

/**
 * 评论服务接口
 */
public interface ReviewService {
    
    /**
     * 添加评论
     * @param userId 用户ID
     * @param reviewDTO 评论信息
     * @return 添加的评论
     */
    Review add(Long userId, ReviewDTO reviewDTO);
    
    /**
     * 根据ID查询评论
     * @param id 评论ID
     * @return 评论信息
     */
    Review findById(Long id);
    
    /**
     * 根据商品ID查询评论
     * @param productId 商品ID
     * @return 评论列表
     */
    List<Review> findByProductId(Long productId);
    
    /**
     * 根据商品ID和状态查询评论
     * @param productId 商品ID
     * @param status 状态
     * @return 评论列表
     */
    List<Review> findByProductIdAndStatus(Long productId, Integer status);
    
    /**
     * 根据商品ID为商家查询评论
     * @param productId 商品ID
     * @param userId 商家ID
     * @return 评论列表
     */
    List<Review> findByProductIdForMerchant(Long productId, Long userId);
    
    /**
     * 根据用户ID查询评论
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Review> findByUserId(Long userId);
    
    /**
     * 根据订单ID查询评论
     * @param orderId 订单ID
     * @return 评论列表
     */
    List<Review> findByOrderId(Long orderId);
    
    /**
     * 查询待审核评论
     * @return 待审核评论列表
     */
    List<Review> findPendingReviews();
    
    /**
     * 分页查询评论
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 包含分页信息的评论列表
     */
    Map<String, Object> findByPage(int pageNum, int pageSize);
    
    /**
     * 商家回复评论
     * @param id 评论ID
     * @param reply 回复内容
     * @return 是否回复成功
     */
    boolean reply(Long id, String reply);
    
    /**
     * 管理员审核评论
     * @param id 评论ID
     * @param status 状态(1-显示, 2-隐藏)
     * @return 是否审核成功
     */
    boolean audit(Long id, Integer status);
    
    /**
     * 管理员设置评论置顶状态
     * @param id 评论ID
     * @param isTop 是否置顶
     * @return 是否设置成功
     */
    boolean setTop(Long id, Boolean isTop);
    
    /**
     * 删除评论
     * @param id 评论ID
     * @return 是否删除成功
     */
    boolean delete(Long id);
    
    /**
     * 统计商品评论数量
     * @param productId 商品ID
     * @return 评论数量
     */
    int countByProductId(Long productId);
    
    /**
     * 计算商品平均评分
     * @param productId 商品ID
     * @return 平均评分
     */
    Double calculateAverageRating(Long productId);
    
    /**
     * 更新商品评分
     * @param productId 商品ID
     * @return 是否更新成功
     */
    boolean updateProductRating(Long productId);
} 