package com.team04.back.domain.review.review.repository;

import com.team04.back.domain.review.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, ReviewRepositoryCustom {
    Optional<Review> findFirstByOrderByIdDesc();
}
