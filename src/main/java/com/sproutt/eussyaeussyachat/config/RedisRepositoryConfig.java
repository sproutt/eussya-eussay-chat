package com.sproutt.eussyaeussyachat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
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

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("127.0.0.1", 6379);
    }

    @Bean
    public RedisTemplate<String, OneToOneChatMessage> ChatMessageRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
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

//    @Bean
//    public ObjectMapper objectMapper() {
//        var mapper = new ObjectMapper();
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        mapper.registerModules(new JavaTimeModule(), new Jdk8Module());
//        return mapper;
//    }

}
