package com.ruttu.project_02_backend.util;

import java.security.MessageDigest;

//SHA-256 기반 hash 처리(refresh token을 안전한 문자열로 변환)
public class RefreshTokenHashUtil {

    public static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(token.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : encoded) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("HASH_ERROR");
        }
    }
}
