package com.eight.demo.module.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.eight.demo.module.websocket.WebSocketChannel;
import com.eight.demo.module.websocket.WebSocketMessageListner;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisPubSubConfig {

    private final WebSocketMessageListner webSocketMessageListner;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(webSocketMessageListner, new ChannelTopic(WebSocketChannel.PUSH_MESSAGE));
        return container;
    }
}
