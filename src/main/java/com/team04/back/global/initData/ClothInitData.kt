package com.team04.back.global.initData

import com.team04.back.domain.cloth.cloth.entity.ClothInfo.Companion.create
import com.team04.back.domain.cloth.cloth.entity.ExtraCloth.Companion.create
import com.team04.back.domain.cloth.cloth.enums.Category
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

        // === ClothInfo 저장 ===
        // CASUAL_DAILY - Cold
        clothService.save(create("cold_wool_warm_sweater", "https://i.postimg.cc/vZvNvYP5/cold-shirts.png", Category.CASUAL_DAILY, -10.0, 9.9))
        clothService.save(create("cold_wool_warm_pants", "https://i.postimg.cc/vmNXPc5x/cold-pants.png", Category.CASUAL_DAILY, -10.0, 9.9))
        clothService.save(create("cold_leather_winter_boots", "https://i.postimg.cc/65Xc1s4c/cold-shoes.png", Category.CASUAL_DAILY, -10.0, 9.9))

        // CASUAL_DAILY - Warm
        clothService.save(create("warm_denim_light_jacket", "https://i.postimg.cc/sgfcspcZ/warm-shirts.png", Category.CASUAL_DAILY, 10.0, 20.0))
        clothService.save(create("warm_denim_jeans", "https://i.postimg.cc/HLV97dFY/warm-pants.png", Category.CASUAL_DAILY, 10.0, 20.0))
        clothService.save(create("warm_canvas_sneakers", "https://i.postimg.cc/SRp658RR/warm-shoes.png", Category.CASUAL_DAILY, 10.0, 20.0))

        // CASUAL_DAILY - Hot
        clothService.save(create("hot_cotton_tshirt", "https://i.postimg.cc/h49bmCZd/hot-shirts.png", Category.CASUAL_DAILY, 20.1, 50.0))
        clothService.save(create("hot_cotton_shorts", "https://i.postimg.cc/FsNbVjwn/hot-pant.png", Category.CASUAL_DAILY, 20.1, 50.0))
        clothService.save(create("hot_synthetic_sandals", "https://i.postimg.cc/63CRd3cX/hot-shoes.png", Category.CASUAL_DAILY, 20.1, 50.0))

        // FORMAL_OFFICE - Cold
        clothService.save(create("cold_wool_sweater","https://i.postimg.cc/1XPFMQ7N/cold-shirts.png", Category.FORMAL_OFFICE, -10.0, 9.9))
        clothService.save(create("cold_wool_dress_pants", "https://i.postimg.cc/DwmXx565/cold-pants.png", Category.FORMAL_OFFICE, -10.0, 9.9))
        clothService.save(create("cold_leather_boots", "https://i.postimg.cc/Dwt8Bh8y/cold-shoes.png", Category.FORMAL_OFFICE, -10.0, 9.9))

        // FORMAL_OFFICE - Warm
        clothService.save(create("warm_blend_blazer", "https://i.postimg.cc/fLskQvcG/warm-shirts.png", Category.FORMAL_OFFICE, 10.0, 20.0))
        clothService.save(create("warm_cotton_chinos", "https://i.postimg.cc/J06hJVf8/warm-pants.png", Category.FORMAL_OFFICE, 10.0, 20.0))
        clothService.save(create("warm_leather_oxfords", "https://i.postimg.cc/9F00209Q/warm-shoes.png", Category.FORMAL_OFFICE, 10.0, 20.0))

        // FORMAL_OFFICE - Hot
        clothService.save(create("hot_cotton_dress_shirt", "https://i.postimg.cc/MGSznpnR/hot-shirts.png", Category.FORMAL_OFFICE, 20.1, 50.0))
        clothService.save(create("hot_polyester_slacks", "https://i.postimg.cc/wBQgLYNr/hot-pants.png", Category.FORMAL_OFFICE, 20.1, 50.0))
        clothService.save(create("hot_leather_loafers", "https://i.postimg.cc/6qm9qVZ5/hot-shoes.png", Category.FORMAL_OFFICE, 20.1, 50.0))

        // OUTDOOR - Cold
        clothService.save(create("cold_wool_thermal_jacket", "https://i.postimg.cc/sxvrkBLF/cold-shirts.png", Category.OUTDOOR, -10.0, 9.9))
        clothService.save(create("cold_wool_thermal_pants", "https://i.postimg.cc/nc9xTD7B/cold-pants.png", Category.OUTDOOR, -10.0, 9.9))
        clothService.save(create("cold_leather_hiking_boots", "https://i.postimg.cc/cLQSGGFJ/cold-shoes.png", Category.OUTDOOR, -10.0, 9.9))

        // OUTDOOR - Warm
        clothService.save(create("warm_nylon_windbreaker", "https://i.postimg.cc/nVjVSgfX/warm-shirts.png", Category.OUTDOOR, 10.0, 20.0))
        clothService.save(create("warm_cotton_cargo_pants", "https://i.postimg.cc/rzXPfKXN/warm-pants.png", Category.OUTDOOR, 10.0, 20.0))
        clothService.save(create("warm_mesh_trail_sneakers", "https://i.postimg.cc/8k2p24Tt/warm-shoes.png", Category.OUTDOOR, 10.0, 20.0))

        // OUTDOOR - Hot
        clothService.save(create("hot_polyester_moisture_wicking_shirt", "https://i.postimg.cc/2yqsHsqr/hot-shirts.png", Category.OUTDOOR, 20.1, 50.0))
        clothService.save(create("hot_polyester_shorts", "https://i.postimg.cc/0yGLHdNM/hot-pants.png", Category.OUTDOOR, 20.1, 50.0))
        clothService.save(create("hot_synthetic_sandals", "https://i.postimg.cc/vBKj600j/hot-shoes.png", Category.OUTDOOR, 20.1, 50.0))

        // DATE_LOOK - Cold
        clothService.save(create("cold_wool_knit_sweater", "https://i.postimg.cc/dtcFH09f/cold-shirts.png", Category.DATE_LOOK, -10.0, 9.9))
        clothService.save(create("cold_corduroy_pants", "https://i.postimg.cc/9QJjQYj3/cold-pants.png", Category.DATE_LOOK, -10.0, 9.9))
        clothService.save(create("cold_leather_ankle_boots", "https://i.postimg.cc/FKzXdk9Y/cold-shoes.png", Category.DATE_LOOK, -10.0, 9.9))

        // DATE_LOOK - Warm
        clothService.save(create("warm_cotton_cardigan", "https://i.postimg.cc/NMrhbh2Y/warm-shirts.png", Category.DATE_LOOK, 10.0, 20.0))
        clothService.save(create("warm_denim_slim_jeans", "https://i.postimg.cc/9X7VY9yT/warm-pants.png", Category.DATE_LOOK, 10.0, 20.0))
        clothService.save(create("warm_leather_ballet_flats", "https://i.postimg.cc/ryxXrkLN/warm-shoes.png", Category.DATE_LOOK, 10.0, 20.0))

        // DATE_LOOK - Hot
        clothService.save(create("hot_polyester_floral_blouse", "https://i.postimg.cc/521x0tYz/hot-shirts.png", Category.DATE_LOOK, 20.1, 50.0))
        clothService.save(create("hot_polyester_skirt", "https://i.postimg.cc/3NmTrM39/hot-pants.png", Category.DATE_LOOK, 20.1, 50.0))
        clothService.save(create("hot_leather_open_toe_heels", "https://i.postimg.cc/XY8ny0dz/hot-shoes.png", Category.DATE_LOOK, 20.1, 50.0))

        // === ExtraCloth 저장 ===
        clothService.save(create("Heatwave Hat", "https://i.postimg.cc/fbzgqVkM/image.png", Weather.HEAT_WAVE))
        clothService.save(create("Moderate Rain Umbrella", "https://i.postimg.cc/RFm813m1/image.png", Weather.MODERATE_RAIN))
        clothService.save(create("Snow Umbrella", "https://i.postimg.cc/RFm813m1/image.png", Weather.SNOW))
        clothService.save(create("Dust Mask", "https://i.postimg.cc/Sx7c2jZb/image.png", Weather.MIST))
    }
}
