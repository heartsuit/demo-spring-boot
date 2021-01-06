package com.heartsuit;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@SpringBootTest
class SpringbootMinioApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testUploadPlay() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("https://play.min.io", "Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");

            // Check if the bucket already exists.
            boolean isExist =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("asiatrip").build());
            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // Make a new bucket called asiatrip to hold a zip file of photos.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("asiatrip").build());
            }

            // Upload the zip file to the bucket with putObject
            minioClient.putObject("asiatrip", "asiaphotos.zip", "/home/user/Photos/asiaphotos.zip", null);
            System.out.println("/home/user/Photos/asiaphotos.zip is successfully uploaded as asiaphotos.zip to `asiatrip` bucket.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testUploadCustom() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            // Check if the bucket already exists.
            boolean isExist =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("heartsuit").build());
            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // Make a new bucket called asiatrip to hold a zip file of photos.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("heartsuit").build());
            }

            // Upload the zip file to the bucket with putObject
//            minioClient.putObject("heartsuit", "README.md", "F:\\Java\\IdeaProjects\\demo-spring-boot\\README.md", null);
            minioClient.putObject("heartsuit", "config.json", "F:\\config.json", null);
            System.out.println("F:\\Java\\IdeaProjects\\demo-spring-boot\\README.md is successfully uploaded as README.md to `heartsuit` bucket.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
