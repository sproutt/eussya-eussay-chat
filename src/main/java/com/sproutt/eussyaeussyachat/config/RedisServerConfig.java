package com.sproutt.eussyaeussyachat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sproutt.eussyaeussyachat.domain.chat.OneToOneChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisServerConfig {

    @Value("${redis.server.host}")
    private String host;

    @Value("${redis.server.port}")
    private int port;

    @Bean(name = "redisServerConnectionFactory")
    @Primary
    public LettuceConnectionFactory redisServerConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(@Qualifier("redisServerConnectionFactory") RedisConnectionFactory connectionFactory, @Qualifier("redisServerTemplate") RedisTemplate redisTemplate) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("Server_A"); // TODO 외부 변수로 빼기
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