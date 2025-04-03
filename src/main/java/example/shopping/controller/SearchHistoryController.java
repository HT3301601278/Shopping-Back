package example.shopping.controller;

import example.shopping.dto.SearchHistoryDTO;
import example.shopping.entity.SearchHistory;
import example.shopping.entity.User;
import example.shopping.service.SearchHistoryService;
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
 * 搜索历史控制器
 */
@RestController
@RequestMapping("/api/search-history")
@PreAuthorize("isAuthenticated()")
public class SearchHistoryController {

    @Autowired
    private SearchHistoryService searchHistoryService;
    
    @Autowired
    private UserService userService;

    /**
     * 获取当前用户的搜索历史
     * @return 搜索历史列表
     */
    @GetMapping
    public Result<List<SearchHistory>> getUserSearchHistory() {
        Long userId = getCurrentUserId();
        return Result.success(searchHistoryService.findByUserId(userId));
    }

    /**
     * 获取当前用户最近的搜索历史
     * @param limit 返回数量
     * @return 最近的搜索历史列表
     */
    @GetMapping("/latest")
    public Result<List<SearchHistory>> getLatestSearchHistory(
            @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = getCurrentUserId();
        return Result.success(searchHistoryService.findLatestByUserId(userId, limit));
    }

    /**
     * 添加搜索历史
     * @param searchHistoryDTO 搜索历史信息
     * @return 添加的搜索历史
     */
    @PostMapping
    public Result<SearchHistory> addSearchHistory(@Valid @RequestBody SearchHistoryDTO searchHistoryDTO) {
        Long userId = getCurrentUserId();
        return Result.success(searchHistoryService.add(userId, searchHistoryDTO));
    }

    /**
     * 删除搜索历史
     * @param id 搜索历史ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSearchHistory(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return Result.success(searchHistoryService.delete(userId, id), "删除搜索历史成功");
    }

    /**
     * 清空所有搜索历史
     * @return 是否清空成功
     */
    @DeleteMapping
    public Result<Boolean> clearSearchHistory() {
        Long userId = getCurrentUserId();
        return Result.success(searchHistoryService.clear(userId), "清空搜索历史成功");
    }

    /**
     * 获取当前用户的热门搜索关键词
     * @param limit 返回数量
     * @return 热门关键词列表
     */
    @GetMapping("/hot-keywords")
    public Result<List<Map<String, Object>>> getHotKeywords(
            @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = getCurrentUserId();
        return Result.success(searchHistoryService.getHotKeywords(userId, limit));
    }

    /**
     * 获取全站热门搜索关键词（可公开访问）
     * @param limit 返回数量
     * @return 热门关键词列表
     */
    @GetMapping("/global-hot-keywords")
    @PreAuthorize("permitAll()")
    public Result<List<Map<String, Object>>> getGlobalHotKeywords(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(searchHistoryService.getGlobalHotKeywords(limit));
    }

    /**
     * 获取当前登录用户ID
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