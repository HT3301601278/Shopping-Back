package example.shopping.controller;

import example.shopping.dto.OrderDTO;
import example.shopping.entity.Order;
import example.shopping.entity.User;
import example.shopping.service.OrderService;
import example.shopping.service.UserService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    /**
     * 创建订单
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
     * @return 订单列表
     */
    @GetMapping
    public Result<List<Order>> getOrderList() {
        Long userId = getCurrentUserId();
        return Result.success(orderService.findByUserId(userId));
    }

    /**
     * 分页获取订单
     * @param pageNum 页码
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
     * @return 状态统计
     */
    @GetMapping("/count")
    public Result<Map<String, Integer>> getOrderCount() {
        Long userId = getCurrentUserId();
        return Result.success(orderService.countByStatus(userId));
    }

    /**
     * 获取当前登录用户ID
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