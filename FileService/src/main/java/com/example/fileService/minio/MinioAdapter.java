package com.example.fileService.minio;

import com.opencsv.CSVWriter;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MinioAdapter {
    private final MinioClient minioClient;
    public MinioAdapter(MinioClient minioClient){ this.minioClient = minioClient;}

    public List<Bucket> getAllBackets() {
        try{
            System.out.println("service backets");
            return minioClient.listBuckets();
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
     public void createBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, XmlParserException{
        try{
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        }catch (ServerException | ErrorResponseException | InternalException | InvalidKeyException | InvalidResponseException |
                IOException | NoSuchAlgorithmException | XmlParserException e) {
             e.printStackTrace();
         }
     }
    public boolean uploadFile(String bucket, String filename, byte[] data) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .stream(bis, data.length, -1)
                            .build()
            );

            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public byte[] getFile(String bucket, String key) throws Exception {
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(key)
                    .build());

            return IOUtils.toByteArray(stream);
        } catch (Exception ex) {
            ex.printStackTrace(); // Log the exception for debugging
            throw new RuntimeException(ex.getMessage());
        }
    }
}
