package example.shopping.service;

import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 支付订单
     *
     * @param orderId     订单ID
     * @param paymentType 支付方式
     * @return 支付结果
     */
    Map<String, Object> pay(Long orderId, String paymentType);

    /**
     * 申请退款
     *
     * @param orderId 订单ID
     * @param reason  退款原因
     * @return 是否申请成功
     */
    boolean applyRefund(Long orderId, String reason);

    /**
     * 处理退款申请
     *
     * @param orderId 订单ID
     * @param isAgree 是否同意退款
     * @return 是否处理成功
     */
    boolean handleRefund(Long orderId, Boolean isAgree);

    /**
     * 获取支付方式列表
     *
     * @return 支付方式列表
     */
    Map<String, String> getPaymentMethods();
}
