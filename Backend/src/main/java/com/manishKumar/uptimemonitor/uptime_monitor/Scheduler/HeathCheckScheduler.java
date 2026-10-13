package com.manishKumar.uptimemonitor.uptime_monitor.Scheduler;

import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Monitor;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.MonitorRepository;
import com.manishKumar.uptimemonitor.uptime_monitor.Services.HealthCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
@Slf4j
public class HeathCheckScheduler {
    private final MonitorRepository monitorRepository;
    private final HealthCheckService heathCheckService;

    @Scheduled(fixedRate = 60000,initialDelay = 10000)
    public void scheduleHeathChecks() {
        List<Monitor> activeMonitors = monitorRepository.findByIsActiveTrue();
        if (activeMonitors.isEmpty()) {
            return;
        }
        log.info("Triggering heath checks for {} active monitors", activeMonitors.size());
        for (Monitor monitor : activeMonitors) {
            heathCheckService.performHealthCheckAsync(monitor.getId());
        }
    }
}
