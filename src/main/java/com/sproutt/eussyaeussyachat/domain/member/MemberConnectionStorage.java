package com.sproutt.eussyaeussyachat.domain.member;

import org.springframework.data.redis.listener.ChannelTopic;

public interface MemberConnectionStorage {

    String findByMemberId(long memberId);

    void saveSessionIdWithMemberId(String sessionId, long memberId);

    void saveMemberId(long memberId, ChannelTopic channelTopic);

    void removeDisconnectedMemberBySessionId(String sessionId);
}
