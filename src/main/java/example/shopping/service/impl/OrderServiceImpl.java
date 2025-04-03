package example.shopping.service.impl;

import com.alibaba.fastjson.JSON;
import example.shopping.dto.OrderDTO;
import example.shopping.entity.Order;
import example.shopping.entity.Product;
import example.shopping.entity.User;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.OrderMapper;
import example.shopping.mapper.ProductMapper;
import example.shopping.mapper.UserMapper;
import example.shopping.service.CartService;
import example.shopping.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 订单服务实现类
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CartService cartService;

    @Override
    @Transactional
    public Order create(Long userId, OrderDTO orderDTO) {
        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查订单项是否为空
        if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            throw new BusinessException("订单项不能为空");
        }

        // 生成订单编号
        String orderNo = generateOrderNo();

        // 计算总金额并构建订单项JSON
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Map<String, Object>> orderItems = new ArrayList<>();

        for (OrderDTO.OrderItemDTO item : orderDTO.getItems()) {
            // 查询商品信息
            Product product = productMapper.findById(item.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在: " + item.getProductId());
            }

            // 检查商品是否已下架
            if (product.getStatus() != 1) {
                throw new BusinessException("商品已下架: " + product.getName());
            }

            // 检查库存是否足够
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("商品库存不足: " + product.getName());
            }

            // 减少商品库存
            int result = productMapper.decreaseStock(item.getProductId(), item.getQuantity());
            if (result <= 0) {
                throw new BusinessException("商品库存不足: " + product.getName());
            }

            // 计算商品总价
            BigDecimal itemTotalPrice = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotalPrice);

            // 构建订单项
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("productId", item.getProductId());
            orderItem.put("productName", product.getName());
            orderItem.put("price", product.getPrice());
            orderItem.put("quantity", item.getQuantity());
            orderItem.put("specInfo", item.getSpecInfo());
            orderItem.put("totalPrice", itemTotalPrice);
            orderItems.add(orderItem);

            // 更新商品销量
            productMapper.updateSales(item.getProductId(), item.getQuantity());
        }

        // 获取收货地址
        String addressInfo = getAddressInfo(user, orderDTO.getAddressId());

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStoreId(orderDTO.getStoreId());
        order.setItems(JSON.toJSONString(orderItems));
        order.setTotalAmount(totalAmount);
        order.setAddressInfo(addressInfo);
        order.setPaymentType(orderDTO.getPaymentType());
        order.setStatus(0); // 0-待付款
        order.setRefundStatus(0); // 0-无退款
        Date now = new Date();
        order.setCreateTime(now);
        order.setUpdateTime(now);

        orderMapper.insert(order);

        // 清空购物车中已购买的商品
        cartService.deleteSelected(userId);

        return order;
    }

    @Override
    public Order findById(Long id) {
        return orderMapper.findById(id);
    }

    @Override
    public Order findByOrderNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    @Override
    public List<Order> findByStoreId(Long storeId) {
        return orderMapper.findByStoreId(storeId);
    }

    @Override
    public List<Order> findByUserIdAndStatus(Long userId, Integer status) {
        return orderMapper.findByUserIdAndStatus(userId, status);
    }

    @Override
    public Map<String, Object> findByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> orders = orderMapper.findByPage(offset, pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("list", orders);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        // TODO: 添加总数统计

        return result;
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, Integer status) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        orderMapper.updateStatus(id, status);
        order.setStatus(status);
        order.setUpdateTime(new Date());

        return order;
    }

    @Override
    @Transactional
    public boolean cancel(Long userId, Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单是否属于该用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 检查订单状态是否为待付款
        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消待付款订单");
        }

        // 恢复商品库存
        List<Map> orderItemsList = JSON.parseArray(order.getItems(), Map.class);
        for (Map item : orderItemsList) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());

            // 增加商品库存
            Product product = productMapper.findById(productId);
            if (product != null) {
                product.setStock(product.getStock() + quantity);
                product.setSales(product.getSales() - quantity);
                productMapper.update(product);
            }
        }

        // 更新订单状态为已取消
        order.setStatus(4); // 4-已取消
        order.setUpdateTime(new Date());
        return orderMapper.update(order) > 0;
    }

    @Override
    @Transactional
    public boolean pay(Long userId, Long id, String paymentType) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单是否属于该用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 检查订单状态是否为待付款
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态错误");
        }

        // 模拟支付成功
        order.setStatus(1); // 1-待发货
        order.setPaymentType(paymentType);
        order.setPaymentTime(new Date());
        order.setUpdateTime(new Date());

        return orderMapper.update(order) > 0;
    }

    @Override
    @Transactional
    public boolean ship(Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态是否为待发货
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态错误");
        }

        order.setStatus(2); // 2-待收货
        order.setShippingTime(new Date());
        order.setUpdateTime(new Date());

        return orderMapper.update(order) > 0;
    }

    @Override
    @Transactional
    public boolean receive(Long userId, Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单是否属于该用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 检查订单状态是否为待收货
        if (order.getStatus() != 2) {
            throw new BusinessException("订单状态错误");
        }

        order.setStatus(3); // 3-已完成
        order.setUpdateTime(new Date());

        return orderMapper.update(order) > 0;
    }

    @Override
    @Transactional
    public boolean applyRefund(Long userId, Long id, String reason) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单是否属于该用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 检查订单状态是否为待发货或待收货
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new BusinessException("当前订单状态不支持申请退款");
        }

        order.setRefundStatus(1); // 1-申请退款
        order.setRefundReason(reason);
        order.setUpdateTime(new Date());

        return orderMapper.update(order) > 0;
    }

    @Override
    @Transactional
    public boolean handleRefund(Long id, boolean isAgree) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查退款状态是否为申请退款
        if (order.getRefundStatus() != 1) {
            throw new BusinessException("退款状态错误");
        }

        if (isAgree) {
            // 同意退款
            order.setRefundStatus(2); // 2-退款成功
            order.setStatus(5); // 5-已退款

            // 恢复商品库存
            List<Map> orderItemsList = JSON.parseArray(order.getItems(), Map.class);
            for (Map item : orderItemsList) {
                Long productId = Long.valueOf(item.get("productId").toString());
                Integer quantity = Integer.valueOf(item.get("quantity").toString());

                // 增加商品库存
                Product product = productMapper.findById(productId);
                if (product != null) {
                    product.setStock(product.getStock() + quantity);
                    product.setSales(product.getSales() - quantity);
                    productMapper.update(product);
                }
            }
        } else {
            // 拒绝退款
            order.setRefundStatus(3); // 3-退款失败
        }

        order.setUpdateTime(new Date());
        return orderMapper.update(order) > 0;
    }

    @Override
    public Map<String, Integer> countByStatus(Long userId) {
        Map<String, Integer> result = new HashMap<>();
        result.put("pendingPayment", orderMapper.findByUserIdAndStatus(userId, 0).size());
        result.put("pendingShipment", orderMapper.findByUserIdAndStatus(userId, 1).size());
        result.put("pendingReceipt", orderMapper.findByUserIdAndStatus(userId, 2).size());
        result.put("completed", orderMapper.findByUserIdAndStatus(userId, 3).size());
        result.put("pendingReview", orderMapper.findPendingReview(userId).size());

        return result;
    }

    @Override
    public List<Order> findPendingPayment(Long userId) {
        return orderMapper.findPendingPayment(userId);
    }

    @Override
    public List<Order> findPendingShipment(Long userId) {
        return orderMapper.findPendingShipment(userId);
    }

    @Override
    public List<Order> findPendingReceipt(Long userId) {
        return orderMapper.findPendingReceipt(userId);
    }

    @Override
    public List<Order> findPendingReview(Long userId) {
        return orderMapper.findPendingReview(userId);
    }

    /**
     * 生成订单编号
     * @return 订单编号
     */
    private String generateOrderNo() {
        StringBuilder sb = new StringBuilder();
        // 年月日时分秒
        sb.append(new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        // 6位随机数
        sb.append(String.format("%06d", new Random().nextInt(1000000)));
        return sb.toString();
    }

    /**
     * 获取收货地址信息
     * @param user 用户
     * @param addressId 地址ID
     * @return 地址信息JSON字符串
     */
    private String getAddressInfo(User user, Long addressId) {
        String addresses = user.getAddresses();
        if (addresses == null || addresses.isEmpty()) {
            throw new BusinessException("用户没有配置收货地址");
        }

        List<Map> addressList = JSON.parseArray(addresses, Map.class);
        Map selectedAddress = null;

        for (Map address : addressList) {
            if (address.get("id").equals(addressId.toString())) {
                selectedAddress = address;
                break;
            }
        }

        if (selectedAddress == null) {
            throw new BusinessException("收货地址不存在");
        }

        return JSON.toJSONString(selectedAddress);
    }
}
