package com.heartsuit;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2020-11-06
 */
@SpringBootTest
public class PresignedOperationTest {
    @Test
    public void testPresignedGetObject() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            String bucketName = "heartsuit";
            String objectName = "config.json";

            // Get presigned URL string to download 'objectName' in 'bucketName' and its life time is one day.
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(60 * 60 * 24)
                            .build());
            System.out.println(url);
//          http://hadoop1:9000/heartsuit/config.json?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIOSFODNN7EXAMPLE%2F20201106%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20201106T051555Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host&X-Amz-Signature=1ff2d9f7b10c7a829f5b5036768aacfdcf48121c4057693176132579a6918d05
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testPresignedPutObject() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            String bucketName = "heartsuit";
            String objectName = "config.json";

            // Get presigned URL string to upload 'my-objectname' in 'my-bucketname' with response-content-type as application/json and its life time is one day.
            Map<String, String> reqParams = new HashMap<String, String>();
            reqParams.put("response-content-type", "application/json");

            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(60 * 60 * 24)
                            .extraQueryParams(reqParams)
                            .build());
            System.out.println(url);
//            http://hadoop1:9000/heartsuit/config.json?response-content-type=application%2Fjson&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIOSFODNN7EXAMPLE%2F20201106%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20201106T052023Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host&X-Amz-Signature=36a1c4e34ad4baa0897fadbf10973fbe5913a7ef7b2246b14998f15a3e358f9e
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
