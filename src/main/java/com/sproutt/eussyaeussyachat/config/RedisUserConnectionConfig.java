package com.sproutt.eussyaeussyachat.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisUserConnectionConfig {

    @Value("${redis.userConnection.host}")
    private String host;

    @Value("${redis.userConnection.port}")
    private int port;

    @Bean(name = "redisUserConnectionFactory")
    public LettuceConnectionFactory redisRepositoryFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean(name = "redisUserConnectionTemplate")
    public RedisTemplate<String, Object> redisRepositoryTemplate(@Qualifier("redisUserConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));

        return redisTemplate;
    }
}
