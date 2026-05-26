package com.ruttu.project_02_backend.service.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.ruttu.project_02_backend.config.JwtUtil;
import com.ruttu.project_02_backend.dto.auth.GoogleLoginRequestDto;
import com.ruttu.project_02_backend.dto.auth.LoginResponseDto;
import com.ruttu.project_02_backend.entity.auth.TokenMngtEntity;
import com.ruttu.project_02_backend.entity.user.UserEntity;
import com.ruttu.project_02_backend.exception.auth.InvalidGoogleTokenException;
import com.ruttu.project_02_backend.exception.auth.MissingTokenException;
import com.ruttu.project_02_backend.repository.auth.TokenMngtRepository;
import com.ruttu.project_02_backend.repository.user.UserRepository;
import com.ruttu.project_02_backend.util.RefreshTokenHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;

import static com.ruttu.project_02_backend.util.RefreshTokenHashUtil.hash;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${google.client-id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final TokenMngtRepository tokenMngtRepository;
    private final JwtUtil jwtUtil;

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
            //JWT 발급
            String accessToken =
                    jwtUtil.generateAccessToken(userEntity.getId(), userEntity.getRole());

            String refreshToken =
                    jwtUtil.generateRefreshToken(userEntity.getId());
            //refreshToken 저장
            Instant expiresAt =
                    Instant.now().plusMillis(jwtUtil.getRefreshExpirationMs());

            TokenMngtEntity token = new TokenMngtEntity();
            token.setUserId(userEntity.getId());
            token.setRefreshTokenHash(hash(refreshToken));
            token.setExpiresAt(expiresAt);
            token.setRevoked(false);

            token.setCreatedAt(Instant.now());

            tokenMngtRepository.save(token);

            // 이 dto 형식으로 반환
            return new LoginResponseDto(
                    accessToken,
                    refreshToken,
                    userEntity.getId(),
                    email,
                    nickname
            );
        }catch(Exception e){
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
                .build();

        return userRepository.save(userEntity);
    }
    //토큰 검증 이후 유저 정보 꺼내오기
    public UserDetails loadUserById(Long userId){

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow();

        return User.builder()
                .username(String.valueOf(userEntity.getId()))
                .password("")
                .roles(userEntity.getRole())
                .build();
    }

    @Transactional
    //로그아웃 서비스
    public void logout(String refreshToken) {
        String hashed =
                RefreshTokenHashUtil.hash(refreshToken);

        TokenMngtEntity token = tokenMngtRepository.findByRefreshTokenHash(hashed)
                .orElseThrow(() -> new MissingTokenException("토큰이 존재하지 않습니다"));

        token.setRevoked(true);

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

        return new LoginResponseDto(
                newAccessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}