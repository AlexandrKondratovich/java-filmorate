package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewRepository {

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReviewById(long id);

    Review getReviewById(long id);

    List<Review> getAllReviewByFilmId(Long filmId, int count);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);
}
