package com.team04.back.domain.review.review.service

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.review.review.dto.ClothItemReqBody
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.review.review.entity.ReviewClothInfo
import com.team04.back.domain.review.review.repository.ReviewClothInfoRepository
import com.team04.back.domain.review.review.repository.ReviewRepository
import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.service.WeatherService
import com.team04.back.global.exception.ServiceException
import com.team04.back.standard.dto.ReviewSearchDto
import com.team04.back.standard.dto.ReviewSearchSortType
import com.team04.back.standard.dto.ReviewSearchSortType.ID
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val reviewClothInfoRepository: ReviewClothInfoRepository,
    private val clothService: ClothService,
    private val geoService: GeoService,
    private val weatherService: WeatherService,
    private val passwordEncoder: PasswordEncoder,
) {
    fun count(): Long = reviewRepository.count()

    fun findById(id: Int): Review? = reviewRepository.findById(id).orElse(null)

    fun findLatest(): Review? = reviewRepository.findFirstByOrderByIdDesc()

    fun findBySearch(
        search: ReviewSearchDto,
        page: Int = 1,
        pageSize: Int = 10,
        sort: ReviewSearchSortType = ID
    ): Page<Review> {
        val pageSize = if (pageSize in 1..100) pageSize else 30
        val page = if (page > 0) page else 1
        val pageable = PageRequest.of(page - 1, pageSize, sort.sortBy)
        return reviewRepository.findBySearch(search, pageable)
    }

    fun verifyPassword(review: Review, rawPassword: String): Boolean = passwordEncoder.matches(rawPassword, review.password)

    fun checkCanDelete(review: Review, member: Member) {
        val reviewMember = review.member
            ?: throw ServiceException("403-2", "회원 리뷰가 아니므로 회원 권한으로 삭제할 수 없습니다.")
        if (member.id != reviewMember.id)
            throw ServiceException("403-1", "${review.id}번 리뷰 삭제 권한이 없습니다.")
    }

    fun checkCanModify(review: Review, member: Member) {
        val reviewMember = review.member
            ?: throw ServiceException("403-4", "회원 리뷰가 아니므로 회원 권한으로 수정할 수 없습니다.")
        if (member.id != reviewMember.id)
            throw ServiceException("403-3", "${review.id}번 리뷰 수정 권한이 없습니다.")
    }


    @Transactional
    fun createReview(
        member: Member? = null,
        email: String? = null,
        password: String? = null,
        imageUrl: String?,
        title: String,
        sentence: String,
        tagString: String?,
        cityName: String? = null,
        countryCode: String? = null,
        date: LocalDate? = null,
        weatherInfo: WeatherInfo? = null,
        clothList: List<ClothItemReqBody>?
    ): Review {
        require((member != null) xor (email != null && password != null)) { "Either member or (email and password) must be provided, but not both" }

        val weatherInfo = weatherInfo ?: getWeatherInfo(cityName, countryCode, date)

        val encodedPassword = password?.let { passwordEncoder.encode(password)}

        val review = Review(null, email, encodedPassword, title, sentence, tagString, imageUrl, weatherInfo)
        val savedReview = reviewRepository.save(review)
        createClothInfo(savedReview.id, clothList)

        return savedReview
    }

    @Transactional
    fun modifyReview(
        review: Review,
        title: String,
        sentence: String,
        tagString: String?,
        imageUrl: String?,
        cityName: String? = null,
        countryCode: String? = null,
        date: LocalDate? = null,
        weatherInfo: WeatherInfo? = null,
        clothList: List<ClothItemReqBody>?
    ): Review {
        val newTitle = title.takeIf { it != review.title }
        val newSentence = sentence.takeIf { it != review.sentence }
        val newTagString = tagString.takeIf { it != review.tagString }
        val newImageUrl = imageUrl.takeIf { it != review.imageUrl }
        val weatherInfo = weatherInfo ?: getWeatherInfo(cityName, countryCode, date)
        val newWeatherInfo = weatherInfo.takeIf { it != review.weatherInfo }

        clothList?.let {
            updateClothInfoEfficiently(review.id, clothList)
        }

        return review.modify(newTitle, newSentence, newTagString, newImageUrl, newWeatherInfo)
    }

    @Transactional
    fun deleteReview(review: Review) {
        reviewClothInfoRepository.deleteByReviewId(review.id)
        reviewRepository.delete(review)
    }


    fun findReviewClothInfo(reviewId: Int): List<ReviewClothInfo> = reviewClothInfoRepository.findByReviewId(reviewId)

    fun findRecommendedClothInfo(reviewId: Int): List<ClothInfo> {
        val reviewClothList = findReviewClothInfo(reviewId)
        val clothInfoIdList = reviewClothList
            .filter { it.isRecommend }
            .map { it.clothInfoId }

        return clothService.findByIdList(clothInfoIdList)
    }

    fun findNonRecommendedClothInfo(reviewId: Int): List<ClothInfo> {
        val reviewClothList = findReviewClothInfo(reviewId)
        val clothInfoIdList = reviewClothList
            .filter { !it.isRecommend }
            .map { it.clothInfoId }

        return clothService.findByIdList(clothInfoIdList)
    }


    private fun updateClothInfoEfficiently(reviewId: Int, newClothList: List<ClothItemReqBody>) {
        // 기존 ClothInfo 조회
        val existingReviewClothInfos = reviewClothInfoRepository.findByReviewId(reviewId)
        val existingClothInfos = clothService.findByIdList(existingReviewClothInfos.map { it.clothInfoId })

        // 새로운 cloth 정보를 처리하기 위한 맵 생성 (중복 제거 포함)
        val newClothMap = newClothList.distinctBy {
            Triple(it.clothName, it.category, it.style)
        }.associateBy {
            Triple(it.clothName, it.category, it.style)
        }.toMutableMap()

        // 기존 항목들 검사
        for (existingReviewClothInfo in existingReviewClothInfos) {
            val existingClothInfo = existingClothInfos.find { it.id == existingReviewClothInfo.clothInfoId }

            if (existingClothInfo != null) {
                val key = Triple(existingClothInfo.clothName, existingClothInfo.category, existingClothInfo.style)
                val newClothItem = newClothMap[key]

                if (newClothItem != null) {
                    // 추천 상태가 다르면 업데이트
                    if (existingReviewClothInfo.isRecommend != newClothItem.isRecommend) {
                        existingReviewClothInfo.isRecommend = newClothItem.isRecommend
                        reviewClothInfoRepository.save(existingReviewClothInfo)
                    }
                    // 처리된 항목은 newClothMap에서 제거
                    newClothMap.remove(key)
                } else {
                    // 새 목록에 없으면 삭제
                    reviewClothInfoRepository.delete(existingReviewClothInfo)
                }
            } else {
                // ClothInfo가 없으면 삭제 (데이터 무결성 문제)
                reviewClothInfoRepository.delete(existingReviewClothInfo)
            }
        }

        // 새로운 항목들 생성
        createClothInfo(reviewId, newClothMap.values.toList())
    }

    private fun addReviewClothInfo(reviewId: Int, clothInfoId: Int, isRecommend: Boolean) {
        val reviewClothInfo = ReviewClothInfo(reviewId, clothInfoId, isRecommend)
        reviewClothInfoRepository.save(reviewClothInfo)
    }

    private fun createClothInfo(reviewId: Int, clothList: List<ClothItemReqBody>?) {
        clothList?.forEach { clothItem ->
            // ClothName을 이용해 대표 ClothInfo 조회 (이미지 사용 위해)
            val defaultClothInfo = clothService.findByClothNameAndStyle(clothItem.clothName, null)
                ?: throw ServiceException("404-1","옷 정보를 찾을 수 없습니다.")

            val clothInfo = ClothInfo.create(
                clothName = clothItem.clothName,
                imageUrl = defaultClothInfo.imageUrl,
                category = clothItem.category,
                style = clothItem.style,
                material = clothItem.material,
                minFeelsLike = null,
                maxFeelsLike = null
            )
            val savedClothInfo = clothService.save(clothInfo)
            addReviewClothInfo(reviewId, savedClothInfo.id, clothItem.isRecommend)
        }
    }

    private fun getWeatherInfo(cityName: String?, countryCode: String?, date: LocalDate?): WeatherInfo {
        requireNotNull(cityName) { "City name must not be null" }
        requireNotNull(countryCode) { "Country code must not be null" }
        requireNotNull(date) { "Date must not be null" }

        val coordinates = geoService.getCoordinatesFromLocation(cityName, countryCode)
        return weatherService.getWeatherInfo(
            coordinates[0],
            coordinates[1],
            date,
            cityName
        )
    }
}
