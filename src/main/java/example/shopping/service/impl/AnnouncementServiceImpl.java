package example.shopping.service.impl;

import com.alibaba.fastjson.JSON;
import example.shopping.dto.AnnouncementDTO;
import example.shopping.entity.Announcement;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.AnnouncementMapper;
import example.shopping.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 公告服务实现类
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    @Transactional
    public Announcement add(Long publisherId, AnnouncementDTO announcementDTO) {
        Announcement announcement = new Announcement();
        announcement.setTitle(announcementDTO.getTitle());
        announcement.setContent(announcementDTO.getContent());
        announcement.setPublisherId(publisherId);
        announcement.setStatus(announcementDTO.getStatus());
        announcement.setReadUsers("[]"); // 初始化为空数组
        
        Date now = new Date();
        announcement.setCreateTime(now);
        announcement.setUpdateTime(now);
        
        announcementMapper.insert(announcement);
        
        return announcement;
    }

    @Override
    @Transactional
    public Announcement update(Long id, AnnouncementDTO announcementDTO) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        
        announcement.setTitle(announcementDTO.getTitle());
        announcement.setContent(announcementDTO.getContent());
        announcement.setStatus(announcementDTO.getStatus());
        announcement.setUpdateTime(new Date());
        
        announcementMapper.update(announcement);
        
        return announcement;
    }

    @Override
    public Announcement findById(Long id) {
        return announcementMapper.findById(id);
    }

    @Override
    public List<Announcement> findAll() {
        return announcementMapper.findAll();
    }

    @Override
    public List<Announcement> findVisible() {
        return announcementMapper.findVisible();
    }

    @Override
    public Map<String, Object> findByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Announcement> announcements = announcementMapper.findByPage(offset, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", announcements);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        // TODO: 添加总数统计
        
        return result;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        
        return announcementMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        
        if (status != 0 && status != 1) {
            throw new BusinessException("状态值无效");
        }
        
        return announcementMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional
    public boolean markAsRead(Long id, Long userId) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        
        // 获取已读用户列表
        List<Long> readUserIds = JSON.parseArray(announcement.getReadUsers(), Long.class);
        if (readUserIds == null) {
            readUserIds = new ArrayList<>();
        }
        
        // 检查用户是否已经标记为已读
        if (readUserIds.contains(userId)) {
            return true;
        }
        
        // 添加用户ID到已读列表
        readUserIds.add(userId);
        
        // 更新已读用户列表
        String readUsersJson = JSON.toJSONString(readUserIds);
        return announcementMapper.updateReadUsers(id, readUsersJson) > 0;
    }

    @Override
    public List<Announcement> findUnreadByUserId(Long userId) {
        List<Announcement> visibleAnnouncements = findVisible();
        return visibleAnnouncements.stream()
                .filter(a -> !isRead(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> findReadByUserId(Long userId) {
        List<Announcement> visibleAnnouncements = findVisible();
        return visibleAnnouncements.stream()
                .filter(a -> isRead(a, userId))
                .collect(Collectors.toList());
    }
    
    /**
     * 检查用户是否已读公告
     * @param announcement 公告
     * @param userId 用户ID
     * @return 是否已读
     */
    private boolean isRead(Announcement announcement, Long userId) {
        List<Long> readUserIds = JSON.parseArray(announcement.getReadUsers(), Long.class);
        return readUserIds != null && readUserIds.contains(userId);
    }
} 