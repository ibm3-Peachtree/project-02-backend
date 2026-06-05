package com.ruttu.project_02_backend.service.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.config.JwtUtil;
import com.ruttu.project_02_backend.dto.auth.GoogleLoginRequestDto;
import com.ruttu.project_02_backend.dto.auth.LoginResponseDto;
import com.ruttu.project_02_backend.entity.prod.auth.TokenMngtEntity;
import com.ruttu.project_02_backend.entity.prod.user.UserEntity;
import com.ruttu.project_02_backend.exception.auth.InvalidGoogleTokenException;
import com.ruttu.project_02_backend.exception.auth.MissingTokenException;
import com.ruttu.project_02_backend.exception.auth.UnauthorizedException;
import com.ruttu.project_02_backend.repository.prod.auth.TokenMngtRepository;
import com.ruttu.project_02_backend.repository.prod.user.UserRepository;
import com.ruttu.project_02_backend.util.RefreshTokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import static com.ruttu.project_02_backend.util.RefreshTokenHashUtil.hash;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Value("${google.client-id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final TokenMngtRepository tokenMngtRepository;
    private final JwtUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    //로그인 서비스 구현
    public LoginResponseDto googleLogin(GoogleLoginRequestDto request){
        try { //Google이 발급한 ID 토큰이 진짜인지 검증하는 객체를 만듬
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(),
                            GsonFactory.getDefaultInstance()
                    )
                            .setAudience(
                                    Collections.singletonList(googleClientId)
                            )
                            .build();
            //구글 토큰 검증
            GoogleIdToken googleidToken =
                    verifier.verify(request.getIdToken());

            if (googleidToken == null) {
                throw new InvalidGoogleTokenException("토큰이 유효하지 않습니다");
            }
            //사용자 정보 추출
            GoogleIdToken.Payload payload = googleidToken.getPayload();

            String email = payload.getEmail();
            String providerId = payload.getSubject();
            String nickname =
                    (String) payload.get("name");

            UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseGet(() -> // db에 정보가 없으면 자동 회원가입
                            registerGoogleUser(
                                    email,
                                    providerId,
                                    nickname
                            )
                    );
            if ("WITHDRAWN".equals(userEntity.getStatus())) {

                Instant limit = Instant.now().minus(Duration.ofDays(30));

                if (userEntity.getWithdrawnAt() != null &&
                        userEntity.getWithdrawnAt().isAfter(limit)) {

                    return LoginResponseDto.builder()
                            .status("DORMANT")
                            .userId(userEntity.getId())
                            .build();
                }

                throw new UnauthorizedException("탈퇴된 계정입니다");
            }
            //JWT 발급
            Long userId = userEntity.getId();
            String accessToken =
                    jwtUtil.generateAccessToken(userId, userEntity.getRole());

            String refreshToken =
                    jwtUtil.generateRefreshToken(userId);

            //refreshToken 저장
            Instant expiresAt =
                    Instant.now().plusMillis(jwtUtil.getRefreshExpirationMs());
            TokenMngtEntity token = new TokenMngtEntity();
            token.setUserId(userId);
            token.setRefreshTokenHash(hash(refreshToken));
            token.setExpiresAt(expiresAt);
            token.setRevoked(false);

            token.setCreatedAt(Instant.now());

            tokenMngtRepository.save(token);

            // redis에 google 토큰 저장
            redisTemplate.opsForValue().set(
                    getGoogleTokenKey(userId),
                    request.getAccessToken());

            // 이 dto 형식으로 반환
            return LoginResponseDto.builder()
                    .status("ACTIVE")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(userEntity.getId())
                    .email(email)
                    .nickname(userEntity.getNickname())
                    .build();
        }catch(Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException("로그인 실패");
        }
    }
    //자동 회원가입
    private UserEntity registerGoogleUser(
            String email,
            String providerId,
            String nickname
    ) {

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .provider("GOOGLE")
                .providerId(providerId)
                .nickname(nickname)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .role("USER")
                .status("ACTIVE")
                // ✅ NOT NULL 컬럼 기본값 추가
                .alightingAlert(false)
                .departureAlert(false)
                .briefingAlert(false)
                .ttsEnabled(false)
                .build();

        return userRepository.save(userEntity);
    }
    //토큰 검증 이후 유저 정보 꺼내오기
    public CustomUserDetails loadUserById(Long userId){

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow();


        return new CustomUserDetails(
                userEntity.getId(),
                userEntity.getEmail()
        );
    }

    @Transactional
    //로그아웃 서비스
    public void logout(String refreshToken) {
        String hashed =
                RefreshTokenHashUtil.hash(refreshToken);

        TokenMngtEntity token = tokenMngtRepository.findByRefreshTokenHash(hashed)
                .orElseThrow(() -> new MissingTokenException("토큰이 존재하지 않습니다"));

        token.setRevoked(true);

        // redis에 google 토큰 삭제
        redisTemplate.delete(
                getGoogleTokenKey(token.getUserId()));

        System.out.println("로그아웃 성공");
    }

    @Transactional
    public LoginResponseDto refresh(String refreshToken) {

        // 1. hash(암호화)
        String hashed = RefreshTokenHashUtil.hash(refreshToken);

        // 2. DB 조회
        TokenMngtEntity token = tokenMngtRepository
                .findByRefreshTokenHash(hashed)
                .orElseThrow(() -> new RuntimeException("토큰 없음"));

        // 3. revoke 체크
        if (Boolean.TRUE.equals(token.getRevoked())) {
            throw new RuntimeException("로그아웃된 토큰");
        }

        // 4. 만료 체크
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("만료된 토큰");
        }

        // 5. 유저 조회
        UserEntity user = userRepository.findById(token.getUserId())
                .orElseThrow();

        // 6. 새 access token 발급
        String newAccessToken =
                jwtUtil.generateAccessToken(user.getId(), user.getRole());

        return LoginResponseDto.builder()
                .status("ACTIVE")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    public void restoreUser(Long userId) {

        if (userId == null || userId == 0) {
            throw new RuntimeException("유효하지 않은 userId");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다. userId=" + userId));

        user.setStatus("ACTIVE");
        user.setWithdrawnAt(null);

        userRepository.save(user);
        System.out.println(
                "[RESTORE] userId=" + user.getId() + " -> ACTIVE");
    }

    public String getGoogleTokenKey(Long userId){
        return "google:token:" + userId;
    }
}