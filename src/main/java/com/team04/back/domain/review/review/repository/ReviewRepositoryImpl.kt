package com.team04.back.domain.review.review.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import com.team04.back.domain.review.review.dto.ReviewSearchDto
import com.team04.back.domain.review.review.entity.QReview
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.weather.weather.entity.QWeatherInfo
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ReviewRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ReviewRepositoryCustom {

    override fun findBySearch(
        search: ReviewSearchDto,
        pageable: Pageable
    ): Page<Review> {
        val review = QReview.review
        val weather = QWeatherInfo.weatherInfo

        val builder = BooleanBuilder()

        // 검색 조건 추가
        if (search.location != null) {
            builder.and(weather.location.containsIgnoreCase(search.location))
        }
        if (search.month != null) {
            builder.and(weather.date.month().eq(search.month))
        }
        if (search.feelsLikeTemperature != null) {
            val min = search.feelsLikeTemperature - 2.5
            val max = search.feelsLikeTemperature + 2.5
            builder.and(weather.feelsLikeTemperature.between<Double>(min, max))
        }
        if (search.date != null) {
            builder.and(weather.date.month().eq(search.date.monthValue))
        }
        if (search.email != null) {
            builder.and(review.email.eq(search.email))
        }

        // 데이터 조회
        val content = jpaQueryFactory
            .selectFrom<Review?>(review)
            .leftJoin<WeatherInfo?>(review.weatherInfo, weather).fetchJoin()
            .where(builder)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable, review))
            .fetch()

        // 전체 개수
        val total = jpaQueryFactory
            .select(review.count())
            .from(review)
            .leftJoin(review.weatherInfo, weather)
            .where(builder)
            .fetchOne()!!

        return PageImpl(content, pageable, total)
    }

    /**
     * 동적 정렬 처리
     */
    private fun getOrderSpecifier(pageable: Pageable, review: QReview): Array<OrderSpecifier<*>?> {
        val orders: MutableList<OrderSpecifier<*>?> = ArrayList<OrderSpecifier<*>?>()

        if (pageable.sort.isEmpty) {
            orders.add(review.id.desc())
        } else {
            for (order in pageable.sort) {
                val direction = if (order.isAscending) Order.ASC else Order.DESC
                val property = order.property

                when (property) {
                    "id" -> orders.add(OrderSpecifier(direction, review.id))
                    "email" -> orders.add(OrderSpecifier(direction, review.email))
                    "location" -> orders.add(OrderSpecifier(direction, review.weatherInfo.location))
                    else -> orders.add(review.id.desc()) // 기본 정렬
                }
            }
        }

        return orders.toTypedArray<OrderSpecifier<*>?>()
    }
}
