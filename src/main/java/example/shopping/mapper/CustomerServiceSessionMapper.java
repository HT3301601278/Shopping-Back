package example.shopping.mapper;

import example.shopping.entity.CustomerServiceSession;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 客服会话Mapper接口
 */
@Mapper
public interface CustomerServiceSessionMapper {
    
    /**
     * 查询所有会话
     * @return 会话列表
     */
    @Select("SELECT * FROM customer_service_sessions ORDER BY start_time DESC")
    List<CustomerServiceSession> findAll();
    
    /**
     * 根据ID查询会话
     * @param id 会话ID
     * @return 会话信息
     */
    @Select("SELECT * FROM customer_service_sessions WHERE id = #{id}")
    CustomerServiceSession findById(Long id);
    
    /**
     * 根据用户ID查询会话
     * @param userId 用户ID
     * @return 会话列表
     */
    @Select("SELECT * FROM customer_service_sessions WHERE user_id = #{userId} ORDER BY start_time DESC")
    List<CustomerServiceSession> findByUserId(Long userId);
    
    /**
     * 根据店铺ID查询会话
     * @param storeId 店铺ID
     * @return 会话列表
     */
    @Select("SELECT * FROM customer_service_sessions WHERE store_id = #{storeId} ORDER BY start_time DESC")
    List<CustomerServiceSession> findByStoreId(Long storeId);
    
    /**
     * 根据状态查询会话
     * @param status 状态
     * @return 会话列表
     */
    @Select("SELECT * FROM customer_service_sessions WHERE status = #{status} ORDER BY start_time DESC")
    List<CustomerServiceSession> findByStatus(Integer status);
    
    /**
     * 查询用户与店铺的会话
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 会话列表
     */
    @Select("SELECT * FROM customer_service_sessions WHERE user_id = #{userId} AND store_id = #{storeId} ORDER BY start_time DESC")
    List<CustomerServiceSession> findByUserIdAndStoreId(@Param("userId") Long userId, @Param("storeId") Long storeId);
    
    /**
     * 查询用户与店铺的进行中会话
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 会话信息
     */
    @Select("SELECT * FROM customer_service_sessions WHERE user_id = #{userId} AND store_id = #{storeId} AND status = 0 ORDER BY start_time DESC LIMIT 1")
    CustomerServiceSession findActiveByUserIdAndStoreId(@Param("userId") Long userId, @Param("storeId") Long storeId);
    
    /**
     * 分页查询会话
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 会话列表
     */
    @Select("SELECT * FROM customer_service_sessions ORDER BY start_time DESC LIMIT #{offset}, #{limit}")
    List<CustomerServiceSession> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 插入会话
     * @param session 会话信息
     * @return 影响行数
     */
    @Insert("INSERT INTO customer_service_sessions(user_id, store_id, status, start_time, create_time, update_time) " +
            "VALUES(#{userId}, #{storeId}, #{status}, #{startTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CustomerServiceSession session);
    
    /**
     * 更新会话
     * @param session 会话信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE customer_service_sessions " +
            "<set>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='endTime != null'>end_time = #{endTime},</if>" +
            "<if test='evaluation != null'>evaluation = #{evaluation},</if>" +
            "<if test='remark != null'>remark = #{remark},</if>" +
            "update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(CustomerServiceSession session);
    
    /**
     * 更新会话状态
     * @param id 会话ID
     * @param status 状态
     * @param endTime 结束时间
     * @return 影响行数
     */
    @Update("UPDATE customer_service_sessions SET status = #{status}, end_time = #{endTime}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("endTime") Date endTime);
    
    /**
     * 更新会话评价
     * @param id 会话ID
     * @param evaluation 评价
     * @param remark 评价备注
     * @return 影响行数
     */
    @Update("UPDATE customer_service_sessions SET " +
            "evaluation = #{evaluation}, " +
            "remark = #{remark}, " +
            "complaint_status = CASE WHEN #{evaluation} <= 2 THEN 0 ELSE NULL END, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int updateEvaluation(@Param("id") Long id, @Param("evaluation") Integer evaluation, @Param("remark") String remark);
    
    /**
     * 统计店铺的会话数量
     * @param storeId 店铺ID
     * @return 会话数量
     */
    @Select("SELECT COUNT(*) FROM customer_service_sessions WHERE store_id = #{storeId}")
    int countByStoreId(Long storeId);
    
    /**
     * 统计店铺的评价数量
     * @param storeId 店铺ID
     * @return 评价数量
     */
    @Select("SELECT COUNT(*) FROM customer_service_sessions WHERE store_id = #{storeId} AND evaluation IS NOT NULL")
    int countEvaluationByStoreId(Long storeId);
    
    /**
     * 计算店铺的平均评价
     * @param storeId 店铺ID
     * @return 平均评价
     */
    @Select("SELECT AVG(evaluation) FROM customer_service_sessions WHERE store_id = #{storeId} AND evaluation IS NOT NULL")
    Double calculateAverageEvaluation(Long storeId);
    
    /**
     * 查询所有投诉会话
     * @return 投诉会话列表
     */
    @Select("SELECT * FROM customer_service_sessions WHERE evaluation <= 2 AND complaint_status = 0 ORDER BY create_time DESC")
    List<CustomerServiceSession> findComplainedSessions();
    
    /**
     * 更新投诉状态
     * @param id 会话ID
     * @param complaintStatus 投诉状态
     * @param complaintResult 处理结果
     * @param isPenalty 是否处罚
     * @param penaltyContent 处罚内容
     * @return 影响行数
     */
    @Update("UPDATE customer_service_sessions SET " +
            "complaint_status = #{complaintStatus}, " +
            "complaint_result = #{complaintResult}, " +
            "is_penalty = #{isPenalty}, " +
            "penalty_content = #{penaltyContent}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int updateComplaintStatus(@Param("id") Long id,
                            @Param("complaintStatus") Integer complaintStatus,
                            @Param("complaintResult") String complaintResult,
                            @Param("isPenalty") Boolean isPenalty,
                            @Param("penaltyContent") String penaltyContent);
    
    /**
     * 根据店铺ID查询会话（分页）
     * @param storeId 店铺ID
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 会话列表
     */
    @Select("SELECT * FROM customer_service_sessions WHERE store_id = #{storeId} ORDER BY update_time DESC LIMIT #{offset}, #{limit}")
    List<CustomerServiceSession> findByStoreIdWithPage(@Param("storeId") Long storeId, @Param("offset") int offset, @Param("limit") int limit);
} 