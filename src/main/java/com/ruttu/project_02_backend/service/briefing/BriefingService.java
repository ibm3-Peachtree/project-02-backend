package com.ruttu.project_02_backend.service.briefing;

import com.ruttu.project_02_backend.dto.briefing.*;
import com.ruttu.project_02_backend.entity.prod.user.UserAddressEntity;
import com.ruttu.project_02_backend.repository.prod.user.UserAddressRepository;
import com.ruttu.project_02_backend.service.routine.LiveRouteService;
import com.ruttu.project_02_backend.service.routine.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BriefingService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final RoutineService routineService;
    private final LiveRouteService liveRouteService;
    private final GeminiService geminiService;

    private final UserAddressRepository userAddressRepository;

    public ResponseWeatherDto getOriginWeather(Long userId){
        String address = liveRouteService.getTodayRoutine(userId)
                .getOriginAlias();
        UserAddressEntity userAddress = userAddressRepository.findByUserIdAndAlias(userId, address);
        double lat = userAddress.getLat().doubleValue();
        double lng = userAddress.getLng().doubleValue();
        String locationName = userAddress.getAlias();
        return getWeather(lat, lng, locationName);
    }

    public ResponseWeatherDto getDestinationWeather(Long userId){
        String address = liveRouteService.getTodayRoutine(userId)
                .getDestinationAlias();
        UserAddressEntity userAddress = userAddressRepository.findByUserIdAndAlias(userId, address);
        double lat = userAddress.getLat().doubleValue();
        double lng = userAddress.getLng().doubleValue();
        String locationName = userAddress.getAlias();
        return getWeather(lat, lng, locationName);
    }

    private ResponseWeatherDto getWeather(double lat, double lng, String locationName) {
        LocalDateTime today = LocalDateTime.now();
        String date = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        String hour = String.format("%02d", today.getHour());

        TodayWeatherAirQualityDto todayWeather;

        try {
            String weatherKey = getWeatherKey(lat, lng, date);
            Set<String> keys = redisTemplate.keys(weatherKey + ":*");
            List<WeatherDto> weathers = keys.stream()
                    .map(key -> routineService.readJson(
                            (String) redisTemplate.opsForValue().get(key),
                            WeatherDto.class
                    ))
                    .toList();
            System.out.println("weather key: "+weatherKey + ":" + hour);
            WeatherDto weatherInfo = routineService.readJson(
                    (String) redisTemplate.opsForValue().get(weatherKey + ":" + hour),
                    WeatherDto.class
            );
            double minTemp = weathers.stream()
                    .map(WeatherDto::getTMP)
                    .filter(s -> s != null && !s.isBlank())
                    .mapToDouble(Double::parseDouble)
                    .min().orElse(0);
            double maxTemp = weathers.stream()
                    .map(WeatherDto::getTMP)
                    .filter(s -> s != null && !s.isBlank())
                    .mapToDouble(Double::parseDouble)
                    .max().orElse(0);

            AirQualityDto airQuality = routineService.readJson(
                    (String) redisTemplate.opsForValue().get(getPmKey(date)),
                    new TypeReference<AirQualityDto>() {}
            );

            todayWeather = new TodayWeatherAirQualityDto(
                    Double.parseDouble(weatherInfo.getTMP()),
                    minTemp, maxTemp,
                    weatherInfo.getSKY(), weatherInfo.getPCP(),
                    airQuality.getPm10().getSeoul(), airQuality.getPm25().getSeoul()
            );



            return new ResponseWeatherDto(locationName, todayWeather);

        } catch (IllegalStateException e) {
            return null;  // ✅ 컨트롤러에서 204 처리
        }
    }

    public GeminiSuppliesResultDto getSupplies(Long userId){
        LocalDateTime today = LocalDateTime.now();
        String date = today.format(DateTimeFormatter.BASIC_ISO_DATE);

        Long routineId = liveRouteService.getTodayRoutine(userId)
                .getId();
        ResponseWeatherDto originWeather = getOriginWeather(userId);
        ResponseWeatherDto destinationWeather = getDestinationWeather(userId);
        String key = getSuppliesKey(routineId, date);

        try{

            return routineService.readJson(
                    (String) redisTemplate.opsForValue()
                            .get(key),
                    GeminiSuppliesResultDto.class
            );

        } catch (IllegalStateException e) {
            // ✅ 타입 명시
            String prompt = """
            %s의 현재 날씨 정보:
            온도: %.1f℃
            최저온도: %.1f℃
            최고온도: %.1f℃
            구름: %s
            강수량: %s
            미세먼지: %s
            초미세먼지: %s
            
            %s의 현재 날씨 정보:
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
            """.formatted(
                    originWeather.getLocationName(),
                    originWeather.getTmp(),
                    originWeather.getMinTemp(),
                    originWeather.getMaxTemp(),
                    originWeather.getSky(),
                    originWeather.getPcp(),
                    originWeather.getPm10(),
                    originWeather.getPm25(),

                    destinationWeather.getLocationName(),
                    destinationWeather.getTmp(),
                    destinationWeather.getMinTemp(),
                    destinationWeather.getMaxTemp(),
                    destinationWeather.getSky(),
                    destinationWeather.getPcp(),
                    destinationWeather.getPm10(),
                    destinationWeather.getPm25()
            );

            return geminiService.generate(
                    prompt, GeminiSuppliesResultDto.class, key);
    }

    }

    public String getTodayBriefing(Long userId, String contents){

        LocalDateTime today = LocalDateTime.now();
        String date = today.format(DateTimeFormatter.BASIC_ISO_DATE);

        Long routineId = liveRouteService.getTodayRoutine(userId)
                .getId();
        String key = getBriefingKey(routineId, date);


        try {

            return (String) redisTemplate.opsForValue()
                            .get(key);
        } catch (IllegalStateException e) {
            String prompt = """
                %s 내용을 바탕으로 최대 2줄로 요약해줘.
                요약된 내용은 오늘의 브리핑 내용으로 들어갈거니깐 그 점 참조해서 주요 내용만 뽑아줘.
                """.formatted(contents);

            return geminiService.generate(
                    prompt, String.class, key);
        }

    }

    private String getSuppliesKey(Long routineId, String date){
        return "supplies:" + ":" + routineId + ":" + date;
    }

    private String getBriefingKey(Long routineId, String date){
        return "briefing:" + ":" + routineId + ":" + date;
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
