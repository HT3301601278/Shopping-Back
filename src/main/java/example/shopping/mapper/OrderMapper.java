package example.shopping.mapper;

import example.shopping.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {

    /**
     * 查询所有订单
     * @return 订单列表
     */
    @Select("SELECT * FROM orders ORDER BY create_time DESC")
    List<Order> findAll();
    
    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单信息
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order findById(Long id);
    
    /**
     * 根据订单编号查询订单
     * @param orderNo 订单编号
     * @return 订单信息
     */
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Order findByOrderNo(String orderNo);
    
    /**
     * 根据用户ID查询订单
     * @param userId 用户ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据店铺ID查询订单
     * @param storeId 店铺ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE store_id = #{storeId} ORDER BY create_time DESC")
    List<Order> findByStoreId(Long storeId);
    
    /**
     * 根据订单状态查询订单
     * @param status 订单状态
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE status = #{status} ORDER BY create_time DESC")
    List<Order> findByStatus(Integer status);
    
    /**
     * 根据用户ID和订单状态查询订单
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND status = #{status} ORDER BY create_time DESC")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 分页查询订单
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 订单列表
     */
    @Select("SELECT * FROM orders ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Order> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 插入订单
     * @param order 订单信息
     * @return 影响行数
     */
    @Insert("INSERT INTO orders(order_no, user_id, store_id, items, total_amount, address_info, " +
            "payment_type, status, refund_status, remark, create_time, update_time) " +
            "VALUES(#{orderNo}, #{userId}, #{storeId}, #{items}, #{totalAmount}, #{addressInfo}, " +
            "#{paymentType}, #{status}, #{refundStatus}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);
    
    /**
     * 更新订单
     * @param order 订单信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE orders " +
            "<set>" +
            "<if test='paymentType != null'>payment_type = #{paymentType},</if>" +
            "<if test='paymentTime != null'>payment_time = #{paymentTime},</if>" +
            "<if test='shippingTime != null'>shipping_time = #{shippingTime},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='refundStatus != null'>refund_status = #{refundStatus},</if>" +
            "<if test='refundReason != null'>refund_reason = #{refundReason},</if>" +
            "<if test='remark != null'>remark = #{remark},</if>" +
            "update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Order order);
    
    /**
     * 删除订单
     * @param id 订单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 影响行数
     */
    @Update("UPDATE orders SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 统计用户订单数量
     * @param userId 用户ID
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId}")
    int countByUserId(Long userId);
    
    /**
     * 统计店铺订单数量
     * @param storeId 店铺ID
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE store_id = #{storeId}")
    int countByStoreId(Long storeId);
    
    /**
     * 查询待付款订单
     * @param userId 用户ID
     * @return 待付款订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND status = 0 ORDER BY create_time DESC")
    List<Order> findPendingPayment(Long userId);
    
    /**
     * 查询待发货订单
     * @param userId 用户ID
     * @return 待发货订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND status = 1 ORDER BY create_time DESC")
    List<Order> findPendingShipment(Long userId);
    
    /**
     * 查询待收货订单
     * @param userId 用户ID
     * @return 待收货订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND status = 2 ORDER BY create_time DESC")
    List<Order> findPendingReceipt(Long userId);
    
    /**
     * 查询待评价订单
     * @param userId 用户ID
     * @return 待评价订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND status = 3 AND id NOT IN (SELECT order_id FROM reviews WHERE user_id = #{userId}) ORDER BY create_time DESC")
    List<Order> findPendingReview(Long userId);
} 