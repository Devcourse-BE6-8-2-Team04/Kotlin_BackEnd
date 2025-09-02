package com.team04.back.global.initData

import com.team04.back.domain.cloth.cloth.entity.ClothInfo.Companion.create
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.Category.*
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.enums.ClothName.*
import com.team04.back.domain.cloth.cloth.enums.Material
import com.team04.back.domain.cloth.cloth.enums.Material.*
import com.team04.back.domain.cloth.cloth.enums.Style
import com.team04.back.domain.cloth.cloth.enums.Style.*
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.review.review.dto.ClothItemReqBody
import com.team04.back.domain.review.review.service.ReviewService
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import com.team04.back.domain.weather.weather.service.WeatherService
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@TestConfiguration
class TestInitData(
    private val reviewService: ReviewService,
    private val weatherService: WeatherService,
    private val clothService: ClothService
) {
    @Autowired
    @Lazy
    private lateinit var self: TestInitData

    @Bean
    fun testInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments? ->
            self.work1()
        }
    }

    @Transactional
    fun work1() {
//        if (clothService.count() > 0) return

        // === ClothInfo Style별 기본 데이터 ===
        // CASUAL_DAILY - Cold
        clothService.save(create(SWEATER, "https://i.postimg.cc/vZvNvYP5/cold-shirts.png", TOP, CASUAL_DAILY, WOOL, -10.0, 9.9)) // "cold_wool_warm_sweater"
        clothService.save(create(CARGO_PANTS, "https://i.postimg.cc/vmNXPc5x/cold-pants.png", BOTTOM, CASUAL_DAILY, WOOL, -10.0, 9.9)) // "cold_wool_warm_pants"
        clothService.save(create(LEATHER_BOOTS, "https://i.postimg.cc/65Xc1s4c/cold-shoes.png", SHOES, CASUAL_DAILY, LEATHER, -10.0, 9.9)) // "cold_leather_winter_boots"

        // CASUAL_DAILY - Warm
        clothService.save(create(DENIM_JACKET, "https://i.postimg.cc/sgfcspcZ/warm-shirts.png", TOP, CASUAL_DAILY, DENIM, 10.0, 20.0)) // "warm_denim_light_jacket"
        clothService.save(create(JEANS, "https://i.postimg.cc/HLV97dFY/warm-pants.png", BOTTOM, CASUAL_DAILY, DENIM, 10.0, 20.0)) // "warm_denim_jeans"
        clothService.save(create(SNEAKERS, "https://i.postimg.cc/SRp658RR/warm-shoes.png", SHOES, CASUAL_DAILY, null, 10.0, 20.0)) // "warm_canvas_sneakers"

        // CASUAL_DAILY - Hot
        clothService.save(create(T_SHIRT, "https://i.postimg.cc/h49bmCZd/hot-shirts.png", TOP, CASUAL_DAILY, COTTON, 20.1, 50.0)) // "hot_cotton_t_shirt"
        clothService.save(create(SHORTS, "https://i.postimg.cc/FsNbVjwn/hot-pant.png", BOTTOM, CASUAL_DAILY, COTTON, 20.1, 50.0)) // "hot_cotton_shorts"
        clothService.save(create(SANDALS, "https://i.postimg.cc/63CRd3cX/hot-shoes.png", SHOES, CASUAL_DAILY, null, 20.1, 50.0)) // "hot_synthetic_sandals"

        // FORMAL_OFFICE - Cold
        clothService.save(create(SWEATER,"https://i.postimg.cc/1XPFMQ7N/cold-shirts.png", TOP, FORMAL_OFFICE, WOOL, -10.0, 9.9)) // "cold_wool_sweater"
        clothService.save(create(JEANS, "https://i.postimg.cc/DwmXx565/cold-pants.png", BOTTOM, FORMAL_OFFICE, WOOL, -10.0, 9.9)) // "cold_wool_dress_pants"
        clothService.save(create(LEATHER_BOOTS, "https://i.postimg.cc/Dwt8Bh8y/cold-shoes.png", SHOES, FORMAL_OFFICE, LEATHER, -10.0, 9.9)) // "cold_leather_boots"

        // FORMAL_OFFICE - Warm
        clothService.save(create(BLAZER, "https://i.postimg.cc/fLskQvcG/warm-shirts.png", TOP, FORMAL_OFFICE, WOOL, 10.0, 20.0)) // "warm_blend_blazer"
        clothService.save(create(CHINOS, "https://i.postimg.cc/J06hJVf8/warm-pants.png", BOTTOM, FORMAL_OFFICE, COTTON, 10.0, 20.0)) // "warm_cotton_chinos"
        clothService.save(create(OXFORDS, "https://i.postimg.cc/9F00209Q/warm-shoes.png", SHOES, FORMAL_OFFICE, LEATHER, 10.0, 20.0)) // "warm_leather_oxfords"

        // FORMAL_OFFICE - Hot
        clothService.save(create(DRESS_SHIRT, "https://i.postimg.cc/MGSznpnR/hot-shirts.png", TOP, FORMAL_OFFICE, COTTON, 20.1, 50.0)) // "hot_cotton_dress_shirt"
        clothService.save(create(SLACKS, "https://i.postimg.cc/wBQgLYNr/hot-pants.png", BOTTOM, FORMAL_OFFICE, POLYESTER, 20.1, 50.0)) // "hot_polyester_slacks"
        clothService.save(create(LOAFERS, "https://i.postimg.cc/6qm9qVZ5/hot-shoes.png", SHOES, FORMAL_OFFICE, LEATHER, 20.1, 50.0)) // "hot_leather_loafers"

        // OUTDOOR - Cold
        clothService.save(create(PADDING, "https://i.postimg.cc/sxvrkBLF/cold-shirts.png", TOP, OUTDOOR, WOOL, -10.0, 9.9)) // "cold_wool_thermal_jacket"
        clothService.save(create(SKI_PANTS, "https://i.postimg.cc/nc9xTD7B/cold-pants.png", BOTTOM, OUTDOOR, WOOL, -10.0, 9.9)) // "cold_wool_thermal_pants"
        clothService.save(create(HIKING_SHOES, "https://i.postimg.cc/cLQSGGFJ/cold-shoes.png", SHOES, OUTDOOR, LEATHER, -10.0, 9.9)) // "cold_leather_hiking_boots"

        // OUTDOOR - Warm
        clothService.save(create(WINDBREAKER, "https://i.postimg.cc/nVjVSgfX/warm-shirts.png", TOP, OUTDOOR, NYLON, 10.0, 20.0)) // "warm_nylon_windbreaker"
        clothService.save(create(CARGO_PANTS, "https://i.postimg.cc/rzXPfKXN/warm-pants.png", BOTTOM, OUTDOOR, COTTON, 10.0, 20.0)) // "warm_cotton_cargo_pants"
        clothService.save(create(SNEAKERS, "https://i.postimg.cc/8k2p24Tt/warm-shoes.png", SHOES, OUTDOOR, null, 10.0, 20.0)) // "warm_mesh_trail_sneakers"

        // OUTDOOR - Hot
        clothService.save(create(FUNCTIONAL_T_SHIRT, "https://i.postimg.cc/2yqsHsqr/hot-shirts.png", TOP, OUTDOOR, POLYESTER, 20.1, 50.0)) // "hot_polyester_moisture_wicking_shirt"
        clothService.save(create(SHORTS, "https://i.postimg.cc/0yGLHdNM/hot-pants.png", BOTTOM, OUTDOOR, POLYESTER, 20.1, 50.0)) // "hot_polyester_shorts"
        clothService.save(create(SANDALS, "https://i.postimg.cc/vBKj600j/hot-shoes.png", SHOES, OUTDOOR, null, 20.1, 50.0)) // "hot_synthetic_sandals"

        // DATE_LOOK - Cold
        clothService.save(create(SWEATER, "https://i.postimg.cc/dtcFH09f/cold-shirts.png", TOP, DATE_LOOK, WOOL, -10.0, 9.9)) // "cold_wool_knit_sweater"
        clothService.save(create(CORDUROY_PANTS, "https://i.postimg.cc/9QJjQYj3/cold-pants.png", BOTTOM, DATE_LOOK, CORDUROY, -10.0, 9.9)) // "cold_corduroy_pants"
        clothService.save(create(ANKLE_BOOTS, "https://i.postimg.cc/FKzXdk9Y/cold-shoes.png", SHOES, DATE_LOOK, LEATHER, -10.0, 9.9)) // "cold_leather_ankle_boots"

        // DATE_LOOK - Warm
        clothService.save(create(CARDIGAN, "https://i.postimg.cc/NMrhbh2Y/warm-shirts.png", TOP, DATE_LOOK, COTTON, 10.0, 20.0)) // "warm_cotton_cardigan"
        clothService.save(create(JEANS, "https://i.postimg.cc/9X7VY9yT/warm-pants.png", BOTTOM, DATE_LOOK, DENIM, 10.0, 20.0)) // "warm_denim_slim_jeans"
        clothService.save(create(FLATS, "https://i.postimg.cc/ryxXrkLN/warm-shoes.png", SHOES, DATE_LOOK, LEATHER, 10.0, 20.0)) // "warm_leather_ballet_flats"

        // DATE_LOOK - Hot
        clothService.save(create(BLOUSE, "https://i.postimg.cc/521x0tYz/hot-shirts.png", TOP, DATE_LOOK, POLYESTER, 20.1, 50.0)) // "hot_polyester_floral_blouse"
        clothService.save(create(SKIRT, "https://i.postimg.cc/3NmTrM39/hot-pants.png", BOTTOM, DATE_LOOK, POLYESTER, 20.1, 50.0)) // "hot_polyester_skirt"
        clothService.save(create(HEELS, "https://i.postimg.cc/XY8ny0dz/hot-shoes.png", SHOES, DATE_LOOK, LEATHER, 20.1, 50.0)) // "hot_leather_open_toe_heels"

        // === ExtraCloth 저장 ===
        clothService.save(create(HAT, "https://i.postimg.cc/DytgTxVq/hat.png", Category.EXTRA, null, null, null, null)) // weather = Weather.HEAT_WAVE
        clothService.save(create(CAP, "https://i.postimg.cc/ZYDkrz34/cap.png", Category.EXTRA, null, null, null, null))
        clothService.save(create(BEANIE, "https://i.postimg.cc/GhBWKgJY/beanie.png", Category.EXTRA, null, null, null, null))
        clothService.save(create(SCARF, "https://i.postimg.cc/XXMQQL4c/scarf.png", Category.EXTRA, null, null, null, null))
        clothService.save(create(GLOVES, "https://i.postimg.cc/T15tVFM3/gloves.png", Category.EXTRA, null, null, null, null))
        clothService.save(create(BELT, "https://i.postimg.cc/R0srGk87/belt.png", Category.EXTRA, null, null, null, null))
        clothService.save(create(BAG, "https://i.postimg.cc/Y9Z5B1tj/bag.png", Category.EXTRA, null, null, null, null))
        clothService.save(create(BACKPACK, "https://i.postimg.cc/L6dcyByb/backpack.png", Category.EXTRA, null, null, null, null))
        clothService.save(create(CROSSBODY_BAG, "https://i.postimg.cc/13sLSR10/crossbody-bag.png", Category.EXTRA, null, null, null, null))
        clothService.save(create(SUNGLASSES, "https://i.postimg.cc/6QQg7H3y/sunglasses.png", Category.EXTRA, null, null, null, null))
        clothService.save(create(UMBRELLA, "https://i.postimg.cc/PqFc1mpK/umbrella.png", Category.EXTRA, null, null, null, null)) // weather = Weather.MODERATE_RAIN, Weather.SNOW
        clothService.save(create(MASK, "https://i.postimg.cc/9M6nnCB7/mask.png", Category.EXTRA, null, null, null, null)) // weather = Weather.MIST


        // === ClothInfo ClothName별 기본 데이터 ===
        // TOP
        clothService.save(create(T_SHIRT, "https://i.postimg.cc/GhZ3xc7G/t-shirt.png", TOP, null, null, null, null))
        clothService.save(create(SWEATSHIRT, "https://i.postimg.cc/d0qbjN96/sweatshirt.png", TOP, null, null, null, null))
        clothService.save(create(HOODIE, "https://i.postimg.cc/QMHRz9RK/hoodie.png", TOP, null, null, null, null))
        clothService.save(create(SHIRT, "https://i.postimg.cc/YqMTHy5H/shirt.png", TOP, null, null, null, null))
        clothService.save(create(DRESS_SHIRT, "https://i.postimg.cc/NM6JJn1L/dress-shirt.png", TOP, null, null, null, null))
        clothService.save(create(BLOUSE, "https://i.postimg.cc/VvVtDhMC/blouse.png", TOP, null, null, null, null))
        clothService.save(create(SWEATER, "https://i.postimg.cc/tgjxSW12/sweater.png", TOP, null, null, null, null))
        clothService.save(create(CARDIGAN, "https://i.postimg.cc/GhvYkXtH/cardigan.png", TOP, null, null, null, null))
        clothService.save(create(COAT, "https://i.postimg.cc/g2NxDQWk/coat.png", TOP, null, null, null, null))
        clothService.save(create(JACKET, "https://i.postimg.cc/8z1f1VGN/jacket.png", TOP, null, null, null, null))
        clothService.save(create(LEATHER_JACKET, "https://i.postimg.cc/ncw8GGWf/leather-jacket.png", TOP, null, null, null, null))
        clothService.save(create(DENIM_JACKET, "https://i.postimg.cc/k53pT1Fz/denim-jacket.png", TOP, null, null, null, null))
        clothService.save(create(BLAZER, "https://i.postimg.cc/T1F9p1tF/blazer.png", TOP, null, null, null, null))
        clothService.save(create(PADDING, "https://i.postimg.cc/qBSXctGZ/padding.png", TOP, null, null, null, null))
        clothService.save(create(VEST, "https://i.postimg.cc/gjZhtDKt/vest.png", TOP, null, null, null, null))
        clothService.save(create(WINDBREAKER, "https://i.postimg.cc/3JLhbTd6/jacket.png", TOP, null, null, null, null))
        clothService.save(create(FUNCTIONAL_T_SHIRT, "https://i.postimg.cc/QMk1RZyL/functional-t-shirt.png", TOP, null, null, null, null))

        // BOTTOM
        clothService.save(create(JEANS, "https://i.postimg.cc/rwSjHTJT/jeans.png", BOTTOM, null, null, null, null))
        clothService.save(create(SLACKS, "https://i.postimg.cc/bYDBRKKy/slacks.png", BOTTOM, null, null, null, null))
        clothService.save(create(SHORTS, "https://i.postimg.cc/448Zqk3r/shorts.png", BOTTOM, null, null, null, null))
        clothService.save(create(SKIRT, "https://i.postimg.cc/3wZYh9pq/skirt.png", BOTTOM, null, null, null, null))
        clothService.save(create(JOGGER_PANTS, "https://i.postimg.cc/Hn9hcPWG/jogger-pants.png", BOTTOM, null, null, null, null))
        clothService.save(create(TRACK_PANTS, "https://i.postimg.cc/vmdMVCbT/track-pants.png", BOTTOM, null, null, null, null))
        clothService.save(create(LEGGINGS, "https://i.postimg.cc/3NkkWzFN/leggings.png", BOTTOM, null, null, null, null))
        clothService.save(create(CARGO_PANTS, "https://i.postimg.cc/9f3sLZv8/cargo-pants.png", BOTTOM, null, null, null, null))
        clothService.save(create(CORDUROY_PANTS, "https://i.postimg.cc/g2LfyNBL/corduroy-pants.png", BOTTOM, null, null, null, null))
        clothService.save(create(CHINOS, "https://i.postimg.cc/R0QpG81h/chinos.png", BOTTOM, null, null, null, null))
        clothService.save(create(SKI_PANTS, "https://i.postimg.cc/02y6NJXB/ski-pants.png", BOTTOM, null, null, null, null))

        // SHOES
        clothService.save(create(SNEAKERS, "https://i.postimg.cc/1zkQQTYs/sneakers.png", SHOES, null, null, null, null))
        clothService.save(create(ATHLETIC_SHOES, "https://i.postimg.cc/Bbg92vZn/athletic-shoes.png", SHOES, null, null, null, null))
        clothService.save(create(FLATS, "https://i.postimg.cc/J7K23vc0/flats.png", SHOES, null, null, null, null))
        clothService.save(create(HEELS, "https://i.postimg.cc/bND9GBmK/heels.png", SHOES, null, null, null, null))
        clothService.save(create(LOAFERS, "https://i.postimg.cc/2SFGmXjh/loafers.png", SHOES, null, null, null, null))
        clothService.save(create(SLIPPERS, "https://i.postimg.cc/nc35YgxT/slippers.png", SHOES, null, null, null, null))
        clothService.save(create(LEATHER_BOOTS, "https://i.postimg.cc/tgBYXXGW/leather-boots.png", SHOES, null, null, null, null))
        clothService.save(create(FUR_BOOTS, "https://i.postimg.cc/YCpnVFsH/fur-boots.png", SHOES, null, null, null, null))
        clothService.save(create(RAIN_BOOTS, "https://i.postimg.cc/jSZgrjpJ/rain-boots.png", SHOES, null, null, null, null))
        clothService.save(create(SANDALS, "https://i.postimg.cc/3R7dCKwn/sandals.png", SHOES, null, null, null, null))
        clothService.save(create(OXFORDS, "https://i.postimg.cc/DzFbQYyx/oxfords.png", SHOES, null, null, null, null))
        clothService.save(create(HIKING_SHOES, "https://i.postimg.cc/4xwh5Q7Z/hiking-shoes.png", SHOES, null, null, null, null))
        clothService.save(create(ANKLE_BOOTS, "https://i.postimg.cc/Xq8VbQ0Y/ankle-boots.png", SHOES, null, null, null, null))


        if (reviewService.count() > 0) return

        val weatherInfo1 = WeatherInfo(Weather.CLEAR_SKY, 7.0, 33.0, 37.0, 24.0, "서울", LocalDate.parse("2022-07-28"))
        val weatherInfo2 = WeatherInfo(Weather.SNOW, 8.0, -6.0, -2.0, -10.0, "삿포로", LocalDate.parse("2023-01-15"))
        val weatherInfo3 = WeatherInfo(Weather.FEW_CLOUDS, 12.0, 26.0, 29.0, 17.0, "파리", LocalDate.parse("2023-08-05"))
        val weatherInfo4 = WeatherInfo(Weather.MODERATE_RAIN, 7.0, 14.0, 16.0, 9.0, "런던", LocalDate.parse("2023-10-20"))

        every { weatherService.save(any()) } answers { it.invocation.args[0] as WeatherInfo } // 전달받은 객체를 그대로 반환

        weatherService.save(weatherInfo1)
        weatherService.save(weatherInfo2)
        weatherService.save(weatherInfo3)
        weatherService.save(weatherInfo4)

        reviewService.createReview(
            email = "user1@test.com",
            password = "1234",
            imageUrl = "https://images.unsplash.com/photo-1658874761235-8d56cbd5da2d?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8b290ZCUyMCVFQyU5NyVBQyVFQiVBNiU4NHxlbnwwfHwwfHx8MA%3D%3D",
            title = "서울 여름 진짜 장난 아니네요;;",
            sentence = "요즘 서울 진짜 미쳤어요... 햇빛이 너무 따갑고, 낮엔 밖에 나가면 숨이 턱턱 막혀요. 아침저녁은 그나마 나은데 낮 기온은 거의 37도 가까이 올라가네요. 에어컨 없으면 진짜 버티기 힘듭니다 ㅠㅠ",
            tagString = "#한국여름#폭염주의",
            weatherInfo = weatherInfo1,
            clothList = listOf(
                ClothItemReqBody(
                    clothName = ClothName.FUNCTIONAL_T_SHIRT,
                    category = Category.TOP,
                    style = Style.CASUAL_DAILY,
                    material = Material.POLYESTER,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.SHORTS,
                    category = Category.BOTTOM,
                    style = Style.CASUAL_DAILY,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.ATHLETIC_SHOES,
                    category = Category.SHOES,
                    style = Style.CASUAL_DAILY,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.CAP,
                    category = Category.EXTRA,
                    isRecommend = true
                )
            )
        )
        reviewService.createReview(
            email = "user2@test.com",
            password = "1234",
            imageUrl = "https://images.unsplash.com/photo-1638385583463-e3d424c22916?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjB8fCVFQyU5RCVCQyVFQiVCMyVCOCUyMCVFQyU4MiVCRiVFRCU4RiVBQyVFQiVBMSU5QyUyMCVFQyVCRCU5NCVFQiU5NCU5NHxlbnwwfHwwfHx8MA%3D%3D",
            title = "삿포로는 진짜 눈나라가 맞아요",
            sentence = "삿포로의 겨울은 진짜 말 그대로 눈의 도시예요. 하루에도 몇 번씩 폭설이 내리고, 도로에 눈이 수북히 쌓여요. 체감 온도는 -15도까지도 떨어지고... 그래도 하얀 세상이 너무 예쁘고, 눈 오는 날 산책하는 재미도 있어요. 다만 옷 정말 단단히 입어야 합니다!",
            tagString = "#일본#삿포로#겨울#눈폭탄",
            weatherInfo = weatherInfo2,
            clothList = listOf(
                ClothItemReqBody(
                    clothName = ClothName.COAT,
                    category = Category.TOP,
                    style = Style.DATE_LOOK,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.JEANS,
                    category = Category.BOTTOM,
                    style = Style.DATE_LOOK,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.FUR_BOOTS,
                    category = Category.SHOES,
                    style = Style.DATE_LOOK,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.SCARF,
                    category = Category.EXTRA,
                    material = Material.WOOL,
                    isRecommend = true
                )
            )
        )
        reviewService.createReview(
            email = "user3@test.com",
            password = "1234",
            imageUrl = "https://images.unsplash.com/photo-1569789496053-095f2d6e3b05?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OTR8fCVFRCU4QyU4QyVFQiVBNiVBQyUyMCVFQyU4MiVCMCVFQyVCMSU4NXxlbnwwfHwwfHx8MA%3D%3D",
            title = "파리 여름밤 산책은 무조건이에요",
            sentence = "파리에 처음 왔는데 여름 날씨가 너무 좋아요. 낮에는 약간 덥긴 한데 그늘에 들어가면 시원하고, 밤에는 선선해서 산책하기 딱이에요. 센 강 주변이나 에펠탑 근처 돌아다니다 보면 기분이 정말 좋아져요. 덕분에 하루에 만 보 넘게 걷고 있어요 :)",
            tagString = "#파리#유럽여행#여름날씨#산책",
            weatherInfo = weatherInfo3,
            clothList = listOf(
                ClothItemReqBody(
                    clothName = ClothName.T_SHIRT,
                    category = Category.TOP,
                    style = Style.CASUAL_DAILY,
                    material = Material.COTTON,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.CARDIGAN,
                    category = Category.TOP,
                    style = Style.CASUAL_DAILY,
                    material = Material.COTTON,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.CHINOS,
                    category = Category.BOTTOM,
                    style = Style.CASUAL_DAILY,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.ATHLETIC_SHOES,
                    category = Category.SHOES,
                    style = Style.CASUAL_DAILY,
                    isRecommend = true
                )
            )
        )
        reviewService.createReview(
            email = "user4@test.com",
            password = "1234",
            imageUrl = "https://images.unsplash.com/photo-1518090753814-263ac71fc863?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8JUVCJTlGJUIwJUVCJThEJTk4JTIwJUVDJTlBJUIwJUVDJTgyJUIwfGVufDB8fDB8fHww",
            title = "런던은 정말 자주 비가 오네요... 우산 필수입니다 ☔",
            sentence = "런던 가을비는 정말 자주 오는 편이네요. 하루에도 몇 번씩 비가 왔다 그쳤다 하고, 흐린 날이 많아요. 덕분에 분위기는 정말 좋지만, 외출할 때마다 우산은 필수예요. 현지 사람들은 그냥 맞고 다니던데... 전 그건 아직 무리네요 ㅎㅎ",
            tagString = "#런던#가을비#우산#영국날씨",
            weatherInfo = weatherInfo4,
            clothList = listOf(
                ClothItemReqBody(
                    clothName = ClothName.COAT,
                    category = Category.TOP,
                    style = Style.CASUAL_DAILY,
                    material = Material.WOOL,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.SLACKS,
                    category = Category.BOTTOM,
                    style = Style.CASUAL_DAILY,
                    material = Material.POLYESTER,
                    isRecommend = true
                ),
                ClothItemReqBody(
                    clothName = ClothName.SNEAKERS,
                    category = Category.SHOES,
                    style = Style.CASUAL_DAILY,
                    isRecommend = false
                ),
                ClothItemReqBody(
                    clothName = ClothName.UMBRELLA,
                    category = Category.EXTRA,
                    isRecommend = true
                )
            )
        )

        println("초기 데이터가 생성되었습니다.")
    }
}