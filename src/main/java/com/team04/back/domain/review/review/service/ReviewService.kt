package com.team04.back.domain.review.review.service

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.review.review.dto.ClothItemReqBody
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.review.review.entity.ReviewClothInfo
import com.team04.back.domain.review.review.repository.ReviewClothInfoRepository
import com.team04.back.domain.review.review.repository.ReviewRepository
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.global.exception.ServiceException
import com.team04.back.standard.dto.ReviewSearchDto
import com.team04.back.standard.dto.ReviewSearchSortType
import com.team04.back.standard.dto.ReviewSearchSortType.ID
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val reviewClothInfoRepository: ReviewClothInfoRepository,
    private val clothService: ClothService
) {
    fun count(): Long = reviewRepository.count()

    fun findById(id: Int): Review? = reviewRepository.findById(id).orElse(null)

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

    fun verifyPassword(review: Review, password: String): Boolean = review.password == password

    fun delete(review: Review) {
        reviewRepository.delete(review)
        reviewClothInfoRepository.deleteByReviewId(review.id)
    }

    fun createReview(
        email: String,
        password: String,
        imageUrl: String?,
        title: String,
        sentence: String,
        tagString: String?,
        weatherInfo: WeatherInfo,
        clothList: List<ClothItemReqBody>
    ): Review {
        val review = Review(email, password, title, sentence, tagString, imageUrl, weatherInfo)
        reviewRepository.save(review)

        clothList.forEach { clothItem ->
            // ClothName을 이용해 대표 ClothInfo 조회 (이미지 사용 위해)
            val defaultClothInfo = clothService.findByClothNameAndStyle(clothItem.clothName, null) ?: throw ServiceException("400-1","옷 정보를 찾을 수 없습니다.")
            val clothInfo = ClothInfo.create(
                clothName = clothItem.clothName,
                imageUrl = defaultClothInfo.imageUrl,
                category = clothItem.category,
                style = clothItem.style,
                material = clothItem.material,
                minFeelsLike = null,
                maxFeelsLike = null
            )
            clothService.save(clothInfo)
            addReviewClothInfo(review.id, clothInfo.id, clothItem.isRecommend)
        }

        return review
    }

    fun findLatest(): Review? = reviewRepository.findFirstByOrderByIdDesc()

    fun modify(
        review: Review,
        title: String,
        sentence: String,
        tagString: String?,
        imageUrl: String?,
        weatherInfo: WeatherInfo
    ): Review = review.modify(title, sentence, tagString, imageUrl, weatherInfo)

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

    fun addReviewClothInfo(reviewId: Int, clothInfoId: Int, isRecommend: Boolean) {
        val reviewClothInfo = ReviewClothInfo(reviewId, clothInfoId, isRecommend)
        reviewClothInfoRepository.save(reviewClothInfo)
    }
}
