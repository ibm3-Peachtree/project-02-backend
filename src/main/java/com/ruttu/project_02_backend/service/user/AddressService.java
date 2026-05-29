package com.ruttu.project_02_backend.service.user;

import com.ruttu.project_02_backend.dto.kakao.GeoResultDto;
import com.ruttu.project_02_backend.dto.user.CreateAddressRequestDto;
import com.ruttu.project_02_backend.dto.user.CreateAddressResponseDto;
import com.ruttu.project_02_backend.entity.user.UserAddressEntity;
import com.ruttu.project_02_backend.repository.user.UserAddressRepository;
import com.ruttu.project_02_backend.service.kakao.KakaoGeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final KakaoGeoService kakaoGeoService;
    private final UserAddressRepository userAddressRepository;
    // 주소 생성
    public CreateAddressResponseDto createAddress(CreateAddressRequestDto createAddressRequestDto){

        String targetAddress =
                getTargetAddress(createAddressRequestDto);
        GeoResultDto geoResultDto =
                kakaoGeoService.getCoordinates(targetAddress);

        // 1️⃣ Entity 생성
        UserAddressEntity userAddressEntity = new UserAddressEntity();
        userAddressEntity.setAlias(createAddressRequestDto.getName());
        userAddressEntity.setRoadAddress(createAddressRequestDto.getRoadAddress());
        userAddressEntity.setJibunAddress(createAddressRequestDto.getJibunAddress());
        userAddressEntity.setLat(BigDecimal.valueOf(geoResultDto.getLatitude()));
        userAddressEntity.setLng(BigDecimal.valueOf(geoResultDto.getLongitude()));

        // 현재 로그인 사용자 정보 가져오기
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        // 로그인 사용자 ID 저장
        userAddressEntity.setUserId(
                Long.valueOf(user.getUsername())
        );

        // 2️⃣ DB 저장
        UserAddressEntity saved = userAddressRepository.save(userAddressEntity);

        // 3️⃣ Response 반환
        return new CreateAddressResponseDto(
                saved.getId(),
                saved.getAlias(),
                saved.getRoadAddress(),
                saved.getJibunAddress(),
                saved.getLat().doubleValue(),
                saved.getLng().doubleValue()
        );
    }
    // 좌표 변환용 주소 선택 (도로명 우선, 없으면 지번 사용)
    private String getTargetAddress(CreateAddressRequestDto createAddressRequestDto) {

        if (createAddressRequestDto.getRoadAddress() != null &&
                !createAddressRequestDto.getRoadAddress().isBlank()) {

            return createAddressRequestDto.getRoadAddress();
        }

        return createAddressRequestDto.getJibunAddress();
    }

}
