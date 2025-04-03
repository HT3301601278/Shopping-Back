package example.shopping.service.impl;

import example.shopping.entity.Category;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.CategoryMapper;
import example.shopping.mapper.ProductMapper;
import example.shopping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }

    @Override
    public List<Category> findRootCategories() {
        return categoryMapper.findRootCategories();
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return categoryMapper.findByParentId(parentId);
    }

    @Override
    @Transactional
    public Category add(Category category) {
        // 检查同级分类下是否有相同名称
        if (category.getParentId() != null) {
            Category existingCategory = categoryMapper.findByName(category.getName());
            if (existingCategory != null) {
                throw new BusinessException("该分类名称已存在");
            }
        }
        
        // 设置分类层级
        if (category.getParentId() == null || category.getParentId() == 0) {
            category.setLevel(1);
        } else {
            Category parentCategory = categoryMapper.findById(category.getParentId());
            if (parentCategory == null) {
                throw new BusinessException("父分类不存在");
            }
            category.setLevel(parentCategory.getLevel() + 1);
        }
        
        // 如果没有设置排序值，默认放到最后
        if (category.getSortOrder() == null) {
            category.setSortOrder(categoryMapper.count() + 1);
        }
        
        // 默认启用状态
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public Category update(Category category) {
        Category existingCategory = categoryMapper.findById(category.getId());
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查名称是否重复
        if (category.getName() != null && !category.getName().equals(existingCategory.getName())) {
            Category sameNameCategory = categoryMapper.findByName(category.getName());
            if (sameNameCategory != null && !sameNameCategory.getId().equals(category.getId())) {
                throw new BusinessException("该分类名称已存在");
            }
        }
        
        // 更新层级
        if (category.getParentId() != null && !category.getParentId().equals(existingCategory.getParentId())) {
            if (category.getParentId() == 0) {
                category.setLevel(1);
            } else {
                Category parentCategory = categoryMapper.findById(category.getParentId());
                if (parentCategory == null) {
                    throw new BusinessException("父分类不存在");
                }
                category.setLevel(parentCategory.getLevel() + 1);
            }
        }
        
        categoryMapper.update(category);
        return categoryMapper.findById(category.getId());
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查是否有子分类
        List<Category> children = categoryMapper.findByParentId(id);
        if (children != null && !children.isEmpty()) {
            throw new BusinessException("请先删除子分类");
        }
        
        // 检查是否有关联的商品
        if (productMapper.countByCategoryId(id) > 0) {
            throw new BusinessException("该分类下存在商品，无法删除");
        }
        
        return categoryMapper.deleteById(id) > 0;
    }

    @Override
    public List<Category> getTree() {
        // 获取所有分类
        List<Category> allCategories = categoryMapper.findAll();
        
        // 按父ID分组
        Map<Long, List<Category>> parentIdMap = allCategories.stream()
                .collect(Collectors.groupingBy(category -> 
                    category.getParentId() == null ? 0L : category.getParentId()));
        
        // 获取根分类
        List<Category> rootCategories = parentIdMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归构建子分类
        rootCategories.forEach(root -> buildChildren(root, parentIdMap));
        
        return rootCategories;
    }
    
    /**
     * 递归构建子分类
     * @param parent 父分类
     * @param parentIdMap 按父ID分组的分类Map
     */
    private void buildChildren(Category parent, Map<Long, List<Category>> parentIdMap) {
        List<Category> children = parentIdMap.get(parent.getId());
        if (children != null) {
            //parent.setChildren(children);
            children.forEach(child -> buildChildren(child, parentIdMap));
        }
    }
} 