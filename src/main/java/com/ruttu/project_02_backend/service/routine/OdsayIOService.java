package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.OdsayResponseDto;
import com.ruttu.project_02_backend.dto.routine.OdsayXYDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class OdsayIOService {
    @Value("${odsay.api.key}")
    private String apiKey;


    public OdsayResponseDto getOdsay(OdsayXYDto xy) throws Exception {

        String urlInfo =
                "https://api.odsay.com/v1/api/searchPubTransPathT" +
                        "?SX=" + xy.getSx() +
                        "&SY=" + xy.getSy() +
                        "&EX=" + xy.getEx() +
                        "&EY=" + xy.getEy() +
                        "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8");

        // URL 생성
        URL url = new URL(urlInfo);

        // HTTP 연결
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        // 응답 코드 확인
        int responseCode = conn.getResponseCode();
        System.out.println("responseCode = " + responseCode);

        BufferedReader br;

        // 정상 응답 / 에러 응답 분기
        if (responseCode >= 200 && responseCode < 300) {
            br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }

        // 응답 읽기
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        conn.disconnect();

        ObjectMapper objectMapper = new ObjectMapper();

        OdsayResponseDto response =
                objectMapper.readValue(sb.toString(), OdsayResponseDto.class);

        return response;
    }
}
