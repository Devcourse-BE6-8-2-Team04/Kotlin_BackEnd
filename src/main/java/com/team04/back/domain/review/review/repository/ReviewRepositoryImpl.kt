package com.team04.back.domain.review.review.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.team04.back.domain.review.review.entity.QReview.review
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.weather.weather.entity.QWeatherInfo.weatherInfo
import com.team04.back.standard.dto.ReviewSearchDto
import com.team04.back.standard.util.QueryDslUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class ReviewRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ReviewRepositoryCustom {

    override fun findBySearch(
        search: ReviewSearchDto,
        pageable: Pageable
    ): Page<Review> {
        val builder = BooleanBuilder()

        // filter
        applySearchFilter(builder, search)

        // query 생성
        val reviewsQuery = createReviewsQuery(builder)

        // sort
        applySorting(reviewsQuery, pageable)

        // paging
        reviewsQuery.offset(pageable.offset).limit(pageable.pageSize.toLong())

        // total
        val totalQuery = createTotalQuery(builder)

        return PageableExecutionUtils.getPage(reviewsQuery.fetch(), pageable) { totalQuery.fetchOne()!! }
    }

    private fun applySearchFilter(builder: BooleanBuilder, search: ReviewSearchDto) = with(search) {
        builder.apply {
            location?.let { and(weatherInfo.location.containsIgnoreCase(it)) }
            month?.let { and(weatherInfo.date.month().eq(it)) }
            feelsLikeTemperature?.let { temp -> and(weatherInfo.feelsLikeTemperature.between(temp - 2.5, temp + 2.5)) }
            date?.let { and(weatherInfo.date.month().eq(it.monthValue)) }
            email?.let { and(review.email.eq(it)) }
        }
    }

    private fun createReviewsQuery(builder: BooleanBuilder): JPAQuery<Review> {
        return jpaQueryFactory
            .selectFrom(review)
            .where(builder)
    }

    private fun applySorting(query: JPAQuery<Review>, pageable: Pageable) {
        QueryDslUtil.applySorting(query, pageable) {
            when (it) {
                "id" -> review.id                           // 작성 날짜
                "email" -> review.email
                "location" -> review.weatherInfo.location
                "date" -> review.weatherInfo.date           // 여행 날짜
                else -> null
            }
        }
    }

    private fun createTotalQuery(builder: BooleanBuilder): JPAQuery<Long> {
        return jpaQueryFactory
            .select(review.count())
            .from(review)
            .where(builder)
    }
}
