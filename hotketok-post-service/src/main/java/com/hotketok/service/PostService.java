package com.hotketok.service;

import com.hotketok.domain.Post;
import com.hotketok.domain.PostTag;
import com.hotketok.domain.PostToTag;
import com.hotketok.dto.internalApi.*;
import com.hotketok.exception.PostErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.HouseServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.PostRepository;
import com.hotketok.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final UserServiceClient userServiceClient;
    private final HouseServiceClient houseServiceClient;

    // 받은 쪽지 목록 조회
    public List<PostResponse> getReceiveList(Long userId) {
        List<Post> posts = postRepository.findByReceiverId(userId);

        CurrentAddressResponse currentAddressResponse = userServiceClient.getCurrentAddress(userId);
        String currentAddress = currentAddressResponse.currentAddress();

        return posts.stream()
                .map(post -> {
                    List<HouseInfoResponse> matchedHouses = houseServiceClient.getMatchedHousesByTenantAndAddress(post.getSenderId(), currentAddress);

                    HouseInfoResponse matchedHouse = matchedHouses.stream()
                            .filter(house -> house.userId().equals(post.getSenderId()))
                            .findFirst()
                            .orElse(null);

                    return PostResponse.of(post, matchedHouse);
                })
                .collect(Collectors.toList());
    }

    // 보낸 쪽지 목록 조회
    public List<PostResponse> getSendList(Long userId) {
        List<Post> posts = postRepository.findBySenderId(userId);

        CurrentAddressResponse currentAddressResponse = userServiceClient.getCurrentAddress(userId);
        String currentAddress = currentAddressResponse.currentAddress();

        return posts.stream()
                .map(post -> {
                    List<HouseInfoResponse> matchedHouses = houseServiceClient.getMatchedHousesByTenantAndAddress(post.getReceiverId(), currentAddress);

                    HouseInfoResponse matchedHouse = matchedHouses.stream()
                            .filter(house -> house.userId().equals(post.getReceiverId()))
                            .findFirst()
                            .orElse(null);

                    return PostResponse.of(post, matchedHouse);
                })
                .collect(Collectors.toList());
    }

    // 쪽지 상세 조회
    @Transactional
    public PostDetailResponse getPostDetail(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

        if (!post.getSenderId().equals(userId) && !post.getReceiverId().equals(userId)) {
            throw new CustomException(PostErrorCode.POST_ACCESS_DENIED);
        }

        // 주소까지 기반으로 해서 필터링
        CurrentAddressResponse currentAddressResponse = userServiceClient.getCurrentAddress(userId);
        String currentAddress = currentAddressResponse.currentAddress();
        List<HouseInfoResponse> residents = houseServiceClient.getResidentsByAddress(currentAddress);

        // 이웃들 id
        Set<Long> residentIds = residents.stream()
                .map(HouseInfoResponse::userId)
                .collect(Collectors.toSet());

        if (!residentIds.contains(post.getSenderId()) || !residentIds.contains(post.getReceiverId())) {
            throw new CustomException(PostErrorCode.POST_ACCESS_DENIED);
        }
        HouseInfoResponse houseInfo = houseServiceClient.getHouseInfoByUserId(post.getSenderId());
        return PostDetailResponse.of(post, houseInfo);
    }

    // 쪽지 쓰기
    @Transactional
    public void sendPost(Long senderId, SendPostRequest request) {
        Post post = Post.builder()
                .senderId(senderId)
                .receiverId(request.receiverId())
                .content(request.detailContent())
                .isAnonymous(request.isAnonymous())
                .silentTime(request.silentTime())
                .build();

        List<String> tagNames = request.tag();

        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                PostTag tag = postTagRepository.findByContent(tagName)
                        .orElseGet(() -> postTagRepository.save(PostTag.createPostTag(tagName)));
                post.addTag(tag);
            }
        }
        postRepository.save(post);
    }

    // 이웃 목록 조회
    public NeighborListResponse getAllHouseTagsWithCurrentUser(Long userId) {
        log.info(">>> 요청 사용자 ID (currentUserId): {}", userId);
        CurrentAddressResponse currentAddressResponse = userServiceClient.getCurrentAddress(userId);
        String currentAddress = currentAddressResponse.currentAddress();

        List<HouseInfoResponse> residents = houseServiceClient.getResidentsByAddress(currentAddress);

        Map<String, List<HouseInfoResponse>> residentsByFloor = residents.stream()
                .collect(Collectors.groupingBy(HouseInfoResponse::floor));

        List<FloorResponse> floorResponses = residentsByFloor.entrySet().stream()
                .map(floorEntry -> {
                    String floor = floorEntry.getKey();
                    List<HouseInfoResponse> residentsOnThisFloor = floorEntry.getValue();

                    List<UnitResponse> units = residentsOnThisFloor.stream()
                            .map(resident -> new UnitResponse(
                                    resident.userId(),
                                    resident.number(),
                                    resident.houseTags()
                            ))
                            .collect(Collectors.toList());

                    return new FloorResponse(floor, units);
                })
                .sorted((f1, f2) -> f1.floor().compareTo(f2.floor())) // 층별로 정렬
                .collect(Collectors.toList());

        return new NeighborListResponse(userId, floorResponses);
    }

    // 쪽지 신고하기
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

        if (!post.getSenderId().equals(userId) && !post.getReceiverId().equals(userId)) {
            throw new CustomException(PostErrorCode.POST_ACCESS_DENIED);
        }
        postRepository.delete(post);
    }
}