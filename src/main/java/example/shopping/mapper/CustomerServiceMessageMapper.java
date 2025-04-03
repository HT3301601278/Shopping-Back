package example.shopping.mapper;

import example.shopping.entity.CustomerServiceMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 客服消息Mapper接口
 */
@Mapper
public interface CustomerServiceMessageMapper {
    
    /**
     * 根据会话ID查询消息
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Select("SELECT * FROM customer_service_messages WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<CustomerServiceMessage> findBySessionId(Long sessionId);
    
    /**
     * 根据ID查询消息
     * @param id 消息ID
     * @return 消息信息
     */
    @Select("SELECT * FROM customer_service_messages WHERE id = #{id}")
    CustomerServiceMessage findById(Long id);
    
    /**
     * 插入消息
     * @param message 消息信息
     * @return 影响行数
     */
    @Insert("INSERT INTO customer_service_messages(session_id, user_id, store_id, from_type, content, content_type, read_status, create_time) " +
            "VALUES(#{sessionId}, #{userId}, #{storeId}, #{fromType}, #{content}, #{contentType}, #{readStatus}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CustomerServiceMessage message);
    
    /**
     * 更新消息已读状态
     * @param id 消息ID
     * @param readStatus 已读状态
     * @return 影响行数
     */
    @Update("UPDATE customer_service_messages SET read_status = #{readStatus} WHERE id = #{id}")
    int updateReadStatus(@Param("id") Long id, @Param("readStatus") Boolean readStatus);
    
    /**
     * 更新会话中所有消息的已读状态
     * @param sessionId 会话ID
     * @param fromType 发送方类型
     * @param readStatus 已读状态
     * @return 影响行数
     */
    @Update("UPDATE customer_service_messages SET read_status = #{readStatus} WHERE session_id = #{sessionId} AND from_type = #{fromType}")
    int updateReadStatusBySessionIdAndFromType(@Param("sessionId") Long sessionId, @Param("fromType") Integer fromType, @Param("readStatus") Boolean readStatus);
    
    /**
     * 统计会话中未读消息数量
     * @param sessionId 会话ID
     * @param fromType 发送方类型
     * @return 未读消息数量
     */
    @Select("SELECT COUNT(*) FROM customer_service_messages WHERE session_id = #{sessionId} AND from_type = #{fromType} AND read_status = 0")
    int countUnreadBySessionIdAndFromType(@Param("sessionId") Long sessionId, @Param("fromType") Integer fromType);
    
    /**
     * 删除消息
     * @param id 消息ID
     * @return 影响行数
     */
    @Delete("DELETE FROM customer_service_messages WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 删除会话的所有消息
     * @param sessionId 会话ID
     * @return 影响行数
     */
    @Delete("DELETE FROM customer_service_messages WHERE session_id = #{sessionId}")
    int deleteBySessionId(Long sessionId);
} 