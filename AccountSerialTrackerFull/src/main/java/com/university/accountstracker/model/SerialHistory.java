package com.university.accountstracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SerialHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    @Enumerated(EnumType.STRING)
    private QueueSerials.QueueType queueType;
    private int serialValue;
    private LocalDateTime timestamp;

    public SerialHistory() {}

    public SerialHistory(String username, QueueSerials.QueueType queueType, int serialValue, LocalDateTime timestamp) {
        this.username = username;
        this.queueType = queueType;
        this.serialValue = serialValue;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public QueueSerials.QueueType getQueueType() { return queueType; }
    public void setQueueType(QueueSerials.QueueType queueType) { this.queueType = queueType; }
    public int getSerialValue() { return serialValue; }
    public void setSerialValue(int serialValue) { this.serialValue = serialValue; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
} 