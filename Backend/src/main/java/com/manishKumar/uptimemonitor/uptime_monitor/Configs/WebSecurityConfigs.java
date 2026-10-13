package com.manishKumar.uptimemonitor.uptime_monitor.Configs;

import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Type.Role;
import com.manishKumar.uptimemonitor.uptime_monitor.Entities.User;
import com.manishKumar.uptimemonitor.uptime_monitor.Repositories.UserRepository;
import com.manishKumar.uptimemonitor.uptime_monitor.Security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfigs {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults()) // Enable CORS integration
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Public authentication endpoints
                    .requestMatchers("/login/**").permitAll()
                    .requestMatchers("/status/**").permitAll()
                    // Secure API endpoints
                    .requestMatchers("/monitor/**").hasAnyRole("USER", "ADMIN")
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow Vite React development server origin
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CommandLineRunner seedAdmin() {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
            if (userRepository.findByEmail("user@gmail.com").isEmpty()) {
                User normalUser = new User();
                normalUser.setName("Regular User");
                normalUser.setEmail("user@gmail.com");
                normalUser.setPassword(passwordEncoder.encode("user123"));
                normalUser.setRole(Role.USER);
                userRepository.save(normalUser);
            }
        };
    }
}
