package example.shopping.service;

import example.shopping.dto.AnnouncementDTO;
import example.shopping.entity.Announcement;

import java.util.List;
import java.util.Map;

/**
 * 公告服务接口
 */
public interface AnnouncementService {
    
    /**
     * 添加公告
     * @param publisherId 发布者ID
     * @param announcementDTO 公告信息
     * @return 添加的公告
     */
    Announcement add(Long publisherId, AnnouncementDTO announcementDTO);
    
    /**
     * 更新公告
     * @param id 公告ID
     * @param announcementDTO 公告信息
     * @return 更新后的公告
     */
    Announcement update(Long id, AnnouncementDTO announcementDTO);
    
    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告信息
     */
    Announcement findById(Long id);
    
    /**
     * 查询所有公告
     * @return 公告列表
     */
    List<Announcement> findAll();
    
    /**
     * 查询显示中的公告
     * @return 公告列表
     */
    List<Announcement> findVisible();
    
    /**
     * 分页查询公告
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 包含分页信息的公告列表
     */
    Map<String, Object> findByPage(int pageNum, int pageSize);
    
    /**
     * 删除公告
     * @param id 公告ID
     * @return 是否删除成功
     */
    boolean delete(Long id);
    
    /**
     * 更新公告状态
     * @param id 公告ID
     * @param status 状态(0-隐藏, 1-显示)
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 标记用户已读公告
     * @param id 公告ID
     * @param userId 用户ID
     * @return 是否标记成功
     */
    boolean markAsRead(Long id, Long userId);
    
    /**
     * 查询用户未读公告
     * @param userId 用户ID
     * @return 未读公告列表
     */
    List<Announcement> findUnreadByUserId(Long userId);
    
    /**
     * 查询用户已读公告
     * @param userId 用户ID
     * @return 已读公告列表
     */
    List<Announcement> findReadByUserId(Long userId);
} 