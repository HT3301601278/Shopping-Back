package example.shopping.service;

import example.shopping.entity.Category;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface CategoryService {

    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    List<Category> findAll();

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    Category findById(Long id);

    /**
     * 查询根分类
     *
     * @return 根分类列表
     */
    List<Category> findRootCategories();

    /**
     * 根据父ID查询子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 添加后的分类
     */
    Category add(Category category);

    /**
     * 更新分类
     *
     * @param category 分类信息
     * @return 更新后的分类
     */
    Category update(Category category);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 获取树形结构的分类数据
     *
     * @return 树形结构的分类数据
     */
    List<Category> getTree();
}
