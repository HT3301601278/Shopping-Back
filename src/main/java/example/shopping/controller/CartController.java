package example.shopping.controller;

import example.shopping.dto.CartDTO;
import example.shopping.entity.Cart;
import example.shopping.entity.User;
import example.shopping.service.CartService;
import example.shopping.service.UserService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;

    /**
     * 获取当前用户购物车列表
     * @return 购物车列表
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getCartList() {
        Long userId = getCurrentUserId();
        return Result.success(cartService.getCartList(userId));
    }

    /**
     * 添加商品到购物车
     * @param cartDTO 购物车商品信息
     * @return 添加结果
     */
    @PostMapping
    public Result<Cart> addToCart(@Valid @RequestBody CartDTO cartDTO) {
        Long userId = getCurrentUserId();
        return Result.success(cartService.add(userId, cartDTO), "添加购物车成功");
    }
    
    /**
     * 批量添加商品到购物车
     * @param cartDTOList 购物车商品信息列表
     * @return 添加结果
     */
    @PostMapping("/batch")
    public Result<Integer> batchAddToCart(@Valid @RequestBody List<CartDTO> cartDTOList) {
        Long userId = getCurrentUserId();
        int count = cartService.batchAdd(userId, cartDTOList);
        return Result.success(count, "成功添加" + count + "件商品到购物车");
    }

    /**
     * 更新购物车商品数量
     * @param id 购物车项ID
     * @param quantity 数量
     * @return 更新结果
     */
    @PutMapping("/{id}/quantity")
    public Result<Cart> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        Long userId = getCurrentUserId();
        return Result.success(cartService.updateQuantity(userId, id, quantity), "更新数量成功");
    }

    /**
     * 更新购物车商品选择状态
     * @param id 购物车项ID
     * @param selected 选择状态
     * @return 更新结果
     */
    @PutMapping("/{id}/selected")
    public Result<Cart> updateSelected(@PathVariable Long id, @RequestParam Boolean selected) {
        Long userId = getCurrentUserId();
        return Result.success(cartService.updateSelected(userId, id, selected), "更新选择状态成功");
    }

    /**
     * 更新所有购物车商品选择状态
     * @param selected 选择状态
     * @return 更新结果
     */
    @PutMapping("/selected")
    public Result<Boolean> updateAllSelected(@RequestParam Boolean selected) {
        Long userId = getCurrentUserId();
        return Result.success(cartService.updateAllSelected(userId, selected), "更新所有选择状态成功");
    }

    /**
     * 删除购物车商品
     * @param id 购物车项ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCart(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return Result.success(cartService.delete(userId, id), "删除成功");
    }

    /**
     * 清空购物车
     * @return 清空结果
     */
    @DeleteMapping
    public Result<Boolean> deleteAllCart() {
        Long userId = getCurrentUserId();
        return Result.success(cartService.deleteAll(userId), "清空成功");
    }

    /**
     * 删除已选择的购物车商品
     * @return 删除结果
     */
    @DeleteMapping("/selected")
    public Result<Boolean> deleteSelectedCart() {
        Long userId = getCurrentUserId();
        return Result.success(cartService.deleteSelected(userId), "删除已选择商品成功");
    }

    /**
     * 获取购物车商品数量
     * @return 商品数量
     */
    @GetMapping("/count")
    public Result<Integer> getCartCount() {
        Long userId = getCurrentUserId();
        return Result.success(cartService.getCartProductCount(userId));
    }

    /**
     * 获取已选择的购物车商品列表
     * @return 已选择的购物车商品列表
     */
    @GetMapping("/selected")
    public Result<List<Map<String, Object>>> getSelectedCartList() {
        Long userId = getCurrentUserId();
        return Result.success(cartService.getSelectedCartList(userId));
    }
    
    /**
     * 获取购物车已选择商品的总价
     * @return 总价信息
     */
    @GetMapping("/amount")
    public Result<Map<String, Object>> getCartAmount() {
        Long userId = getCurrentUserId();
        return Result.success(cartService.getCartAmount(userId));
    }
    
    /**
     * 检查商品是否已在购物车中
     * @param productId 商品ID
     * @param specInfo 规格信息
     * @return 是否在购物车中
     */
    @GetMapping("/check")
    public Result<Boolean> checkProductInCart(@RequestParam Long productId, 
                                             @RequestParam(required = false) String specInfo) {
        Long userId = getCurrentUserId();
        Cart cart = cartService.checkProductInCart(userId, productId, specInfo);
        return Result.success(cart != null);
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