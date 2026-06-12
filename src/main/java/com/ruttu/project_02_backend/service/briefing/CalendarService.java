package com.ruttu.project_02_backend.service.briefing;

import com.ruttu.project_02_backend.dto.briefing.CalendarIdListDto;
import com.ruttu.project_02_backend.dto.briefing.CalendarItemsDto;
import com.ruttu.project_02_backend.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final AuthService authService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;


    public List<CalendarItemsDto> getCalendar(
            Long userId
    ) {

        String token = (String) redisTemplate.opsForValue().get(authService.getGoogleTokenKey(userId));

        if (token == null || token.isBlank()) {
            throw new RuntimeException("구글 액세스 토큰이 없습니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ZoneId seoul = ZoneId.of("Asia/Seoul");

        String timeMin = OffsetDateTime.now(seoul)
                .withNano(0)
                .withOffsetSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);

        String timeMax = LocalDate.now(seoul)
                .plusDays(1)
                .atStartOfDay(seoul)
                .toOffsetDateTime()
                .withOffsetSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);

        // id 가져오기
        ResponseEntity<CalendarIdListDto> ids = restTemplate.exchange(
                "https://www.googleapis.com/calendar/v3/users/me/calendarList",
                HttpMethod.GET,
                entity,
                CalendarIdListDto.class
        );
        List<CalendarIdListDto.CalendarId> items =
                Optional.ofNullable(ids.getBody())
                        .map(CalendarIdListDto::getItems)
                        .orElse(Collections.emptyList());
        return items
                .stream()
                .map(item -> {
                    try {
                        String url = UriComponentsBuilder
                                .fromUriString("https://www.googleapis.com/calendar/v3/calendars/{id}/events")
                                .queryParam("singleEvents", true)
                                .queryParam("orderBy", "startTime")
                                .queryParam("timeMin", timeMin)
                                .queryParam("timeMax", timeMax)
                                .buildAndExpand(item.getId())
                                .toUriString();
                        return restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                entity,
                                CalendarItemsDto.class
                        ).getBody();
                    }catch (HttpClientErrorException.NotFound e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }


}


