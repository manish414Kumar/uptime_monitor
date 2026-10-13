package com.manishKumar.uptimemonitor.uptime_monitor.Controllers;

import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.PublicStatusDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Services.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {
    private final StatusService statusService;

    @GetMapping
    public ResponseEntity<List<PublicStatusDto>> getAllPublicStatuses() {
        return ResponseEntity.ok(statusService.getAllPublicStatuses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicStatusDto> getPublicStatus(@PathVariable Long id) {
        return ResponseEntity.ok(statusService.getPublicStatus(id));
    }
}
