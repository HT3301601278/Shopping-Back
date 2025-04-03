package example.shopping.mapper;

import example.shopping.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartMapper {
    
    /**
     * 根据用户ID查询购物车项
     * @param userId 用户ID
     * @return 购物车项列表
     */
    @Select("SELECT * FROM carts WHERE user_id = #{userId}")
    List<Cart> findByUserId(Long userId);
    
    /**
     * 根据ID查询购物车项
     * @param id 购物车项ID
     * @return 购物车项
     */
    @Select("SELECT * FROM carts WHERE id = #{id}")
    Cart findById(Long id);
    
    /**
     * 根据用户ID和商品ID查询购物车项
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    @Select("SELECT * FROM carts WHERE user_id = #{userId} AND product_id = #{productId}")
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 插入购物车项
     * @param cart 购物车项
     * @return 影响行数
     */
    @Insert("INSERT INTO carts(user_id, product_id, spec_info, quantity, selected, create_time) " +
            "VALUES(#{userId}, #{productId}, #{specInfo}, #{quantity}, #{selected}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cart cart);
    
    /**
     * 更新购物车项
     * @param cart 购物车项
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE carts " +
            "<set>" +
            "<if test='specInfo != null'>spec_info = #{specInfo},</if>" +
            "<if test='quantity != null'>quantity = #{quantity},</if>" +
            "<if test='selected != null'>selected = #{selected},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Cart cart);
    
    /**
     * 更新购物车项数量
     * @param id 购物车项ID
     * @param quantity 数量
     * @return 影响行数
     */
    @Update("UPDATE carts SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 更新购物车项选择状态
     * @param id 购物车项ID
     * @param selected 选择状态
     * @return 影响行数
     */
    @Update("UPDATE carts SET selected = #{selected} WHERE id = #{id}")
    int updateSelected(@Param("id") Long id, @Param("selected") Boolean selected);
    
    /**
     * 更新用户所有购物车项选择状态
     * @param userId 用户ID
     * @param selected 选择状态
     * @return 影响行数
     */
    @Update("UPDATE carts SET selected = #{selected} WHERE user_id = #{userId}")
    int updateAllSelected(@Param("userId") Long userId, @Param("selected") Boolean selected);
    
    /**
     * 删除购物车项
     * @param id 购物车项ID
     * @return 影响行数
     */
    @Delete("DELETE FROM carts WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 删除用户的购物车项
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 影响行数
     */
    @Delete("DELETE FROM carts WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM carts WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 清空用户已选择的购物车项
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM carts WHERE user_id = #{userId} AND selected = 1")
    int deleteSelectedByUserId(Long userId);
    
    /**
     * 根据用户ID、商品ID和规格信息查询购物车项
     * @param userId 用户ID
     * @param productId 商品ID
     * @param specInfo 规格信息
     * @return 购物车项
     */
    @Select("SELECT * FROM carts WHERE user_id = #{userId} AND product_id = #{productId} AND spec_info = #{specInfo}")
    Cart findByUserIdAndProductIdAndSpecInfo(
            @Param("userId") Long userId, 
            @Param("productId") Long productId, 
            @Param("specInfo") String specInfo);
} 