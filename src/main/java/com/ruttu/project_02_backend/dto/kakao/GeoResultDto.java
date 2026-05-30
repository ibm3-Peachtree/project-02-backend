package com.ruttu.project_02_backend.dto.kakao;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeoResultDto { // Kakao 좌표 변환 결과 DTO(위도/경도)
    private Double latitude; //위도
    private Double longitude; //경도
}
