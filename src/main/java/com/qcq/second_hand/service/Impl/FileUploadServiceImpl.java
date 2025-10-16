package com.qcq.second_hand.service.Impl;

import com.qcq.second_hand.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadServiceImpl extends FileUploadService {

    @Value("${app.upload.path:D:/Project/Fosu-secondHand/uploads}")
    private String uploadPath;

    public String uploadFile(MultipartFile file) throws IOException {
        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = System.currentTimeMillis() + "_" +
                UUID.randomUUID().toString().substring(0, 8) + extension;

        // 保存文件
        Path filePath = Paths.get(uploadPath, filename);
        Files.write(filePath, file.getBytes());

        // 返回访问URL
        return "/uploads/" + filename;
    }
}
