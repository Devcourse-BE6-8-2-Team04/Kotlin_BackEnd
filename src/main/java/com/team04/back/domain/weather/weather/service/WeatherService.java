package com.team04.back.domain.weather.weather.service;

import com.team04.back.domain.weather.geo.service.GeoService;
import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import com.team04.back.domain.weather.weather.enums.Weather;
import com.team04.back.domain.weather.weather.repository.WeatherRepository;
import com.team04.back.infra.weather.WeatherApiClient;
import com.team04.back.infra.weather.dto.DailyData;
import com.team04.back.infra.weather.dto.OneCallApiResponse;
import com.team04.back.infra.weather.dto.TimeMachineApiResponse;
import com.team04.back.infra.weather.dto.TimeMachineData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherRepository weatherRepository;
    private final WeatherApiClient weatherApiClient;
    private final GeoService geoService;

    public List<WeatherInfo> getDurationWeather(String location, LocalDate start, LocalDate end) {
        return weatherRepository.findByLocationAndDateBetween(location, start, end);
    }
    public void save(WeatherInfo weather) {
        weatherRepository.save(weather);
    }

    /**
     * 좌표와 날짜를 이용하여 날씨 정보 단건을 조회합니다.
     * @param lat 위도
     * @param lon 경도
     * @param date 조회할 날짜
     * @return 해당 좌표와 날짜에 대한 날씨 정보
     */
    @Transactional
    public WeatherInfo getWeatherInfo(double lat, double lon, LocalDate date) {
        String location = geoService.getLocationFromCoordinates(lat, lon);
        return getWeatherInfo(location, lat, lon, date);
    }

    /**
     * 지역 이름과 날짜를 이용하여 날씨 정보 단건을 조회합니다.
     * @param location 지역 이름
     * @param lat 위도
     * @param lon 경도
     * @param date 조회할 날짜
     * @return 해당 지역과 날짜에 대한 날씨 정보
     */
    @Transactional
    public WeatherInfo getWeatherInfo(String location, double lat, double lon, LocalDate date) {
        Optional<WeatherInfo> weatherInfoOpt = weatherRepository.findByLocationAndDate(location, date);

        // 조회 결과가 있고 유효한 경우
        if (weatherInfoOpt.isPresent() && isValid(weatherInfoOpt.get())) {
            return weatherInfoOpt.get();
        }
        // 조회 결과가 없거나 유효하지 않은 경우
        WeatherInfo info = weatherInfoOpt.orElseGet(WeatherInfo::new);
        return updateWeatherInfo(info, location, lat, lon, date);
    }

    /**
     * 좌표와 날짜 범위를 이용하여 날씨 정보 리스트를 조회합니다.
     * @param lat 위도
     * @param lon 경도
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 좌표와 날짜 범위에 대한 날씨 정보 리스트
     */
    @Transactional
    public List<WeatherInfo> getWeatherInfos(double lat, double lon, LocalDate startDate, LocalDate endDate) {
        String location = geoService.getLocationFromCoordinates(lat, lon);
        return getWeatherInfos(location, lat, lon, startDate, endDate);
    }

    /**
     * 지역 이름과 날짜 범위를 이용하여 날씨 정보 리스트를 조회합니다.
     * @param lat 위도
     * @param lon 경도
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 좌표와 날짜 범위에 대한 날씨 정보 리스트
     */
    @Transactional
    public List<WeatherInfo> getWeatherInfos(String location, double lat, double lon, LocalDate startDate, LocalDate endDate) {
        // 시작 날짜와 종료 날짜 유효성 검사
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 날짜(" + startDate + ")는 종료 날짜(" + endDate + ")보다 이후일 수 없습니다.");
        }

        // 범위 내의 모든 날짜에 대해 날씨 정보 조회
        List<WeatherInfo> result = new ArrayList<>();
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            WeatherInfo info = getWeatherInfo(location, lat, lon, date);
            result.add(info);
            date = date.plusDays(1);
        }
        return result;
    }

    // 유효성 검사: 마지막 업데이트가 3시간 이내인지 확인
    private boolean isValid(WeatherInfo weatherInfo) {
        LocalDateTime lastUpdated = weatherInfo.getModifyDate();
        if (lastUpdated == null) return false;
        return lastUpdated.isAfter(LocalDateTime.now().minusHours(3));
    }

    // 요청된 날짜에 따라 날씨 정보를 업데이트
    private WeatherInfo updateWeatherInfo(WeatherInfo info, String location, double lat, double lon, LocalDate date) {
        LocalDate today = LocalDate.now();

        // 요청된 날짜가 오늘 이전인 경우
        if (date.isBefore(today)) {
            return updateFromTimeMachineApi(info, location, lat, lon, date);
        }
        // 요청된 날짜가 오늘부터 7일 이내인 경우
        else if (!date.isAfter(today.plusDays(7))) {
            return updateFromForecastApi(info, location, lat, lon, date);
        }
        // 요청된 날짜가 오늘 이후 7일 이상인 경우
        else {
            // 추후 필요 시 장기 예보 데이터 업데이트 기능 구현
            // return updateFromLongTermForecastApi(info, location, lat, lon, date);
            throw new IllegalArgumentException("해당 날짜(" + date + ")에 대한 예보 데이터가 존재하지 않습니다.");
        }
    }

    // 예보 API를 통해 날씨 정보를 업데이트
    private WeatherInfo updateFromForecastApi(WeatherInfo info, String location, double lat, double lon, LocalDate date) {
        // OpenWeatherMap One Call API를 통해 예보 데이터 조회
        OneCallApiResponse response = weatherApiClient.fetchOneCallWeatherData(
                lat,
                lon,
                List.of("minutely", "hourly", "current", "alerts"),
                "metric",
                "kr"
        ).block(); // 블록킹 호출로 API 응답을 기다림

        // 예보 데이터에서 요청된 날짜에 해당하는 DailyData 찾기
        DailyData matchedDaily = response.getDaily().stream()
                .filter(d -> LocalDateTime.ofEpochSecond(d.getDt(), 0, ZoneOffset.UTC)
                        .toLocalDate().isEqual(date))
                .findFirst()
                .orElse(null);

        // 해당 날짜 데이터가 없는 경우 예외 처리
        if (matchedDaily == null) {
            throw new IllegalArgumentException("해당 날짜(" + date + ")에 대한 예보 데이터가 존재하지 않습니다.");
        }

        // 날씨 정보 갱신 및 저장
        mapDailyDataToWeatherInfo(info, matchedDaily, location, date);
        return weatherRepository.save(info);
    }

    // DailyData를 WeatherInfo로 매핑
    private void mapDailyDataToWeatherInfo(WeatherInfo info, DailyData data, String location, LocalDate date) {
        info.setWeather(Weather.fromCode(data.getWeather().getFirst().getId()));
        info.setDescription(info.getWeather().getDescription());
        info.setDailyTemperatureGap(data.getTemp().getMax() - data.getTemp().getMin());
        info.setFeelsLikeTemperature(data.getFeelsLike().getDay());
        info.setMaxTemperature(data.getTemp().getMax());
        info.setMinTemperature(data.getTemp().getMin());
        info.setLocation(location);
        info.setDate(date);
        info.setPop(data.getPop());
        info.setRain(data.getRain());
        info.setSnow(data.getSnow());
        info.setHumidity(data.getHumidity());
        info.setWindSpeed(data.getWindSpeed());
        info.setWindDeg(data.getWindDeg());
        info.setUvi(data.getUvi());
    }

    private WeatherInfo updateFromTimeMachineApi(WeatherInfo info, String location, double lat, double lon, LocalDate date) {
        // LocalDate를 Unix 타임스탬프로 변환, API요구사항
        long dt = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        TimeMachineApiResponse response = weatherApiClient.fetchTimeMachineWeatherData(
                lat,
                lon,
                dt,
                "metric",
                "kr"
        ).block();

        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            throw new IllegalArgumentException("해당 날짜(" + date + ")에 대한 과거 날씨 데이터가 존재하지 않습니다.");
        }


        List<TimeMachineData> hourlyData = response.getData();

        double minTemp = hourlyData.stream().mapToDouble(TimeMachineData::getTemp).min().orElse(0.0);
        double maxTemp = hourlyData.stream().mapToDouble(TimeMachineData::getTemp).max().orElse(0.0);

        TimeMachineData data = hourlyData.get(0);

        mapTimeMachineDataToWeatherInfo(info, data, location, date, minTemp, maxTemp);
        return weatherRepository.save(info);
    }

    // TimeMachineData를 WeatherInfo로 매핑
    private void mapTimeMachineDataToWeatherInfo(WeatherInfo info, TimeMachineData data, String location, LocalDate date, double minTemp, double maxTemp) {
        Weather weather = Weather.fromCode(data.getWeather().getFirst().getId());
        double pop = 0.0;
        int weatherCode = weather.getCode();

        if ((weatherCode >= 200 && weatherCode < 400) || (weatherCode >= 500 && weatherCode < 700)) {
            pop = 1.0;
        }

        info.setWeather(weather);
        info.setDescription(data.getWeather().getFirst().getDescription());
        info.setDailyTemperatureGap(maxTemp - minTemp);
        info.setFeelsLikeTemperature(data.getFeelsLike());
        info.setMaxTemperature(maxTemp);
        info.setMinTemperature(minTemp);
        info.setLocation(location);
        info.setDate(date);
        info.setPop(pop);
        info.setHumidity(data.getHumidity());
        info.setWindSpeed(data.getWindSpeed());
        info.setWindDeg(data.getWindDeg());
        info.setUvi(data.getUvi());
    }
}