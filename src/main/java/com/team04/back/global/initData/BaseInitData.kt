package com.team04.back.global.initData

import com.team04.back.domain.review.review.service.ReviewService
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import com.team04.back.domain.weather.weather.service.WeatherService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Configuration
@Profile("dev")
class BaseInitData(
    private val reviewService: ReviewService,
    private val weatherService: WeatherService
) {
    @Autowired
    @Lazy
    private lateinit var self: BaseInitData

    @Bean
    fun baseInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments? ->
            self.work1()
        }
    }

    @Transactional
    fun work1() {
        if (reviewService.count() > 0) return

        val weatherInfo1 = WeatherInfo(Weather.CLEAR_SKY, 7.0, 33.0, 37.0, 24.0, "서울", LocalDate.parse("2022-07-28"))
        val weatherInfo2 = WeatherInfo(Weather.SNOW, 8.0, -6.0, -2.0, -10.0, "삿포로", LocalDate.parse("2023-01-15"))
        val weatherInfo3 = WeatherInfo(Weather.FEW_CLOUDS, 12.0, 26.0, 29.0, 17.0, "파리", LocalDate.parse("2023-08-05"))
        val weatherInfo4 = WeatherInfo(Weather.MODERATE_RAIN, 7.0, 14.0, 16.0, 9.0, "런던", LocalDate.parse("2023-10-20"))
        weatherService.save(weatherInfo1)
        weatherService.save(weatherInfo2)
        weatherService.save(weatherInfo3)
        weatherService.save(weatherInfo4)

        reviewService.createReview(
            "user1@test.com",
            "1234",
            "https://images.unsplash.com/photo-1658874761235-8d56cbd5da2d?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8b290ZCUyMCVFQyU5NyVBQyVFQiVBNiU4NHxlbnwwfHwwfHx8MA%3D%3D",
            "서울 여름 진짜 장난 아니네요;;",
            "요즘 서울 진짜 미쳤어요... 햇빛이 너무 따갑고, 낮엔 밖에 나가면 숨이 턱턱 막혀요. 아침저녁은 그나마 나은데 낮 기온은 거의 37도 가까이 올라가네요. 에어컨 없으면 진짜 버티기 힘듭니다 ㅠㅠ",
            "#한국여름#폭염주의",
            weatherInfo1
        )
        reviewService.createReview(
            "user2@test.com",
            "1234",
            "https://images.unsplash.com/photo-1638385583463-e3d424c22916?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjB8fCVFQyU5RCVCQyVFQiVCMyVCOCUyMCVFQyU4MiVCRiVFRCU4RiVBQyVFQiVBMSU5QyUyMCVFQyVCRCU5NCVFQiU5NCU5NHxlbnwwfHwwfHx8MA%3D%3D",
            "삿포로는 진짜 눈나라가 맞아요",
            "삿포로의 겨울은 진짜 말 그대로 눈의 도시예요. 하루에도 몇 번씩 폭설이 내리고, 도로에 눈이 수북히 쌓여요. 체감 온도는 -15도까지도 떨어지고... 그래도 하얀 세상이 너무 예쁘고, 눈 오는 날 산책하는 재미도 있어요. 다만 옷 정말 단단히 입어야 합니다!",
            "#일본#삿포로#겨울#눈폭탄",
            weatherInfo2
        )
        reviewService.createReview(
            "user3@test.com",
            "1234",
            "https://images.unsplash.com/photo-1569789496053-095f2d6e3b05?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OTR8fCVFRCU4QyU4QyVFQiVBNiVBQyUyMCVFQyU4MiVCMCVFQyVCMSU4NXxlbnwwfHwwfHx8MA%3D%3D",
            "파리 여름밤 산책은 무조건이에요",
            "파리에 처음 왔는데 여름 날씨가 너무 좋아요. 낮에는 약간 덥긴 한데 그늘에 들어가면 시원하고, 밤에는 선선해서 산책하기 딱이에요. 센 강 주변이나 에펠탑 근처 돌아다니다 보면 기분이 정말 좋아져요. 덕분에 하루에 만 보 넘게 걷고 있어요 :)",
            "#파리#유럽여행#여름날씨#산책",
            weatherInfo3
        )
        reviewService.createReview(
            "user4@test.com",
            "1234",
            "https://images.unsplash.com/photo-1518090753814-263ac71fc863?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8JUVCJTlGJUIwJUVCJThEJTk4JTIwJUVDJTlBJUIwJUVDJTgyJUIwfGVufDB8fDB8fHww",
            "런던은 정말 자주 비가 오네요... 우산 필수입니다 ☔",
            "런던 가을비는 정말 자주 오는 편이네요. 하루에도 몇 번씩 비가 왔다 그쳤다 하고, 흐린 날이 많아요. 덕분에 분위기는 정말 좋지만, 외출할 때마다 우산은 필수예요. 현지 사람들은 그냥 맞고 다니던데... 전 그건 아직 무리네요 ㅎㅎ",
            "#런던#가을비#우산#영국날씨",
            weatherInfo4
        )

        println("초기 데이터가 생성되었습니다.")
    }
}