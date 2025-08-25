package com.team04.back.domain.review.review.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team04.back.domain.review.review.dto.ReviewSearchDto;
import com.team04.back.domain.review.review.entity.QReview;
import com.team04.back.domain.review.review.entity.Review;
import com.team04.back.domain.weather.weather.entity.QWeatherInfo;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReviewRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Review> findBySearch(ReviewSearchDto search, Pageable pageable) {
        QReview review = QReview.review;
        QWeatherInfo weather = QWeatherInfo.weatherInfo;

        BooleanBuilder builder = new BooleanBuilder();

        // 검색 조건 추가
        if (search.hasLocation()) {
            builder.and(weather.location.containsIgnoreCase(search.location()));
        }
        if (search.hasMonth()) {
            builder.and(weather.date.month().eq(search.month()));
        }
        if (search.hasFeelsLikeTemperature()) {
            double min = search.feelsLikeTemperature() - 2.5;
            double max = search.feelsLikeTemperature() + 2.5;
            builder.and(weather.feelsLikeTemperature.between(min, max));
        }
        if (search.hasDate()) {
            builder.and(weather.date.month().eq(search.date().getMonthValue()));
        }
        if (search.hasEmail()) {
            builder.and(review.email.eq(search.email()));
        }

        // 데이터 조회
        List<Review> content = queryFactory
                .selectFrom(review)
                .leftJoin(review.weatherInfo, weather).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, review))
                .fetch();

        // 전체 개수
        long total = queryFactory
                .selectFrom(review)
                .leftJoin(review.weatherInfo, weather)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 동적 정렬 처리
     */
    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QReview review) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort().isEmpty()) {
            orders.add(review.id.desc());
        } else {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                String property = order.getProperty();

                switch (property) {
                    case "id":
                        orders.add(new OrderSpecifier<>(direction, review.id));
                        break;
                    case "email":
                        orders.add(new OrderSpecifier<>(direction, review.email));
                        break;
                    case "location":
                        orders.add(new OrderSpecifier<>(direction, review.weatherInfo.location));
                        break;
                    default:
                        orders.add(review.id.desc()); // 기본 정렬
                        break;
                }
            }
        }

        return orders.toArray(new OrderSpecifier[0]);
    }
}
