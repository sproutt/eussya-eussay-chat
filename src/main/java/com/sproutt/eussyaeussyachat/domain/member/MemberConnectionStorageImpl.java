package com.sproutt.eussyaeussyachat.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberConnectionStorageImpl implements MemberConnectionStorage {

    private final RedisTemplate redisMemberConnectionTemplate;

    private static final String PREFIX_SESSION_ID = "sessionId:";

    @Override
    public String findByMemberId(long memberId) {
        return (String) redisMemberConnectionTemplate.opsForValue().get(String.valueOf(memberId));
    }

    @Override
    public void saveSessionIdWithMemberId(String sessionId, long memberId) {
        redisMemberConnectionTemplate.opsForValue().set(PREFIX_SESSION_ID + sessionId, String.valueOf(memberId));
    }

    @Override
    public void saveMemberId(long memberId, ChannelTopic channelTopic) {
        redisMemberConnectionTemplate.opsForValue().set(String.valueOf(memberId), channelTopic.getTopic());
    }

    @Override
    public void removeDisconnectedMemberBySessionId(String sessionId) {
        String memberId = (String) redisMemberConnectionTemplate.opsForValue().get(PREFIX_SESSION_ID + sessionId);

        redisMemberConnectionTemplate.delete(String.valueOf(memberId));
        redisMemberConnectionTemplate.delete(PREFIX_SESSION_ID + sessionId);
    }
}
