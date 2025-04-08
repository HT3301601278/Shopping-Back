package example.shopping.service;

import example.shopping.dto.OrderDTO;
import example.shopping.entity.Order;

import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param userId 用户ID
     * @param orderDTO 订单信息
     * @return 创建的订单
     */
    Order create(Long userId, OrderDTO orderDTO);
    
    /**
     * 根据订单ID查询订单
     * @param id 订单ID
     * @return 订单信息
     */
    Order findById(Long id);
    
    /**
     * 根据订单编号查询订单
     * @param orderNo 订单编号
     * @return 订单信息
     */
    Order findByOrderNo(String orderNo);
    
    /**
     * 根据用户ID查询订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据店铺ID查询订单
     * @param storeId 店铺ID
     * @return 订单列表
     */
    List<Order> findByStoreId(Long storeId);
    
    /**
     * 根据用户ID和订单状态查询订单
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 分页查询订单
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 包含分页信息的订单列表
     */
    Map<String, Object> findByPage(int pageNum, int pageSize);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 更新后的订单
     */
    Order updateStatus(Long id, Integer status);
    
    /**
     * 取消订单
     * @param userId 用户ID
     * @param id 订单ID
     * @return 是否取消成功
     */
    boolean cancel(Long userId, Long id);
    
    /**
     * 支付订单
     * @param userId 用户ID
     * @param id 订单ID
     * @param paymentType 支付方式
     * @return 是否支付成功
     */
    boolean pay(Long userId, Long id, String paymentType);
    
    /**
     * 发货
     * @param id 订单ID
     * @return 是否发货成功
     */
    boolean ship(Long id);
    
    /**
     * 确认收货
     * @param userId 用户ID
     * @param id 订单ID
     * @return 是否确认收货成功
     */
    boolean receive(Long userId, Long id);
    
    /**
     * 申请退款
     * @param userId 用户ID
     * @param id 订单ID
     * @param reason 退款原因
     * @return 是否申请退款成功
     */
    boolean applyRefund(Long userId, Long id, String reason);
    
    /**
     * 处理退款
     * @param id 订单ID
     * @param isAgree 是否同意
     * @return 是否处理成功
     */
    boolean handleRefund(Long id, boolean isAgree);
    
    /**
     * 查询用户各状态订单数量
     * @param userId 用户ID
     * @return 各状态订单数量
     */
    Map<String, Integer> countByStatus(Long userId);
    
    /**
     * 查询用户待付款订单
     * @param userId 用户ID
     * @return 待付款订单列表
     */
    List<Order> findPendingPayment(Long userId);
    
    /**
     * 查询用户待发货订单
     * @param userId 用户ID
     * @return 待发货订单列表
     */
    List<Order> findPendingShipment(Long userId);
    
    /**
     * 查询用户待收货订单
     * @param userId 用户ID
     * @return 待收货订单列表
     */
    List<Order> findPendingReceipt(Long userId);
    
    /**
     * 查询用户待评价订单
     * @param userId 用户ID
     * @return 待评价订单列表
     */
    List<Order> findPendingReview(Long userId);

    Order getOrderById(Long orderId);
    void updateOrder(Order order);
} 