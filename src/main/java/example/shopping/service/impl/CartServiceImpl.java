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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // 验证规格信息
        if (cartDTO.getSpecInfo() != null) {
            try {
                // 解析商品规格信息
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.Map<String, Object> productSpecifications =
                        mapper.readValue(product.getSpecifications(), new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {
                        });

                // 验证规格是否合法
                for (Map.Entry<String, String> entry : cartDTO.getSpecInfo().entrySet()) {
                    String specName = entry.getKey();
                    String specValue = entry.getValue();

                    // 检查规格名称是否存在
                    if (!productSpecifications.containsKey(specName)) {
                        throw new BusinessException("无效的规格名称：" + specName);
                    }

                    // 检查规格值是否有效
                    Object specValues = productSpecifications.get(specName);
                    if (specValues instanceof java.util.List) {
                        // 处理列表形式的规格值
                        @SuppressWarnings("unchecked")
                        java.util.List<String> valueList = (java.util.List<String>) specValues;
                        if (!valueList.contains(specValue)) {
                            throw new BusinessException("无效的规格值：" + specValue);
                        }
                    } else if (specValues instanceof String) {
                        // 处理字符串形式的规格值
                        if (!specValues.equals(specValue)) {
                            throw new BusinessException("无效的规格值：" + specValue);
                        }
                    } else {
                        throw new BusinessException("规格信息格式不支持");
                    }
                }
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new BusinessException("规格信息格式错误");
            }
        }

        // 将规格信息转换为JSON字符串
        String specInfoJson = null;
        if (cartDTO.getSpecInfo() != null) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                specInfoJson = mapper.writeValueAsString(cartDTO.getSpecInfo());
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new BusinessException("规格信息转换失败");
            }
        }

        // 检查是否已经添加过该商品（相同规格）
        Cart existingCart = cartMapper.findByUserIdAndProductIdAndSpecInfo(userId, cartDTO.getProductId(), specInfoJson);
        if (existingCart != null) {
            // 如果已经添加过，则更新数量
            int newQuantity = existingCart.getQuantity() + cartDTO.getQuantity();

            // 再次检查库存是否足够
            if (product.getStock() < newQuantity) {
                throw new BusinessException("商品库存不足");
            }

            existingCart.setQuantity(newQuantity);
            cartMapper.update(existingCart);
            return existingCart;
        } else {
            // 如果没有添加过，则新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(cartDTO.getProductId());
            cart.setSpecInfo(specInfoJson);
            cart.setQuantity(cartDTO.getQuantity());
            cart.setSelected(true);
            cart.setCreateTime(new Date());

            cartMapper.insert(cart);
            return cart;
        }
    }

    @Override
    @Transactional
    public int batchAdd(Long userId, List<CartDTO> cartDTOList) {
        int successCount = 0;

        for (CartDTO cartDTO : cartDTOList) {
            try {
                add(userId, cartDTO);
                successCount++;
            } catch (BusinessException e) {
                // 记录异常，继续处理下一个
                // 这里可以选择记录日志
            }
        }

        return successCount;
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

    @Override
    public Map<String, Object> getCartAmount(Long userId) {
        List<Map<String, Object>> selectedCartList = getSelectedCartList(userId);
        Map<String, Object> result = new HashMap<>();

        java.math.BigDecimal totalAmount = selectedCartList.stream()
                .map(item -> (java.math.BigDecimal) item.get("totalPrice"))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        result.put("totalAmount", totalAmount);
        result.put("itemCount", selectedCartList.size());
        return result;
    }

    @Override
    public Cart checkProductInCart(Long userId, Long productId, String specInfo) {
        return cartMapper.findByUserIdAndProductIdAndSpecInfo(userId, productId, specInfo);
    }

    /**
     * 将购物车项转换为Map，包含商品详情
     *
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
