package com.manishKumar.uptimemonitor.uptime_monitor.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class CheckDto {
    private Long id;
    private Integer statusCode;
    private Long responseTimeMs;
    private Boolean isUp;
    private LocalDateTime checkedAt;
}
