import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class ClothRecommendationHistory(
    // 위도
    @Column(name = "lat")
    val lat: Double,

    // 경도
    @Column(name = "lon")
    val lon: Double,

    // 체감온도
    @Column(name = "feels_like")
    val feelsLike: Double,

    // UVI
    @Column(name = "uvi")
    val uvi: Double,

    // 강수량
    @Column(name = "rain")
    val rain: Double,

    // 적설량
    @Column(name = "snow")
    val snow: Double,

    // 습도
    @Column(name = "humidity")
    val humidity: Int,

    // 풍속
    @Column(name = "wind_speed")
    val windSpeed: Double,

    // 최저기온
    @Column(name = "temp_min")
    val tempMin: Double,

    // 최고기온
    @Column(name = "temp_max")
    val tempMax: Double,

    ): BaseEntity(){

}