package com.manishKumar.uptimemonitor.uptime_monitor.Services;

import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Check;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Monitor;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.CheckRepository;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.MonitorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthCheckService {
    private final MonitorRepository monitorRepository;
    private final CheckRepository checkRepository;
    private final RestClient restClient;
    private final AlertService alertService;
    private final RecoveryAlertService recoveryAlertService;

    @Async("healthCheckExecutor")
    public void performHealthCheckAsync(Long monitorId) {
        // Load monitor using ID
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElse(null);

        if (monitor == null) {
            log.warn("Monitor with ID {} not found", monitorId);
            return;
        }

        long startTime = System.currentTimeMillis();
        boolean isUp = false;
        int statusCode = 0;

        try{
            var response = restClient.get()
                    .uri(monitor.getUrl())
                    .retrieve()
                    .toBodilessEntity();
            statusCode = response.getStatusCode().value();
            isUp = response.getStatusCode().is2xxSuccessful();
        } catch (RestClientResponseException ex) {
            statusCode = ex.getStatusCode().value();
            isUp = false;
            log.warn("HTTP {} for monitor '{}' ({})", statusCode, monitor.getName(), monitor.getUrl());
        }catch (ResourceAccessException ex) {
            statusCode = 504;
            isUp = false;
            log.warn("Connection timeout/failure for monitor '{}' ({}): {}", monitor.getName(), monitor.getUrl(), ex.getMessage());
        }catch (Exception ex) {
            statusCode = 500;
            isUp = false;
            log.error("Unexpected error for monitor '{}' ({}): {}", monitor.getName(), monitor.getUrl(), ex.getMessage());
        }
        long responseTimeMs = System.currentTimeMillis() - startTime;
        int failuresBeforeThisCheck = monitor.getConsecutiveFailures(); // capture BEFORE reset
        persistResult(monitor, statusCode, isUp, responseTimeMs);

        if (!isUp && monitor.getConsecutiveFailures() == 3) {
            alertService.sendDowntimeAlert(monitor,"HTTP " + statusCode);
        }
        if (isUp && failuresBeforeThisCheck >= 3) {
            recoveryAlertService.sendRecoveryAlert(monitor);
        }
    }

    @Transactional
    public void persistResult(Monitor monitor, int statusCode, boolean isUp, long responseTimeMs) {
        Check check = Check.builder()
                .monitor(monitor)
                .statusCode(statusCode)
                .isUp(isUp)
                .responseTimeMs(responseTimeMs)
                .build();
        checkRepository.save(check);

        if (isUp) {
            monitor.setConsecutiveFailures(0);
        }else{
            monitor.setConsecutiveFailures(monitor.getConsecutiveFailures() + 1);
        }
        monitorRepository.save(monitor);
        log.info("Finished Check for [{}] | Up: {} | Status: {} | Latency: {}ms | Consecutive Failures: {}",
                monitor.getName(), isUp, statusCode, responseTimeMs, monitor.getConsecutiveFailures());
    }
}
