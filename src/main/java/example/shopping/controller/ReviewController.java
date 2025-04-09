package example.shopping.controller;

import example.shopping.dto.ReviewDTO;
import example.shopping.entity.Review;
import example.shopping.entity.User;
import example.shopping.service.ReviewService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 添加评论
     * @param reviewDTO 评论信息
     * @return 添加的评论
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Review> addReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        Long userId = getCurrentUserId();
        return Result.success(reviewService.add(userId, reviewDTO), "评论发布成功");
    }

    /**
     * 获取商品评论列表
     * @param productId 商品ID
     * @return 评论列表
     */
    @GetMapping("/product/{productId}")
    public Result<List<Review>> getProductReviews(@PathVariable Long productId) {
        return Result.success(reviewService.findByProductId(productId));
    }

    /**
     * 获取用户评论列表
     * @param userId 用户ID
     * @return 评论列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Review>> getUserReviews(@PathVariable Long userId) {
        return Result.success(reviewService.findByUserId(userId));
    }

    /**
     * 获取订单评论
     * @param orderId 订单ID
     * @return 评论列表
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public Result<List<Review>> getOrderReviews(@PathVariable Long orderId) {
        return Result.success(reviewService.findByOrderId(orderId));
    }

    /**
     * 分页查询评论
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页评论列表
     */
    @GetMapping
    public Result<Map<String, Object>> getReviewsByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(reviewService.findByPage(pageNum, pageSize));
    }

    /**
     * 商家回复评论
     * @param id 评论ID
     * @param reply 回复内容
     * @return 是否回复成功
     */
    @PutMapping("/{id}/reply")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Boolean> replyReview(
            @PathVariable Long id,
            @RequestParam String reply) {
        return Result.success(reviewService.reply(id, reply), "回复成功");
    }

    /**
     * 管理员审核评论
     * @param id 评论ID
     * @param status 状态(1-显示, 2-隐藏)
     * @return 是否审核成功
     */
    @PutMapping("/{id}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> auditReview(
            @PathVariable Long id,
            @RequestParam Integer status) {
        return Result.success(reviewService.audit(id, status), "审核成功");
    }

    /**
     * 管理员设置评论置顶
     * @param id 评论ID
     * @param isTop 是否置顶
     * @return 是否设置成功
     */
    @PutMapping("/{id}/top")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> setReviewTop(
            @PathVariable Long id,
            @RequestParam Boolean isTop) {
        return Result.success(reviewService.setTop(id, isTop), 
            isTop ? "评论已置顶" : "评论已取消置顶");
    }

    /**
     * 删除评论
     * @param id 评论ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> deleteReview(@PathVariable Long id) {
        return Result.success(reviewService.delete(id), "评论删除成功");
    }

    /**
     * 获取待审核评论列表（管理员功能）
     * @return 待审核评论列表
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Review>> getPendingReviews() {
        return Result.success(reviewService.findPendingReviews());
    }

    /**
     * 获取商品评论统计
     * @param productId 商品ID
     * @return 评论统计信息
     */
    @GetMapping("/product/{productId}/stats")
    public Result<Map<String, Object>> getProductReviewStats(@PathVariable Long productId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("count", reviewService.countByProductId(productId));
        stats.put("averageRating", reviewService.calculateAverageRating(productId));
        return Result.success(stats);
    }

    // 工具方法：获取当前登录用户ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        return ((User) authentication.getPrincipal()).getId();
    }
} 