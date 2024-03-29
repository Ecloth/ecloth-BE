package com.ecloth.beta.domain.chat.service;

import com.ecloth.beta.domain.chat.document.ChatMessage;
import com.ecloth.beta.domain.chat.dto.ChatRoomCreateRequest;
import com.ecloth.beta.domain.chat.dto.ChatRoomCreateResponse;
import com.ecloth.beta.domain.chat.dto.ChatRoomExitRequest;
import com.ecloth.beta.domain.chat.dto.ChatRoomListResponse;
import com.ecloth.beta.domain.chat.dto.ChatRoomListResponse.ChatRoomInfo;
import com.ecloth.beta.domain.chat.entity.ChatRoom;
import com.ecloth.beta.domain.chat.exception.ChatException;
import com.ecloth.beta.domain.chat.exception.ErrorCode;
import com.ecloth.beta.domain.chat.repository.ChatRoomRepository;
import com.ecloth.beta.common.page.CustomPage;
import com.ecloth.beta.domain.member.entity.Member;
import com.ecloth.beta.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 채팅(룸) 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;

    // 채팅룸 생성
    public ChatRoomCreateResponse createChat(ChatRoomCreateRequest request) {

        Set<Member> chatRoomMembers = new HashSet<>();
        request.getMemberIds().forEach(x -> chatRoomMembers.add(memberRepository.findById(x)
                .orElseThrow(() -> new UsernameNotFoundException("Member Not Found"))
        ));

        validateIfChatRoomAlreadyExist(chatRoomMembers);

        ChatRoom newChatRoom = chatRoomRepository.save(ChatRoom.builder().members(chatRoomMembers).build());

        return ChatRoomCreateResponse.fromEntity(newChatRoom);
    }

    private void validateIfChatRoomAlreadyExist(Set<Member> chatRoomMembers) {

        List<Member> members = chatRoomMembers.stream().collect(Collectors.toList());

        if (chatRoomRepository.existsByMembersContainingAndMembersContaining(members.get(0), members.get(1))) {

            log.info("ChatRoomCreateResponse.validateIfChatRoomAlreadyExist : Chat Already Exist");

            throw new ChatException(ErrorCode.CHAT_ROOM_ALREADY_EXIST);
        }
    }

    // 채팅룸에 소속된 사람인지 확인
    public boolean isMemberOfChatRoom(Long chatRoomId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Member Not Found"));

        return chatRoomRepository.existsByChatRoomIdAndMembersContaining(chatRoomId, member);
    }

    // 채팅룸 목록 조회 (회원이 소속한 채팅룸)
    public ChatRoomListResponse findChatList(Long memberId, CustomPage requestPage) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Member Not Found"));

        Page<ChatRoom> chatRooms = chatRoomRepository.findByMembersContaining(member, toPageable(requestPage));
        CustomPage pageResult = getPageResultInfo(requestPage, chatRooms);

        List<ChatRoomInfo> chatRoomInfoList = new ArrayList<>();
        for (ChatRoom room : chatRooms.getContent()) {
            Member partnerMember = room.getMembers().stream().filter(x -> x != member).findFirst()
                            .orElseThrow(() -> new UsernameNotFoundException("Member Not Found"));
            Optional<ChatMessage> latestPartnerMessage = chatMessageService.findLatestMessage(room.getChatRoomId());
            chatRoomInfoList.add(ChatRoomInfo.of(room, partnerMember, latestPartnerMessage));
        }

        return ChatRoomListResponse.fromEntity(chatRooms.getTotalElements(), pageResult, chatRoomInfoList);
    }

    private Pageable toPageable(CustomPage requestPage) {
        int page = requestPage.getPage() == 0 ? 1 : requestPage.getPage();
        int size = requestPage.getSize() == 0 ? 5 : requestPage.getSize();
        String sortBy = StringUtils.hasText(requestPage.getSortBy()) ? requestPage.getSortBy() : "registerDate";
        Sort.Direction dir = StringUtils.hasText(requestPage.getSortOrder()) ?
                Sort.Direction.valueOf(requestPage.getSortOrder().toUpperCase(Locale.ROOT)) : Sort.Direction.DESC;
        return PageRequest.of(page - 1, size, dir, sortBy);
    }

    private CustomPage getPageResultInfo(CustomPage requestPage, Page<ChatRoom> chatRooms) {
        return new CustomPage(
                chatRooms.getNumber(), chatRooms.getSize(), requestPage.getSortBy(), requestPage.getSortBy()
        );
    }

    // 채팅룸 나가기
    @Transactional
    public void exitFromChatRoom(ChatRoomExitRequest request) {

        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new UsernameNotFoundException("Member Not Found"));

        chatRoom.getMembers().remove(member);

        if (chatRoom.getMembers().size() == 0) {
            deleteChatRoom(chatRoom);
        }

    }

    // 채팅룸 삭제
    public void deleteChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.delete(chatRoom);
    }

}