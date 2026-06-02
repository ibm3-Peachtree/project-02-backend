package com.ruttu.project_02_backend.controller.user;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.user.MyInfoDto;
import com.ruttu.project_02_backend.dto.user.UpdateNicknameDto;
import com.ruttu.project_02_backend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User API", description = "닉네임 관리, 내 정보 조회, 회원탈퇴 API")
public class UserController {
    private final UserService userService;
    @PatchMapping("/nickname")
    @Operation(
            summary = "닉네임 등록 및 변경",
            description = "닉네임 등록 및 변경"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Map<String, String>> updateNickname(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody UpdateNicknameDto updateNicknameDto
    ) {
        userService.updateNickname(
                user.getUserId(),
                updateNicknameDto
        );

        return ResponseEntity.ok(
                Map.of("message", "닉네임 변경 완료")
        );
    }

    @GetMapping("/me")
    @Operation(
            summary = "내 정보 조회",
            description = "내 정보(이메일, 닉네임 조회)"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<MyInfoDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                userService.getMyInfo(user.getUserId())
        );
    }

    @DeleteMapping("/me")
    @Operation(
            summary = "회원 탈퇴",
            description = "회원 탈퇴"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Map<String, String>> withdrawUser(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        userService.withdrawUser(user.getUserId());

        return ResponseEntity.ok(
                Map.of("message", "회원 탈퇴되었습니다")
        );
    }
}
