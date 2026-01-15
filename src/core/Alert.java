package core;

import java.time.LocalDateTime;

public class Alert {
    private final LocalDateTime timestamp;
    private final String module;
    private final String message;
    private final Severity severity;

    public Alert(LocalDateTime timestamp, String module, String message, Severity severity) {
        this.timestamp = timestamp;
        this.module = module;
        this.message = message;
        this.severity = severity;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getModule() { return module; }
    public String getMessage() { return message; }
    public Severity getSeverity() { return severity; }
}
