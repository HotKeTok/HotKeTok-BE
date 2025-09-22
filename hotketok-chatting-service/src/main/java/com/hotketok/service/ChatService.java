package com.hotketok.service;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import com.hotketok.domain.Participant;
import com.hotketok.domain.enums.SenderType;
import com.hotketok.dto.internalApi.ChatMessageResponse;
import com.hotketok.dto.internalApi.ChatRoomResponse;
import com.hotketok.dto.internalApi.CreateChatRoomRequest;
import com.hotketok.repository.ChatMessageRepository;
import com.hotketok.repository.ChatRoomRepository;
import com.hotketok.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public Long createChatRoom(CreateChatRoomRequest request) {
        ChatRoom chatRoom = ChatRoom.createChatRoom(request.roomName());
        chatRoomRepository.save(chatRoom);

        List<Participant> participants = request.participantUserIds().stream()
                .map(userId -> Participant.createParticipant(chatRoom, userId, SenderType.HOUSE_USER))
                .collect(Collectors.toList());
        participantRepository.saveAll(participants);

        return chatRoom.getId();
    }

    public List<ChatRoomResponse> findChatRoomsByUserId(Long userId) {
        return participantRepository.findByUserId(userId).stream()
                .map(participant -> new ChatRoomResponse(participant.getChatRoom()))
                .collect(Collectors.toList());
    }

    public List<ChatMessageResponse> findMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId).stream()
                .map(ChatMessageResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + roomId));

        ChatMessage chatMessage = ChatMessage.createChatMessage(chatRoom, senderId, content);
        return chatMessageRepository.save(chatMessage);
    }
}