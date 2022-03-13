package com.sproutt.eussyaeussyachat.application.member;

import org.springframework.data.redis.listener.ChannelTopic;

public interface MemberConnectionService {

    String findConnectionServerTopic(long memberId);

    void saveAsConnectedMember(String sessionId, long userId, ChannelTopic channelTopic);

    void removeDisconnectedMemberBySessionId(String sessionId);
}
