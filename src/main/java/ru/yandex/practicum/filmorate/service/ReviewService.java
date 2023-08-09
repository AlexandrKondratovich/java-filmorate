package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeAlreadyExistException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.ValidationException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    @Qualifier("reviewDbRepository")
    final ReviewRepository reviewRepository;
    @Qualifier("userDbRepository")
    final UserRepository userRepository;
    @Qualifier("filmDbRepository")
    final FilmRepository filmRepository;
    @Qualifier("eventDbRepository")
    final EventRepository eventRepository;

    public Review addReview(Review review) {
        validation(review);
        reviewRepository.addReview(review);
        eventRepository.add(EventRepository.createEvent(review.getUserId(),
                EventType.REVIEW,
                review.getReviewId(),
                Operation.ADD));
        return getReviewById(review.getReviewId());
    }

    public Review updateReview(Review review) {
        validation(review);
        eventRepository.add(EventRepository.createEvent(getReviewById(review.getReviewId()).getUserId(),
                EventType.REVIEW,
                review.getReviewId(),
                Operation.UPDATE));
        reviewRepository.updateReview(review);
        return getReviewById(review.getReviewId());
    }

    public void deleteReviewById(long id) {
        eventRepository.add(EventRepository.createEvent(getReviewById(id).getUserId(),
                EventType.REVIEW,
                id,
                Operation.REMOVE));
        reviewRepository.deleteReviewById(id);
    }

    public Review getReviewById(long id) {
        return reviewRepository.getReviewById(id);
    }

    public List<Review> getAllReviewByFilmId(Long filmId, Integer count) {
        if (filmId != 0) {
            Film receivedFilm = filmRepository.getById(filmId);
            if (receivedFilm == null) {
                throw new FilmNotFoundException(filmId);
            }
        }
        return reviewRepository.getAllReviewByFilmId(filmId, count);
    }

    public void addLike(long reviewId, long userId) {
        userRepository.getById(userId);
        Review receivedReview = reviewRepository.getReviewById(reviewId);
        if (receivedReview.getUserId() == userId) {
            throw new LikeAlreadyExistException(userId);
        }
        reviewRepository.addLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        userRepository.getById(userId);
        Review receivedReview = reviewRepository.getReviewById(reviewId);
        if (receivedReview.getUserId() == userId) {
            throw new LikeAlreadyExistException(userId);
        }
        reviewRepository.addDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        userRepository.getById(userId);
        reviewRepository.getReviewById(reviewId);

        reviewRepository.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        userRepository.getById(userId);
        reviewRepository.getReviewById(reviewId);

        reviewRepository.deleteDislike(reviewId, userId);
    }

    public void validation(Review review) {
        userRepository.getById(review.getUserId());
        filmRepository.getById(review.getFilmId());
        if (review.getContent() == null || review.getIsPositive() == null ||
                review.getUserId() == null || review.getFilmId() == null) {
            throw new ValidationException("Неправильно передеан отзыв");
        }
    }
}
