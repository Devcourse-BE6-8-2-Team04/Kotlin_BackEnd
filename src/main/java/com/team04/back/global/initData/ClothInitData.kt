package com.team04.back.global.initData

import com.team04.back.domain.cloth.cloth.entity.ClothInfo.Companion.create
import com.team04.back.domain.cloth.cloth.entity.ExtraCloth.Companion.create
import com.team04.back.domain.cloth.cloth.enums.Category.*
import com.team04.back.domain.cloth.cloth.enums.ClothName.*
import com.team04.back.domain.cloth.cloth.enums.Material.*
import com.team04.back.domain.cloth.cloth.enums.Style.*
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.weather.weather.enums.Weather
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Transactional

@Configuration
class ClothInitData(
    private val clothService: ClothService
) {
    @Autowired
    @Lazy
    private lateinit var self: ClothInitData

    @Bean
    fun clothInitDataRunner(): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments -> self.insertData() }
    }

    @Transactional
    fun insertData() {
        if (clothService.count() > 0 || clothService.countExtra() > 0) return

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
        clothService.save(create("Heatwave Hat", "https://i.postimg.cc/fbzgqVkM/image.png", Weather.HEAT_WAVE))
        clothService.save(create("Moderate Rain Umbrella", "https://i.postimg.cc/RFm813m1/image.png", Weather.MODERATE_RAIN))
        clothService.save(create("Snow Umbrella", "https://i.postimg.cc/RFm813m1/image.png", Weather.SNOW))
        clothService.save(create("Dust Mask", "https://i.postimg.cc/Sx7c2jZb/image.png", Weather.MIST))


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
    }
}
