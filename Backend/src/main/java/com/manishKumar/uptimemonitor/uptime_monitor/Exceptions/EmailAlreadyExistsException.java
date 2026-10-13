package com.manishKumar.uptimemonitor.uptime_monitor.Exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}