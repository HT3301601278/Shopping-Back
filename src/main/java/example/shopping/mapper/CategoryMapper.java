package example.shopping.mapper;

import example.shopping.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品分类Mapper接口
 */
@Mapper
public interface CategoryMapper {

    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    @Select("SELECT * FROM categories ORDER BY sort_order")
    List<Category> findAll();

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    @Select("SELECT * FROM categories WHERE id = #{id}")
    Category findById(Long id);

    /**
     * 查询根分类（父ID为0或null的分类）
     *
     * @return 根分类列表
     */
    @Select("SELECT * FROM categories WHERE parent_id IS NULL OR parent_id = 0 ORDER BY sort_order")
    List<Category> findRootCategories();

    /**
     * 根据父ID查询子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} ORDER BY sort_order")
    List<Category> findByParentId(Long parentId);

    /**
     * 插入分类
     *
     * @param category 分类信息
     * @return 影响行数
     */
    @Insert("INSERT INTO categories(name, parent_id, level, status, sort_order) " +
            "VALUES(#{name}, #{parentId}, #{level}, #{status}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    /**
     * 更新分类
     *
     * @param category 分类信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE categories " +
            "<set>" +
            "<if test='name != null'>name = #{name},</if>" +
            "<if test='parentId != null'>parent_id = #{parentId},</if>" +
            "<if test='level != null'>level = #{level},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='sortOrder != null'>sort_order = #{sortOrder},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Category category);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    @Delete("DELETE FROM categories WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 查询分类数量
     *
     * @return 分类数量
     */
    @Select("SELECT COUNT(*) FROM categories")
    int count();

    /**
     * 根据分类名称查询分类
     *
     * @param name 分类名称
     * @return 分类信息
     */
    @Select("SELECT * FROM categories WHERE name = #{name} LIMIT 1")
    Category findByName(String name);
}
