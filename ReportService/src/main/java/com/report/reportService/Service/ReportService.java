package com.report.reportService.Service;

import com.report.reportService.Model.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.report.reportService.Repository.ProviderRepository;
import java.util.List;

@Service
public class ReportService {
    private final ProviderRepository providerRepository;
    @Autowired
    public ReportService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public List<Provider> getAllProviders() {
        List<Provider> providers = providerRepository.findAll();
        return providers;
    }

    public List<Provider> getProvidersByStatus(boolean status) {
        return providerRepository.findAllByStatus(status);
    }

    public String check(){
        return "TestOk";
    }
}

