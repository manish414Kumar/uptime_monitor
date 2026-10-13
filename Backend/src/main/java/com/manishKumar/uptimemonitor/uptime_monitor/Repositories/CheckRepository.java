package com.manishKumar.uptimemonitor.uptime_monitor.Repositories;

import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Check;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CheckRepository extends JpaRepository<Check,Long> {

    List<Check> findByMonitorIdOrderByCheckedAtDesc(Long monitorId);


    @Query(value = "SELECT COALESCE(AVG(CASE WHEN is_up THEN 100.0 ELSE 0.0 END), 0.0) " +
            "FROM checks WHERE monitor_id = :monitorId AND checked_at > :startRange",
            nativeQuery = true)
    Double calculateUptimePercentage(@Param("monitorId") Long monitorId, @Param("startRange") LocalDateTime startRange);
}
