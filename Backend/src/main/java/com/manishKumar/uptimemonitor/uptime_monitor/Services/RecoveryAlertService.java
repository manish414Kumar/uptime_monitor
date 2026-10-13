package com.manishKumar.uptimemonitor.uptime_monitor.Services;

import com.manishKumar.uptimemonitor.uptime_monitor.Entities.Monitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecoveryAlertService {
    private final RestClient restClient;
    private final JavaMailSender mailSender;

    public void sendRecoveryAlert(Monitor monitor) {
        sendWebhookRecovery(monitor);
        sendEmailRecovery(monitor);
    }

    private void sendWebhookRecovery(Monitor monitor) {
        if (monitor.getWebHookUrl() == null || monitor.getWebHookUrl().isBlank()) {
            return;
        }

        String message = String.format(
                "✅ *RECOVERED: Monitor is back up!*\n*Name:* %s\n*URL:* %s",
                monitor.getName(), monitor.getUrl()
        );

        Map<String, Object> payload = monitor.getWebHookUrl().contains("hooks.slack.com")
                ? Map.of("text", message)
                : Map.of("content", message);

        try {
            restClient.post()
                    .uri(monitor.getWebHookUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Recovery webhook sent for monitor ID: {}", monitor.getId());
        } catch (Exception e) {
            log.error("Failed to send recovery webhook for monitor ID: {}", monitor.getId(), e);
        }
    }

    private void sendEmailRecovery(Monitor monitor) {
        if (monitor.getAlertEmail() == null || monitor.getAlertEmail().isBlank()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(monitor.getAlertEmail());
        message.setSubject("✅ Monitor Recovered: " + monitor.getName());
        message.setText(String.format(
                "Monitor: %s\nURL: %s\n\nThis monitor is back up and responding normally.",
                monitor.getName(), monitor.getUrl()
        ));

        try {
            mailSender.send(message);
            log.info("Recovery email sent for monitor ID: {}", monitor.getId());
        } catch (Exception e) {
            log.error("Failed to send recovery email for monitor ID: {}", monitor.getId(), e);
        }
    }

}
