package example.shopping.controller;

import example.shopping.dto.CustomerServiceDTO;
import example.shopping.entity.CustomerServiceMessage;
import example.shopping.entity.CustomerServiceSession;
import example.shopping.entity.User;
import example.shopping.entity.Store;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.UserMapper;
import example.shopping.mapper.StoreMapper;
import example.shopping.service.CustomerServiceInterface;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 客服控制器
 */
@RestController
@RequestMapping("/api/customer-service")
public class CustomerServiceController {

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StoreMapper storeMapper;

    /**
     * 创建客服会话
     * @param sessionDTO 会话信息
     * @return 创建的会话
     */
    @PostMapping("/sessions")
    @PreAuthorize("hasRole('USER')")
    public Result<CustomerServiceSession> createSession(@Valid @RequestBody CustomerServiceDTO.SessionDTO sessionDTO) {
        Long userId = getCurrentUserId();
        return Result.success(customerService.createSession(userId, sessionDTO), "会话创建成功");
    }

    /**
     * 获取用户的会话列表
     * @return 用户的会话列表
     */
    @GetMapping("/sessions/user")
    @PreAuthorize("hasRole('USER')")
    public Result<List<Map<String, Object>>> getUserSessions() {
        Long userId = getCurrentUserId();
        return Result.success(customerService.findSessionsByUserId(userId));
    }

    /**
     * 获取店铺的会话列表
     * @param storeId 店铺ID
     * @return 店铺的会话列表
     */
    @GetMapping("/sessions/store/{storeId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<List<Map<String, Object>>> getStoreSessions(@PathVariable Long storeId) {
        // 验证当前用户是否为店铺所有者
        validateStoreOwner(storeId);
        return Result.success(customerService.findSessionsByStoreId(storeId));
    }

    /**
     * 结束会话
     * @param sessionId 会话ID
     * @return 是否结束成功
     */
    @PutMapping("/sessions/{sessionId}/end")
    public Result<Boolean> endSession(@PathVariable Long sessionId) {
        // 验证权限
        validateSessionPermission(sessionId);
        return Result.success(customerService.endSession(sessionId), "会话已结束");
    }

    /**
     * 获取会话消息
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @GetMapping("/messages/{sessionId}")
    public Result<List<CustomerServiceMessage>> getSessionMessages(@PathVariable Long sessionId) {
        // 验证权限
        validateSessionPermission(sessionId);
        return Result.success(customerService.findMessagesBySessionId(sessionId));
    }

    /**
     * 发送消息（用户）
     * @param sessionId 会话ID
     * @param messageDTO 消息信息
     * @return 发送的消息
     */
    @PostMapping("/messages/{sessionId}/user")
    @PreAuthorize("hasRole('USER')")
    public Result<CustomerServiceMessage> sendUserMessage(
            @PathVariable Long sessionId,
            @Valid @RequestBody CustomerServiceDTO.MessageDTO messageDTO) {
        Long userId = getCurrentUserId();
        // 从会话中获取storeId
        Long storeId = getStoreIdFromSession(sessionId);
        // 用户发送消息，fromType=0
        return Result.success(customerService.sendMessage(userId, storeId, 0, messageDTO), "消息发送成功");
    }

    /**
     * 发送消息（商家）
     * @param sessionId 会话ID
     * @param messageDTO 消息信息
     * @return 发送的消息
     */
    @PostMapping("/messages/{sessionId}/merchant")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<CustomerServiceMessage> sendMerchantMessage(
            @PathVariable Long sessionId,
            @Valid @RequestBody CustomerServiceDTO.MessageDTO messageDTO) {
        // 从会话中获取userId和storeId
        Long userId = getUserIdFromSession(sessionId);
        Long storeId = getStoreIdFromSession(sessionId);
        // 验证当前用户是否为店铺所有者
        validateStoreOwner(storeId);
        // 商家发送消息，fromType=1
        return Result.success(customerService.sendMessage(userId, storeId, 1, messageDTO), "消息发送成功");
    }

    /**
     * 标记消息已读（用户）
     * @param sessionId 会话ID
     * @return 是否标记成功
     */
    @PutMapping("/messages/{sessionId}/read/user")
    @PreAuthorize("hasRole('USER')")
    public Result<Boolean> markAsReadByUser(@PathVariable Long sessionId) {
        // 用户标记商家消息为已读，fromType=1
        return Result.success(customerService.markMessagesAsRead(sessionId, 1), "消息已标记为已读");
    }

    /**
     * 标记消息已读（商家）
     * @param sessionId 会话ID
     * @return 是否标记成功
     */
    @PutMapping("/messages/{sessionId}/read/merchant")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Boolean> markAsReadByMerchant(@PathVariable Long sessionId) {
        // 商家标记用户消息为已读，fromType=0
        return Result.success(customerService.markMessagesAsRead(sessionId, 0), "消息已标记为已读");
    }

    /**
     * 评价会话
     * @param sessionId 会话ID
     * @param evaluationDTO 评价信息
     * @return 是否评价成功
     */
    @PostMapping("/sessions/{sessionId}/evaluate")
    @PreAuthorize("hasRole('USER')")
    public Result<Boolean> evaluateSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody CustomerServiceDTO.EvaluationDTO evaluationDTO) {
        Long userId = getCurrentUserId();
        // 设置会话ID
        evaluationDTO.setSessionId(sessionId);
        return Result.success(customerService.evaluateSession(userId, evaluationDTO), "评价成功");
    }

    /**
     * 获取店铺的客服满意度
     * @param storeId 店铺ID
     * @return 客服满意度评分
     */
    @GetMapping("/rating/{storeId}")
    public Result<Double> getStoreServiceRating(@PathVariable Long storeId) {
        return Result.success(customerService.calculateAverageEvaluation(storeId));
    }

    /**
     * 获取客服满意度统计（管理员功能）
     * @return 各店铺的客服满意度统计
     */
    @GetMapping("/rating/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Map<String, Object>>> getServiceRatingStats() {
        return Result.success(customerService.getServiceRatingStats());
    }

    /**
     * 处理客服投诉（管理员功能）
     * @param sessionId 会话ID
     * @param complaintDTO 投诉处理信息
     * @return 是否处理成功
     */
    @PostMapping("/complaints/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> handleComplaint(
            @PathVariable Long sessionId,
            @Valid @RequestBody CustomerServiceDTO.ComplaintDTO complaintDTO) {
        return Result.success(customerService.handleComplaint(sessionId, complaintDTO), "投诉处理成功");
    }

    // 工具方法：获取当前登录用户ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        String username = authentication.getName();
        if (username == null) {
            throw new BusinessException("无法获取用户名");
        }

        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user.getId();
    }

    // 工具方法：验证会话权限
    private void validateSessionPermission(Long sessionId) {
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }

        // 获取会话信息
        CustomerServiceSession session = customerService.findById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        // 获取当前用户ID
        Long currentUserId = getCurrentUserId();

        // 判断用户角色
        boolean isMerchant = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MERCHANT"));

        if (isMerchant) {
            // 如果是商家，验证是否为该店铺的所有者
            validateStoreOwner(session.getStoreId());
        } else {
            // 如果是普通用户，验证是否为会话的用户
            if (!session.getUserId().equals(currentUserId)) {
                throw new BusinessException("您无权访问此会话");
            }
        }
    }

    // 工具方法：从会话中获取storeId
    private Long getStoreIdFromSession(Long sessionId) {
        CustomerServiceSession session = customerService.findById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        return session.getStoreId();
    }

    // 工具方法：从会话中获取userId
    private Long getUserIdFromSession(Long sessionId) {
        CustomerServiceSession session = customerService.findById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        return session.getUserId();
    }

    // 工具方法：验证当前用户是否为店铺所有者
    private void validateStoreOwner(Long storeId) {
        // 获取当前用户ID
        Long userId = getCurrentUserId();

        // 查询店铺信息
        Store store = storeMapper.findById(storeId);
        if (store == null) {
            throw new BusinessException("店铺不存在");
        }

        // 验证当前用户是否为店铺所有者
        if (!store.getUserId().equals(userId)) {
            throw new BusinessException("您不是该店铺的所有者，无权访问");
        }
    }
}
