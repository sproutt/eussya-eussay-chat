package com.sproutt.eussyaeussyachat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtHelper jwtHelper;
    private final RedisTemplate redisUserConnectionTemplate;
    private final ChannelTopic channelTopic;

    private static final String PREFIX_SESSION_ID = "sessionId:";

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
        long userId = jwtHelper.getUserIdFromToken(token);
        saveAsConnectedUser(accessor.getSessionId(), userId);
    }

    private void disconnect(StompHeaderAccessor accessor) {
        String userId = (String) redisUserConnectionTemplate.opsForValue().get(PREFIX_SESSION_ID + accessor.getSessionId());
        removeDisconnectedUser(accessor.getSessionId(), userId);
    }

    private void saveAsConnectedUser(String sessionId, long userId) {
        saveSessionId(sessionId, userId);
        saveUserId(userId);
    }

    private void saveSessionId(String sessionId, long userId) {
        redisUserConnectionTemplate.opsForValue().set(PREFIX_SESSION_ID + sessionId, String.valueOf(userId));
    }

    private void saveUserId(long userId) {
        redisUserConnectionTemplate.opsForValue().set(String.valueOf(userId), channelTopic.getTopic());
    }

    private void removeDisconnectedUser(String sessionId, String userId) {
        removeUserId(userId);
        removeSessionId(sessionId);
    }

    private void removeUserId(String userId) {
        redisUserConnectionTemplate.delete(String.valueOf(userId));
    }

    private void removeSessionId(String sessionId) {
        redisUserConnectionTemplate.delete(PREFIX_SESSION_ID + sessionId);
    }
}
