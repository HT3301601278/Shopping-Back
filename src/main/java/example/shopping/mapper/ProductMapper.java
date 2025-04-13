package example.shopping.mapper;

import example.shopping.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper {

    /**
     * 查询所有商品
     *
     * @return 商品列表
     */
    @Select("SELECT * FROM products")
    List<Product> findAll();

    /**
     * 根据ID查询商品
     *
     * @param id 商品ID
     * @return 商品信息
     */
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findById(Long id);

    /**
     * 根据店铺ID查询商品
     *
     * @param storeId 店铺ID
     * @return 商品列表
     */
    @Select("SELECT * FROM products WHERE store_id = #{storeId}")
    List<Product> findByStoreId(Long storeId);

    /**
     * 根据分类ID查询商品
     *
     * @param categoryId 分类ID
     * @return 商品列表
     */
    @Select("SELECT * FROM products WHERE category_id = #{categoryId}")
    List<Product> findByCategoryId(Long categoryId);

    /**
     * 根据分类ID统计商品数量
     *
     * @param categoryId 分类ID
     * @return 商品数量
     */
    @Select("SELECT COUNT(*) FROM products WHERE category_id = #{categoryId}")
    int countByCategoryId(Long categoryId);

    /**
     * 分页查询商品
     *
     * @param offset 偏移量
     * @param limit  数量限制
     * @return 商品列表
     */
    @Select("SELECT * FROM products LIMIT #{offset}, #{limit}")
    List<Product> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 插入商品
     *
     * @param product 商品信息
     * @return 影响行数
     */
    @Insert("INSERT INTO products(name, store_id, category_id, price, stock, description, " +
            "images, detail, specifications, status, sales, rating, create_time, update_time) " +
            "VALUES(#{name}, #{storeId}, #{categoryId}, #{price}, #{stock}, #{description}, " +
            "#{images}, #{detail}, #{specifications}, #{status}, #{sales}, #{rating}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    /**
     * 更新商品
     *
     * @param product 商品信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE products " +
            "<set>" +
            "<if test='name != null'>name = #{name},</if>" +
            "<if test='storeId != null'>store_id = #{storeId},</if>" +
            "<if test='categoryId != null'>category_id = #{categoryId},</if>" +
            "<if test='price != null'>price = #{price},</if>" +
            "<if test='stock != null'>stock = #{stock},</if>" +
            "<if test='description != null'>description = #{description},</if>" +
            "<if test='images != null'>images = #{images},</if>" +
            "<if test='detail != null'>detail = #{detail},</if>" +
            "<if test='specifications != null'>specifications = #{specifications},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='sales != null'>sales = #{sales},</if>" +
            "<if test='rating != null'>rating = #{rating},</if>" +
            "update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Product product);

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @return 影响行数
     */
    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 更新商品销量
     *
     * @param id        商品ID
     * @param increment 增量
     * @return 影响行数
     */
    @Update("UPDATE products SET sales = sales + #{increment} WHERE id = #{id}")
    int updateSales(@Param("id") Long id, @Param("increment") int increment);

    /**
     * 更新商品库存
     *
     * @param id        商品ID
     * @param decrement 减少量
     * @return 影响行数
     */
    @Update("UPDATE products SET stock = stock - #{decrement} WHERE id = #{id} AND stock >= #{decrement}")
    int decreaseStock(@Param("id") Long id, @Param("decrement") int decrement);

    /**
     * 根据关键字搜索商品
     *
     * @param keyword 关键字
     * @return 商品列表
     */
    @Select("SELECT * FROM products WHERE name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')")
    List<Product> search(String keyword);

    /**
     * 获取热门商品
     *
     * @param limit 数量限制
     * @return 热门商品列表
     */
    @Select("SELECT * FROM products WHERE status = 1 ORDER BY sales DESC LIMIT #{limit}")
    List<Product> findHotProducts(int limit);

    /**
     * 获取新品
     *
     * @param limit 数量限制
     * @return 新品列表
     */
    @Select("SELECT * FROM products WHERE status = 1 ORDER BY create_time DESC LIMIT #{limit}")
    List<Product> findNewProducts(int limit);
}
