//package com.ecloth.beta.follow.service;
//
//import com.ecloth.beta.common.page.CustomPage;
//import com.ecloth.beta.follow.dto.FollowListRequest;
//import com.ecloth.beta.follow.dto.FollowingRequest;
//import com.ecloth.beta.follow.dto.FollowingResponse;
//import com.ecloth.beta.follow.entity.Follow;
//import com.ecloth.beta.follow.exception.FollowException;
//import com.ecloth.beta.follow.repository.FollowRepository;
//import com.ecloth.beta.follow.type.PointDirection;
//import com.ecloth.beta.member.entity.Member;
//import com.ecloth.beta.member.repository.MemberRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Optional;
//
//import static com.ecloth.beta.follow.exception.ErrorCode.FOLLOW_DUPLICATE_REQUEST;
//import static com.ecloth.beta.follow.exception.ErrorCode.FOLLOW_TARGET_NOT_FOUND;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(MockitoExtension.class)
//class FollowServiceTest {
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private FollowRepository followRepository;
//
//    @InjectMocks
//    private FollowService followService;
//
//    @Test
//    @DisplayName("팔로우 또는 언팔로우 하려는 대상 Id가 회원 테이블에 존재하지 않는 경우")
//    void saveFollow_fail() {
//
//        // given
//        Member requester = sampleRequesterMember();
//        Member target = sampleTargetMember();
//
//        FollowingRequest request = FollowingRequest.builder()
//                .targetId(target.getMemberId())
//                .followStatus(true)
//                .build();
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.ofNullable(requester));
//
//        given(memberRepository.findById(anyLong()))
//                .willReturn(Optional.empty());
//
//        // when & then
//        FollowException exception = Assertions.assertThrows(FollowException.class,
//                () -> followService.saveFollow(requester.getEmail(), request));
//        Assertions.assertEquals(exception.getErrorCode(), FOLLOW_TARGET_NOT_FOUND);
//
//    }
//
//    @Test
//    @DisplayName("중복된 팔로우 요청 또는 중복된 언팔로우 요청을 보낸 경우")
//    void saveFollow_fail2() {
//
//        // given
//        Member requester = sampleRequesterMember();
//        Member target = sampleTargetMember();
//        Follow previousFollow = sampleFollow(requester.getMemberId(), target.getMemberId(), true);
//
//        FollowingRequest request = FollowingRequest.builder()
//                .targetId(target.getMemberId())
//                .followStatus(true)
//                .build();
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.ofNullable(requester));
//
//        given(memberRepository.findById(anyLong()))
//                .willReturn(Optional.ofNullable(target));
//
//        given(followRepository.findByRequesterIdAndTargetId(anyLong(), anyLong()))
//                .willReturn(Optional.ofNullable(previousFollow));
//
//        // when & then
//        FollowException exception = Assertions.assertThrows(FollowException.class,
//                () -> followService.saveFollow(requester.getEmail(), request));
//        Assertions.assertEquals(exception.getErrorCode(), FOLLOW_DUPLICATE_REQUEST);
//
//    }
//
//    @Test
//    @DisplayName("팔로우 또는 언팔로우 성공")
//    void followOrUnFollowTarget_success() {
//
//        // given
//        Member requester = sampleRequesterMember();
//        Member target = sampleTargetMember();
//
//        FollowingRequest request = FollowingRequest.builder()
//                .targetId(target.getMemberId())
//                .followStatus(true)
//                .build();
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.ofNullable(requester));
//
//        given(memberRepository.findById(anyLong()))
//                .willReturn(Optional.ofNullable(target));
//
//        given(followRepository.save(any()))
//                .willReturn(sampleFollow(requester.getMemberId(), request));
//
//    }
//
//    @Test
//    @DisplayName("팔로우 상태 확인 대상 Id가 회원 테이블에 존재하지 않는 경우")
//    void getFollowStatus_fail() {
//
//        // given
//        Member requester = sampleRequesterMember();
//        Member target = sampleTargetMember();
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.ofNullable(requester));
//
//        given(memberRepository.findById(anyLong()))
//                .willReturn(Optional.empty());
//
//        // when & then
//        FollowException exception = Assertions.assertThrows(FollowException.class,
//                () -> followService.getFollowInfo(requester.getEmail(), target.getId()));
//        Assertions.assertEquals(exception.getErrorCode(), FOLLOW_TARGET_NOT_FOUND);
//
//    }
//
//    @Test
//    @DisplayName("팔로우 상태 확인 성공 - 팔로우 테이블에 정보가 없는 경우")
//    void getFollowStatus_success_followNotExist() {
//
//        // given
//        Member requester = sampleRequesterMember();
//        Member target = sampleTargetMember();
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.ofNullable(requester));
//
//        given(memberRepository.findById(anyLong()))
//                .willReturn(Optional.ofNullable(target));
//
//        given(followRepository.findByRequesterIdAndTargetId(anyLong(), anyLong()))
//                .willReturn(Optional.empty());
//
//        // when
//        Following.Response response = followService.getFollowInfo(requester.getEmail(), target.getId());
//
//        // then
//        Assertions.assertEquals(response.getTargetId(), target.getId());
//        Assertions.assertEquals(false, response.isFollowStatus());
//
//    }
//
//    @Test
//    @DisplayName("팔로우 상태 확인 성공 - 팔로우 테이블에 정보가 있는 경우")
//    void getFollowStatus_success_followExist() {
//
//        // given
//        Member requester = sampleRequesterMember();
//        Member target = sampleTargetMember();
//        Follow follow = sampleFollow(requester.getId(), target.getId(), true);
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.ofNullable(requester));
//
//        given(memberRepository.findById(anyLong()))
//                .willReturn(Optional.ofNullable(target));
//
//        given(followRepository.findByRequesterIdAndTargetId(anyLong(), anyLong()))
//                .willReturn(Optional.ofNullable(follow));
//
//        // when
//        Following.Response response = followService.getFollowInfo(requester.getEmail(), target.getId());
//
//        // then
//        Assertions.assertEquals(response.getTargetId(), target.getId());
//        Assertions.assertEquals(true, response.isFollowStatus());
//
//    }
//
//    @Test
//    @DisplayName("나의 팔로우 목록에 저장된 회원이 존재하지 않는 회원인 경우")
//    void getFollowList_whomIFollow_fail() {
//
//        // given
//        Member requester = sampleRequesterMember();
//        FollowList.Request request = sampleGetFollowListRequest(requester.getId(), FOLLOWING);
//        PageRequest pageRequest = PageRequest.of(request.getPageNumber() - 1, request.getRecordSize()
//                , Sort.Direction.valueOf(request.getSortOrder().toUpperCase(Locale.ROOT)), request.getSortBy());
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.ofNullable(requester));
//
//        given(followRepository.findAll(pageRequest))
//                .willReturn(sampleFollows(requester.getId(), pageRequest, 5));
//
//        given(memberRepository.findById(anyLong()))
//                .willReturn(Optional.empty());
//
//        // when & then
//        FollowException exception = Assertions.assertThrows(FollowException.class,
//                () -> followService.getFollowingOrFollowerList(requester.getEmail(), request));
//        Assertions.assertEquals(exception.getErrorCode(), FOLLOW_TARGET_NOT_FOUND);
//
//    }
//
//    @Test
//    @DisplayName("내가 팔로우한 사람이 아무도 없는 경우")
//    void getFollowList_whomIFollow_success() {
//
//        // given
//        Member requester = sampleRequesterMember();
//        FollowList.Request request = sampleGetFollowListRequest(requester.getId(), FOLLOWING);
//        PageRequest pageRequest = PageRequest.of(request.getPageNumber() - 1, request.getRecordSize()
//                , Sort.Direction.valueOf(request.getSortOrder().toUpperCase(Locale.ROOT)), request.getSortBy());
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.ofNullable(requester));
//
//        given(followRepository.findAll(pageRequest))
//                .willReturn(sampleFollows(requester.getId(), pageRequest, 0));
//
//        // when
//        FollowList.Response response = followService.getFollowingOrFollowerList(requester.getEmail(), request);
//
//        // then
//        List<FollowMember> followMembers = response.getFollowList();
//        Assertions.assertEquals(followMembers.size(), 0);
//        Assertions.assertEquals(response.getPointDirection(), FOLLOWING.name());
//
//    }
//
//    @Test
//    @DisplayName("나를 팔로우한 사람 목록 조회")
//    void getFollowList_WhoFollowsMe_success() {
//
//        // given
//        Member requester = sampleRequesterMember();
//        Member target = sampleTargetMember();
//        FollowList.Request request = sampleGetFollowListRequest(requester.getId(), FOLLOWER);
//        PageRequest pageRequest = PageRequest.of(request.getPageNumber() - 1, request.getRecordSize()
//                , Sort.Direction.valueOf(request.getSortOrder().toUpperCase(Locale.ROOT)), request.getSortBy());
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.ofNullable(requester));
//
//        given(followRepository.findAllByTargetId(requester.getId(), pageRequest))
//                .willReturn(sampleFollowers(requester.getId(), pageRequest, 5));
//
//        given(memberRepository.findById(anyLong()))
//                .willReturn(Optional.ofNullable(target));
//
//        // when
//        FollowList.Response response = followService.getFollowingOrFollowerList(requester.getEmail(), request);
//
//        // then
//        List<FollowMember> followMembers = response.getFollowList();
//        Assertions.assertEquals(followMembers.size(), 5);
//        Assertions.assertEquals(response.getPointDirection(), FOLLOWER.name());
//        Assertions.assertEquals(response.getRequesterId(), requester.getId());
//
//    }
//
//    Member sampleRequesterMember(){
//        return Member.builder()
//                .memberId(1L)
//                .email("requester@gmail.com")
//                .build();
//    }
//
//    Member sampleTargetMember(){
//        return Member.builder()
//                .memberId(2L)
//                .email("target@gmail.com")
//                .build();
//    }
//
//    Follow sampleFollow(Long requesterId, FollowingRequest request) {
//        return sampleFollow(requesterId, request.getTargetId(), request.isFollowStatus());
//    }
//
//    Follow sampleFollow(Long requesterId, Long targetId, boolean followStatus) {
//        return Follow.builder()
//                .requesterId(requesterId)
//                .targetId(targetId)
//                .followStatus(followStatus)
//                .build();
//    }
//
//    FollowListRequest sampleGetFollowListRequest(PointDirection dir) {
//        return FollowListRequest.builder()
//                .pointDirection(dir.name())
//                .page(CustomPage.builder()
//                        .pageNumber(1)
//                        .recordSize(5)
//                        .sortBy("createdDate")
//                        .sortOrder("desc")
//                        .build())
//                .build();
//    }
//
//    Page<Follow> sampleFollows(Long requesterId, PageRequest page, int size){
//        List<Follow> follows = new ArrayList<>();
//
//        for (int i = 1; i <= size; i++) {
//            follows.add(sampleFollow(requesterId, i * 1L, true));
//        }
//
//        return new PageImpl<>(follows, page, follows.size());
//    }
//
//    Page<Follow> sampleFollowers(Long requesterId, PageRequest page, int size){
//        List<Follow> follows = new ArrayList<>();
//
//        for (int i = 1; i <= size; i++) {
//            follows.add(sampleFollow(i * 1L, requesterId, true));
//        }
//
//        return new PageImpl<>(follows, page, follows.size());
//    }
//
//}