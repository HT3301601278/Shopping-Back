package example.shopping.controller;

import example.shopping.service.FileStorageService;
import example.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "image/jpeg", "image/png"
    );

    /**
     * 上传商品图片
     */
    @PostMapping("/products")
    @PreAuthorize("hasRole('MERCHANT') or hasRole('ADMIN')")
    public Result<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        validateImageFile(file);
        String fileUrl = fileStorageService.store(file, "products");
        return Result.success(fileUrl, "图片上传成功");
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/avatars")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        validateImageFile(file);
        String fileUrl = fileStorageService.store(file, "avatars");
        return Result.success(fileUrl, "头像上传成功");
    }

    /**
     * 上传店铺logo
     */
    @PostMapping("/stores/logos")
    public Result<String> uploadStoreLogo(@RequestParam("file") MultipartFile file) {
        validateImageFile(file);
        String fileUrl = fileStorageService.store(file, "stores/logos");
        return Result.success(fileUrl, "店铺logo上传成功");
    }

    /**
     * 上传营业执照
     */
    @PostMapping("/stores/licenses")
    public Result<String> uploadStoreLicense(@RequestParam("file") MultipartFile file) {
        validateDocumentFile(file);
        String fileUrl = fileStorageService.store(file, "stores/licenses");
        return Result.success(fileUrl, "营业执照上传成功");
    }

    /**
     * 上传评价图片
     */
    @PostMapping("/reviews")
    public Result<String> uploadReviewImage(@RequestParam("file") MultipartFile file) {
        validateImageFile(file);
        String fileUrl = fileStorageService.store(file, "reviews");
        return Result.success(fileUrl, "评价图片上传成功");
    }

    /**
     * 上传客服消息文件
     */
    @PostMapping("/customer-service")
    @PreAuthorize("isAuthenticated()")
    public Result<String> uploadCustomerServiceFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.store(file, "customer-service");
        return Result.success(fileUrl, "文件上传成功");
    }

    /**
     * 删除文件
     */
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        boolean result = fileStorageService.delete(fileUrl);
        return Result.success(result, "文件删除成功");
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("不支持的图片格式，仅支持：" +
                    ALLOWED_IMAGE_TYPES.stream()
                            .map(type -> type.substring(type.lastIndexOf("/") + 1))
                            .collect(Collectors.joining(", ")));
        }
    }

    /**
     * 验证文档文件
     */
    private void validateDocumentFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        if (!ALLOWED_DOCUMENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("不支持的文件格式，仅支持：" +
                    ALLOWED_DOCUMENT_TYPES.stream()
                            .map(type -> type.substring(type.lastIndexOf("/") + 1))
                            .collect(Collectors.joining(", ")));
        }
    }
}
