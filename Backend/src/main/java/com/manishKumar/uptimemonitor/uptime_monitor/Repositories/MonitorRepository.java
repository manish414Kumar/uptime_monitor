package com.manishKumar.uptimemonitor.uptime_monitor.Repositories;

import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Long> {
    List<Monitor> findByIsActiveTrue();
    List<Monitor> findByUserId(Long userId);
    List<Monitor> findByIsActiveTrueAndIsPublicTrue();
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
    boolean existsByUserIdAndUrl(Long userId, String url);
}
