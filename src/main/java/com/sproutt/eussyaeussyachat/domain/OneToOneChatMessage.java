package com.sproutt.eussyaeussyachat.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;

@Data
@NoArgsConstructor
public class OneToOneChatMessage {

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

    private String getRoomId(Long... members) {
        validateMembersSize(members.length);
        Arrays.sort(members);
        return members[0] + "-" + members[1];
    }

    private void validateMembersSize(int length) {
        if (length != 2) {
            throw new RuntimeException();
        }
    }

    public static OneToOneChatMessage of(OneToOneChatMessageDTO dto) {
        return new OneToOneChatMessage(dto.getFrom(), dto.getTo(), dto.getContent());
    }

    public double getScore() {
        return Timestamp.valueOf(this.createdAt).getTime() * -1;
    }
}
