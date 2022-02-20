package com.sproutt.eussyaeussyachat.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisUserConnectionConfig {

    @Bean(name = "redisUserConnectionFactory")
    public LettuceConnectionFactory redisRepositoryFactory() {
        return new LettuceConnectionFactory("127.0.0.1", 6378);
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
