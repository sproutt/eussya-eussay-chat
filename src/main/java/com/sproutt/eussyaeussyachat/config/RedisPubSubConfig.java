package com.sproutt.eussyaeussyachat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sproutt.eussyaeussyachat.application.pubsub.RedisSubscriber;
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
public class RedisPubSubConfig {

    @Value("${redis.pubsub.host}")
    private String host;

    @Value("${redis.pubsub.port}")
    private int port;

    @Value("${redis.pubsub.channelName}")
    private String channelName;

    @Bean(name = "redisPubSubConnectionFactory")
    @Primary
    public LettuceConnectionFactory redisServerConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(channelName);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(@Qualifier("redisPubSubConnectionFactory") RedisConnectionFactory connectionFactory, RedisSubscriber redisSubscriber, ChannelTopic channelTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisSubscriber, channelTopic);

        return container;
    }

    @Bean(name = "redisPubSubTemplate")
    public RedisTemplate<String, OneToOneChatMessage> redisServerTemplate(@Qualifier("redisPubSubConnectionFactory") RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
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