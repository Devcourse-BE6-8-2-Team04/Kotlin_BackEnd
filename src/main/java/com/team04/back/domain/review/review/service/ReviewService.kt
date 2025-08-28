package com.team04.back.domain.review.review.service

import com.team04.back.domain.review.review.dto.ReviewSearchDto
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.review.review.repository.ReviewRepository
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository
) {
    fun count(): Long = reviewRepository.count()

    fun findById(id: Int): Review? = reviewRepository.findById(id).orElse(null)

    fun findBySearch(
        search: ReviewSearchDto,
        pageable: Pageable
    ): Page<Review> = reviewRepository.findBySearch(search, pageable)

    fun verifyPassword(comment: Review, password: String): Boolean = comment.password == password

    fun delete(comment: Review) = reviewRepository.delete(comment)

    fun createComment(
        email: String,
        password: String,
        imageUrl: String?,
        title: String,
        sentence: String,
        tagString: String?,
        weatherInfo: WeatherInfo
    ): Review {
        val comment = Review(email, password, title, sentence, tagString, imageUrl, weatherInfo)
        return reviewRepository.save(comment)
    }

    fun findLatest(): Review? = reviewRepository.findFirstByOrderByIdDesc()

    fun modify(
        comment: Review,
        title: String,
        sentence: String,
        tagString: String?,
        imageUrl: String?,
        weatherInfo: WeatherInfo
    ): Review = comment.modify(title, sentence, tagString, imageUrl, weatherInfo)
}
