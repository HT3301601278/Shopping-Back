package example.shopping.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    
    /**
     * 存储文件
     * @param file 上传的文件
     * @param directory 存储目录（例如：products, avatars, licenses等）
     * @return 文件访问URL
     */
    String store(MultipartFile file, String directory);
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean delete(String fileUrl);
    
    /**
     * 获取文件的完整URL
     * @param filename 文件名
     * @param directory 目录名
     * @return 完整的文件URL
     */
    String getFileUrl(String filename, String directory);
} 