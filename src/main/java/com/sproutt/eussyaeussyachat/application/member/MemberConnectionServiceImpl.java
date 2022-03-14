package com.sproutt.eussyaeussyachat.application.member;

import com.sproutt.eussyaeussyachat.domain.member.ConnectionInfo;
import com.sproutt.eussyaeussyachat.domain.member.MemberConnectionStorage;
import lombok.RequiredArgsConstructor;
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
    public void saveAsConnectedMember(ConnectionInfo connectionInfo) {
        memberConnectionStorage.saveConnectedMember(connectionInfo);
    }

    @Override
    public void removeDisconnectedMemberBySessionId(String sessionId) {
        memberConnectionStorage.removeDisconnectedMemberBySessionId(sessionId);
    }
}
