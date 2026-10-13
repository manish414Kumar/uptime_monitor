package com.manishKumar.uptimemonitor.uptime_monitor.Exceptions;


public class MonitorNotFound extends RuntimeException {
    public MonitorNotFound(String message) {
        super(message);
    }
}
