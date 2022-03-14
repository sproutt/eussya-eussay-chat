package com.sproutt.eussyaeussyachat.config;

import com.sproutt.eussyaeussyachat.application.member.MemberConnectionService;
import com.sproutt.eussyaeussyachat.domain.member.ConnectionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtHelper jwtHelper;
    private final MemberConnectionService memberConnectionService;
    private final ChannelTopic channelTopic;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String token = accessor.getFirstNativeHeader("token");
            if (isValid(token)) {
                connect(accessor, token);
            }
        }

        if (accessor.getCommand() == StompCommand.DISCONNECT) {
            disconnect(accessor);
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    private boolean isValid(String token) {
        return jwtHelper.validateToken(token);
    }

    private void connect(StompHeaderAccessor accessor, String token) {
        String memberId = String.valueOf(jwtHelper.getMemberIdFromToken(token));
        memberConnectionService.saveAsConnectedMember(new ConnectionInfo(accessor.getSessionId(), memberId, this.channelTopic.getTopic()));
    }

    private void disconnect(StompHeaderAccessor accessor) {
        memberConnectionService.removeDisconnectedMemberBySessionId(accessor.getSessionId());
    }
}
