package com.manishKumar.uptimemonitor.uptime_monitor.Exceptions;

public class DuplicateMonitorException extends RuntimeException {
    public DuplicateMonitorException(String message) {
        super(message);
    }
}