package com.heartsuit.controller;

import com.heartsuit.config.MinIOConfig;
import com.heartsuit.result.Result;
import com.heartsuit.service.MinIOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RequestMapping("/minio")
@RestController
@Slf4j
public class MinIOController {
    @Autowired
    private MinIOService minIOService;

    @Autowired
    private MinIOConfig minioConfig;

    @PostMapping("/uploadFile")
    public Result<String> uploadFile(MultipartFile file, String bucketName) {
        try {
            bucketName = !StringUtils.isEmpty(bucketName) ? bucketName : minioConfig.getBucketName();
            if (!minIOService.bucketExists(bucketName)) {
                minIOService.makeBucket(bucketName);
            }
            String fileName = file.getOriginalFilename();
            assert fileName != null;

            // 根据业务设计，设置存储路径：按天创建目录
            String objectName = new SimpleDateFormat("yyyy-MM-dd/").format(new Date())
                    + UUID.randomUUID().toString()
                    + fileName.substring(fileName.lastIndexOf("."));

            minIOService.putObject(bucketName, file, objectName);
            log.info("文件格式为:{}", file.getContentType());
            log.info("文件原名称为:{}", fileName);
            log.info("文件存储的桶为:{}", bucketName);
            log.info("文件对象路径为:{}", objectName);
            return Result.success(minIOService.getObjectUrl(bucketName, objectName));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }

    @PostMapping("/deleteFile")
    public Result<String> deleteFile(String bucketName, String filePath) throws Exception {
        if (!minIOService.bucketExists(bucketName)) {
            throw new Exception("不存在该桶");
        }
        boolean status = minIOService.removeObject(bucketName, filePath);
        return status ? Result.success("删除成功") : Result.success("删除失败");
    }

    @PostMapping("/downloadFile")
    public Result<String> downloadFile(String bucketName, String filePath, String originalName, HttpServletResponse response) throws Exception {
        if (!minIOService.bucketExists(bucketName)) {
            throw new Exception("不存在该桶");
        }
        boolean status = minIOService.downloadFile(bucketName, filePath, originalName, response);
        return status ? Result.success("下载成功") : Result.success("下载失败");
    }
}
