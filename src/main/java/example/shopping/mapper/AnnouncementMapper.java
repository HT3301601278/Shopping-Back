package example.shopping.mapper;

import example.shopping.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 公告Mapper接口
 */
@Mapper
public interface AnnouncementMapper {
    
    /**
     * 查询所有公告
     * @return 公告列表
     */
    @Select("SELECT * FROM announcements ORDER BY create_time DESC")
    List<Announcement> findAll();
    
    /**
     * 查询显示中的公告
     * @return 公告列表
     */
    @Select("SELECT * FROM announcements WHERE status = 1 ORDER BY create_time DESC")
    List<Announcement> findVisible();
    
    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告信息
     */
    @Select("SELECT * FROM announcements WHERE id = #{id}")
    Announcement findById(Long id);
    
    /**
     * 分页查询公告
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 公告列表
     */
    @Select("SELECT * FROM announcements ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Announcement> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 插入公告
     * @param announcement 公告信息
     * @return 影响行数
     */
    @Insert("INSERT INTO announcements(title, content, publisher_id, status, read_users, create_time, update_time) " +
            "VALUES(#{title}, #{content}, #{publisherId}, #{status}, #{readUsers}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);
    
    /**
     * 更新公告
     * @param announcement 公告信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE announcements " +
            "<set>" +
            "<if test='title != null'>title = #{title},</if>" +
            "<if test='content != null'>content = #{content},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='readUsers != null'>read_users = #{readUsers},</if>" +
            "update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Announcement announcement);
    
    /**
     * 删除公告
     * @param id 公告ID
     * @return 影响行数
     */
    @Delete("DELETE FROM announcements WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 更新公告状态
     * @param id 公告ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE announcements SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 更新公告已读用户
     * @param id 公告ID
     * @param readUsers 已读用户JSON
     * @return 影响行数
     */
    @Update("UPDATE announcements SET read_users = #{readUsers}, update_time = NOW() WHERE id = #{id}")
    int updateReadUsers(@Param("id") Long id, @Param("readUsers") String readUsers);
} 