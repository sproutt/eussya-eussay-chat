package com.sproutt.eussyaeussyachat.domain.chat;

import com.sproutt.eussyaeussyachat.api.dto.OneToOneChatMessageDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
public class OneToOneChatMessage {

    public static final String ROOM_ID_PREFIX = "oneToOne:";

    private Long from;
    private Long to;
    private String content;
    private LocalDateTime createdAt;

    public OneToOneChatMessage(Long from, Long to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public String getRoomId() {
        return ROOM_ID_PREFIX + Math.min(from, to) + "-" + Math.max(from, to);
    }

    public static OneToOneChatMessage of(OneToOneChatMessageDTO dto) {
        return new OneToOneChatMessage(dto.getFrom(), dto.getTo(), dto.getContent());
    }

    public double makeScore() {
        return this.createdAt.toInstant(ZoneOffset.UTC).toEpochMilli() * -1;
    }
}
