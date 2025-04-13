package example.shopping.controller;

import example.shopping.entity.Category;
import example.shopping.service.CategoryService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品分类控制器
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.findAll());
    }

    /**
     * 获取树形结构的分类
     *
     * @return 树形结构的分类数据
     */
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        return Result.success(categoryService.getTree());
    }

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        return Result.success(categoryService.findById(id));
    }

    /**
     * 获取根分类
     *
     * @return 根分类列表
     */
    @GetMapping("/roots")
    public Result<List<Category>> getRootCategories() {
        return Result.success(categoryService.findRootCategories());
    }

    /**
     * 获取子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @GetMapping("/children/{parentId}")
    public Result<List<Category>> getChildrenCategories(@PathVariable Long parentId) {
        return Result.success(categoryService.findByParentId(parentId));
    }

    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 添加的分类
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Category> addCategory(@Valid @RequestBody Category category) {
        return Result.success(categoryService.add(category), "添加分类成功");
    }

    /**
     * 更新分类
     *
     * @param id       分类ID
     * @param category 分类信息
     * @return 更新后的分类
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        category.setId(id);
        return Result.success(categoryService.update(category), "更新分类成功");
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        return Result.success(categoryService.delete(id), "删除分类成功");
    }
}
