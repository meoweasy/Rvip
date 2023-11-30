package com.report.reportService.Repository;

import com.report.reportService.Model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    List<Provider> findAllByStatus(boolean status);
}
