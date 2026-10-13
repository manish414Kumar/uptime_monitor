package com.manishKumar.uptimemonitor.uptime_monitor.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "monitor",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_name", columnNames = {"user_id", "name"}),
                @UniqueConstraint(name = "uk_user_url", columnNames = {"user_id", "url"})
        }
)
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"user", "checks"}) // Exclude LAZY associations to avoid LazyInitializationException in logs
public class Monitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column(name = "is_active",nullable = false)
    @ColumnDefault("true")
    private Boolean isActive = true;

    @Column(name = "consecutive_failures", nullable = false)
    @ColumnDefault("0")
    private Integer consecutiveFailures = 0;

    @CreationTimestamp
    @Column(updatable = false,nullable = false,columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private LocalDateTime createdAt;

    @Column(name = "webhook_url")
    private String webHookUrl;

    @Column(name = "alert_email")
    private String alertEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ColumnDefault("true")
    private Boolean isPublic;

    @OneToMany(mappedBy = "monitor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Check> checks = new ArrayList<>();
}
