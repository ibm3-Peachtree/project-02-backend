package com.ruttu.project_02_backend.controller.user;

import com.ruttu.project_02_backend.dto.user.CreateAddressRequestDto;
import com.ruttu.project_02_backend.dto.user.CreateAddressResponseDto;
import com.ruttu.project_02_backend.service.user.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
@Tag(name = "Address API", description = "주소 관리 API")
public class AddressController {
    private final AddressService addressService;
    @PostMapping
    @Operation(
            summary = "주소 생성(저장)",
            description = "주소 생성(저장)"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<CreateAddressResponseDto> createAddress( // 주소 생성
            @RequestBody CreateAddressRequestDto request
    ) {

        CreateAddressResponseDto response = addressService.createAddress(request);
        System.out.println("🔥 주소 생성 API 들어옴");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
