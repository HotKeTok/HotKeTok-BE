package com.hotketok.domain;

import com.hotketok.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");

            try {
                if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
                    Authentication auth = jwtUtil.getAuthentication(jwtToken);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("STOMP Connection Authenticated: {}", auth.getName());
                } else {
                    throw new AccessDeniedException("STOMP: Invalid or Missing Token");
                }
            } catch (Exception e) {
                log.error("STOMP 토큰 인증 실패: {}", e.getMessage());
                throw new AccessDeniedException("STOMP: Authentication failed");
            }
        }
        return message;
    }
}
