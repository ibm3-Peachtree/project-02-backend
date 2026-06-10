package com.ruttu.project_02_backend.service.user;

import com.ruttu.project_02_backend.dto.user.MyInfoDto;
import com.ruttu.project_02_backend.dto.user.UpdateNicknameDto;
import com.ruttu.project_02_backend.entity.prod.user.UserEntity;
import com.ruttu.project_02_backend.exception.user.DuplicateNicknameException;
import com.ruttu.project_02_backend.exception.user.UserNotFoundException;
import com.ruttu.project_02_backend.repository.prod.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    @Transactional
    // 닉네임 등록 및 변경
    public void updateNickname(
            Long userId,
            UpdateNicknameDto updateNicknameDto
    ) {
        String nickname = updateNicknameDto.getNickname().trim();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        if (user.getNickname() == null || !nickname.equals(user.getNickname())
                && userRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
        }

        System.out.println("닉네임 변경 전 : " + user.getNickname());

        user.setNickname(nickname);

        System.out.println("닉네임 변경 후 : " + user.getNickname());
    }

    public void updateFcmToken(Long userId, String token){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        user.setAppPushToken(token);
        userRepository.save(user);
    }

    // 내 정보 조회
    public MyInfoDto getMyInfo(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return MyInfoDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    @Transactional
    // 회원 탈퇴
    public void withdrawUser(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        user.setStatus("WITHDRAWN");
        user.setWithdrawnAt(Instant.now());
    }
}
