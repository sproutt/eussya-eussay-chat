package com.sproutt.eussyaeussyachat.application.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sproutt.eussyaeussyachat.domain.chat.OneToOneChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, OneToOneChatMessage> redisPubSubTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = redisPubSubTemplate.getStringSerializer().deserialize(message.getBody());
            OneToOneChatMessage roomMessage = objectMapper.readValue(publishMessage, OneToOneChatMessage.class);

            messagingTemplate.convertAndSend("/sub/chat/" + roomMessage.getTo(), roomMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}