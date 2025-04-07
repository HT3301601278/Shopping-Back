package example.shopping.service.impl;

import example.shopping.entity.Product;
import example.shopping.entity.Store;
import example.shopping.entity.User;
import example.shopping.entity.SearchHistory;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.ProductMapper;
import example.shopping.service.ProductService;
import example.shopping.service.StoreService;
import example.shopping.service.SearchHistoryService;
import example.shopping.service.UserService;
import example.shopping.dto.SearchHistoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private StoreService storeService;

    @Autowired
    private SearchHistoryService searchHistoryService;
    
    @Autowired
    private UserService userService;

    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productMapper.findById(id);
    }

    @Override
    public List<Product> findByStoreId(Long storeId) {
        return productMapper.findByStoreId(storeId);
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    @Override
    public Map<String, Object> findByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.findByPage(offset, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", products);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        // TODO: 添加总数统计
        
        return result;
    }

    @Override
    @Transactional
    public Product add(Product product) {
        // 检查店铺状态
        Store store = storeService.findById(product.getStoreId());
        if (store == null) {
            throw new BusinessException("店铺不存在");
        }
        
        // 检查店铺审核状态
        if (store.getStatus() != 1) {
            String message = store.getStatus() == 0 ? "店铺正在审核中" : "店铺已关闭";
            throw new BusinessException(message + "，无法添加商品");
        }
        
        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus(1); // 1-上架
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        if (product.getRating() == null) {
            product.setRating(5.0); // 默认5星
        }
        
        Date now = new Date();
        product.setCreateTime(now);
        product.setUpdateTime(now);
        
        productMapper.insert(product);
        return product;
    }

    @Override
    @Transactional
    public Product update(Product product) {
        Product existingProduct = productMapper.findById(product.getId());
        if (existingProduct == null) {
            throw new BusinessException("商品不存在");
        }
        
        product.setUpdateTime(new Date());
        productMapper.update(product);
        
        return productMapper.findById(product.getId());
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        return productMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean updateSales(Long id, int increment) {
        if (increment <= 0) {
            throw new BusinessException("销量增量必须大于0");
        }
        
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        return productMapper.updateSales(id, increment) > 0;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Long id, int decrement) {
        if (decrement <= 0) {
            throw new BusinessException("库存减少量必须大于0");
        }
        
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        if (product.getStock() < decrement) {
            throw new BusinessException("商品库存不足");
        }
        
        return productMapper.decreaseStock(id, decrement) > 0;
    }

    @Override
    public List<Product> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException("搜索关键字不能为空");
        }
        
        List<Product> products = productMapper.search(keyword);
        
        // 获取当前登录用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            
            User user = userService.findByUsername(username);
            if (user != null) {
                // 保存搜索历史
                SearchHistoryDTO searchHistoryDTO = new SearchHistoryDTO();
                searchHistoryDTO.setKeyword(keyword);
                searchHistoryDTO.setResultCount(products.size());
                searchHistoryService.add(user.getId(), searchHistoryDTO);
            }
        }
        
        return products;
    }

    @Override
    public List<Product> findHotProducts(int limit) {
        if (limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        return productMapper.findHotProducts(limit);
    }

    @Override
    public List<Product> findNewProducts(int limit) {
        if (limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        return productMapper.findNewProducts(limit);
    }
} 