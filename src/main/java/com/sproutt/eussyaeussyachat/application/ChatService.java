package com.sproutt.eussyaeussyachat.application;

import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import com.sproutt.eussyaeussyachat.api.dto.OneToOneChatMessageDTO;

import java.util.List;

public interface ChatService {

    OneToOneChatMessage save(OneToOneChatMessageDTO dto);

    List<OneToOneChatMessage> findOneToOneChatMessages(Long from, Long with, int page);
}
