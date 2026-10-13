package com.manishKumar.uptimemonitor.uptime_monitor.Services;

import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.CheckDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Check;
import com.manishKumar.uptimemonitor.uptime_monitor.Exceptions.MonitorNotFound;
import com.manishKumar.uptimemonitor.uptime_monitor.Mapper.CheckMapper;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.CheckRepository;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.MonitorRepository;
import com.manishKumar.uptimemonitor.uptime_monitor.Security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckService {
    private final CheckRepository checkRepository;
    private final CheckMapper checkMapper;
    private final MonitorService monitorService;

    @Transactional(readOnly = true)
    public List<CheckDto> getChecksforMonitor(Long id) {
        monitorService.getMonitorWithAccessCheck(id, SecurityUtils.getCurrentUser());
        List<Check> checks = checkRepository.findByMonitorIdOrderByCheckedAtDesc(id);
        return checkMapper.toCheckDtoList(checks);
    }


    public Double calculateUpTime(Long id, int hours) {
        monitorService.getMonitorWithAccessCheck(id, SecurityUtils.getCurrentUser());
        LocalDateTime startRange = LocalDateTime.now().minusHours(hours);
        double uptime = checkRepository.calculateUptimePercentage(id, startRange);
        return Math.round(uptime * 100.0) / 100.0;
    }

}
