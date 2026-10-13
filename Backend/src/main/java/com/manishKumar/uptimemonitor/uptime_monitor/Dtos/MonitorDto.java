package com.manishKumar.uptimemonitor.uptime_monitor.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MonitorDto {
    private Long id;
    @NotBlank(message = "Required field in monitor : name")
    private String name;
    @NotBlank
    @URL(message = "url should be valid")
    private String url;

    private Boolean isActive = true;
    private Integer consecutiveFailures = 0;
    private LocalDateTime createdAt;

    private Boolean isPublic;

    private String alertEmail;
    private String webHookUrl;
}
