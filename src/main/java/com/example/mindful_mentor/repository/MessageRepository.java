package com.example.mindful_mentor.repository;

import com.example.mindful_mentor.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySenderIdAndReceiverId(UUID senderId, UUID receiverId);
    List<Message> findByReceiverId(UUID receiverId);
    List<Message> findByReceiverIdAndTimestampBetween(UUID receiverId, LocalDateTime startDate, LocalDateTime endDate);
}
