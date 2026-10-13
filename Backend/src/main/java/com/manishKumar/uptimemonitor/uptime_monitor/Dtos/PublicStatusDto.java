package com.manishKumar.uptimemonitor.uptime_monitor.Dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PublicStatusDto {
    private Long id;
    private String name;
    private Boolean isUp;
    private Double uptimePercentage;
}
