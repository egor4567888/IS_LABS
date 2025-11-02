package com.example.spacemarine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.cors.allowed-origins:}")
    private String[] allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Настройка CORS для WebSocket endpoints
        if (allowedOrigins != null && allowedOrigins.length > 0 && !allowedOrigins[0].isEmpty()) {
            registry.addEndpoint("/ws")
                    .setAllowedOrigins(allowedOrigins)
                    .withSockJS();
        } else {
            registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("*")
                    .withSockJS();
        }
    }
}