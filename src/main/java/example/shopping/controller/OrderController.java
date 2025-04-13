package example.shopping.controller;

import example.shopping.dto.OrderDTO;
import example.shopping.entity.Order;
import example.shopping.entity.Store;
import example.shopping.entity.User;
import example.shopping.service.OrderService;
import example.shopping.service.StoreService;
import example.shopping.service.UserService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    /**
     * 创建订单
     *
     * @param orderDTO 订单信息
     * @return 创建的订单
     */
    @PostMapping
    public Result<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        Long userId = getCurrentUserId();
        Order order = orderService.create(userId, orderDTO);
        return Result.success(order, "创建订单成功");
    }

    /**
     * 获取当前用户的订单列表
     *
     * @return 订单列表
     */
    @GetMapping
    public Result<List<Order>> getOrderList() {
        Long userId = getCurrentUserId();
        return Result.success(orderService.findByUserId(userId));
    }

    /**
     * 分页获取订单
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页订单列表
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> getOrdersByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.findByPage(pageNum, pageSize));
    }

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 获取当前用户角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 如果不是管理员，验证订单所属
        if (!isAdmin && !order.getUserId().equals(getCurrentUserId())) {
            return Result.error("无权查看此订单");
        }
        return Result.success(order);
    }

    /**
     * 按状态查询订单
     *
     * @param status 订单状态
     * @return 订单列表
     */
    @GetMapping("/status/{status}")
    public Result<List<Order>> getOrdersByStatus(@PathVariable Integer status) {
        // 获取当前用户角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // 管理员可以查看所有订单
            return Result.success(orderService.findByStatus(status));
        } else {
            // 普通用户只能查看自己的订单
            Long userId = getCurrentUserId();
            return Result.success(orderService.findByUserIdAndStatus(userId, status));
        }
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 取消结果
     */
    @PostMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        boolean success = orderService.cancel(userId, id);
        return Result.success(success, "取消订单成功");
    }

    /**
     * 商家发货
     *
     * @param id 订单ID
     * @return 发货结果
     */
    @PostMapping("/{id}/ship")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Boolean> shipOrder(@PathVariable Long id) {
        boolean success = orderService.ship(id);
        return Result.success(success, "发货成功");
    }

    /**
     * 确认收货
     *
     * @param id 订单ID
     * @return 确认结果
     */
    @PostMapping("/{id}/receive")
    public Result<Boolean> receiveOrder(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        boolean success = orderService.receive(userId, id);
        return Result.success(success, "确认收货成功");
    }

    /**
     * 获取订单状态统计
     *
     * @return 状态统计
     */
    @GetMapping("/count")
    public Result<Map<String, Integer>> getOrderCount() {
        Long userId = getCurrentUserId();
        return Result.success(orderService.countByStatus(userId));
    }

    /**
     * 获取店铺订单列表
     *
     * @param storeId 店铺ID
     * @return 订单列表
     */
    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<List<Order>> getStoreOrders(@PathVariable Long storeId) {
        // 获取当前商家用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证店铺所有权
        Store store = storeService.findById(storeId);
        if (store == null) {
            return Result.error("店铺不存在");
        }
        if (!store.getUserId().equals(user.getId())) {
            return Result.error("无权查看此店铺的订单");
        }

        return Result.success(orderService.findByStoreId(storeId));
    }

    /**
     * 获取店铺订单统计信息
     *
     * @param storeId 店铺ID
     * @return 订单统计信息
     */
    @GetMapping("/store/{storeId}/stats")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Map<String, Object>> getStoreOrderStats(@PathVariable Long storeId) {
        // 获取当前商家用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证店铺所有权
        Store store = storeService.findById(storeId);
        if (store == null) {
            return Result.error("店铺不存在");
        }
        if (!store.getUserId().equals(user.getId())) {
            return Result.error("无权查看此店铺的订单统计");
        }

        // 获取店铺订单列表
        List<Order> orders = orderService.findByStoreId(storeId);

        // 统计各状态订单数量
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", orders.size());
        stats.put("pendingPayment", orders.stream().filter(o -> o.getStatus() == 0).count());
        stats.put("pendingShipment", orders.stream().filter(o -> o.getStatus() == 1).count());
        stats.put("shipped", orders.stream().filter(o -> o.getStatus() == 2).count());
        // 已完成订单包括状态为3（已完成）和状态为8（已评价）的订单
        stats.put("completed", orders.stream().filter(o -> o.getStatus() == 3 || o.getStatus() == 8).count());
        stats.put("cancelled", orders.stream().filter(o -> o.getStatus() == 4).count());
        stats.put("refunded", orders.stream().filter(o -> o.getStatus() == 5).count());
        // 添加退款申请中的订单统计
        stats.put("refundPending", orders.stream().filter(o -> o.getStatus() == 6).count());

        return Result.success(stats);
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getId();
    }
}
