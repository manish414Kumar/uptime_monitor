package com.manishKumar.uptimemonitor.uptime_monitor.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "checks")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Check {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "is_up",nullable = false)
    private Boolean isUp;

    @CreationTimestamp
    @Column(name = "checked_at",nullable = false,updatable = false)
    private LocalDateTime checkedAt;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "monitor_id", nullable = false)
    private Monitor monitor;

}
