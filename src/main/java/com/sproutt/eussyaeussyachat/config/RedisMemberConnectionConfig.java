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
public class RedisMemberConnectionConfig {

    @Value("${redis.memberConnection.host}")
    private String host;

    @Value("${redis.memberConnection.port}")
    private int port;

    @Bean(name = "redisMemberConnectionFactory")
    public LettuceConnectionFactory redisRepositoryFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean(name = "redisMemberConnectionTemplate")
    public RedisTemplate<String, Object> redisRepositoryTemplate(@Qualifier("redisMemberConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));

        return redisTemplate;
    }
}
