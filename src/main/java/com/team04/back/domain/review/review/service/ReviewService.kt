package com.team04.back.domain.review.review.service

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.review.review.entity.ReviewClothInfo
import com.team04.back.domain.review.review.repository.ReviewClothInfoRepository
import com.team04.back.domain.review.review.repository.ReviewRepository
import com.team04.back.domain.weather.weather.entity.WeatherInfo
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
        weatherInfo: WeatherInfo
    ): Review {
        val review = Review(email, password, title, sentence, tagString, imageUrl, weatherInfo)
        return reviewRepository.save(review)
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
}
