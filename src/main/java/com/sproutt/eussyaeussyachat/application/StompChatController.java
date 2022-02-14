package com.sproutt.eussyaeussyachat.application;

import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template;

    @MessageMapping(value = "/chat/one-to-one")
    public void message(OneToOneChatMessageDTO message){
        // TODO message 영속화
        template.convertAndSend("/sub/chat/" + message.getTo(), OneToOneChatMessage.of(message));
    }
}