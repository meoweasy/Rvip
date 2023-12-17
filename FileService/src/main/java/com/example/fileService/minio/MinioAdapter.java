package com.example.fileService.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
     public boolean uploadFile(String bucket, String name, byte[] content) {
        File file = new File(name);
        file.canWrite();
        file.canRead();
        try{
            FileOutputStream iofs = new FileOutputStream(file);
            iofs.write(content);

            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(name)
                    .stream(new FileInputStream(name), file.length(), -1)
                    .build());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
        return true;
    }

    public byte[] getFile(String bucket, String key) throws Exception{
        System.out.println("tryam");

        try{
            InputStream stream =
                    minioClient.getObject(GetObjectArgs
                            .builder()
                            .bucket(bucket)
                            .object(key)
                            .build());
            byte[] content = IOUtils.toByteArray(stream);
            return content;

        }catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
}
