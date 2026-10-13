package com.manishKumar.uptimemonitor.uptime_monitor.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginResponseDto {
    private String token;
    private String email;
    private String role;
}
