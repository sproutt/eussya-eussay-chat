package com.sproutt.eussyaeussyachat.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberConnectionStorageImpl implements MemberConnectionStorage {

    private final RedisTemplate<String, String> redisMemberConnectionTemplate;

    private static final String PREFIX_SESSION_ID = "sessionId:";

    @Override
    public String findByMemberId(long memberId) {
        return (String) redisMemberConnectionTemplate.opsForValue().get(String.valueOf(memberId));
    }

    @Override
    public void saveConnectedMember(ConnectionInfo connectionInfo) {
        redisMemberConnectionTemplate.opsForValue().set(PREFIX_SESSION_ID + connectionInfo.getSessionId(), connectionInfo.getMemberId());
        redisMemberConnectionTemplate.opsForValue().set(connectionInfo.getMemberId(), connectionInfo.getChannelName());
    }

    @Override
    public void removeDisconnectedMemberBySessionId(String sessionId) {
        String memberId = (String) redisMemberConnectionTemplate.opsForValue().get(PREFIX_SESSION_ID + sessionId);

        redisMemberConnectionTemplate.delete(String.valueOf(memberId));
        redisMemberConnectionTemplate.delete(PREFIX_SESSION_ID + sessionId);
    }
}
