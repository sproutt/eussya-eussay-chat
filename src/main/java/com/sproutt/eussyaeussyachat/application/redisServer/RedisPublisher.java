package com.sproutt.eussyaeussyachat.application.redisServer;

import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, OneToOneChatMessage> redisServerTemplate;

    public void publish(ChannelTopic topic, OneToOneChatMessage message) {
        redisServerTemplate.convertAndSend(topic.getTopic(), message);
    }

}
