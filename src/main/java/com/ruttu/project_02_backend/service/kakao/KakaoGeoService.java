package com.ruttu.project_02_backend.service.kakao;

import com.ruttu.project_02_backend.dto.kakao.GeoResultDto;
import com.ruttu.project_02_backend.dto.kakao.KakaoGeoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoGeoService {
    private final WebClient webClient;

    @Value("${kakao.rest.api.key}")
    private String kakaoKey;

    // 주소를 위도/경도로 변환
    public GeoResultDto getCoordinates(String address) {

        // Kakao 주소 검색 API 호출
       KakaoGeoResponseDto kakaoGeoResponseDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", address)
                        .build())
                .header("Authorization", "KakaoAK " + kakaoKey)
                .retrieve()
                .bodyToMono(KakaoGeoResponseDto.class)
                .block();

        System.out.println(kakaoGeoResponseDto);

        // 주소 검색 결과 검증
        if (kakaoGeoResponseDto == null ||
                kakaoGeoResponseDto.getDocuments() == null ||
                kakaoGeoResponseDto.getDocuments().isEmpty()) {

            throw new RuntimeException("주소 검색 결과가 없습니다.");
        }
        // 첫 번째 검색 결과 사용
        KakaoGeoResponseDto.Document document =
                kakaoGeoResponseDto.getDocuments().get(0);
        // 위도(y), 경도(x) 반환
        return new GeoResultDto(
                Double.parseDouble(document.getY()),
                Double.parseDouble(document.getX())
        );
    }
}
