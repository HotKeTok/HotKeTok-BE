package com.hotketok.service;

import com.hotketok.exception.AuthErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String fromPhoneNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendSms(String to, String code) {
        try {
            Message message = new Message();
            message.setFrom(fromPhoneNumber);
            message.setTo(to);
            message.setText("[Hotketok] 아래의 인증번호를 입력해주세요\n" + code);
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            return response;

        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.NOT_SEND_SMS_SERVICE);
        }
    }
}
