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
    public List<Announcement> findAllVisible() {
        return announcementMapper.findVisible();
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
    public int getUnreadCount(Long userId) {
        List<Announcement> unreadAnnouncements = findAllVisible().stream()
                .filter(a -> !isRead(a, userId))
                .collect(Collectors.toList());
        return unreadAnnouncements.size();
    }

    @Override
    public List<Map<String, Object>> getUserAnnouncements(Long userId, boolean isRead) {
        List<Announcement> announcements = findAllVisible();

        // 根据已读状态过滤公告
        List<Announcement> filteredAnnouncements = announcements.stream()
                .filter(a -> isRead == isRead(a, userId))
                .collect(Collectors.toList());

        // 转换为Map列表，添加额外信息
        return filteredAnnouncements.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("title", a.getTitle());
            map.put("content", a.getContent());
            map.put("publisherId", a.getPublisherId());
            map.put("status", a.getStatus());
            map.put("createTime", a.getCreateTime());
            map.put("updateTime", a.getUpdateTime());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 检查用户是否已读公告
     *
     * @param announcement 公告
     * @param userId       用户ID
     * @return 是否已读
     */
    private boolean isRead(Announcement announcement, Long userId) {
        List<Long> readUserIds = JSON.parseArray(announcement.getReadUsers(), Long.class);
        return readUserIds != null && readUserIds.contains(userId);
    }
}
