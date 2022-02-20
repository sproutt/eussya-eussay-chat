package com.sproutt.eussyaeussyachat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sproutt.eussyaeussyachat.application.redisServer.RedisSubscriber;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisServerConfig {

    @Bean(name = "redisServerConnectionFactory")
    @Primary
    public LettuceConnectionFactory redisServerConnectionFactory() {
        return new LettuceConnectionFactory("127.0.0.1", 6380);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(@Qualifier("redisServerConnectionFactory") RedisConnectionFactory connectionFactory, @Qualifier("redisServerTemplate") RedisTemplate redisTemplate) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean(name = "redisServerTemplate")
    public RedisTemplate<String, OneToOneChatMessage> redisServerTemplate(@Qualifier("redisServerConnectionFactory") RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        var serializer = new Jackson2JsonRedisSerializer<>(OneToOneChatMessage.class);
        serializer.setObjectMapper(objectMapper);

        RedisTemplate<String, OneToOneChatMessage> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }

}