package com.hotketok.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        // 유저의 쪽지인지 확인
        if (!post.getSenderId().equals(userId) && !post.getReceiverId().equals(userId)) {
            throw new CustomException(PostErrorCode.POST_ACCESS_DENIED);
        }

        // HouseServiceClient로 집정보 받아옴
        HouseInfoResponse houseInfo = houseServiceClient.getHouseInfoByUserId(post.getSenderId());

        return PostDetailResponse.of(post, houseInfo);
    }

    // 쪽지 쓰기
    @Transactional
    public void sendPost(Long senderId, SendPostRequest request) {
        // 1. Post 엔티티 먼저 생성 (태그 연결 없이)
        Post post = Post.builder()
                .senderId(senderId)
                .receiverId(request.receiverId())
                .content(request.detailContent())
                .isAnonymous(request.isAnonymous())
                .silentTime(request.silentTime())
                .build();

        List<String> tagNames = request.tags();

        // 2. 태그 존재하면 각 태그 처리
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                PostTag tag = postTagRepository.findByContent(tagName)
                        .orElseGet(() -> postTagRepository.save(PostTag.createPostTag(tagName)));

                post.addTag(tag);
            }
        }

        // 4. Post 저장 -> PostToTag도 같이 저장됨
        postRepository.save(post);
    }

    // 이웃 목록 조회
    public List<FloorResponse> getAllHouseTags(Long userId) {
        CurrentAddressResponse currentAddressResponse = userServiceClient.getCurrentAddress(userId);
        String currentAddress = currentAddressResponse.currentAddress();

        List<HouseInfoResponse> residents = houseServiceClient.getResidentsByAddress(currentAddress);

        Map<String, Map<String, String>> tagsByFloor = new LinkedHashMap<>();
        for (HouseInfoResponse resident : residents) {
            String floor = resident.floor();
            String number = resident.number();

            String tagsAsString = null;
            if (resident.houseTags() != null && !resident.houseTags().isEmpty()) {
                tagsAsString = String.join(", ", resident.houseTags());
            }

            tagsByFloor.computeIfAbsent(floor, k -> new LinkedHashMap<>()).put(number, tagsAsString);
        }

        return tagsByFloor.entrySet().stream()
                .map(entry -> new FloorResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}