package com.example.fileService.rest;

import com.example.fileService.minio.MinioAdapter;
import io.minio.errors.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                System.out.println("Received file: " + file.getOriginalFilename());
                minioAdapter.uploadFile("provider1", file.getOriginalFilename(), file.getBytes());

                return ResponseEntity.ok("Файл " + file.getOriginalFilename() + " успешно загружен");
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при загрузке файла");
            }
        } else {
            return ResponseEntity.badRequest().body("Файл не был загружен");
        }
    }

    @GetMapping(path = "/download")
    public ResponseEntity<?> downloadFile(@RequestParam("filename") String filename) {
        try {
            byte[] data = minioAdapter.getFile("provider1", filename);

            if (data.length > 0) {
                ByteArrayResource resource = new ByteArrayResource(data);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=" + filename);

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(data.length)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при скачивании файла");
        }
    }
}
