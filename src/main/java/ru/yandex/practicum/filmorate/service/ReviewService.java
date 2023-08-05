package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbRepository;
import ru.yandex.practicum.filmorate.dao.ReviewDbRepository;
import ru.yandex.practicum.filmorate.dao.UserDbRepository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ReviewNotExistObject;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
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

    public List<Review> getAllReviewByFilmId(Optional<Long> filmId, Optional<Integer> count) {
        long id = filmId.orElse(Long.valueOf(0));
        int newCount = count.orElse(10);
        if (id != 0) {
            Film receivedFilm = filmDbRepository.getById(id);
            if (receivedFilm == null) {
                throw new FilmNotFoundException(id);
            }
        }
        return reviewDbRepository.getAllReviewByFilmId(id, newCount);
    }

    public void addLike(long reviewId, long userId) {
        User receivedUser = userDbRepository.getById(userId);
        Review receivedReview = reviewDbRepository.getReviewById(reviewId);
        if (receivedUser == null) {
            throw new UserNotFoundException(userId);
        }
        if (receivedReview == null) {
            throw new ReviewNotExistObject(reviewId);
        }
        if (receivedReview.getUserId() == userId) {
            throw new LikeAlreadyExistException(userId);
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

    public void validation(Review review) {
        User receivedUser = userDbRepository.getById(review.getUserId());
        Film receivedFilm = filmDbRepository.getById(review.getFilmId());
        if (receivedFilm == null) {
            throw new FilmNotFoundException("Фильм с данным айди не существует");
        }
        if (receivedUser == null) {
            throw new UserNotFoundException("Юзер с данным айди не существует");
        }
        if (review.getContent() == null || review.getIsPositive() == null ||
                review.getUserId() == null || review.getFilmId() == null) {
            throw new ValidationException("Неправильно передеан отзыв");
        }
    }
}
