package example.shopping.service.impl;

import example.shopping.exception.BusinessException;
import example.shopping.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${file.upload.url-prefix:http://localhost:8080/uploads}")
    private String urlPrefix;

    @Override
    public String store(MultipartFile file, String directory) {
        try {
            if (file.isEmpty()) {
                throw new BusinessException("文件为空");
            }

            // 获取文件名和扩展名
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // 生成新的文件名
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 创建目标目录
            Path targetDir = Paths.get(uploadDir, directory).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);
            
            // 保存文件
            Path targetPath = targetDir.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 返回文件URL
            return getFileUrl(newFilename, directory);
            
        } catch (IOException ex) {
            log.error("文件存储失败", ex);
            throw new BusinessException("文件存储失败：" + ex.getMessage());
        }
    }

    @Override
    public boolean delete(String fileUrl) {
        try {
            if (fileUrl == null || !fileUrl.startsWith(urlPrefix)) {
                return false;
            }

            // 从URL中提取文件路径
            String relativePath = fileUrl.substring(urlPrefix.length());
            Path filePath = Paths.get(uploadDir).resolve(relativePath.substring(1)).normalize();

            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.error("文件删除失败", ex);
            return false;
        }
    }

    @Override
    public String getFileUrl(String filename, String directory) {
        return urlPrefix + "/" + directory + "/" + filename;
    }
} 