package com.sproutt.eussyaeussyachat.application.member;

import com.sproutt.eussyaeussyachat.domain.member.ConnectionInfo;

public interface MemberConnectionService {

    String findConnectionServerTopic(long memberId);

    void saveAsConnectedMember(ConnectionInfo connectionInfo);

    void removeDisconnectedMemberBySessionId(String sessionId);
}
