package example.shopping.controller;

import example.shopping.service.PaymentService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/payment")
@PreAuthorize("hasRole('USER')")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 支付订单
     * @param orderId 订单ID
     * @param paymentType 支付方式
     * @return 支付结果
     */
    @PostMapping("/pay")
    public Result<Map<String, Object>> pay(
            @RequestParam Long orderId,
            @RequestParam String paymentType) {
        Map<String, Object> result = paymentService.pay(orderId, paymentType);
        return Result.success(result, "支付成功");
    }

    /**
     * 申请退款
     * @param orderId 订单ID
     * @param reason 退款原因
     * @return 退款申请结果
     */
    @PostMapping("/refund")
    public Result<Boolean> refund(
            @RequestParam Long orderId,
            @RequestParam String reason) {
        boolean success = paymentService.applyRefund(orderId, reason);
        return Result.success(success, "退款申请提交成功");
    }

    /**
     * 处理退款申请
     * @param orderId 订单ID
     * @param isAgree 是否同意退款
     * @return 处理结果
     */
    @PostMapping("/refund/handle")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public Result<Boolean> handleRefund(
            @RequestParam Long orderId,
            @RequestParam Boolean isAgree) {
        boolean success = paymentService.handleRefund(orderId, isAgree);
        return Result.success(success, "退款已处理");
    }

    /**
     * 获取支付方式列表
     * @return 支付方式列表
     */
    @GetMapping("/methods")
    public Result<Map<String, String>> getPaymentMethods() {
        return Result.success(paymentService.getPaymentMethods());
    }
} 