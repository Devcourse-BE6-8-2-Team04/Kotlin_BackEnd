package com.team04.back.domain.review.review.service;

import com.team04.back.domain.review.review.entity.Review;
import com.team04.back.domain.review.review.repository.ReviewRepository;
import com.team04.back.domain.review.review.dto.ReviewSearchDto;
import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ReviewRepository commentRepository;

    public void save(Review comment) {
        commentRepository.save(comment);
    }

    public long count() {
        return commentRepository.count();
    }

    public Optional<Review> findById(int id) {
        return commentRepository.findById(id);
    }

    public Page<Review> findBySearch(ReviewSearchDto search, Pageable pageable) {
        return commentRepository.findBySearch(search, pageable);
    }

    public boolean verifyPassword(Review comment, String password) {
        return comment.getPassword().equals(password);
    }

    public void delete(Review comment) {
        commentRepository.delete(comment);
    }

    public Review createComment(String email, String password, String imageUrl, String title, String sentence, String tagString, WeatherInfo weatherInfo) {
        Review comment = new Review(email, password, imageUrl, title, sentence, tagString, weatherInfo);
        return commentRepository.save(comment);
    }

    public Optional<Review> findLatest() {
        return commentRepository.findFirstByOrderByIdDesc();
    }

    public Review modify(Review comment, String title, String sentence, String tagString, String imageUrl, WeatherInfo weatherInfo) {
        return comment.modify(title, sentence, tagString, imageUrl, weatherInfo);
    }
}
