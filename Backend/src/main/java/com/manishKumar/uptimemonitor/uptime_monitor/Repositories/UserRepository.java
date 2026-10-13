package com.manishKumar.uptimemonitor.uptime_monitor.Repositories;

import com.manishKumar.uptimemonitor.uptime_monitor.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
