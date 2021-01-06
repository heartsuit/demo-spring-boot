package com.heartsuit;

import io.minio.GetBucketPolicyArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2020-11-06
 */
@SpringBootTest
public class BucketPolicyOperationTest {
    @Test
    public void testSetBucketPolicy() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            String bucketName = "heartsuit";

            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            builder.append("    \"Statement\": [\n");
            builder.append("        {\n");
            builder.append("            \"Action\": [\n");
            builder.append("                \"s3:GetBucketLocation\",\n");
            builder.append("                \"s3:ListBucket\"\n");
            builder.append("            ],\n");
            builder.append("            \"Effect\": \"Allow\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::heartsuit\"\n");
            builder.append("        },\n");
            builder.append("        {\n");
            builder.append("            \"Action\": \"s3:GetObject\",\n");
            builder.append("            \"Effect\": \"Allow\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::heartsuit/myobject*\"\n");
            builder.append("        }\n");
            builder.append("    ],\n");
            builder.append("    \"Version\": \"2012-10-17\"\n");
            builder.append("}\n");
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder().bucket(bucketName).config(builder.toString()).build());
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testGetBucketPolicy() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            String bucketName = "heartsuit";

            String policy = minioClient.getBucketPolicy(GetBucketPolicyArgs.builder().bucket(bucketName).build());
            System.out.println("Current policy: " + policy);
//            Current policy: {
//                "Version": "2012-10-17",
//                        "Statement": [{
//                    "Effect": "Allow",
//                            "Principal": {
//                        "AWS": ["*"]
//                    },
//                    "Action": ["s3:GetBucketLocation", "s3:ListBucket"],
//                    "Resource": ["arn:aws:s3:::heartsuit"]
//                }, {
//                    "Effect": "Allow",
//                            "Principal": {
//                        "AWS": ["*"]
//                    },
//                    "Action": ["s3:GetObject"],
//                    "Resource": ["arn:aws:s3:::heartsuit/*"]
//                }]
//            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
