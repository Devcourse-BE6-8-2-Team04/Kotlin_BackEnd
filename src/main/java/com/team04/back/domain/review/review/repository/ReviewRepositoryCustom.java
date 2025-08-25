package com.team04.back.domain.review.review.repository;

import com.team04.back.domain.review.review.dto.ReviewSearchDto;
import com.team04.back.domain.review.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<Review> findBySearch(ReviewSearchDto searchDto, Pageable pageable);
}
