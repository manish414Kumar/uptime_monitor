package com.manishKumar.uptimemonitor.uptime_monitor.Mapper;

import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.MonitorDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Monitor;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MonitorMapper {
    MonitorDto toMonitorDto(Monitor monitor);

    List<MonitorDto> toMonitorDto(List<Monitor> monitors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "consecutiveFailures", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Monitor toMonitor(MonitorDto monitorDto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "consecutiveFailures", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    // Copies only non-null fields from DTO into existing Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMonitorFromDto(MonitorDto dto, @MappingTarget Monitor monitor);
}
