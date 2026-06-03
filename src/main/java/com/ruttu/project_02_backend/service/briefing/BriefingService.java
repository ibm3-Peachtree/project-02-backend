package com.ruttu.project_02_backend.service.briefing;

import com.ruttu.project_02_backend.dto.briefing.*;
import com.ruttu.project_02_backend.dto.routine.live.CurrentXYDto;
import com.ruttu.project_02_backend.service.routine.LiveRouteService;
import com.ruttu.project_02_backend.service.routine.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BriefingService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    private final RoutineService routineService;
    private final LiveRouteService liveRouteService;
    private final GeminiService geminiService;


    public ResponseWeatherDto getWeather(Long userId){
        LocalDateTime today = LocalDateTime.now();
        String date = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        String hour = String.format("%02d", today.getHour());
        // 현재 위치 조회
        CurrentXYDto latlng = routineService.readJson(
                (String) redisTemplate.opsForValue().get(liveRouteService.getLocationKey(userId)),
                CurrentXYDto.class
        );

        // 오늘의 날씨(tmp: 현재, 최저, 최고), (sky), (pcp)
        String weatherKey = getWeatherKey(latlng.getLatitude(), latlng.getLongitude(), date);
        Set<String> keys = redisTemplate.keys(weatherKey + ":*");
        List<WeatherDto> weathers = keys.stream()
                .map(key -> routineService.readJson(
                        (String) redisTemplate.opsForValue().get(key),
                        WeatherDto.class
                ))
                .toList();
        WeatherDto weatherInfo =  routineService.readJson(
                (String) redisTemplate.opsForValue().get(weatherKey + ":" + hour),
                WeatherDto.class
        );
        double minTemp = weathers.stream()
                .map(WeatherDto::getTMP)
                .filter(s -> s != null && !s.isBlank())
                .mapToDouble(Double::parseDouble)
                .min()
                .orElse(0);

        double maxTemp = weathers.stream()
                .map(WeatherDto::getTMP)
                .filter(s -> s != null && !s.isBlank())
                .mapToDouble(Double::parseDouble)
                .max()
                .orElse(0);


        // 미세먼지 pm10:seoul, pm25:seoul
        AirQualityDto airQuality = routineService.readJson(
                (String) redisTemplate.opsForValue().get(getPmKey(date)),
                new TypeReference<AirQualityDto>() {}
        );

        TodayWeatherAirQualityDto todayWeather = new TodayWeatherAirQualityDto(
                Double.parseDouble(weatherInfo.getTMP()),
                minTemp,
                maxTemp,
                weatherInfo.getSKY(),
                weatherInfo.getPCP(),
                airQuality.getPm10().getSeoul(),
                airQuality.getPm25().getSeoul()
        );

        // 준비물
        String prompt = """
                            현재 날씨 정보:
                온도: %.1f℃
                최저온도: %.1f℃
                최고온도: %.1f℃
                구름: %s
                강수량: %s
                미세먼지: %s
                초미세먼지: %s
                            
                            위 날씨를 바탕으로 오늘의 옷차림과 준비물을 추천해줘.
                            
                            반드시 아래 JSON 형식으로만 응답해.
                            설명, 마크다운, 코드블록 없이 JSON만 반환해.
                            
                            {
                              "clothes": "옷차림 추천",
                              "supplies": "준비물 추천"
                            }
                            """.formatted(todayWeather.getTmp(),
                todayWeather.getMinTemp(),
                todayWeather.getMaxTemp(),
                todayWeather.getSky(),
                todayWeather.getPcp(),
                todayWeather.getPm10(),
                todayWeather.getPm25());

        GeminiResultDto supplies = geminiService.generate(prompt);
        return new ResponseWeatherDto(
                todayWeather,
                supplies
        );

    }


    private String getPmKey(String date){
        return "pm:" + date;
    }

    private String getWeatherKey(double lat, double lng, String date){
        return "weather" + ":" + getLat(lat) + ":" +  getLng(lng) + ":" + date;
    }
    private double getLat(double lat) {
        return Math.round(lat * 100) / 100.0;
    }

    private double getLng(double lng) {
        return Math.round(lng * 100) / 100.0;
    }


}
