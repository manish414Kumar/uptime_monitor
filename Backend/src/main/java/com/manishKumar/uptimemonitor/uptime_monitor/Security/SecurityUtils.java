package com.manishKumar.uptimemonitor.uptime_monitor.Security;

import com.manishKumar.uptimemonitor.uptime_monitor.Entities.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SecurityUtils {
    public static User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user;
        }
        throw new UsernameNotFoundException("No authenticated user context found");
    }
}
