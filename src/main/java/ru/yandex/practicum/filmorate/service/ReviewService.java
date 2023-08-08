package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbRepository;
import ru.yandex.practicum.filmorate.dao.ReviewDbRepository;
import ru.yandex.practicum.filmorate.dao.UserDbRepository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.ValidationException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewDbRepository reviewDbRepository;
    private final UserDbRepository userDbRepository;
    private final FilmDbRepository filmDbRepository;

    public Review addReview(Review review) {
        validation(review);
        return reviewDbRepository.addReview(review);
    }

    public Review updateReview(Review review) {
        validation(review);
        return reviewDbRepository.updateReview(review);
    }

    public void deleteReviewById(long id) {
        reviewDbRepository.deleteReviewById(id);
    }

    public Review getReviewById(long id) {
        return reviewDbRepository.getReviewById(id);
    }

    public List<Review> getAllReviewByFilmId(Long filmId, Integer count) {
        if (filmId != 0) {
            Film receivedFilm = filmDbRepository.getById(filmId);
            if (receivedFilm == null) {
                throw new FilmNotFoundException(filmId);
            }
        }
        return reviewDbRepository.getAllReviewByFilmId(filmId, count);
    }

    public void addLike(long reviewId, long userId) {
        userDbRepository.getById(userId);
        Review receivedReview = reviewDbRepository.getReviewById(reviewId);
        if (receivedReview.getUserId() == userId) {
            throw new LikeAlreadyExistException(userId);
        }
        reviewDbRepository.addLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        userDbRepository.getById(userId);
        Review receivedReview = reviewDbRepository.getReviewById(reviewId);
        if (receivedReview.getUserId() == userId) {
            throw new LikeAlreadyExistException(userId);
        }
        reviewDbRepository.addDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        userDbRepository.getById(userId);
        reviewDbRepository.getReviewById(reviewId);

        reviewDbRepository.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        userDbRepository.getById(userId);
        reviewDbRepository.getReviewById(reviewId);

        reviewDbRepository.deleteDislike(reviewId, userId);
    }

    public void validation(Review review) {
        userDbRepository.getById(review.getUserId());
        filmDbRepository.getById(review.getFilmId());
        if (review.getContent() == null || review.getIsPositive() == null ||
                review.getUserId() == null || review.getFilmId() == null) {
            throw new ValidationException("Неправильно передеан отзыв");
        }
    }
}
