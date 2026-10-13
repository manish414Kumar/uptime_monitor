package com.manishKumar.uptimemonitor.uptime_monitor.Services;

import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.PublicStatusDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Check;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Monitor;
import com.manishKumar.uptimemonitor.uptime_monitor.Exceptions.MonitorNotFound;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.CheckRepository;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.MonitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final MonitorRepository monitorRepository;
    private final CheckRepository checkRepository;

    public List<PublicStatusDto> getAllPublicStatuses() {
        return monitorRepository.findByIsActiveTrueAndIsPublicTrue()
                .stream()
                .map(this::toPublicStatus)
                .toList();
    }

    public PublicStatusDto getPublicStatus(Long id) {
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new MonitorNotFound("Monitor not found with id: " + id));

        if (!Boolean.TRUE.equals(monitor.getIsPublic())) {
            throw new MonitorNotFound("Monitor not found with id: " + id);
        }

        return toPublicStatus(monitor);
    }

    private PublicStatusDto toPublicStatus(Monitor monitor) {
        List<Check> checks = checkRepository.findByMonitorIdOrderByCheckedAtDesc(monitor.getId());
        Boolean isUp = checks.isEmpty() ? null : checks.get(0).getIsUp();
        LocalDateTime startRange = LocalDateTime.now().minusHours(24);
        Double rawUptime = checkRepository.calculateUptimePercentage(monitor.getId(), startRange);
        Double uptime = Math.round(rawUptime * 100.0) / 100.0;
        return new PublicStatusDto(monitor.getId(), monitor.getName(), isUp, uptime);
    }
}
