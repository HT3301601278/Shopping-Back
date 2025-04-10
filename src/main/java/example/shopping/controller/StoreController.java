package example.shopping.controller;

import example.shopping.entity.Store;
import example.shopping.entity.User;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.UserMapper;
import example.shopping.service.StoreService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 店铺控制器
 */
@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;
    
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有店铺
     * @return 店铺列表
     */
    @GetMapping
    public Result<List<Store>> getAllStores() {
        return Result.success(storeService.findAll());
    }

    /**
     * 分页获取店铺
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 包含分页信息的店铺列表
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> getStoresByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(storeService.findByPage(pageNum, pageSize));
    }

    /**
     * 根据ID获取店铺
     * @param id 店铺ID
     * @return 店铺信息
     */
    @GetMapping("/{id}")
    public Result<Store> getStoreById(@PathVariable Long id) {
        return Result.success(storeService.findById(id));
    }

    /**
     * 搜索店铺
     * @param keyword 关键字
     * @return 店铺列表
     */
    @GetMapping("/search")
    public Result<List<Store>> searchStores(@RequestParam String keyword) {
        return Result.success(storeService.search(keyword));
    }

    /**
     * 创建店铺
     * @param store 店铺信息
     * @return 创建的店铺
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Store> createStore(@Valid @RequestBody Store store) {
        // 从当前登录用户获取用户ID
        Long userId = getCurrentUserId();
        return Result.success(storeService.create(userId, store), "店铺创建成功，等待审核");
    }

    /**
     * 更新店铺信息
     * @param id 店铺ID
     * @param store 店铺信息
     * @return 更新后的店铺
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Store> updateStore(@PathVariable Long id, @Valid @RequestBody Store store) {
        // 验证当前用户是否为店铺所有者
        validateStoreOwner(id);
        return Result.success(storeService.update(id, store), "店铺更新成功");
    }

    /**
     * 删除店铺
     * @param id 店铺ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> deleteStore(@PathVariable Long id) {
        return Result.success(storeService.delete(id), "店铺删除成功");
    }

    /**
     * 获取店铺的客服满意度
     * @param id 店铺ID
     * @return 客服满意度评分
     */
    @GetMapping("/{id}/service-rating")
    public Result<Double> getStoreServiceRating(@PathVariable Long id) {
        return Result.success(storeService.getCustomerServiceRating(id));
    }

    /**
     * 审核店铺 (管理员功能)
     * @param id 店铺ID
     * @param status 状态(0-审核中, 1-正常, 2-关闭)
     * @return 是否审核成功
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
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
     * 获取待审核的店铺列表 (管理员功能)
     * @return 待审核的店铺列表
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Store>> getPendingStores() {
        // 假设status=0表示审核中的店铺
        return Result.success(storeService.findByStatus(0), "获取待审核店铺列表成功");
    }

    /**
     * 获取当前登录商家的所有店铺
     * @return 店铺列表
     */
    @GetMapping("/my-stores")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<List<Store>> getMyStores() {
        Long userId = getCurrentUserId();
        return Result.success(storeService.findByUserId(userId));
    }

    /**
     * 获取当前登录商家的所有正常营业的店铺
     * @return 店铺列表
     */
    @GetMapping("/my-stores/active")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<List<Store>> getMyActiveStores() {
        Long userId = getCurrentUserId();
        return Result.success(storeService.findActiveStoresByUserId(userId));
    }

    /**
     * 获取当前登录商家指定状态的店铺
     * @param status 店铺状态（0：待审核，1：正常，2：已关闭，3：审核未通过）
     * @return 店铺列表
     */
    @GetMapping("/my-stores/status/{status}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<List<Store>> getMyStoresByStatus(@PathVariable Integer status) {
        Long userId = getCurrentUserId();
        return Result.success(storeService.findStoresByUserIdAndStatus(userId, status));
    }

    /**
     * 获取当前登录商家的店铺统计信息
     * @return 店铺统计信息
     */
    @GetMapping("/my-stores/stats")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Map<String, Object>> getMyStoresStats() {
        Long userId = getCurrentUserId();
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", storeService.countStoresByUserId(userId));
        stats.put("pending", storeService.countStoresByUserIdAndStatus(userId, 0));
        stats.put("active", storeService.countStoresByUserIdAndStatus(userId, 1));
        stats.put("closed", storeService.countStoresByUserIdAndStatus(userId, 2));
        stats.put("rejected", storeService.countStoresByUserIdAndStatus(userId, 3));
        return Result.success(stats);
    }

    /**
     * 获取当前登录商家的默认店铺（第一个正常营业的店铺）
     * @return 店铺信息
     */
    @GetMapping("/my-store")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Store> getMyStore() {
        Long userId = getCurrentUserId();
        List<Store> activeStores = storeService.findActiveStoresByUserId(userId);
        if (activeStores.isEmpty()) {
            throw new BusinessException("没有找到正常营业的店铺");
        }
        return Result.success(activeStores.get(0));
    }

    // 工具方法：获取当前登录用户ID
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userMapper.findByUsername(username);
            if (user != null) {
                return user.getId();
            }
        }
        throw new BusinessException("用户未登录或登录已过期");
    }

    // 工具方法：验证当前用户是否为店铺所有者
    private void validateStoreOwner(Long storeId) {
        Long userId = getCurrentUserId();
        Store store = storeService.findById(storeId);
        if (store == null || !store.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此店铺");
        }
    }
} 