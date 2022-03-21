package com.sproutt.eussyaeussyachat.application.chat;

import com.sproutt.eussyaeussyachat.api.dto.OneToOneChatMessageDTO;
import com.sproutt.eussyaeussyachat.domain.chat.ChatMessageRepository;
import com.sproutt.eussyaeussyachat.domain.chat.OneToOneChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final int PAGE_SIZE = 20;

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public OneToOneChatMessage save(OneToOneChatMessageDTO dto) {
        return chatMessageRepository.save(OneToOneChatMessage.of(dto));
    }

    @Override
    public List<OneToOneChatMessage> findOneToOneChatMessages(Long from, Long with, int page) {
        return chatMessageRepository.findOneToOneMessagesByRoomIdWithPage(from, with, PageRequest.of(page, PAGE_SIZE));
    }

    @Override
    public void saveAsUnreadMessage(OneToOneChatMessageDTO dto) {
        chatMessageRepository.saveAsUnreadMessage(OneToOneChatMessage.of(dto));
    }
}
