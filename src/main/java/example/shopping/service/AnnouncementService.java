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
     * 获取所有公告
     *
     * @return 公告列表
     */
    List<Announcement> findAll();

    /**
     * 获取所有显示状态的公告
     *
     * @return 公告列表
     */
    List<Announcement> findAllVisible();

    /**
     * 根据ID获取公告
     *
     * @param id 公告ID
     * @return 公告信息
     */
    Announcement findById(Long id);

    /**
     * 添加公告
     *
     * @param publisherId     发布者ID
     * @param announcementDTO 公告信息
     * @return 添加的公告
     */
    Announcement add(Long publisherId, AnnouncementDTO announcementDTO);

    /**
     * 更新公告
     *
     * @param id              公告ID
     * @param announcementDTO 公告信息
     * @return 更新后的公告
     */
    Announcement update(Long id, AnnouncementDTO announcementDTO);

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 更新公告状态
     *
     * @param id     公告ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 标记用户已读公告
     *
     * @param id     公告ID
     * @param userId 用户ID
     * @return 是否标记成功
     */
    boolean markAsRead(Long id, Long userId);

    /**
     * 获取用户未读公告数量
     *
     * @param userId 用户ID
     * @return 未读公告数量
     */
    int getUnreadCount(Long userId);

    /**
     * 获取用户已读/未读公告列表
     *
     * @param userId 用户ID
     * @param isRead 是否已读
     * @return 公告列表
     */
    List<Map<String, Object>> getUserAnnouncements(Long userId, boolean isRead);
}
