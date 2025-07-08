package com.university.accountstracker.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SerialHistoryRepository extends JpaRepository<SerialHistory, Long> {
    List<SerialHistory> findByUsernameOrderByTimestampDesc(String username);
    List<SerialHistory> findByQueueTypeOrderByTimestampDesc(QueueSerials.QueueType queueType);
} 