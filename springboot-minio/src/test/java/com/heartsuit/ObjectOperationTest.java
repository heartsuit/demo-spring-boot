package com.heartsuit;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2020-11-03
 */
@SpringBootTest
public class ObjectOperationTest {
    @Test
    public void testObjectDownload() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            // Get input stream to have content of 'heartsuit' from 'README.md'
            String bucketName = "heartsuit";
            String objectName = "README.md";

            InputStream stream =
                    minioClient.getObject(
                            GetObjectArgs.builder().bucket(bucketName).object(objectName).build());

            // Read the input stream and print to the console till EOF.
            byte[] buf = new byte[16384];
            int bytesRead;
            while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
                System.out.println(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));
            }

            // Close the input stream.
            stream.close();
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testObjectDownloadPartial() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            // Get input stream to have content of 'heartsuit' from 'README.md'
            String bucketName = "heartsuit";
            String objectName = "README.md";

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .offset(10L)
                            .length(20L)
                            .build());

            // Read the input stream and print to the console till EOF.
            byte[] buf = new byte[16384];
            int bytesRead;
            while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
                System.out.println(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));
            }

            // Close the input stream.
            stream.close();
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testObjectRemove() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            String bucketName = "heartsuit";
            String objectName = "config.json";

            // Remove object.
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());

            // Remove versioned object.
//            minioClient.removeObject(
//                    RemoveObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object("my-versioned-objectname")
//                            .versionId("my-versionid")
//                            .build());

            // Remove versioned object bypassing Governance mode.
//            minioClient.removeObject(
//                    RemoveObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object("my-versioned-objectname")
//                            .versionId("my-versionid")
//                            .bypassGovernanceMode(true)
//                            .build());
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testObjectRemoveList() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            String bucketName = "heartsuit";

            // Remove objects.
            List<DeleteObject> objects = new LinkedList<>();
            objects.add(new DeleteObject("words.txt"));
            objects.add(new DeleteObject("config.json"));

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                System.out.println(
                        "Error in deleting object " + error.objectName() + "; " + error.message());
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void testObjectStat() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("http://hadoop1:9000", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

            String bucketName = "heartsuit";
//            String objectName = "README.md";
            String objectName = "config.json";

            {
                // Get information of an object.
                ObjectStat stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
                System.out.println(stat);
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
