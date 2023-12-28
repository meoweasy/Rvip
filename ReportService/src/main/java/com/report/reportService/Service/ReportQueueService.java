package com.report.reportService.Service;

import com.report.reportService.Model.Provider;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class ReportQueueService {
    private final AmqpTemplate amqpTemplate;

    public ReportQueueService(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void sendReportToQueue(MultipartFile file, String correlationId) {
        try {
            if (file != null && !file.isEmpty() && file.getBytes().length > 0) {
                // Логирование информации о файле
                byte[] fileContent = file.getBytes();
                System.out.println("Имя файла: " + file.getOriginalFilename());
                System.out.println("Тип контента: " + file.getContentType());
                System.out.println("Размер файла: " + fileContent.length + " байт");

                // Отправка содержимого файла в очередь
                amqpTemplate.convertAndSend("reportQueue", file.getBytes());
                System.out.println("Файл отправлен в очередь");
            } else {
                System.out.println("Файл пустой или не существует");
            }
        } catch (IOException ex) {
            System.out.println("Ошибка при чтении файла: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Произошла ошибка: " + ex.getMessage());
        }
    }
}
