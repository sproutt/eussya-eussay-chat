package com.sproutt.eussyaeussyachat.api;

import com.sproutt.eussyaeussyachat.application.ChatService;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import com.sproutt.eussyaeussyachat.api.dto.OneToOneChatMessageDTO;
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
    private final ChatService chatService;

    @MessageMapping(value = "/chat/one-to-one")
    public void message(OneToOneChatMessageDTO messageDto){
        OneToOneChatMessage message = chatService.save(messageDto);
        template.convertAndSend("/sub/chat/" + messageDto.getTo(), OneToOneChatMessage.of(messageDto));
    }
}