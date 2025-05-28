package org.example.websockets;

import org.example.websockets.FlightWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new FlightWebSocketHandler(), "/ws/flights").setAllowedOrigins("*");
    }
}
