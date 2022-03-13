package com.sproutt.eussyaeussyachat.api;

import com.sproutt.eussyaeussyachat.api.dto.OneToOneChatMessageDTO;
import com.sproutt.eussyaeussyachat.application.chat.ChatService;
import com.sproutt.eussyaeussyachat.application.member.MemberConnectionService;
import com.sproutt.eussyaeussyachat.application.pubsub.RedisPublisher;
import com.sproutt.eussyaeussyachat.application.pubsub.RedisSubscriber;
import com.sproutt.eussyaeussyachat.domain.chat.OneToOneChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final RedisPublisher redisPublisher;
    private final MemberConnectionService memberConnectionService;
    private final ChatService chatService;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;
    private final ChannelTopic channelTopic;

    // TODO 해당 설정은 ChatController 밖에서 작성되도록 수정 필요
    @PostConstruct
    public void init() {
        redisMessageListenerContainer.addMessageListener(redisSubscriber, channelTopic);
    }

    @MessageMapping(value = "/chat/one-to-one")
    public void message(OneToOneChatMessageDTO messageDto) {
        OneToOneChatMessage message = chatService.save(messageDto);

        String receiverConnectionServerTopic = memberConnectionService.findConnectionServerTopic(messageDto.getFrom());
        if (receiverConnectionServerTopic == null) {
            // TODO messageDTO의 from user가 존재하는 사용자인지 여부 체크 로직
            chatService.saveAsUnreadMessage(messageDto);
            return;
        }

        redisPublisher.publish(ChannelTopic.of(receiverConnectionServerTopic), message);
    }
}