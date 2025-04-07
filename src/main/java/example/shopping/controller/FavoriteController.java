package example.shopping.controller;

import example.shopping.entity.Address;
import example.shopping.entity.Favorite;
import example.shopping.entity.User;
import example.shopping.service.FavoriteService;
import example.shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/favorites")
@PreAuthorize("isAuthenticated()")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    /**
     * 添加收藏
     * @param productId 商品ID
     * @return 添加的收藏
     */
    @PostMapping
    public Address.Result<Favorite> addFavorite(@RequestBody Map<String, Long> params) {
        Long userId = getCurrentUserId();
        Long productId = params.get("productId");
        return Address.Result.success(favoriteService.add(userId, productId), "添加收藏成功");
    }

    /**
     * 取消收藏
     * @param productId 商品ID
     * @return 是否取消成功
     */
    @DeleteMapping("/{productId}")
    public Address.Result<Boolean> cancelFavorite(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        return Address.Result.success(favoriteService.cancel(userId, productId), "取消收藏成功");
    }

    /**
     * 获取收藏列表
     * @return 收藏列表
     */
    @GetMapping
    public Address.Result<List<Map<String, Object>>> getFavorites() {
        Long userId = getCurrentUserId();
        return Address.Result.success(favoriteService.findByUserId(userId));
    }

    /**
     * 分页获取收藏列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页收藏列表
     */
    @GetMapping("/page")
    public Address.Result<Map<String, Object>> getFavoritesWithPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = getCurrentUserId();
        return Address.Result.success(favoriteService.findByUserIdWithPage(userId, pageNum, pageSize));
    }

    /**
     * 检查是否已收藏
     * @param productId 商品ID
     * @return 是否已收藏
     */
    @GetMapping("/check/{productId}")
    public Address.Result<Boolean> checkFavorite(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        return Address.Result.success(favoriteService.isFavorite(userId, productId));
    }

    /**
     * 获取商品收藏数
     * @param productId 商品ID
     * @return 收藏数
     */
    @GetMapping("/count/{productId}")
    public Address.Result<Integer> getFavoriteCount(@PathVariable Long productId) {
        return Address.Result.success(favoriteService.countByProductId(productId));
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
