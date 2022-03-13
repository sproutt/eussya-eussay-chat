package com.sproutt.eussyaeussyachat.domain.chat;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatMessageRepository {

    OneToOneChatMessage save(OneToOneChatMessage message);

    List<OneToOneChatMessage> findOneToOneMessagesByRoomIdWithPage(Long from, Long with, Pageable pageable);

    void saveAsUnreadMessage(OneToOneChatMessage message);
}
