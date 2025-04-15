package example.shopping.service.impl;

import example.shopping.dto.CustomerServiceDTO;
import example.shopping.entity.CustomerServiceMessage;
import example.shopping.entity.CustomerServiceSession;
import example.shopping.entity.Store;
import example.shopping.entity.User;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.CustomerServiceMessageMapper;
import example.shopping.mapper.CustomerServiceSessionMapper;
import example.shopping.mapper.StoreMapper;
import example.shopping.mapper.UserMapper;
import example.shopping.service.CustomerServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 客服服务实现类
 */
@Service
public class CustomerServiceImpl implements CustomerServiceInterface {

    @Autowired
    private CustomerServiceSessionMapper sessionMapper;

    @Autowired
    private CustomerServiceMessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Override
    @Transactional
    public CustomerServiceSession createSession(Long userId, CustomerServiceDTO.SessionDTO sessionDTO) {
        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查店铺是否存在
        Store store = storeMapper.findById(sessionDTO.getStoreId());
        if (store == null) {
            throw new BusinessException("店铺不存在");
        }

        // 检查是否已有进行中的会话
        CustomerServiceSession activeSession = sessionMapper.findActiveByUserIdAndStoreId(userId, sessionDTO.getStoreId());
        if (activeSession != null) {
            return activeSession;
        }

        // 创建新会话
        CustomerServiceSession session = new CustomerServiceSession();
        session.setUserId(userId);
        session.setStoreId(sessionDTO.getStoreId());
        session.setStatus(0); // 0-进行中

        Date now = new Date();
        session.setStartTime(now);
        session.setCreateTime(now);
        session.setUpdateTime(now);

        sessionMapper.insert(session);

        return session;
    }

    @Override
    @Transactional
    public boolean endSession(Long sessionId) {
        CustomerServiceSession session = sessionMapper.findById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        if (session.getStatus() == 1) {
            return true; // 已经是结束状态
        }

        Date now = new Date();
        return sessionMapper.updateStatus(sessionId, 1, now) > 0; // 1-已结束
    }

    @Override
    @Transactional
    public boolean evaluateSession(Long userId, CustomerServiceDTO.EvaluationDTO evaluationDTO) {
        CustomerServiceSession session = sessionMapper.findById(evaluationDTO.getSessionId());
        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        // 检查会话是否属于该用户
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权评价此会话");
        }

        // 检查会话是否已结束
        if (session.getStatus() != 1) { // 1-已结束
            throw new BusinessException("只能评价已结束的会话");
        }

        return sessionMapper.updateEvaluation(
                evaluationDTO.getSessionId(),
                evaluationDTO.getEvaluation(),
                evaluationDTO.getRemark()
        ) > 0;
    }

    @Override
    @Transactional
    public CustomerServiceMessage sendMessage(Long userId, Long storeId, Integer fromType, CustomerServiceDTO.MessageDTO messageDTO) {
        CustomerServiceSession session = sessionMapper.findById(messageDTO.getSessionId());
        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        // 检查会话是否进行中
        if (session.getStatus() != 0) { // 0-进行中
            throw new BusinessException("只能在进行中的会话中发送消息");
        }

        // 检查发送权限
        if (fromType == 0) { // 用户发送
            if (!session.getUserId().equals(userId)) {
                throw new BusinessException("无权在此会话中发送消息");
            }
        } else if (fromType == 1) { // 商家发送
            if (!session.getStoreId().equals(storeId)) {
                throw new BusinessException("无权在此会话中发送消息");
            }
        } else {
            throw new BusinessException("发送方类型无效");
        }

        // 创建消息
        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setSessionId(messageDTO.getSessionId());
        message.setUserId(userId);
        message.setStoreId(storeId);
        message.setFromType(fromType);
        message.setContent(messageDTO.getContent());
        message.setContentType(messageDTO.getContentType());
        message.setReadStatus(false); // 初始为未读
        message.setCreateTime(new Date());

        messageMapper.insert(message);

        // 更新会话的更新时间
        session.setUpdateTime(new Date());
        sessionMapper.update(session);

        return message;
    }

    @Override
    @Transactional
    public boolean markMessagesAsRead(Long sessionId, Integer fromType) {
        CustomerServiceSession session = sessionMapper.findById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        // 直接标记指定发送方类型的消息为已读
        return messageMapper.updateReadStatusBySessionIdAndFromType(sessionId, fromType, true) > 0;
    }

    @Override
    public List<CustomerServiceMessage> findMessagesBySessionId(Long sessionId) {
        CustomerServiceSession session = sessionMapper.findById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        return messageMapper.findBySessionId(sessionId);
    }

    @Override
    public List<Map<String, Object>> findSessionsByUserId(Long userId) {
        // 获取当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userMapper.findByUsername(username);
        
        // 验证权限
        if (!currentUser.getId().equals(userId)) {
            throw new BusinessException("您无权查看其他用户的会话");
        }
        
        List<CustomerServiceSession> sessions = sessionMapper.findByUserId(userId);
        return sessions.stream().map(this::convertSessionToMap).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findSessionsByStoreId(Long storeId) {
        List<CustomerServiceSession> sessions = sessionMapper.findByStoreId(storeId);
        return sessions.stream().map(this::convertSessionToMap).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findSessionsByStoreId(Long storeId, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        List<CustomerServiceSession> sessions = sessionMapper.findByStoreIdWithPage(storeId, offset, size);
        return sessions.stream().map(this::convertSessionToMap).collect(Collectors.toList());
    }

    @Override
    public CustomerServiceSession findActiveSession(Long userId, Long storeId) {
        return sessionMapper.findActiveByUserIdAndStoreId(userId, storeId);
    }

    @Override
    public int countUnreadMessages(Long sessionId, Integer fromType) {
        return messageMapper.countUnreadBySessionIdAndFromType(sessionId, fromType);
    }

    @Override
    public Double calculateAverageEvaluation(Long storeId) {
        return sessionMapper.calculateAverageEvaluation(storeId);
    }

    @Override
    public List<Map<String, Object>> getServiceRatingStats() {
        List<Map<String, Object>> stats = new ArrayList<>();

        // 获取所有店铺
        List<Store> stores = storeMapper.findAll();

        for (Store store : stores) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("storeId", store.getId());
            stat.put("storeName", store.getName());
            stat.put("averageRating", calculateAverageEvaluation(store.getId()));
            stat.put("sessionCount", getSessionCount(store.getId()));
            stat.put("responseTime", getAverageResponseTime(store.getId()));
            stats.add(stat);
        }

        return stats;
    }

    @Override
    @Transactional
    public boolean handleComplaint(Long sessionId, CustomerServiceDTO.ComplaintDTO complaintDTO) {
        CustomerServiceSession session = sessionMapper.findById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        // 检查投诉状态
        if (session.getComplaintStatus() != 0) {
            throw new BusinessException("该投诉已处理");
        }

        // 更新投诉状态
        return sessionMapper.updateComplaintStatus(
                sessionId,
                complaintDTO.getStatus(),
                complaintDTO.getResult(),
                complaintDTO.getIsPenalty(),
                complaintDTO.getPenaltyContent()
        ) > 0;
    }

    @Override
    public double getAverageResponseTime(Long storeId) {
        Double avgTime = messageMapper.calculateAverageResponseTime(storeId);
        return avgTime != null ? avgTime : 0.0;
    }

    @Override
    public List<Map<String, Object>> getComplaints() {
        List<CustomerServiceSession> complainedSessions = sessionMapper.findComplainedSessions();
        List<Map<String, Object>> result = new ArrayList<>();

        for (CustomerServiceSession session : complainedSessions) {
            Map<String, Object> complaint = new HashMap<>();
            complaint.put("sessionId", session.getId());
            complaint.put("userId", session.getUserId());
            complaint.put("storeId", session.getStoreId());

            // 获取店铺信息
            Store store = storeMapper.findById(session.getStoreId());
            if (store != null) {
                complaint.put("storeName", store.getName());
            }

            // 获取用户评价备注作为投诉内容
            complaint.put("complaintContent", session.getRemark());
            complaint.put("status", session.getComplaintStatus());
            complaint.put("createTime", session.getCreateTime());

            result.add(complaint);
        }

        return result;
    }

    @Override
    public CustomerServiceSession findById(Long sessionId) {
        return sessionMapper.findById(sessionId);
    }

    @Override
    public List<Map<String, Object>> findSessionsByUserId(Long userId, int page, int size) {
        // 获取当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userMapper.findByUsername(username);
        
        // 验证权限
        if (!currentUser.getId().equals(userId)) {
            throw new BusinessException("您无权查看其他用户的会话");
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        List<CustomerServiceSession> sessions = sessionMapper.findByUserIdWithPage(userId, offset, size);
        return sessions.stream().map(session -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", session.getId());
            map.put("storeId", session.getStoreId());

            // 获取店铺名称
            Store store = storeMapper.findById(session.getStoreId());
            map.put("storeName", store != null ? store.getName() : null);

            map.put("status", session.getStatus());
            map.put("startTime", session.getStartTime());
            map.put("endTime", session.getEndTime());

            // 获取最后一条消息
            CustomerServiceMessage lastMessage = messageMapper.findLastMessageBySessionId(session.getId());
            if (lastMessage != null) {
                map.put("lastMessage", lastMessage.getContent());
            }

            // 获取未读消息数
            map.put("unreadCount", messageMapper.countUnreadBySessionIdAndFromType(session.getId(), 1));  // 商家发送的未读消息

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public int getSessionCount(Long id) {
        if (id == null) {
            return 0;
        }
        // 根据当前用户角色判断是查询用户的会话数还是店铺的会话数
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isMerchant = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MERCHANT"));
        
        if (isMerchant) {
            return sessionMapper.countByStoreId(id);
        } else {
            return sessionMapper.countByUserId(id);
        }
    }

    /**
     * 转换会话对象为Map
     *
     * @param session 会话对象
     * @return Map
     */
    private Map<String, Object> convertSessionToMap(CustomerServiceSession session) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", session.getId());
        map.put("storeId", session.getStoreId());

        // 获取店铺名称
        Store store = storeMapper.findById(session.getStoreId());
        map.put("storeName", store != null ? store.getName() : null);

        map.put("userId", session.getUserId());

        // 获取用户名
        User user = userMapper.findById(session.getUserId());
        map.put("username", user != null ? user.getUsername() : null);

        // 获取最后一条消息
        List<CustomerServiceMessage> messages = messageMapper.findBySessionId(session.getId());
        if (messages != null && !messages.isEmpty()) {
            CustomerServiceMessage lastMessage = messages.get(messages.size() - 1);
            Map<String, Object> lastMessageMap = new HashMap<>();
            lastMessageMap.put("content", lastMessage.getContent());
            lastMessageMap.put("fromType", lastMessage.getFromType());
            lastMessageMap.put("createTime", lastMessage.getCreateTime());
            map.put("lastMessage", lastMessageMap);
        }

        // 获取店铺未读消息数
        map.put("unreadCount", messageMapper.countUnreadBySessionIdAndFromType(session.getId(), 0));  // 用户发送的未读消息

        map.put("status", session.getStatus());
        map.put("updateTime", session.getUpdateTime());

        return map;
    }
}
