package com.hotketok.service;

import com.hotketok.dto.SendPhoneAuthResponse;
import com.hotketok.dto.VerifyPhoneAuthResponse;
import com.hotketok.exception.AuthErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.repository.PhoneAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneAuthService {
    private final PhoneAuthRepository phoneAuthRepository;
    private final SmsUtil smsUtil;

    public SendPhoneAuthResponse sendCode(String phone) {
        String code = String.valueOf(new Random().nextInt(899999) + 100000); // 6자리 OTP
        phoneAuthRepository.saveCode(phone, code, 3 * 60 * 1000); // 3분 TTL
        smsUtil.sendSms(phone,code);
        return new SendPhoneAuthResponse(phone);
    }

    public VerifyPhoneAuthResponse verify(String phone, String code) {
        String saved = phoneAuthRepository.findCode(phone);
        if (saved != null && saved.equals(code)) {
            phoneAuthRepository.deleteCode(phone);
            phoneAuthRepository.markVerified(phone, 10 * 60 * 1000); // 10분간 유효
            return new VerifyPhoneAuthResponse("인증번호가 일치해요");
        }
        throw new CustomException(AuthErrorCode.NOT_EQUAL_SMS_NUMBER);
    }
}
