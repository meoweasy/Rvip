package com.example.fileService.rest;

import com.example.fileService.minio.MinioAdapter;
import io.minio.errors.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MinioStorageController {
    private final MinioAdapter minioAdapter;

    public MinioStorageController(MinioAdapter minioAdapter){
        this.minioAdapter = minioAdapter;
    }

    @GetMapping("/addBucket")
    public ResponseEntity add() {
        try {
            if (minioAdapter.getAllBackets().stream().map(e -> e.name()).toList().isEmpty()) {
                minioAdapter.createBucket("provider1");
                System.out.println("Бакет создан");
                return ResponseEntity.ok().build();
            } else {
                System.out.println("Бакет уже существует");
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка создания бакета");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buckets")
    public List<String> listBuckets(){
        System.out.println("buckets");
        return minioAdapter.getAllBackets().stream().map(e->e.name()).toList();
    }

    @PostMapping(path="/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String upload(@RequestPart(value = "file", required = false) MultipartFile files) throws IOException{
        if (minioAdapter.uploadFile("provider1", files.getOriginalFilename(), files.getBytes())) {
            Map<String, String> result = new HashMap<>();
            result.put("key", files.getOriginalFilename());
            System.out.println("Файл сохранен в облаке");
            return result.get("key");
        } else{
            System.out.println("Файл НЕ сохранен в облаке");
            return "Файл уже существует";
        }
    }

    @GetMapping(path = "/download")
    public ResponseEntity<?> uploadFile(@RequestPart(value = "file") String file) throws IOException{
        try{
            byte[] data = minioAdapter.getFile("provider1", file);

            ByteArrayResource resource = new ByteArrayResource(data);
            System.out.println("Файл сохранен в облаке");

            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "applocation/octat-stream")
                    .header("Content-disposition", "attachment; filename=\"" + file + "\"")
                    .body(resource);

        }catch (Exception ex){
            System.out.println("Файл НЕ сохранен в облаке");
            return ResponseEntity
                    .ok("no file");
        }
    }
}
