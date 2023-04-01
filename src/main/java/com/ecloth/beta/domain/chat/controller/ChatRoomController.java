package com.ecloth.beta.domain.chat.controller;

import com.ecloth.beta.domain.chat.dto.ChatRoomCreateRequest;
import com.ecloth.beta.domain.chat.dto.ChatRoomCreateResponse;
import com.ecloth.beta.domain.chat.dto.ChatRoomExitRequest;
import com.ecloth.beta.domain.chat.dto.ChatRoomListResponse;
import com.ecloth.beta.domain.chat.service.ChatRoomService;
import com.ecloth.beta.common.page.CustomPage;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 채팅(룸) API
 * - 1 : 1 채팅룸 생성, 조회, 삭제
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Api(tags = "채팅룸 API")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomCreateResponse> chatRoomCreate(@Valid @RequestBody ChatRoomCreateRequest request){

        ChatRoomCreateResponse response = chatRoomService.createChat(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ChatRoomListResponse> chatRoomList(@PathVariable Long memberId, CustomPage requestPage){

        ChatRoomListResponse response = chatRoomService.findChatList(memberId, requestPage);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> chatRoomExit(@Valid @RequestBody ChatRoomExitRequest request){

        chatRoomService.exitFromChatRoom(request);

        return ResponseEntity.ok().build();
    }

}