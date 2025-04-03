package example.shopping.service;

import example.shopping.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 查询所有商品
     * @return 商品列表
     */
    List<Product> findAll();
    
    /**
     * 根据ID查询商品
     * @param id 商品ID
     * @return 商品信息
     */
    Product findById(Long id);
    
    /**
     * 根据店铺ID查询商品
     * @param storeId 店铺ID
     * @return 商品列表
     */
    List<Product> findByStoreId(Long storeId);
    
    /**
     * 根据分类ID查询商品
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> findByCategoryId(Long categoryId);
    
    /**
     * 分页查询商品
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 包含分页信息的商品列表
     */
    Map<String, Object> findByPage(int pageNum, int pageSize);
    
    /**
     * 添加商品
     * @param product 商品信息
     * @return 添加后的商品
     */
    Product add(Product product);
    
    /**
     * 更新商品
     * @param product 商品信息
     * @return 更新后的商品
     */
    Product update(Product product);
    
    /**
     * 删除商品
     * @param id 商品ID
     * @return 是否删除成功
     */
    boolean delete(Long id);
    
    /**
     * 更新商品销量
     * @param id 商品ID
     * @param increment 增量
     * @return 是否更新成功
     */
    boolean updateSales(Long id, int increment);
    
    /**
     * 减少商品库存
     * @param id 商品ID
     * @param decrement 减少量
     * @return 是否减少成功
     */
    boolean decreaseStock(Long id, int decrement);
    
    /**
     * 根据关键字搜索商品
     * @param keyword 关键字
     * @return 商品列表
     */
    List<Product> search(String keyword);
    
    /**
     * 获取热门商品
     * @param limit 数量限制
     * @return 热门商品列表
     */
    List<Product> findHotProducts(int limit);
    
    /**
     * 获取新品
     * @param limit 数量限制
     * @return 新品列表
     */
    List<Product> findNewProducts(int limit);
} 