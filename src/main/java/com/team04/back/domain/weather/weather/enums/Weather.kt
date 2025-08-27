package com.team04.back.domain.weather.weather.enums

enum class Weather(val code: Int, val description: String) {
    // Group 2xx: Thunderstorm
    THUNDERSTORM_LIGHT_RAIN(200, "약한 비를 동반한 뇌우"),
    THUNDERSTORM_RAIN(201, "비를 동반한 뇌우"),
    THUNDERSTORM_HEAVY_RAIN(202, "강한 비를 동반한 뇌우"),
    LIGHT_THUNDERSTORM(210, "약한 뇌우"),
    THUNDERSTORM(211, "뇌우"),
    HEAVY_THUNDERSTORM(212, "강한 뇌우"),
    RAGGED_THUNDERSTORM(221, "불규칙한 뇌우"),
    THUNDERSTORM_LIGHT_DRIZZLE(230, "약한 이슬비를 동반한 뇌우"),
    THUNDERSTORM_DRIZZLE(231, "이슬비를 동반한 뇌우"),
    THUNDERSTORM_HEAVY_DRIZZLE(232, "강한 이슬비를 동반한 뇌우"),

    // Group 3xx: Drizzle
    LIGHT_DRIZZLE(300, "약한 이슬비"),
    DRIZZLE(301, "이슬비"),
    HEAVY_DRIZZLE(302, "강한 이슬비"),
    LIGHT_DRIZZLE_RAIN(310, "약한 이슬비와 비"),
    DRIZZLE_RAIN(311, "이슬비와 비"),
    HEAVY_DRIZZLE_RAIN(312, "강한 이슬비와 비"),
    SHOWER_RAIN_AND_DRIZZLE(313, "소나기와 이슬비"),
    HEAVY_SHOWER_RAIN_AND_DRIZZLE(314, "강한 소나기와 이슬비"),
    SHOWER_DRIZZLE(321, "소나기성 이슬비"),

    // Group 5xx: Rain
    LIGHT_RAIN(500, "약한 비"),
    MODERATE_RAIN(501, "보통 비"),
    HEAVY_RAIN(502, "강한 비"),
    VERY_HEAVY_RAIN(503, "매우 강한 비"),
    EXTREME_RAIN(504, "극심한 비"),
    FREEZING_RAIN(511, "어는 비"),
    LIGHT_SHOWER_RAIN(520, "약한 소나기"),
    SHOWER_RAIN(521, "소나기"),
    HEAVY_SHOWER_RAIN(522, "강한 소나기"),
    RAGGED_SHOWER_RAIN(531, "불규칙한 소나기"),

    // Group 6xx: Snow
    LIGHT_SNOW(600, "약한 눈"),
    SNOW(601, "눈"),
    HEAVY_SNOW(602, "강한 눈"),
    SLEET(611, "진눈깨비"),
    LIGHT_SHOWER_SLEET(612, "약한 소나기 진눈깨비"),
    SHOWER_SLEET(613, "소나기 진눈깨비"),
    LIGHT_RAIN_AND_SNOW(615, "약한 비와 눈"),
    RAIN_AND_SNOW(616, "비와 눈"),
    LIGHT_SHOWER_SNOW(620, "약한 소나기 눈"),
    SHOWER_SNOW(621, "소나기 눈"),
    HEAVY_SHOWER_SNOW(622, "강한 소나기 눈"),

    // Group 7xx: Atmosphere
    MIST(701, "안개"),
    SMOKE(711, "연기"),
    HAZE(721, "실안개"),
    SAND_DUST_WHIRLS(731, "모래/먼지 회오리"),
    FOG(741, "짙은 안개"),
    SAND(751, "모래"),
    DUST(761, "먼지"),
    VOLCANIC_ASH(762, "화산재"),
    SQUALLS(771, "돌풍"),
    TORNADO(781, "토네이도"),

    // Group 800: Clear
    CLEAR_SKY(800, "맑은 하늘"),

    // Group 80x: Clouds
    FEW_CLOUDS(801, "약간의 구름"),
    SCATTERED_CLOUDS(802, "흩어진 구름"),
    BROKEN_CLOUDS(803, "부서진 구름"),
    OVERCAST_CLOUDS(804, "흐린 하늘"),

    // 폭염
    HEAT_WAVE(900, "폭염");

    companion object {
        private val codeMap: Map<Int, Weather> = entries.associateBy { it.code }

        /**
         * 주어진 코드에 해당하는 Weather enum을 반환합니다.
         *
         * @param code 날씨 코드
         * @return 해당하는 Weather enum
         * @throws IllegalArgumentException 코드에 해당하는 날씨가 없을 경우 예외 발생
         */
        fun fromCode(code: Int): Weather =
            codeMap[code] ?: throw IllegalArgumentException("Unknown weather code: $code")
    }
}
