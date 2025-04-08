package example.shopping.service.impl;

import example.shopping.service.PaymentService;
import example.shopping.service.OrderService;
import example.shopping.entity.Order;
import example.shopping.service.impl.OrderServiceImpl.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderService orderService;

    @Override
    public Map<String, Object> pay(Long orderId, String paymentType) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.UNPAID.getValue()) {
            throw new RuntimeException("订单状态不正确");
        }

        // 模拟支付过程
        order.setStatus(OrderStatus.PAID.getValue());
        order.setPaymentTime(new Date());
        orderService.updateOrder(order);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "支付成功");
        result.put("orderId", orderId);
        result.put("amount", order.getTotalAmount());
        result.put("paymentType", paymentType);
        return result;
    }

    @Override
    public boolean applyRefund(Long orderId, String reason) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PAID.getValue() && 
            order.getStatus() != OrderStatus.SHIPPED.getValue() &&
            order.getStatus() != OrderStatus.COMPLETED.getValue()) {
            throw new RuntimeException("订单状态不允许申请退款");
        }

        order.setStatus(OrderStatus.REFUND_PENDING.getValue());
        order.setRefundReason(reason);
        orderService.updateOrder(order);
        return true;
    }

    @Override
    public boolean handleRefund(Long orderId, Boolean isAgree) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.REFUND_PENDING.getValue()) {
            throw new RuntimeException("订单状态不正确");
        }

        if (isAgree) {
            order.setStatus(OrderStatus.REFUNDED.getValue());
        } else {
            order.setStatus(OrderStatus.REFUND_REJECTED.getValue());
        }
        orderService.updateOrder(order);
        return true;
    }

    @Override
    public Map<String, String> getPaymentMethods() {
        Map<String, String> methods = new HashMap<>();
        methods.put("ALIPAY", "支付宝");
        methods.put("WECHAT", "微信支付");
        methods.put("CREDIT_CARD", "信用卡");
        return methods;
    }
} 