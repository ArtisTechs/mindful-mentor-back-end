package com.example.mindful_mentor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Enable WebSocket message handling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configure the message broker to use an in-memory broker for simplicity
        config.enableSimpleBroker("/topic", "/messages", "/user");
        config.setApplicationDestinationPrefixes("/app"); // Prefix for application messages
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint that the client will use
        registry.addEndpoint("/chat").setAllowedOrigins("http://localhost:3000").withSockJS(); // Enable SockJS fallback
    }
}
