package com.hotketok.domain;

import com.hotketok.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
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

        // 1. STOMP CONNECT 커맨드인지 확인
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("STOMP CONNECT 요청 수신. 헤더: {}", accessor.toNativeHeaderMap());

            // 2. Authorization 헤더에서 JWT 토큰 추출
            // "Bearer eyJ..." 형태의 원본 문자열 그대로 가져옴
            String jwtToken = accessor.getFirstNativeHeader("Authorization");

            try {
                // 3. JwtUtil을 사용하여 토큰 검증
                // JwtUtil.validateToken()이 "Bearer " 접두사를 포함하여 검증하도록 수정되었으므로
                // 여기서는 토큰을 수동으로 파싱(substring)하지 않고 그대로 전달.
                if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {

                    // 4. 토큰이 유효하면 인증 정보(Authentication) 객체를 생성합니다.
                    Authentication auth = jwtUtil.getAuthentication(jwtToken);

                    // 5. Spring Security의 SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // 6. STOMP 세션에 사용자 정보(Principal) 설정
                    // (이후 @AuthenticationPrincipal 등으로 컨트롤러에서 사용자 ID를 주입받을 수 있음)
                    accessor.setUser(auth);

                    log.info("STOMP Connection Authenticated. User ID (Principal): {}", auth.getName());

                } else {
                    // 7. 토큰이 없거나 유효하지 않은 경우
                    log.warn("STOMP Connection 거부: 유효하지 않은 토큰입니다. Token: {}", jwtToken);
                    // null을 반환하면 클라이언트의 연결 시도가 거부됩니다.
                    return null;
                }

            } catch (Exception e) {
                log.error("STOMP 인증 처리 중 예외 발생: {}", e.getMessage(), e);
                return null;
            }
        }

        return message;
    }
}
