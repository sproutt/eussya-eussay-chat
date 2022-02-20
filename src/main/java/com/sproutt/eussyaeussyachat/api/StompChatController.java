package com.sproutt.eussyaeussyachat.api;

import com.sproutt.eussyaeussyachat.api.dto.OneToOneChatMessageDTO;
import com.sproutt.eussyaeussyachat.application.chat.ChatService;
import com.sproutt.eussyaeussyachat.application.redisServer.RedisPublisher;
import com.sproutt.eussyaeussyachat.application.redisServer.RedisSubscriber;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final RedisPublisher redisPublisher;
    private final ChatService chatService;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;
    private final RedisTemplate redisUserConnectionTemplate;

    ChannelTopic server;
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    public void init() {
        topics = new HashMap<>();
        server = new ChannelTopic("server_A");
        topics.put(server.getTopic(), server);
        redisMessageListenerContainer.addMessageListener(redisSubscriber, server);
    }

    @MessageMapping(value = "/enter")
    public void enter(long userId) {
        redisUserConnectionTemplate.opsForValue().set(String.valueOf(userId), server.getTopic());
    }

    @MessageMapping(value = "/chat/one-to-one")
    public void message(OneToOneChatMessageDTO messageDto) {
        OneToOneChatMessage message = chatService.save(messageDto);

        String serverName = (String) redisUserConnectionTemplate.opsForValue().get(String.valueOf(messageDto.getTo()));
        serverName = serverName.trim();

        redisPublisher.publish(topics.get(serverName), message);
    }
}