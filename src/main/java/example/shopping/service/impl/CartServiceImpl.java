package example.shopping.service.impl;

import example.shopping.dto.CartDTO;
import example.shopping.entity.Cart;
import example.shopping.entity.Product;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.CartMapper;
import example.shopping.mapper.ProductMapper;
import example.shopping.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Map<String, Object>> getCartList(Long userId) {
        List<Cart> cartList = cartMapper.findByUserId(userId);
        return cartList.stream().map(this::convertCartToMap).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Cart add(Long userId, CartDTO cartDTO) {
        // 检查商品是否存在
        Product product = productMapper.findById(cartDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查商品是否已下架
        if (product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
        
        // 检查库存是否足够
        if (product.getStock() < cartDTO.getQuantity()) {
            throw new BusinessException("商品库存不足");
        }
        
        // 检查是否已经添加过该商品
        Cart existingCart = cartMapper.findByUserIdAndProductId(userId, cartDTO.getProductId());
        if (existingCart != null) {
            // 如果已经添加过，则更新数量
            int newQuantity = existingCart.getQuantity() + cartDTO.getQuantity();
            
            // 再次检查库存是否足够
            if (product.getStock() < newQuantity) {
                throw new BusinessException("商品库存不足");
            }
            
            existingCart.setQuantity(newQuantity);
            existingCart.setSpecInfo(cartDTO.getSpecInfo());
            cartMapper.update(existingCart);
            return existingCart;
        } else {
            // 如果没有添加过，则新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(cartDTO.getProductId());
            cart.setSpecInfo(cartDTO.getSpecInfo());
            cart.setQuantity(cartDTO.getQuantity());
            cart.setSelected(true);
            cart.setCreateTime(new Date());
            
            cartMapper.insert(cart);
            return cart;
        }
    }

    @Override
    @Transactional
    public Cart updateQuantity(Long userId, Long id, Integer quantity) {
        // 检查购物车项是否存在
        Cart cart = cartMapper.findById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车商品不存在");
        }
        
        // 检查商品是否存在
        Product product = productMapper.findById(cart.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查商品是否已下架
        if (product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
        
        // 检查库存是否足够
        if (product.getStock() < quantity) {
            throw new BusinessException("商品库存不足");
        }
        
        cartMapper.updateQuantity(id, quantity);
        cart.setQuantity(quantity);
        return cart;
    }

    @Override
    @Transactional
    public Cart updateSelected(Long userId, Long id, Boolean selected) {
        // 检查购物车项是否存在
        Cart cart = cartMapper.findById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车商品不存在");
        }
        
        cartMapper.updateSelected(id, selected);
        cart.setSelected(selected);
        return cart;
    }

    @Override
    @Transactional
    public boolean updateAllSelected(Long userId, Boolean selected) {
        return cartMapper.updateAllSelected(userId, selected) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long userId, Long id) {
        // 检查购物车项是否存在
        Cart cart = cartMapper.findById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车商品不存在");
        }
        
        return cartMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean deleteAll(Long userId) {
        return cartMapper.deleteByUserId(userId) > 0;
    }

    @Override
    @Transactional
    public boolean deleteSelected(Long userId) {
        return cartMapper.deleteSelectedByUserId(userId) > 0;
    }

    @Override
    public int getCartProductCount(Long userId) {
        List<Cart> cartList = cartMapper.findByUserId(userId);
        return cartList.stream().mapToInt(Cart::getQuantity).sum();
    }

    @Override
    public List<Map<String, Object>> getSelectedCartList(Long userId) {
        List<Cart> cartList = cartMapper.findByUserId(userId);
        return cartList.stream()
                .filter(Cart::getSelected)
                .map(this::convertCartToMap)
                .collect(Collectors.toList());
    }
    
    /**
     * 将购物车项转换为Map，包含商品详情
     * @param cart 购物车项
     * @return 包含商品详情的Map
     */
    private Map<String, Object> convertCartToMap(Cart cart) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", cart.getId());
        map.put("userId", cart.getUserId());
        map.put("productId", cart.getProductId());
        map.put("specInfo", cart.getSpecInfo());
        map.put("quantity", cart.getQuantity());
        map.put("selected", cart.getSelected());
        map.put("createTime", cart.getCreateTime());
        
        // 获取商品信息
        Product product = productMapper.findById(cart.getProductId());
        if (product != null) {
            map.put("product", product);
            // 计算总价
            map.put("totalPrice", product.getPrice().multiply(java.math.BigDecimal.valueOf(cart.getQuantity())));
        }
        
        return map;
    }
} 