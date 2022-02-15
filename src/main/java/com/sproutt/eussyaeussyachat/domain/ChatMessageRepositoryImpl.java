package com.sproutt.eussyaeussyachat.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final RedisTemplate<String, OneToOneChatMessage> redisTemplate;

    @Override
    public OneToOneChatMessage save(OneToOneChatMessage message) {
        redisTemplate.opsForZSet()
                     .add(message.getRoomId(), message, message.makeScore());

        return message;
    }

    @Override
    public List<OneToOneChatMessage> findOneToOneMessagesByRoomIdWithPage(Long from, Long with, Pageable pageable) {
        long start = pageable.getOffset();

        return new ArrayList<>(Objects.requireNonNull(redisTemplate.opsForZSet()
                                                                   .range(parseKey(from, with), start, start + pageable.getPageSize() - 1)));
    }

    private String parseKey(Long from, Long with) {
        long first = Math.min(from, with);
        long second = Math.max(from, with);

        return OneToOneChatMessage.ROOM_ID_PREFIX + first + "-" + second;
    }
}
