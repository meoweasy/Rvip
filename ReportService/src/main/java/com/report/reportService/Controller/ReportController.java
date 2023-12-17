package com.report.reportService.Controller;

import com.report.reportService.Model.Provider;
import com.report.reportService.Service.ReportService;
import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
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


import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class ReportController {
    private final ReportService reportService;
    private final String myminio;
    private  final String port;
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    public ReportController(ReportService reportService, Environment environment) {
        this.reportService = reportService;
        this.myminio = environment.getProperty("my.myminio");
        this.port = environment.getProperty("my.port");
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
            csvPrinter.printRecords(reportService.getProvidersByStatus(true));
            csvPrinter.println();
            csvPrinter.printRecord("Inactive Providers");
            csvPrinter.printRecords(reportService.getProvidersByStatus(false));
            csvPrinter.close();

            // Получаем массив байтов
            byte[] bytes = byteArrayOutputStream.toByteArray();

            // После успешного создания данных в памяти, вызываем метод сохранения
            saveReport(new ByteArrayResource(bytes), corId);
        } catch (IOException ex){
            System.out.println("Не удалось создать файл");
        }

        return "report_providers";
    }

    public void saveReport(Resource resource, String corId) {
        try {
            if (resource.contentLength() > 0) {
                HttpHeaders headers = new HttpHeaders();
                headers.set("correlation-id", String.format("corrid %s # %s %s ", corId, "report", "saveReport"));
                String correlationId = headers.getFirst("correlation-id");
                RestTemplate restTemplate = new RestTemplate();

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

        } catch (HttpClientErrorException ex) {
            System.out.println("Ошибка HTTP клиента: " + ex.getMessage());
        } catch (HttpServerErrorException ex) {
            System.out.println("Ошибка HTTP сервера: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Произошла ошибка: " + ex.getMessage());
        }
    }



}
