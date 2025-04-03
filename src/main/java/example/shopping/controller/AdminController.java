package example.shopping.controller;

import example.shopping.entity.Store;
import example.shopping.entity.User;
import example.shopping.service.CustomerServiceInterface;
import example.shopping.service.StoreService;
import example.shopping.service.UserService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private CustomerServiceInterface customerService;

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.findAll());
    }

    /**
     * 按角色获取用户列表
     * @param role 角色
     * @return 用户列表
     */
    @GetMapping("/users/role")
    public Result<List<User>> getUsersByRole(@RequestParam String role) {
        return Result.success(userService.findByRole(role));
    }

    /**
     * 获取所有店铺列表
     * @return 店铺列表
     */
    @GetMapping("/stores")
    public Result<List<Store>> getAllStores() {
        return Result.success(storeService.findAll());
    }

    /**
     * 获取待审核店铺列表
     * @return 待审核店铺列表
     */
    @GetMapping("/stores/pending")
    public Result<List<Store>> getPendingStores() {
        return Result.success(storeService.findByStatus(0));
    }

    /**
     * 审核店铺
     * @param id 店铺ID
     * @param status 状态(0-审核中, 1-正常, 2-关闭)
     * @return 是否审核成功
     */
    @PutMapping("/stores/{id}/status")
    public Result<Boolean> auditStore(
            @PathVariable Long id,
            @RequestParam Integer status) {
        boolean result = storeService.updateStatus(id, status);
        String message;
        if (status == 1) {
            message = "店铺审核通过";
        } else if (status == 2) {
            message = "店铺已关闭";
        } else {
            message = "店铺状态更新成功";
        }
        return Result.success(result, message);
    }

    /**
     * 获取所有店铺的客服满意度统计
     * @return 店铺客服满意度统计
     */
    @GetMapping("/customer-service/stats")
    public Result<List<Map<String, Object>>> getServiceRatingStats() {
        return Result.success(customerService.getServiceRatingStats());
    }

    /**
     * 获取客服满意度低于指定值的店铺
     * @param threshold 满意度阈值
     * @return 满意度较低的店铺列表
     */
    @GetMapping("/customer-service/low-rating")
    public Result<List<Map<String, Object>>> getLowRatingStores(@RequestParam double threshold) {
        List<Map<String, Object>> stats = customerService.getServiceRatingStats();
        // 过滤出满意度低于阈值的店铺
        stats.removeIf(stat -> (Double) stat.get("averageRating") >= threshold);
        return Result.success(stats);
    }

    /**
     * 获取指定店铺的客服满意度详情
     * @param storeId 店铺ID
     * @return 客服满意度详情
     */
    @GetMapping("/customer-service/store/{storeId}")
    public Result<Map<String, Object>> getStoreServiceDetail(@PathVariable Long storeId) {
        Store store = storeService.findById(storeId);
        if (store == null) {
            return Result.error("店铺不存在");
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("storeId", store.getId());
        detail.put("storeName", store.getName());
        detail.put("averageRating", storeService.getCustomerServiceRating(storeId));
        detail.put("sessionCount", customerService.getSessionCount(storeId));
        detail.put("responseTime", customerService.getAverageResponseTime(storeId));

        return Result.success(detail);
    }

    /**
     * 获取客服会话投诉列表
     * @return 投诉列表
     */
    @GetMapping("/customer-service/complaints")
    public Result<List<Map<String, Object>>> getServiceComplaints() {
        return Result.success(customerService.getComplaints());
    }
}
