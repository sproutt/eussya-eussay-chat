package com.sproutt.eussyaeussyachat.application.redisServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisServerTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // redis에서 발행된 데이터를 받아 deserialize
            String publishMessage = (String) redisServerTemplate.getStringSerializer().deserialize(message.getBody());
            // ChatMessage 객채로 맵핑
            OneToOneChatMessage roomMessage = objectMapper.readValue(publishMessage, OneToOneChatMessage.class);
            // Websocket 구독자에게 채팅 메시지 Send
            messagingTemplate.convertAndSend("/sub/chat/" + roomMessage.getTo(), roomMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}