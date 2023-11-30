package com.report.reportService.Controller;

import com.report.reportService.Model.Provider;
import com.report.reportService.Service.ReportService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/report_providers")
    public String getActiveProviders(Model model) {
        model.addAttribute("activeProviders", reportService.getProvidersByStatus(true));
        model.addAttribute("inactiveProviders", reportService.getProvidersByStatus(false));
        return "report_providers";
    }
}
