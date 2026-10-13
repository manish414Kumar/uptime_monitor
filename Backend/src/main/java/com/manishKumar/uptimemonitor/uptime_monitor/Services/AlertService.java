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
public class AlertService {
    private final RestClient restClient;
    private final JavaMailSender mailSender;

    public void sendDowntimeAlert(Monitor monitor, String reason) {
        sendWebhookAlert(monitor, reason);
        sendEmailAlert(monitor, reason);
    }

    public void sendWebhookAlert(Monitor monitor, String reason){
        if (monitor.getWebHookUrl() == null || monitor.getWebHookUrl().isBlank()) {
            log.info("No webhook URL configured for monitor ID: {}", monitor.getId());
            return;
        }

        String message = String.format(
                "🚨 *ALERT: Monitor Down!*\n*Name:* %s\n*URL:* %s\n*Consecutive Failures:* %d\n*Reason:* %s",
                monitor.getName(), monitor.getUrl(), monitor.getConsecutiveFailures(), reason
        );

        Map<String, Object> payload = buildPayload(monitor.getWebHookUrl(), message);

        try {
            restClient.post()
                    .uri(monitor.getWebHookUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Webhook alert sent for monitor ID: {}", monitor.getId());
        } catch (Exception e) {
            log.error("Failed to send webhook alert for monitor ID: {}", monitor.getId(), e);
        }
    }
    private Map<String, Object> buildPayload(String webhookUrl, String message) {
        if (webhookUrl.contains("hooks.slack.com")) {
            return Map.of("text", message);
        }
        // default: Discord format
        return Map.of("content", message);
    }
    private void sendEmailAlert(Monitor monitor, String reason) {
        if (monitor.getAlertEmail() == null || monitor.getAlertEmail().isBlank()) {
            log.info("No alert email configured for monitor ID: {}", monitor.getId());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(monitor.getAlertEmail());
        message.setSubject("🚨 Downtime Alert: " + monitor.getName());
        message.setText(String.format(
                "Monitor: %s\nURL: %s\nConsecutive Failures: %d\nReason: %s",
                monitor.getName(), monitor.getUrl(), monitor.getConsecutiveFailures(), reason
        ));

        try {
            mailSender.send(message);
            log.info("Alert email sent for monitor ID: {}", monitor.getId());
        } catch (Exception e) {
            log.error("Failed to send alert email for monitor ID: {}", monitor.getId(), e);
        }
    }
}
