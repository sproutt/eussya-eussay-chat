package com.sproutt.eussyaeussyachat.domain.member;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberConnectionStorage {

    String findByMemberId(long memberId);

    void saveConnectedMember(ConnectionInfo connectionInfo);

    void removeDisconnectedMemberBySessionId(String sessionId);
}
