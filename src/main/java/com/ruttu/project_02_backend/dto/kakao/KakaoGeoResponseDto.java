package com.ruttu.project_02_backend.dto.kakao;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KakaoGeoResponseDto { // Kakao 주소 검색 API 응답 DTO
    private List<Document> documents;

    @Getter
    @Setter
    public static class Document {

        private String x; //경도
        private String y; //위도
    }
}
