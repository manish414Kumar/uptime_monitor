package com.manishKumar.uptimemonitor.uptime_monitor.Controllers;

import com.manishKumar.uptimemonitor.uptime_monitor.Advices.ApiResponse;
import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.LoginRequestDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.LoginResponseDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.RegisterRequestDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/login")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        String message = authService.registerUser(registerRequest);
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @PostMapping("/user")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
