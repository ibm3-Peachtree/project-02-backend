package com.ruttu.project_02_backend.controller.user;

import com.ruttu.project_02_backend.dto.user.*;
import com.ruttu.project_02_backend.service.user.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    @Operation(summary = "주소 목록 조회 + 별칭 조회",
            description = "사용자의 주소 목록 조회 + 별칭으로 조회")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<List<AddressDto>> getAddresses(
            @RequestParam(required = false) String alias
    ) {
        List<AddressDto> response = addressService.getAddresses(alias);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "특정 주소 조회",
            description = "addressId로 특정 주소 상세 조회")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<DetailAddressDto> getAddressById(
            @PathVariable Long addressId
    ) {
        DetailAddressDto response = addressService.getAddressById(addressId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{addressId}")
    @Operation(summary = "주소 수정",
            description = "주소 수정")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<UpdateAddressResponseDto> updateAddress(
            @PathVariable Long addressId,
            @RequestBody UpdateAddressDto request
    ) {
        UpdateAddressResponseDto response = addressService.updateAddress(addressId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "주소 삭제",
            description = "저장된 주소를 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId
    ) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
