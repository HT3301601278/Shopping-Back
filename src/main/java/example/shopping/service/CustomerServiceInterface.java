package example.shopping.service;

import example.shopping.dto.CustomerServiceDTO;
import example.shopping.entity.CustomerServiceMessage;
import example.shopping.entity.CustomerServiceSession;

import java.util.List;
import java.util.Map;

/**
 * 客服服务接口
 */
public interface CustomerServiceInterface {
    
    /**
     * 创建会话
     * @param userId 用户ID
     * @param sessionDTO 会话信息
     * @return 创建的会话
     */
    CustomerServiceSession createSession(Long userId, CustomerServiceDTO.SessionDTO sessionDTO);
    
    /**
     * 结束会话
     * @param sessionId 会话ID
     * @return 是否结束成功
     */
    boolean endSession(Long sessionId);
    
    /**
     * 评价会话
     * @param userId 用户ID
     * @param evaluationDTO 评价信息
     * @return 是否评价成功
     */
    boolean evaluateSession(Long userId, CustomerServiceDTO.EvaluationDTO evaluationDTO);
    
    /**
     * 发送消息
     * @param userId 用户ID
     * @param storeId 店铺ID(商家发送时需要)
     * @param fromType 发送方类型(0-用户, 1-商家)
     * @param messageDTO 消息信息
     * @return 发送的消息
     */
    CustomerServiceMessage sendMessage(Long userId, Long storeId, Integer fromType, CustomerServiceDTO.MessageDTO messageDTO);
    
    /**
     * 标记消息已读
     * @param sessionId 会话ID
     * @param fromType 发送方类型(标记对方发送的消息为已读)
     * @return 是否标记成功
     */
    boolean markMessagesAsRead(Long sessionId, Integer fromType);
    
    /**
     * 查询会话消息
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<CustomerServiceMessage> findMessagesBySessionId(Long sessionId);
    
    /**
     * 查询用户的会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Map<String, Object>> findSessionsByUserId(Long userId);
    
    /**
     * 查询店铺的会话列表
     * @param storeId 店铺ID
     * @return 会话列表
     */
    List<Map<String, Object>> findSessionsByStoreId(Long storeId);
    
    /**
     * 查询用户与店铺的进行中会话
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 会话信息
     */
    CustomerServiceSession findActiveSession(Long userId, Long storeId);
    
    /**
     * 查询会话未读消息数
     * @param sessionId 会话ID
     * @param fromType 发送方类型
     * @return 未读消息数
     */
    int countUnreadMessages(Long sessionId, Integer fromType);
    
    /**
     * 计算店铺的平均评价
     * @param storeId 店铺ID
     * @return 平均评价
     */
    Double calculateAverageEvaluation(Long storeId);
    
    /**
     * 获取客服满意度统计
     * @return 各店铺的客服满意度统计
     */
    List<Map<String, Object>> getServiceRatingStats();
    
    /**
     * 处理客服投诉
     * @param sessionId 会话ID
     * @param complaintDTO 投诉处理信息
     * @return 是否处理成功
     */
    boolean handleComplaint(Long sessionId, CustomerServiceDTO.ComplaintDTO complaintDTO);
    
    /**
     * 获取店铺的会话数量
     * @param storeId 店铺ID
     * @return 会话数量
     */
    int getSessionCount(Long storeId);
    
    /**
     * 获取店铺的平均响应时间（分钟）
     * @param storeId 店铺ID
     * @return 平均响应时间
     */
    double getAverageResponseTime(Long storeId);
    
    /**
     * 获取客服会话投诉列表
     * @return 投诉列表
     */
    List<Map<String, Object>> getComplaints();
} 