package com.manishKumar.uptimemonitor.uptime_monitor.Services;

import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.MonitorDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Monitor;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Type.Role;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.User;
import com.manishKumar.uptimemonitor.uptime_monitor.Exceptions.DuplicateMonitorException;
import com.manishKumar.uptimemonitor.uptime_monitor.Exceptions.MonitorNotFound;
import com.manishKumar.uptimemonitor.uptime_monitor.Mapper.MonitorMapper;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.MonitorRepository;
import com.manishKumar.uptimemonitor.uptime_monitor.Security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitorService {
    private final MonitorRepository monitorRepository;
    private final MonitorMapper monitorMapper;

    public MonitorDto getMonitorById(Long id){
        User currentUser = SecurityUtils.getCurrentUser();
        Monitor monitor = getMonitorWithAccessCheck(id, currentUser);
        return monitorMapper.toMonitorDto(monitor);
    }

    @Transactional
    public MonitorDto createMonitor(MonitorDto input) {
        User currentUser = SecurityUtils.getCurrentUser();

        if (monitorRepository.existsByUserIdAndNameIgnoreCase(currentUser.getId(), input.getName().trim())) {
            throw new DuplicateMonitorException("You already have a monitor named '" + input.getName() + "'");
        }

        if (monitorRepository.existsByUserIdAndUrl(currentUser.getId(), input.getUrl().trim())) {
            throw new DuplicateMonitorException("You are already monitoring this URL: " + input.getUrl());
        }
        Monitor  monitor = monitorMapper.toMonitor(input);
        monitor.setUser(currentUser);
        return monitorMapper.toMonitorDto(monitorRepository.save(monitor));
    }

    public List<MonitorDto> getAllMonitors() {
        User currentUser = SecurityUtils.getCurrentUser();
        // ADMINs see all monitors; regular USERs only see their own
        List<Monitor> monitors = (currentUser.getRole() == Role.ADMIN)
                ? monitorRepository.findAll()
                : monitorRepository.findByUserId(currentUser.getId());

        return monitorMapper.toMonitorDto(monitors);
    }

    @Transactional
    public void deleteMonitorById(Long id){
        User currentUser = SecurityUtils.getCurrentUser();
        Monitor monitor = getMonitorWithAccessCheck(id, currentUser);
        monitorRepository.deleteById(id);
    }

    @Transactional
    public MonitorDto updateMonitorById(MonitorDto input, Long id){
        User currentUser = SecurityUtils.getCurrentUser();
        Monitor monitor = getMonitorWithAccessCheck(id, currentUser);
        monitor.setName(input.getName());
        monitor.setUrl(input.getUrl());
        return monitorMapper.toMonitorDto(monitorRepository.save(monitor));
    }

    @Transactional
    public MonitorDto patchMonitorById(MonitorDto input, Long id){
        User currentUser = SecurityUtils.getCurrentUser();
        Monitor monitor = getMonitorWithAccessCheck(id, currentUser);
        monitorMapper.updateMonitorFromDto(input, monitor);
        return monitorMapper.toMonitorDto(monitorRepository.save(monitor));
    }


    /**
     * Helper method to enforce ownership and permission rules.
     */
    public Monitor getMonitorWithAccessCheck(Long id, User currentUser){
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new MonitorNotFound("Monitor with id " + id + " not found"));

        boolean isOwner = monitor.getUser() != null && monitor.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to access or modify monitor ID: " + id);
        }

        return monitor;
    }
}
