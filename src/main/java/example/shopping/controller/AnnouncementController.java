package example.shopping.controller;

import example.shopping.dto.AnnouncementDTO;
import example.shopping.entity.Announcement;
import example.shopping.entity.User;
import example.shopping.service.AnnouncementService;
import example.shopping.service.UserService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    /**
     * 获取所有公告（管理员）
     *
     * @return 公告列表
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Announcement>> getAllAnnouncements() {
        return Result.success(announcementService.findAll());
    }

    /**
     * 获取显示中的公告（全部用户）
     *
     * @return 公告列表
     */
    @GetMapping
    public Result<List<Announcement>> getVisibleAnnouncements() {
        return Result.success(announcementService.findAllVisible());
    }

    /**
     * 根据ID获取公告详情
     *
     * @param id 公告ID
     * @return 公告信息
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        return Result.success(announcementService.findById(id));
    }

    /**
     * 添加公告（管理员）
     *
     * @param announcementDTO 公告信息
     * @return 添加的公告
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Announcement> addAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDTO) {
        Long publisherId = getCurrentUserId();
        return Result.success(announcementService.add(publisherId, announcementDTO), "添加公告成功");
    }

    /**
     * 更新公告（管理员）
     *
     * @param id              公告ID
     * @param announcementDTO 公告信息
     * @return 更新后的公告
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Announcement> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementDTO announcementDTO) {
        return Result.success(announcementService.update(id, announcementDTO), "更新公告成功");
    }

    /**
     * 删除公告（管理员）
     *
     * @param id 公告ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> deleteAnnouncement(@PathVariable Long id) {
        return Result.success(announcementService.delete(id), "删除公告成功");
    }

    /**
     * 更新公告状态（管理员）
     *
     * @param id     公告ID
     * @param status 状态(0-隐藏, 1-显示)
     * @return 是否更新成功
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> updateAnnouncementStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        return Result.success(announcementService.updateStatus(id, status), "更新公告状态成功");
    }

    /**
     * 标记公告为已读（登录用户）
     *
     * @param id 公告ID
     * @return 是否标记成功
     */
    @PostMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> markAnnouncementAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return Result.success(announcementService.markAsRead(id, userId), "标记公告为已读成功");
    }

    /**
     * 获取当前用户未读公告数量（登录用户）
     *
     * @return 未读公告数量
     */
    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    public Result<Integer> getUnreadAnnouncementCount() {
        Long userId = getCurrentUserId();
        return Result.success(announcementService.getUnreadCount(userId));
    }

    /**
     * 获取当前用户未读公告列表（登录用户）
     *
     * @return 未读公告列表
     */
    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public Result<List<Map<String, Object>>> getUnreadAnnouncements() {
        Long userId = getCurrentUserId();
        return Result.success(announcementService.getUserAnnouncements(userId, false));
    }

    /**
     * 获取当前用户已读公告列表（登录用户）
     *
     * @return 已读公告列表
     */
    @GetMapping("/read")
    @PreAuthorize("isAuthenticated()")
    public Result<List<Map<String, Object>>> getReadAnnouncements() {
        Long userId = getCurrentUserId();
        return Result.success(announcementService.getUserAnnouncements(userId, true));
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getId();
    }
}
