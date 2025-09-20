package com.hotketok.service;

import com.hotketok.domain.RequestForm;
import com.hotketok.domain.enums.Status;
import com.hotketok.dto.CreateRequestFormRequest;
import com.hotketok.dto.GetUserInfoResponse;
import com.hotketok.repository.RequestFormImageRepository;
import com.hotketok.repository.RequestFormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestFormService {
    private final RequestFormRepository requestFormRepository;
    // private final RequestFormImageRepository requestFormImageRepository;

    public void post(
            CreateRequestFormRequest createRequestFormRequest,
            long userId){

        // 사용자 서비스에서 주택주소, authorId, payerId 가져오는 서비스 로직
        // GetUserInfoResponse getUserInfoResponse = memberClient.getUserInfoByPayType(userId,createRequestFormRequest.payType());

        GetUserInfoResponse getUserInfoResponse = new GetUserInfoResponse(1L,2L,"동작 핫케톡 스테이 304호");

        RequestForm requestForm = RequestForm.createRequestForm(
                getUserInfoResponse.userId(),
                getUserInfoResponse.proprietorId(),
                createRequestFormRequest.payType(),
                createRequestFormRequest.description(),
                createRequestFormRequest.requestSchedule(),
                createRequestFormRequest.category(),
                Status.CHOOSING
          );

        // 이미지 저장 로직 필요

        requestFormRepository.save(requestForm);
    }
}
