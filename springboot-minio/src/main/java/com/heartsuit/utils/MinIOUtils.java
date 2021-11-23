package com.heartsuit.utils;

import io.minio.*;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2021-11-23
 */
@Component
public class MinIOUtils {

    @Autowired
    private MinioClient minIOClient;

    private static final int DEFAULT_EXPIRY_TIME = 7 * 24 * 3600;

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        BucketExistsArgs args = BucketExistsArgs.builder().bucket(bucketName).build();
        return minIOClient.bucketExists(args);
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     */
    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            MakeBucketArgs args = MakeBucketArgs.builder().bucket(bucketName).build();
            minIOClient.makeBucket(args);
        }
    }

    /**
     * 列出所有存储桶名称
     *
     * @return
     */
    @SneakyThrows
    public List<String> listBucketNames() {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 列出所有存储桶
     *
     * @return
     */
    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minIOClient.listBuckets();
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public boolean removeBucket(String bucketName) {
        if (bucketExists(bucketName)) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 有对象文件，则删除失败
                if (item.size() > 0) {
                    return false;
                }
            }
            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
            RemoveBucketArgs args = RemoveBucketArgs.builder().bucket(bucketName).build();
            minIOClient.removeBucket(args);
            return !bucketExists(bucketName);

        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象名称
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public List<String> listObjectNames(String bucketName) {
        List<String> listObjectNames = new ArrayList<>();
        if (bucketExists(bucketName)) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        }
        return listObjectNames;
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {
        if (bucketExists(bucketName)) {
            ListObjectsArgs args = ListObjectsArgs.builder().bucket(bucketName).build();
            return minIOClient.listObjects(args);
        }
        return null;
    }

    /**
     * 通过文件上传到对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param fileName   File name
     * @return
     */
    @SneakyThrows
    public boolean putObject(String bucketName, String objectName, String fileName) {
        if (bucketExists(bucketName)) {
            UploadObjectArgs args = UploadObjectArgs.builder().bucket(bucketName).object(objectName).filename(fileName).build();
            minIOClient.uploadObject(args);
            ObjectStat statObject = statObject(bucketName, objectName);
            return statObject != null && statObject.length() > 0;
        }
        return false;
    }

    /**
     * 文件上传
     *
     * @param bucketName
     * @param multipartFile
     */
    @SneakyThrows
    public void putObject(String bucketName, MultipartFile multipartFile, String filename) {
//        PutObjectOptions putObjectOptions = new PutObjectOptions(multipartFile.getSize(), PutObjectOptions.MIN_MULTIPART_SIZE);
//        putObjectOptions.setContentType(Objects.requireNonNull(multipartFile.getContentType()));
//        minioClient.putObject(bucketName, filename, multipartFile.getInputStream(), putObjectOptions);
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(filename)
                .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                .contentType(multipartFile.getContentType())
                .build();
        minIOClient.putObject(args);
    }

    /**
     * 通过InputStream上传对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param stream     要上传的流
     * @param stream     要上传的文件类型 MimeTypeUtils.IMAGE_JPEG_VALUE
     * @return
     */
    @SneakyThrows
    public boolean putObject(String bucketName, String objectName, InputStream stream, String contentType) {
        if (bucketExists(bucketName)) {
            PutObjectOptions putObjectOptions = new PutObjectOptions(stream.available(), -1);
            /**
             * 开启公共类功能设置setContentType
             */
            if (!StringUtils.isEmpty(contentType)) {
                putObjectOptions.setContentType(contentType);
            }
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(stream, putObjectOptions.objectSize(), putObjectOptions.partSize())
                    .build();
            minIOClient.putObject(args);
            ObjectStat statObject = statObject(bucketName, objectName);
            return statObject != null && statObject.length() > 0;
        }
        return false;
    }

    /**
     * 以流的形式获取一个文件对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {
            ObjectStat statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.length() > 0) {
                GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName).object(objectName).build();
                return minIOClient.getObject(args);
            }
        }
        return null;
    }

    /**
     * 以流的形式获取一个文件对象（断点下载）
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度 (可选，如果无值则代表读到文件结尾)
     * @return
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName, long offset, Long length) {
        if (bucketExists(bucketName)) {
            ObjectStat statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.length() > 0) {
                GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName).object(objectName).offset(offset).length(length).build();
                return minIOClient.getObject(args);
            }
        }
        return null;
    }

    /**
     * 下载并将文件保存到本地
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param fileName   File name
     * @return
     */
    @SneakyThrows
    public boolean getObject(String bucketName, String objectName, String fileName) {
        if (bucketExists(bucketName)) {
            ObjectStat statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.length() > 0) {
                DownloadObjectArgs args = DownloadObjectArgs.builder().bucket(bucketName).object(objectName).filename(fileName).build();
                minIOClient.downloadObject(args);
                return true;
            }
        }
        return false;
    }

    /**
     * 删除一个对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     */
    @SneakyThrows
    public boolean removeObject(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {
            RemoveObjectArgs args = RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build();
            minIOClient.removeObject(args);
            return true;
        }
        return false;
    }

    /**
     * 删除指定桶的多个文件对象,返回删除错误的对象列表，全部删除成功，返回空列表
     *
     * @param bucketName  存储桶名称
     * @param objectNames 含有要删除的多个object名称的迭代器对象
     * @return
     */
    @SneakyThrows
    public List<String> removeObject(String bucketName, List<String> objectNames) {
        List<String> deleteErrorNames = new ArrayList<>();
        if (bucketExists(bucketName)) {
            Iterable<Result<DeleteError>> results = minIOClient.removeObjects(bucketName, objectNames);
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                deleteErrorNames.add(error.objectName());
            }
        }
        return deleteErrorNames;
    }

    /**
     * 生成一个给HTTP GET请求用的presigned URL。
     * 浏览器/移动端的客户端可以用这个URL进行下载，即使其所在的存储桶是私有的。这个presigned URL可以设置一个失效时间，默认值是7天。
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param expires    失效时间（以秒为单位），默认是7天，不得大于七天
     * @return
     */
    @SneakyThrows
    public String presignedGetObject(String bucketName, String objectName, Integer expires) {
        String url = "";
        if (bucketExists(bucketName)) {
            if (expires < 1 || expires > DEFAULT_EXPIRY_TIME) {
                throw new InvalidExpiresRangeException(expires,
                        "expires must be in range of 1 to " + DEFAULT_EXPIRY_TIME);
            }
            url = minIOClient.presignedGetObject(bucketName, objectName, expires);
        }
        return url;
    }

    /**
     * 生成一个给HTTP PUT请求用的presigned URL。
     * 浏览器/移动端的客户端可以用这个URL进行上传，即使其所在的存储桶是私有的。这个presigned URL可以设置一个失效时间，默认值是7天。
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param expires    失效时间（以秒为单位），默认是7天，不得大于七天
     * @return
     */
    @SneakyThrows
    public String presignedPutObject(String bucketName, String objectName, Integer expires) {
        String url = "";
        if (bucketExists(bucketName)) {
            if (expires < 1 || expires > DEFAULT_EXPIRY_TIME) {
                throw new InvalidExpiresRangeException(expires,
                        "expires must be in range of 1 to " + DEFAULT_EXPIRY_TIME);
            }
            url = minIOClient.presignedPutObject(bucketName, objectName, expires);
        }
        return url;
    }

    /**
     * 获取对象的元数据
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    @SneakyThrows
    public ObjectStat statObject(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {
            return minIOClient.statObject(bucketName, objectName);
        }
        return null;
    }

    /**
     * 文件访问路径
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    @SneakyThrows
    public String getObjectUrl(String bucketName, String objectName) {
        String url = "";
        if (bucketExists(bucketName)) {
            url = minIOClient.getObjectUrl(bucketName, objectName);
        }
        return url;
    }

    public boolean downloadFile(String bucketName, String filePath, String originalName, HttpServletResponse response) {
        boolean status = false;
        try {
            InputStream file = minIOClient.getObject(bucketName, filePath);
            if (!StringUtils.isEmpty(originalName)) {
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(originalName, "UTF-8"));
            } else {
                String filename = new String(filePath.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            }
            ServletOutputStream servletOutputStream = response.getOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = file.read(buffer)) > 0) {
                servletOutputStream.write(buffer, 0, len);
            }
            servletOutputStream.flush();
            file.close();
            servletOutputStream.close();
            status = true;
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            return status;
        }
    }
}
