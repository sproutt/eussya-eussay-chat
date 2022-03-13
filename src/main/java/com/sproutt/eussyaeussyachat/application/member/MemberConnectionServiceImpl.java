package com.sproutt.eussyaeussyachat.application.member;

import com.sproutt.eussyaeussyachat.domain.member.MemberConnectionStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberConnectionServiceImpl implements MemberConnectionService {

    private final MemberConnectionStorage memberConnectionStorage;

    @Override
    public String findConnectionServerTopic(long memberId) {
        return memberConnectionStorage.findByMemberId(memberId);
    }

    @Override
    public void saveAsConnectedMember(String sessionId, long memberId, ChannelTopic channelTopic) {
        memberConnectionStorage.saveSessionIdWithMemberId(sessionId, memberId);
        memberConnectionStorage.saveMemberId(memberId, channelTopic);
    }

    @Override
    public void removeDisconnectedMemberBySessionId(String sessionId) {
        memberConnectionStorage.removeDisconnectedMemberBySessionId(sessionId);
    }
}
