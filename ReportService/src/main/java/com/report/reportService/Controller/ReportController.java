package com.report.reportService.Controller;

import com.report.reportService.Model.Provider;
import com.report.reportService.Service.ReportQueueService;
import com.report.reportService.Service.ReportService;
import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.env.Environment;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class ReportController {
    private final ReportService reportService;
    private final ReportQueueService reportQueueService;
    private final String myminio;
    private  final String port;
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    public ReportController(ReportService reportService, Environment environment, ReportQueueService reportQueueService) {
        this.reportService = reportService;
        this.myminio = environment.getProperty("my.myminio");
        this.port = environment.getProperty("my.port");
        this.reportQueueService = reportQueueService;
    }

    @GetMapping("/report_providers")
    public String getActiveProviders(@RequestHeader("correlation-id") String corId, Model model) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("correlation-id", String.format("corrid %s # %s %s ", corId, "report", "getActiveProviders"));
        String correlationId = headers.getFirst("correlation-id");
        log.info(Objects.requireNonNull(correlationId));
        model.addAttribute("activeProviders", reportService.getProvidersByStatus(true));
        model.addAttribute("inactiveProviders", reportService.getProvidersByStatus(false));

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(byteArrayOutputStream), CSVFormat.DEFAULT);
            csvPrinter.printRecord("Active Providers");
            List<Provider> providers = reportService.getProvidersByStatus(true);
            for (Provider provider : providers) {
                csvPrinter.printRecord(provider.getName(), provider.getSurname());
            }
            csvPrinter.println();
            csvPrinter.printRecord("Inactive Providers");
            List<Provider> providers2 = reportService.getProvidersByStatus(false);
            for (Provider provider : providers2) {
                csvPrinter.printRecord(provider.getName(), provider.getSurname());
            }
            csvPrinter.close();

            byte[] bytes = byteArrayOutputStream.toByteArray();

            if (bytes.length > 0) {
                MultipartFile multipartFile = new ByteArrayMultipartFile("file", bytes);

                reportQueueService.sendReportToQueue(multipartFile, corId);
                //saveReport(multipartFile, corId);
            } else {
                System.out.println("Файл пустой");
            }
        } catch (IOException ex) {
            log.error("Ошибка при создании файла CSV: {}", ex.getMessage());
            System.out.println("Не удалось создать файл");
        }

        return "report_providers";
    }

    public void saveReport(MultipartFile file, String corId) {
        try {
            if (file != null && !file.isEmpty() && file.getBytes().length > 0) {
                HttpHeaders headers = new HttpHeaders();
                headers.set("correlation-id", String.format("corrid %s # %s %s ", corId, "report", "saveReport"));
                RestTemplate restTemplate = new RestTemplate();

                // Логирование информации о файле
                byte[] fileContent = file.getBytes();
                System.out.println("Имя файла: " + file.getOriginalFilename());
                System.out.println("Тип контента: " + file.getContentType());
                System.out.println("Размер файла: " + fileContent.length + " байт");

                ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                };


                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", resource);

                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                String serverUrl = "http://myminio:8081/upload";
                restTemplate.postForObject(serverUrl, requestEntity, String.class);
                System.out.println("Файл отправлен");
            } else {
                System.out.println("Файл пустой или не существует");
            }
        } catch (IOException ex) {
            System.out.println("Ошибка при чтении файла: " + ex.getMessage());
        } catch (HttpClientErrorException ex) {
            System.out.println("Ошибка HTTP клиента: " + ex.getMessage());
        } catch (HttpServerErrorException ex) {
            System.out.println("Ошибка HTTP сервера: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Произошла ошибка: " + ex.getMessage());
        }
    }

    public class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String originalFilename;
        private final String contentType;

        public ByteArrayMultipartFile(String name, byte[] content) {
            this.name = name;
            this.originalFilename = name;
            this.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            this.content = content;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            new FileOutputStream(dest).write(content);
        }
    }
}
