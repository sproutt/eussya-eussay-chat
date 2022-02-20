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

    @MessageMapping(value = "/enter")
    public void enter(long userId) {
        redisUserConnectionTemplate.opsForValue().set(String.valueOf(userId), channelTopic.getTopic());
    }

    @MessageMapping(value = "/chat/one-to-one")
    public void message(OneToOneChatMessageDTO messageDto) {
        OneToOneChatMessage message = chatService.save(messageDto);

        String topic = (String) redisUserConnectionTemplate.opsForValue().get(String.valueOf(messageDto.getTo()));

        // TODO 사용자가 접속상태가 아닌 경우 로직 추가

        redisPublisher.publish(ChannelTopic.of(topic), message);
    }
}