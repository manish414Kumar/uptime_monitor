package com.manishKumar.uptimemonitor.uptime_monitor.Services;

import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.LoginRequestDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.LoginResponseDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Dtos.RegisterRequestDto;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Type.Role;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.User;
import com.manishKumar.uptimemonitor.uptime_monitor.Exceptions.EmailAlreadyExistsException;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.UserRepository;
import com.manishKumar.uptimemonitor.uptime_monitor.Security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public String registerUser(RegisterRequestDto registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        return "User registered successfully";
    }

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        // Authenticate credentials against DB via Spring Security's AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Safely verify principal type before casting
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new BadCredentialsException("Invalid user principal");
        }
        String token = jwtProvider.generateToken(user);

        return new LoginResponseDto(token, user.getEmail(), user.getRole().name());
    }
}
