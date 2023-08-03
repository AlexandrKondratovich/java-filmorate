package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbRepository;
import ru.yandex.practicum.filmorate.dao.ReviewDbRepository;
import ru.yandex.practicum.filmorate.dao.UserDbRepository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewService {

    private final ReviewDbRepository reviewDbRepository;
    private final UserDbRepository userDbRepository;
    private final FilmDbRepository filmDbRepository;

    public List<Review> getAllReviews() {
        return reviewDbRepository.getAllReviews();
    }

    public Review addReview(Review review) {
        return reviewDbRepository.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewDbRepository.updateReview(review);
    }

    public void deleteReviewById(long id) {
        reviewDbRepository.deleteReviewById(id);
    }

    public Review getReviewById(long id) {
        return reviewDbRepository.getReviewById(id);
    }

    public List<Review> getAllReviewByFilmId(long filmId, int count) {
        Film receivedFilm = filmDbRepository.getById(filmId);
        if (receivedFilm == null) {
            throw new FilmNotFoundException(filmId);
        }
        return reviewDbRepository.getAllReviewByFilmId(filmId, count);
    }

    public void addLike(long reviewId, long userId) {
        User receivedUser = userDbRepository.getById(userId);
        Review receivedReview = reviewDbRepository.getReviewById(reviewId);
        if (receivedReview.getUserId() == userId) {
            throw new LikeAlreadyExistException(userId);
        }
        if (receivedUser == null) {
            throw new UserNotFoundException(userId);
        }
        reviewDbRepository.addLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        User receivedUser = userDbRepository.getById(userId);
        Review receivedReview = reviewDbRepository.getReviewById(reviewId);
        if (receivedReview.getUserId() == userId) {
            throw new LikeAlreadyExistException(userId);
        }
        if (receivedUser == null) {
            throw new UserNotFoundException(userId);
        }
        reviewDbRepository.addDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        User receivedUser = userDbRepository.getById(userId);
        reviewDbRepository.getReviewById(reviewId);
        if (receivedUser == null) {
            throw new UserNotFoundException(userId);
        }
        reviewDbRepository.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        User receivedUser = userDbRepository.getById(userId);
        reviewDbRepository.getReviewById(reviewId);
        if (receivedUser == null) {
            throw new UserNotFoundException(userId);
        }
        reviewDbRepository.deleteDislike(reviewId, userId);
    }
}
