package com.example.mindful_mentor.controller;

import com.example.mindful_mentor.model.Message;
import com.example.mindful_mentor.model.Mood;
import com.example.mindful_mentor.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Used to send direct messages to specific users

    // Send a message to a specific user (not broadcast to all)
    @MessageMapping("/sendMessage")
    public void sendMessage(Message message) {
    	
    	System.out.println("chat token: " + message.getChatToken());
    	System.out.println("/user/" + message.getChatToken().toString() + "/topic/messages");
        // Save the message to the database
        messageRepository.save(message);

        // Send the message to the specific receiver (targeting their WebSocket)
        messagingTemplate.convertAndSend("/user/" + message.getChatToken().toString() + "/topic/messages", message);
    }

    // REST endpoint to fetch message history between two users
    @GetMapping("/messages/{senderId}/{receiverId}")
    @ResponseBody
    public List<Message> getMessageHistory(@PathVariable UUID senderId, @PathVariable UUID receiverId) {
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }

    // REST endpoint to fetch messages for a specific receiver filtered by date range
    @GetMapping("/messages/receiver/{receiverId}")
    @ResponseBody
    public List<Message> getMessagesForReceiver(
            @PathVariable UUID receiverId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        return messageRepository.findByReceiverIdAndTimestampBetween(receiverId, startDate, endDate);
    }
}
