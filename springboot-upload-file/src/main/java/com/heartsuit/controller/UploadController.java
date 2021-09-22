package com.heartsuit.controller;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Author Heartsuit
 * @Date 2021-09-22
 */
@RestController
public class UploadController {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final String uploadDir = "/upload/";

    @PostMapping("upload")
    public String upload(MultipartFile uploadFile, HttpServletRequest req) throws FileNotFoundException {
        if(null == uploadFile){
            return "啥也没有";
        }
        // 上传至静态资源目录
        String path = ResourceUtils.getURL("classpath:static").getPath() + uploadDir;
        String date = simpleDateFormat.format(new Date());

        File folder = new File(path + date);
        if(!folder.isDirectory()){
            folder.mkdirs();
        }

        String originalName = uploadFile.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + originalName.substring(originalName.lastIndexOf("."));
        try {
            uploadFile.transferTo(new File(folder, newName));
            return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + uploadDir + date + "/" + newName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "上传失败！";
    }

    @PostMapping("uploads")
    public String uploads(MultipartFile[] uploadFiles, HttpServletRequest req) throws FileNotFoundException {
        // ...
        return "上传失败！";
    }
}
