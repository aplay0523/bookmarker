package com.library.bookmarker.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
public class Sha256EncryptUtil {

    // 비밀 솔트 키
    @Value("${sha.salt.key}")
    private String saltKey;

    public String encryptSHA256(String text) {
        if (text == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            String saltedText = saltKey + text;
            // 문자열 바이트 변환 및 256해싱 계산
            byte[] bytes = md.digest(saltedText.getBytes(StandardCharsets.UTF_8));

            // 16진수 문자열 변환
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 암호화 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}
