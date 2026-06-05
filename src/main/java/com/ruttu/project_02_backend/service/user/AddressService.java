package com.ruttu.project_02_backend.service.user;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.kakao.GeoResultDto;
import com.ruttu.project_02_backend.dto.user.*;
import com.ruttu.project_02_backend.entity.prod.routine.UserRoutineEntity;
import com.ruttu.project_02_backend.entity.prod.user.UserAddressEntity;
import com.ruttu.project_02_backend.exception.user.AddressNotFoundException;
import com.ruttu.project_02_backend.exception.user.LoginRequiredException;
import com.ruttu.project_02_backend.exception.user.RoutineInUseException;
import com.ruttu.project_02_backend.repository.prod.routine.UserRoutineRepository;
import com.ruttu.project_02_backend.repository.prod.user.UserAddressRepository;
import com.ruttu.project_02_backend.service.kakao.KakaoGeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final KakaoGeoService kakaoGeoService;
    private final UserAddressRepository userAddressRepository;
    private final UserRoutineRepository userRoutineRepository;

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
        String jibun = createAddressRequestDto.getJibunAddress();
        userAddressEntity.setJibunAddress(
                (jibun != null && !jibun.isBlank()) ? jibun : createAddressRequestDto.getRoadAddress()
        );

        userAddressEntity.setLat(BigDecimal.valueOf(geoResultDto.getLatitude()));
        userAddressEntity.setLng(BigDecimal.valueOf(geoResultDto.getLongitude()));

        // 현재 로그인 사용자 정보 가져오기
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        // 로그인 사용자 ID 저장
        userAddressEntity.setUserId(user.getUserId());

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
    // 주소 목록 조회 + 별칭 조회
    public List<AddressDto> getAddresses(String alias) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new LoginRequiredException("로그인이 필요합니다");
        }

        Long userId = user.getUserId();

        List<UserAddressEntity> addresses;

        if (alias == null || alias.isBlank()) {
            addresses = userAddressRepository.findByUserId(userId);
        } else {
            addresses = userAddressRepository.findByUserIdAndAliasContaining(userId, alias);
        }

        return addresses.stream()
                .map(this::toDto)
                .toList();
    }
    //주소 목록 반환
    private AddressDto toDto(UserAddressEntity entity) {
        return AddressDto.builder()
                .addressId(entity.getId())
                .name(entity.getAlias())
                .roadAddress(entity.getRoadAddress())
                .build();
    }
    // 특정 주소 조회(상세 조회)
    public DetailAddressDto getAddressById(Long addressId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        Long userId = user.getUserId();

        UserAddressEntity address = userAddressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException("주소를 찾을 수 없습니다."));

        return DetailAddressDto.builder()
                .addressId(address.getId())
                .name(address.getAlias())
                .roadAddress(address.getRoadAddress())
                .jibunAddress(address.getJibunAddress())
                .build();
    }
    //주소 수정
    public UpdateAddressResponseDto updateAddress(Long addressId, UpdateAddressDto request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        Long userId = user.getUserId();

        UserAddressEntity userAddressEntity =
                userAddressRepository.findByIdAndUserId(addressId, userId)
                        .orElseThrow(() -> new AddressNotFoundException("주소를 찾을 수 없습니다."));

        // userRoutine에서 사용 중인지 확인
        String alias = userAddressEntity.getAlias();
        if(userRoutineRepository.existsByUserIdAndOriginAlias(userId, alias)) throw new RoutineInUseException("내 루틴에서 사용 중");
        if(userRoutineRepository.existsByUserIdAndDestinationAlias(userId, alias)) throw new RoutineInUseException("내 루틴에서 사용 중");

        // 1. 별칭 수정
        if (request.getName() != null && !request.getName().isBlank()) {
            userAddressEntity.setAlias(request.getName());
        }

        boolean addressChanged = false;

        // 2. 도로명 주소 수정
        if (request.getRoadAddress() != null && !request.getRoadAddress().isBlank()) {
            userAddressEntity.setRoadAddress(request.getRoadAddress());
            addressChanged = true;
        }

        // 3. 지번 주소 수정
        if (request.getJibunAddress() != null && !request.getJibunAddress().isBlank()) {
            userAddressEntity.setJibunAddress(request.getJibunAddress());
            addressChanged = true;
        }

        // 4. 주소가 바뀐 경우만 좌표 재계산
        if (addressChanged) {
            String fullAddress = userAddressEntity.getRoadAddress(); // 또는 우선순위 선택
            GeoResultDto geoResultDto = kakaoGeoService.getCoordinates(fullAddress);

            userAddressEntity.setLat(
                    BigDecimal.valueOf(geoResultDto.getLatitude())
            );

            userAddressEntity.setLng(
                    BigDecimal.valueOf(geoResultDto.getLongitude())
            );
        }

        UserAddressEntity saved = userAddressRepository.save(userAddressEntity);

        return UpdateAddressResponseDto.builder()
                .addressId(saved.getId())
                .name(saved.getAlias())
                .roadAddress(saved.getRoadAddress())
                .jibunAddress(saved.getJibunAddress())
                .latitude(saved.getLat().doubleValue())
                .longitude(saved.getLng().doubleValue())
                .build();
    }
    //주소 삭제
    public void deleteAddress(Long addressId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        Long userId = user.getUserId();

        UserAddressEntity entity =
                userAddressRepository.findByIdAndUserId(addressId, userId)
                        .orElseThrow(() -> new AddressNotFoundException("주소를 찾을 수 없습니다."));

        userAddressRepository.delete(entity);
    }
}
