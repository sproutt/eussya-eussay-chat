package com.sproutt.eussyaeussyachat.domain.member;

import lombok.Getter;

@Getter
public class ConnectionInfo {
    private final String sessionId;
    private final String memberId;
    private final String channelName;

    public ConnectionInfo(String sessionId, String memberId, String channelName) {
        this.sessionId = sessionId;
        this.memberId = memberId;
        this.channelName = channelName;
    }
}
