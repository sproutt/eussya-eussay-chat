package com.sproutt.eussyaeussyachat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisRepositoryConfig {

    @Value("${redis.repository.host}")
    private String host;

    @Value("${redis.repository.port}")
    private int port;

    @Bean(name = "redisRepositoryFactory")
    public LettuceConnectionFactory redisRepositoryFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean(name = "redisRepositoryTemplate")
    public RedisTemplate<String, OneToOneChatMessage> redisRepositoryTemplate(@Qualifier("redisRepositoryFactory") RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
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
