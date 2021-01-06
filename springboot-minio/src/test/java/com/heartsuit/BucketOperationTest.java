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

/**
 * @Author Heartsuit
 * @Date 2020-11-03
 */
@SpringBootTest
public class BucketOperationTest {
    @Test
    public void testBucketCreate() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

//            // Create bucket 'my-bucketname' if it doesn`t exist.
//            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket("my-bucketname").build())) {
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket("my-bucketname").build());
//                System.out.println("my-bucketname is created successfully");
//            }
//
//            // Create bucket 'my-bucketname-in-eu' in 'eu-west-1' region if it doesn't exist.
//            if (!minioClient.bucketExists(
//                    BucketExistsArgs.builder().bucket("my-bucketname-in-eu").build())) {
//                minioClient.makeBucket(
//                        MakeBucketArgs.builder().bucket("my-bucketname-in-eu").region("eu-west-1").build());
//                System.out.println("my-bucketname-in-eu is created successfully");
//            }

            // Create bucket 'my-bucketname-in-eu-with-object-lock' in 'eu-west-1' with object lock functionality enabled.
            if (!minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket("my-bucketname-in-eu-with-object-lock").build())) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket("my-bucketname-in-eu-with-object-lock")
                                .region("eu-west-1")
                                .objectLock(true)
                                .build());
                System.out.println("my-bucketname-in-eu-with-object-lock is created successfully");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testBucketExists() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            // Check if the bucket already exists.
            String bucketName = "my-bucketname";
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (found) {
                System.out.println(bucketName + " exists");
            } else {
                System.out.println(bucketName + " does not exist");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testBucketList() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            // List buckets we have at least read access.
            List<Bucket> bucketList = minioClient.listBuckets();
            for (Bucket bucket : bucketList) {
                System.out.println(bucket.creationDate() + ", " + bucket.name());
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testBucketListObject() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            String bucketName = "my-bucketname";
            {
                // Lists objects information.
                Iterable<Result<Item>> results =
                        minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());

                for (Result<Item> result : results) {
                    Item item = result.get();
                    System.out.println(item.lastModified() + "\t" + item.size() + "\t" + item.objectName());
                }
            }

            {
                // Lists objects information recursively.
                Iterable<Result<Item>> results =
                        minioClient.listObjects(
                                ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());

                for (Result<Item> result : results) {
                    Item item = result.get();
                    System.out.println(item.lastModified() + "\t" + item.size() + "\t" + item.objectName());
                }
            }

//            {
//                // Lists maximum 100 objects information those names starts with 'E' and after
//                // 'ExampleGuide.pdf'.
//                Iterable<Result<Item>> results =
//                        minioClient.listObjects(
//                                ListObjectsArgs.builder()
//                                        .bucket(bucketName)
//                                        .startAfter("ExampleGuide.pdf")
//                                        .prefix("E")
//                                        .maxKeys(100)
//                                        .build());
//
//                for (Result<Item> result : results) {
//                    Item item = result.get();
//                    System.out.println(item.lastModified() + "\t" + item.size() + "\t" + item.objectName());
//                }
//            }

            {
                // Lists maximum 100 objects information with version those names starts with 'E' and after
                // 'ExampleGuide.pdf'.
                Iterable<Result<Item>> results =
                        minioClient.listObjects(
                                ListObjectsArgs.builder()
                                        .bucket(bucketName)
                                        .startAfter("ExampleGuide.pdf")
                                        .prefix("E")
                                        .maxKeys(100)
                                        .includeVersions(true)
                                        .build());

                for (Result<Item> result : results) {
                    Item item = result.get();
                    System.out.println(
                            item.lastModified()
                                    + "\t"
                                    + item.size()
                                    + "\t"
                                    + item.objectName()
                                    + " ["
                                    + item.versionId()
                                    + "]");
                }
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testBucketRemove() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            // Remove bucket 'my-bucketname' if it exists. This operation will only work if your bucket is empty.
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("my-bucketname").build());
            if (found) {
                minioClient.removeBucket(RemoveBucketArgs.builder().bucket("my-bucketname").build());
                System.out.println("my-bucketname is removed successfully");
            } else {
                System.out.println("my-bucketname does not exist");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
