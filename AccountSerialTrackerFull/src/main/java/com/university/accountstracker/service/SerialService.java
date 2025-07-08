package com.university.accountstracker.service;

import com.university.accountstracker.model.QueueSerials;
import com.university.accountstracker.model.SerialHistory;
import com.university.accountstracker.model.SerialHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.LocalDateTime;

@Service
public class SerialService {

    private final AtomicInteger tuitionFeeSerial = new AtomicInteger(0);
    private final AtomicInteger hallFeeSerial = new AtomicInteger(0);
    private final AtomicInteger clearanceSerial = new AtomicInteger(0);
    private final SimpMessagingTemplate messagingTemplate;
    private final SerialHistoryRepository serialHistoryRepository;

    @Autowired
    public SerialService(SimpMessagingTemplate messagingTemplate, SerialHistoryRepository serialHistoryRepository) {
        this.messagingTemplate = messagingTemplate;
        this.serialHistoryRepository = serialHistoryRepository;
    }

    public QueueSerials getCurrentQueueSerials() {
        return new QueueSerials(
            tuitionFeeSerial.get(),
            hallFeeSerial.get(),
            clearanceSerial.get()
        );
    }

    public void incrementSerial(QueueSerials.QueueType queueType, String username) {
        switch (queueType) {
            case TUITION_FEE: tuitionFeeSerial.incrementAndGet(); break;
            case HALL_FEE: hallFeeSerial.incrementAndGet(); break;
            case CLEARANCE: clearanceSerial.incrementAndGet(); break;
        }
        recordSerialHistory(queueType, username);
        broadcastSerialNumbers();
    }

    public void setSerial(QueueSerials.QueueType queueType, int serial, String username) {
        if (serial < 0) return;
        switch (queueType) {
            case TUITION_FEE: tuitionFeeSerial.set(serial); break;
            case HALL_FEE: hallFeeSerial.set(serial); break;
            case CLEARANCE: clearanceSerial.set(serial); break;
        }
        recordSerialHistory(queueType, username);
        broadcastSerialNumbers();
    }
    
    public void resetAllSerials() {
        tuitionFeeSerial.set(0);
        hallFeeSerial.set(0);
        clearanceSerial.set(0);
        System.out.println("All serials reset at " + LocalTime.now(ZoneId.of("Asia/Dhaka")));
        broadcastSerialNumbers();
    }

    public void broadcastSerialNumbers() {
        QueueSerials currentSerials = getCurrentQueueSerials();
        messagingTemplate.convertAndSend("/topic/serials", currentSerials);
        System.out.println("Broadcasting serials: " + currentSerials.toString());
    }

    private void recordSerialHistory(QueueSerials.QueueType queueType, String username) {
        int value = 0;
        switch (queueType) {
            case TUITION_FEE: value = tuitionFeeSerial.get(); break;
            case HALL_FEE: value = hallFeeSerial.get(); break;
            case CLEARANCE: value = clearanceSerial.get(); break;
        }
        SerialHistory history = new SerialHistory(username, queueType, value, LocalDateTime.now());
        serialHistoryRepository.save(history);
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Dhaka")
    public void scheduledDailyReset() {
        System.out.println("Executing scheduled daily reset at " + LocalTime.now(ZoneId.of("Asia/Dhaka")));
        resetAllSerials();
    }
}
