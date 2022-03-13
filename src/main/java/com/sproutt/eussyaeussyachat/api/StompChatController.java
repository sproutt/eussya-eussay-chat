package com.sproutt.eussyaeussyachat.api;

import com.sproutt.eussyaeussyachat.api.dto.OneToOneChatMessageDTO;
import com.sproutt.eussyaeussyachat.application.chat.ChatService;
import com.sproutt.eussyaeussyachat.application.pubsub.RedisPublisher;
import com.sproutt.eussyaeussyachat.application.pubsub.RedisSubscriber;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Log4j2
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final RedisPublisher redisPublisher;
    private final ChatService chatService;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;
    private final RedisTemplate redisUserConnectionTemplate;
    private final ChannelTopic channelTopic;

    @PostConstruct
    public void init() {
        redisMessageListenerContainer.addMessageListener(redisSubscriber, channelTopic);
    }

    @MessageMapping(value = "/chat/one-to-one")
    public void message(OneToOneChatMessageDTO messageDto) {
        OneToOneChatMessage message = chatService.save(messageDto);

        String serverTopic = (String) redisUserConnectionTemplate.opsForValue().get(String.valueOf(messageDto.getTo()));

        if (serverTopic == null) {
            // TODO messageDTO의 from user가 존재하는 사용자인지 여부 체크 로직
            chatService.saveAsUnreadMessage(messageDto);
            return;
        }

        redisPublisher.publish(ChannelTopic.of(serverTopic), message);
    }
}