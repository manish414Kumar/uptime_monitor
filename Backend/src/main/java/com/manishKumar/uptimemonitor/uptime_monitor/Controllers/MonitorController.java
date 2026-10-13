package com.manishKumar.uptimemonitor.uptime_monitor.Controllers;

import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.CheckDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.MonitorDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Services.CheckService;
import com.manishKumar.uptimemonitor.uptime_monitor.Services.MonitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/monitor")
public class MonitorController {
    private final MonitorService monitorService;
    private final CheckService checkService;

    @GetMapping(path = "/{monitorId}")
    public ResponseEntity<MonitorDto> getMonitorById(@PathVariable(name="monitorId") Long id){
        MonitorDto monitorDto = monitorService.getMonitorById(id);
        return ResponseEntity.ok(monitorDto);
    }

    @GetMapping
    public ResponseEntity<List<MonitorDto>> getAllMonitors() {
        List<MonitorDto> monitorDtos = monitorService.getAllMonitors();
        return ResponseEntity.ok(monitorDtos);
    }

    @PostMapping
    public ResponseEntity<MonitorDto> createMonitor(@RequestBody @Valid MonitorDto input) {
        MonitorDto monitorDto = monitorService.createMonitor(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(monitorDto);
    }

    @DeleteMapping(path = "/{monitorId}")
    public ResponseEntity<Void> deleteMonitorById(@PathVariable(name="monitorId") Long id){
        monitorService.deleteMonitorById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{monitorId}")
    public ResponseEntity<MonitorDto> updateMonitorById(@RequestBody @Valid MonitorDto input,@PathVariable(name="monitorId") Long id){
        MonitorDto monitorDto = monitorService.updateMonitorById(input,id);
        return ResponseEntity.ok(monitorDto);
    }

    @PatchMapping(path = "/{monitorId}")
    public ResponseEntity<MonitorDto> patchMonitorById(@RequestBody MonitorDto input, @PathVariable(name="monitorId") Long id){
        MonitorDto monitorDto = monitorService.patchMonitorById(input,id);
        return ResponseEntity.ok(monitorDto);
    }

    @GetMapping(path = "/{id}/checks")
    public ResponseEntity<List<CheckDto>> getChecksByMonitorId(@PathVariable Long id) {
        return ResponseEntity.ok(checkService.getChecksforMonitor(id));
    }

    @GetMapping(path = "/{id}/uptime")
    public ResponseEntity<Double> getUptime(@PathVariable Long id,@RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(checkService.calculateUpTime(id,hours));
    }
}
