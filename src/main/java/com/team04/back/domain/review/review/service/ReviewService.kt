package com.team04.back.domain.review.review.service

import com.team04.back.standard.dto.ReviewSearchDto
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.review.review.repository.ReviewRepository
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.standard.dto.ReviewSearchSortType
import com.team04.back.standard.dto.ReviewSearchSortType.ID
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository
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

    fun delete(review: Review) = reviewRepository.delete(review)

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
}
