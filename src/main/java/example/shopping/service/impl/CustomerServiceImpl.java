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
        
        // 标记对方发送的消息为已读
        Integer otherFromType = (fromType == 0) ? 1 : 0;
        return messageMapper.updateReadStatusBySessionIdAndFromType(sessionId, otherFromType, true) > 0;
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
        List<CustomerServiceSession> sessions = sessionMapper.findByUserId(userId);
        return sessions.stream().map(this::convertSessionToMap).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findSessionsByStoreId(Long storeId) {
        List<CustomerServiceSession> sessions = sessionMapper.findByStoreId(storeId);
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
    public boolean handleComplaint(Long sessionId, CustomerServiceDTO.ComplaintDTO complaintDTO) {
        CustomerServiceSession session = sessionMapper.findById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        
        // 暂时用模拟实现替代，实际项目中需要实现数据库方法
        // TODO: 实现CustomerServiceSessionMapper中的updateComplaintStatus方法
        
        // 简单模拟数据更新成功
        return true;
    }
    
    @Override
    public int getSessionCount(Long storeId) {
        // 暂时用模拟实现替代，实际项目中需要实现数据库方法
        // TODO: 实现CustomerServiceSessionMapper中的countByStoreId方法
        
        // 简单返回已有会话数据的大小
        List<CustomerServiceSession> sessions = sessionMapper.findByStoreId(storeId);
        return sessions != null ? sessions.size() : 0;
    }
    
    @Override
    public double getAverageResponseTime(Long storeId) {
        // 暂时用模拟实现替代，实际项目中需要实现数据库方法
        // TODO: 实现CustomerServiceMessageMapper中的calculateAverageResponseTime方法
        
        // 简单返回默认值
        return 5.0; // 假设平均响应时间为5分钟
    }
    
    @Override
    public List<Map<String, Object>> getComplaints() {
        // 暂时用模拟实现替代，实际项目中需要实现数据库方法
        // TODO: 实现CustomerServiceSessionMapper中的findComplainedSessions方法
        
        // 简单返回空列表
        return new ArrayList<>();
    }
    
    @Override
    public CustomerServiceSession findById(Long sessionId) {
        return sessionMapper.findById(sessionId);
    }
    
    /**
     * 转换会话对象为Map
     * @param session 会话对象
     * @return Map
     */
    private Map<String, Object> convertSessionToMap(CustomerServiceSession session) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", session.getId());
        map.put("userId", session.getUserId());
        map.put("storeId", session.getStoreId());
        map.put("status", session.getStatus());
        map.put("startTime", session.getStartTime());
        map.put("endTime", session.getEndTime());
        map.put("evaluation", session.getEvaluation());
        map.put("remark", session.getRemark());
        map.put("createTime", session.getCreateTime());
        map.put("updateTime", session.getUpdateTime());
        
        // 获取用户信息
        User user = userMapper.findById(session.getUserId());
        if (user != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("avatar", user.getAvatar());
            map.put("user", userMap);
        }
        
        // 获取店铺信息
        Store store = storeMapper.findById(session.getStoreId());
        if (store != null) {
            Map<String, Object> storeMap = new HashMap<>();
            storeMap.put("id", store.getId());
            storeMap.put("name", store.getName());
            storeMap.put("logo", store.getLogo());
            map.put("store", storeMap);
        }
        
        // 获取最近一条消息
        List<CustomerServiceMessage> messages = messageMapper.findBySessionId(session.getId());
        if (messages != null && !messages.isEmpty()) {
            CustomerServiceMessage lastMessage = messages.get(messages.size() - 1);
            map.put("lastMessage", lastMessage);
        }
        
        // 获取未读消息数
        int userUnreadCount = messageMapper.countUnreadBySessionIdAndFromType(session.getId(), 1); // 商家发送的未读消息
        int storeUnreadCount = messageMapper.countUnreadBySessionIdAndFromType(session.getId(), 0); // 用户发送的未读消息
        map.put("userUnreadCount", userUnreadCount);
        map.put("storeUnreadCount", storeUnreadCount);
        
        return map;
    }
} 