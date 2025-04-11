package example.shopping.service.impl;

import com.alibaba.fastjson.JSON;
import example.shopping.dto.ReviewDTO;
import example.shopping.entity.Order;
import example.shopping.entity.Product;
import example.shopping.entity.Review;
import example.shopping.entity.Store;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.OrderMapper;
import example.shopping.mapper.ProductMapper;
import example.shopping.mapper.ReviewMapper;
import example.shopping.mapper.StoreMapper;
import example.shopping.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论服务实现类
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Override
    @Transactional
    public Review add(Long userId, ReviewDTO reviewDTO) {
        // 验证必填字段
        if (reviewDTO.getProductId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        if (reviewDTO.getOrderId() == null) {
            throw new BusinessException("订单ID不能为空");
        }
        if (reviewDTO.getRating() == null) {
            throw new BusinessException("评分不能为空");
        }

        // 检查订单是否存在
        Order order = orderMapper.findById(reviewDTO.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单是否属于该用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        // 检查订单状态是否允许评价
        int status = order.getStatus();
        if (status != 3 && status != 5 && status != 6 && status != 7) { 
            throw new BusinessException("当前订单状态不可评价");
        }
        
        // 检查是否已经评价过
        List<Review> existingReviews = reviewMapper.findByOrderId(reviewDTO.getOrderId());
        if (existingReviews != null && !existingReviews.isEmpty()) {
            throw new BusinessException("该订单已评价过");
        }
        
        // 检查商品是否存在
        Product product = productMapper.findById(reviewDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 创建评论
        Review review = new Review();
        review.setProductId(reviewDTO.getProductId());
        review.setUserId(userId);
        review.setOrderId(reviewDTO.getOrderId());
        review.setContent(reviewDTO.getContent());
        review.setRating(reviewDTO.getRating());
        
        // 处理图片
        if (reviewDTO.getImages() != null && !reviewDTO.getImages().isEmpty()) {
            review.setImages(JSON.toJSONString(reviewDTO.getImages()));
        }
        
        // 设置评论类型
        review.setType(reviewDTO.getType() != null ? reviewDTO.getType() : 0); // 默认为用户评论
        
        // 设置默认状态
        review.setStatus(0); // 0-审核中
        review.setIsTop(false);
        
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(new Date());
        
        reviewMapper.insert(review);
        
        // 更新商品评分
        updateProductRating(reviewDTO.getProductId());
        
        // 更新订单状态为已评价
        order.setStatus(8); // 8-已评价
        order.setUpdateTime(new Date());
        orderMapper.update(order);
        
        return review;
    }

    @Override
    public Review findById(Long id) {
        return reviewMapper.findById(id);
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        return reviewMapper.findReviewsAndRepliesByProductId(productId, 0);
    }

    @Override
    public List<Review> findByProductIdAndStatus(Long productId, Integer status) {
        return reviewMapper.findReviewsAndRepliesByProductId(productId, status);
    }

    @Override
    public List<Review> findByProductIdForMerchant(Long productId, Long userId) {
        return reviewMapper.findByProductIdForMerchant(productId);
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewMapper.findByUserId(userId);
    }

    @Override
    public List<Review> findByOrderId(Long orderId) {
        return reviewMapper.findByOrderId(orderId);
    }

    @Override
    public List<Review> findPendingReviews() {
        return reviewMapper.findByStatus(1); // 1-待审核
    }

    @Override
    public Map<String, Object> findByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Review> reviews = reviewMapper.findByPage(offset, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", reviews);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        // TODO: 添加总数统计
        
        return result;
    }

    @Override
    @Transactional
    public Review reply(Long userId, ReviewDTO reviewDTO) {
        // 检查父评论是否存在
        Review parentReview = reviewMapper.findById(reviewDTO.getParentId());
        if (parentReview == null) {
            throw new BusinessException("原评论不存在");
        }

        // 创建回复评论
        Review review = new Review();
        review.setProductId(parentReview.getProductId());
        review.setUserId(userId);
        review.setOrderId(parentReview.getOrderId());
        review.setContent(reviewDTO.getContent());
        review.setParentId(reviewDTO.getParentId());
        review.setType(reviewDTO.getType());
        review.setRating(0); // 回复评论设置评分为0
        review.setStatus(0); // 设置状态为正常显示
        review.setIsTop(false);
        
        // 处理图片
        if (reviewDTO.getImages() != null && !reviewDTO.getImages().isEmpty()) {
            review.setImages(JSON.toJSONString(reviewDTO.getImages()));
        }
        
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(new Date());
        
        reviewMapper.insert(review);
        
        return review;
    }

    @Override
    @Transactional
    public Review submitForReview(Long id, String reason) {
        Review review = reviewMapper.findById(id);
        if (review == null) {
            throw new BusinessException("评论不存在");
        }
        
        // 只有正常显示的评论可以提交审核
        if (review.getStatus() != 0) {
            throw new BusinessException("该评论当前状态不可提交审核");
        }
        
        // 更新评论状态为待审核
        review.setStatus(1);
        review.setReason(reason);
        review.setUpdateTime(new Date());
        
        reviewMapper.updateStatus(id, 1, reason);
        
        return review;
    }

    @Override
    public List<Review> findRepliesByParentId(Long parentId) {
        return reviewMapper.findRepliesByParentId(parentId);
    }

    @Override
    @Transactional
    public boolean audit(Long id, Integer status) {
        Review review = reviewMapper.findById(id);
        if (review == null) {
            throw new BusinessException("评论不存在");
        }
        
        if (status != 1 && status != 2) {
            throw new BusinessException("状态值无效");
        }
        
        boolean result = reviewMapper.updateStatus(id, status, review.getReason()) > 0;
        
        // 如果评论状态有变化，更新商品评分
        if (result && review.getStatus() != status) {
            updateProductRating(review.getProductId());
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean setTop(Long id, Boolean isTop, Long merchantId) {
        Review review = reviewMapper.findById(id);
        if (review == null) {
            throw new BusinessException("评论不存在");
        }
        
        // 检查商品是否存在
        Product product = productMapper.findById(review.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 获取店铺信息
        Store store = storeMapper.findById(product.getStoreId());
        if (store == null) {
            throw new BusinessException("店铺不存在");
        }
        
        // 验证商家权限（检查店铺是否属于该商家）
        if (!store.getUserId().equals(merchantId)) {
            throw new BusinessException("无权操作此评论");
        }
        
        return reviewMapper.updateTopStatus(id, isTop) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Review review = reviewMapper.findById(id);
        if (review == null) {
            throw new BusinessException("评论不存在");
        }
        
        boolean result = reviewMapper.deleteById(id) > 0;
        
        // 更新商品评分
        if (result) {
            updateProductRating(review.getProductId());
        }
        
        return result;
    }

    @Override
    public int countByProductId(Long productId) {
        return reviewMapper.countByProductId(productId);
    }

    @Override
    public Double calculateAverageRating(Long productId) {
        return reviewMapper.calculateAverageRating(productId);
    }

    @Override
    public List<Review> findReviewAndAllReplies(Long reviewId) {
        return reviewMapper.findReviewAndAllReplies(reviewId);
    }

    @Override
    @Transactional
    public boolean updateProductRating(Long productId) {
        Double avgRating = calculateAverageRating(productId);
        if (avgRating == null) {
            avgRating = 5.0; // 默认5星
        }
        
        Product product = productMapper.findById(productId);
        if (product == null) {
            return false;
        }
        
        product.setRating(avgRating);
        product.setUpdateTime(new Date());
        
        return productMapper.update(product) > 0;
    }
} 