package com.hotketok.service;

import com.hotketok.domain.Post;
import com.hotketok.domain.PostTag;
import com.hotketok.domain.PostToTag;
import com.hotketok.dto.internalApi.*;
import com.hotketok.internalApi.HouseServiceClient;
import com.hotketok.repository.PostRepository;
import com.hotketok.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final HouseServiceClient userServiceClient;

    // 받은 쪽지 목록 조회
    public List<PostResponse> getReceiveList(Long userId) {
        List<Post> posts = postRepository.findByReceiverId(userId);

        return posts.stream()
                .map(post -> {
                    // 쪽지를 보낸 사람의 집 정보를 UserService에 요청
                    HouseInfoResponse houseInfo = userServiceClient.getHouseInfoByUserId(post.getSenderId());
                    return PostResponse.of(post, houseInfo);
                })
                .collect(Collectors.toList());
    }

    // 보낸 쪽지 목록 조회
    public List<PostResponse> getSendList(Long userId) {
        List<Post> posts = postRepository.findBySenderId(userId);

        HouseInfoResponse houseInfo = userServiceClient.getHouseInfoByUserId(userId);

        return posts.stream()
                .map(post -> PostResponse.of(post, houseInfo))
                .collect(Collectors.toList());
    }

    // 쪽지 내용 상세 조회
    @Transactional
    public PostDetailResponse getPostDetail(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 쪽지가 존재하지 않습니다. ID: " + postId));

        // 유저의 쪽지인지 확인
        if (!post.getSenderId().equals(userId) && !post.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("해당 쪽지를 조회할 권한이 없습니다.");
        }

        // HouseServiceClient로 집정보 받아옴
        HouseInfoResponse houseInfo = userServiceClient.getHouseInfoByUserId(post.getSenderId());

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
    public AllHouseTagsResponse getAllHouseTags(Long userId) {
        // 유저의 집 정보 가져옴
        HouseIdResponse houseIdResponse = userServiceClient.getHouseIdByUserId(userId);
        if (houseIdResponse == null || houseIdResponse.houseId() == null) {
            return new AllHouseTagsResponse(Collections.emptyList()); // 집 정보가 없으면 빈 목록 반환
        }
        Long houseId = houseIdResponse.houseId();

        List<HouseInfoResponse> residents = userServiceClient.getResidentsByHouseId(houseId);
        List<Long> userIds = residents.stream().map(HouseInfoResponse::userId).toList();

        // 3. 주민들의 ID를 사용하여 관련된 모든 쪽지를 한 번에 조회합니다.
        List<Post> posts = postRepository.findAllBySenderIdIn(userIds);

        // 4. 각 사용자(senderId)별로 가장 대표적인 태그 하나를 매핑합니다.
        Map<Long, String> userTagMap = posts.stream()
                .collect(Collectors.toMap(
                        Post::getSenderId,
                        post -> post.getPostToTags().stream()
                                .findFirst()
                                .map(postToTag -> postToTag.getTag().getContent())
                                .orElse(null),
                        (existing, replacement) -> existing
                ));

        // 5. 주민 정보를 층별 -> 호수별로 그룹화하고, 매핑된 태그를 할당합니다.
        Map<String, Map<String, String>> floorData = new LinkedHashMap<>();
        for (HouseInfoResponse resident : residents) {
            String floor = resident.floor();
            String number = resident.number();
            String tag = userTagMap.get(resident.userId());

            floorData.computeIfAbsent(floor, k -> new LinkedHashMap<>()).put(number, tag);
        }

        // 6. 최종 응답 DTO 형식으로 변환합니다.
        List<FloorResponse> floorResponses = floorData.entrySet().stream()
                .map(entry -> new FloorResponse(entry.getKey(), entry.getValue()))
                .toList();

        return new AllHouseTagsResponse(floorResponses);
    }
}