package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
@Validated
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable long id) {
        reviewService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReviewByFilmId(@RequestParam Optional<Long> filmId, @RequestParam Optional<Integer> count) {
        return reviewService.getAllReviewByFilmId(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void deleteLike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void deleteDislike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.deleteDislike(reviewId, userId);
    }
}
