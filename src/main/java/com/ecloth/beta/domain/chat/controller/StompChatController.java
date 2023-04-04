package com.ecloth.beta.domain.chat.controller;

import com.ecloth.beta.domain.chat.dto.ChatMessageSendRequest;
import com.ecloth.beta.domain.chat.dto.ChatMessageSendResponse;
import com.ecloth.beta.domain.chat.exception.ChatException;
import com.ecloth.beta.domain.chat.exception.ErrorCode;
import com.ecloth.beta.domain.chat.service.ChatMessageService;
import com.ecloth.beta.domain.chat.service.ChatRoomService;
import com.ecloth.beta.domain.member.repository.MemberRepository;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
@Api(tags = "채팅 메세지 API")
@Slf4j
public class StompChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MemberRepository memberRepository;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/enter")
    public void chatRoomEnter(ChatMessageSendRequest request){

        log.info("StompChatController.chatRoomEnter : " +
                 "chatRoomId - {}, writerId - {}, message - {}",
                  request.getChatRoomId(), request.getWriterId(), request.getMessage());

        validateIfWriterIsMemberOfChatRoom(request);
        extractEnterMessageFromMemberNickname(request);
        ChatMessageSendResponse response = chatMessageResponseAfterSavingToMongoDB(request);
        simpMessagingTemplate.convertAndSend(subscriptionURI(request), response);
    }

    private void extractEnterMessageFromMemberNickname(ChatMessageSendRequest request) {
        String nickname = memberRepository.findNicknameByMemberId(request.getWriterId());
        request.setMessage(String.format("%s님이 들어왔습니다.", nickname));
    }

    @MessageMapping("/message")
    public void messageSend(ChatMessageSendRequest request){

        log.info("StompChatController.chatRoomEnter : " +
                        "chatRoomId - {}, writerId - {}, message - {}",
                  request.getChatRoomId(), request.getWriterId(), request.getMessage());

        validateIfWriterIsMemberOfChatRoom(request);
        ChatMessageSendResponse response = chatMessageResponseAfterSavingToMongoDB(request);
        simpMessagingTemplate.convertAndSend(subscriptionURI(request), response);
    }

    private void validateIfWriterIsMemberOfChatRoom(ChatMessageSendRequest request) {
        boolean isMemberOfChatRoom
                = chatRoomService.isMemberOfChatRoom(request.getChatRoomId(), request.getWriterId());
        if (!isMemberOfChatRoom) {
            throw new ChatException(ErrorCode.NOT_MEMBER_OF_CHAT_ROOM);
        }
    }

    private static String subscriptionURI(ChatMessageSendRequest request) {
        return String.format("/queue/chat/%d", request.getChatRoomId());
    }

    private ChatMessageSendResponse chatMessageResponseAfterSavingToMongoDB(ChatMessageSendRequest request) {
        return chatMessageService.saveMessage(request);
    }

}
