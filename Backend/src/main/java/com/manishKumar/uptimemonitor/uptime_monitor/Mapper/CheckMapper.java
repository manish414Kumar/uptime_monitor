package com.manishKumar.uptimemonitor.uptime_monitor.Mapper;

import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.CheckDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Check;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CheckMapper {
    CheckDto toCheckDto(Check check);
    List<CheckDto> toCheckDtoList(List<Check> checks);
}
