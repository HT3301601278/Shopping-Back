package example.shopping.service;

import example.shopping.dto.CartDTO;
import example.shopping.entity.Cart;

import java.util.List;
import java.util.Map;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 获取用户购物车商品列表
     * @param userId 用户ID
     * @return 购物车商品列表，包含商品详情
     */
    List<Map<String, Object>> getCartList(Long userId);
    
    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param cartDTO 购物车商品信息
     * @return 添加后的购物车项
     */
    Cart add(Long userId, CartDTO cartDTO);
    
    /**
     * 批量添加商品到购物车
     * @param userId 用户ID
     * @param cartDTOList 购物车商品信息列表
     * @return 添加成功的数量
     */
    int batchAdd(Long userId, List<CartDTO> cartDTOList);
    
    /**
     * 更新购物车商品数量
     * @param userId 用户ID
     * @param id 购物车项ID
     * @param quantity 数量
     * @return 更新后的购物车项
     */
    Cart updateQuantity(Long userId, Long id, Integer quantity);
    
    /**
     * 更新购物车商品选择状态
     * @param userId 用户ID
     * @param id 购物车项ID
     * @param selected 选择状态
     * @return 更新后的购物车项
     */
    Cart updateSelected(Long userId, Long id, Boolean selected);
    
    /**
     * 更新用户所有购物车商品选择状态
     * @param userId 用户ID
     * @param selected 选择状态
     * @return 是否更新成功
     */
    boolean updateAllSelected(Long userId, Boolean selected);
    
    /**
     * 移除购物车商品
     * @param userId 用户ID
     * @param id 购物车项ID
     * @return 是否移除成功
     */
    boolean delete(Long userId, Long id);
    
    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean deleteAll(Long userId);
    
    /**
     * 删除用户已选择的购物车商品
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteSelected(Long userId);
    
    /**
     * 获取用户购物车商品总数量
     * @param userId 用户ID
     * @return 商品总数量
     */
    int getCartProductCount(Long userId);
    
    /**
     * 获取用户购物车已选择的商品列表
     * @param userId 用户ID
     * @return 已选择的商品列表，包含商品详情
     */
    List<Map<String, Object>> getSelectedCartList(Long userId);
    
    /**
     * 获取用户购物车已选择商品的总价
     * @param userId 用户ID
     * @return 总价
     */
    Map<String, Object> getCartAmount(Long userId);
    
    /**
     * 检查商品是否在购物车中
     * @param userId 用户ID
     * @param productId 商品ID
     * @param specInfo 规格信息
     * @return 如果在购物车中存在则返回Cart对象，否则返回null
     */
    Cart checkProductInCart(Long userId, Long productId, String specInfo);
} 