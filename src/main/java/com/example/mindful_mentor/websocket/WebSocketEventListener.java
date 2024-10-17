package com.example.mindful_mentor.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    // This map will track the online status of each user
    private ConcurrentHashMap<String, Boolean> userStatus = new ConcurrentHashMap<>();

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        // Check if the user is null
        if (event.getUser() != null) {
            String userId = event.getUser().getName(); // Get the user ID
            System.out.println("User connected: " + userId);
            userStatus.put(userId, true);
        } else {
            System.out.println("User connected with no associated Principal.");
        }

        // Optionally notify specific users about status changes
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // Check if the user is null
        if (event.getUser() != null) {
            String userId = event.getUser().getName(); // Get the user ID
            System.out.println("User disconnected: " + userId);
            userStatus.put(userId, false);
        } else {
            System.out.println("User disconnected with no associated Principal.");
        }
    }

    // Method to get a specific user's status
    public boolean isUserOnline(String userId) {
        return userStatus.getOrDefault(userId, false); // Return false if not found
    }
}
