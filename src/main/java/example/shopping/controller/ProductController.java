package example.shopping.controller;

import example.shopping.entity.Product;
import example.shopping.service.ProductService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取所有商品
     *
     * @return 商品列表
     */
    @GetMapping
    public Result<List<Product>> getAllProducts() {
        return Result.success(productService.findAll());
    }

    /**
     * 分页获取商品
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 包含分页信息的商品列表
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> getProductsByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(productService.findByPage(pageNum, pageSize));
    }

    /**
     * 根据ID获取商品
     *
     * @param id 商品ID
     * @return 商品信息
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        return Result.success(productService.findById(id));
    }

    /**
     * 根据店铺ID获取商品
     *
     * @param storeId 店铺ID
     * @return 商品列表
     */
    @GetMapping("/store/{storeId}")
    public Result<List<Product>> getProductsByStoreId(@PathVariable Long storeId) {
        return Result.success(productService.findByStoreId(storeId));
    }

    /**
     * 根据分类ID获取商品
     *
     * @param categoryId 分类ID
     * @return 商品列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> getProductsByCategoryId(@PathVariable Long categoryId) {
        return Result.success(productService.findByCategoryId(categoryId));
    }

    /**
     * 搜索商品
     *
     * @param keyword 关键字
     * @return 商品列表
     */
    @GetMapping("/search")
    public Result<List<Product>> searchProducts(@RequestParam String keyword) {
        return Result.success(productService.search(keyword));
    }

    /**
     * 获取热门商品
     *
     * @param limit 数量限制
     * @return 热门商品列表
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(productService.findHotProducts(limit));
    }

    /**
     * 获取新品
     *
     * @param limit 数量限制
     * @return 新品列表
     */
    @GetMapping("/new")
    public Result<List<Product>> getNewProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(productService.findNewProducts(limit));
    }

    /**
     * 添加商品
     *
     * @param product 商品信息
     * @return 添加的商品
     */
    @PostMapping
    @PreAuthorize("hasRole('MERCHANT') or hasRole('ADMIN')")
    public Result<Product> addProduct(@Valid @RequestBody Product product) {
        return Result.success(productService.add(product), "添加商品成功");
    }

    /**
     * 更新商品
     *
     * @param id      商品ID
     * @param product 商品信息
     * @return 更新后的商品
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT') or hasRole('ADMIN')")
    public Result<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        product.setId(id);
        return Result.success(productService.update(product), "更新商品成功");
    }

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT') or hasRole('ADMIN')")
    public Result<Boolean> deleteProduct(@PathVariable Long id) {
        return Result.success(productService.delete(id), "删除商品成功");
    }
}
