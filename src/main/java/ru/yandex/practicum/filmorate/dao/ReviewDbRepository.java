package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.rowMapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.ReviewNotExistObject;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class ReviewDbRepository implements ReviewRepository{

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * " +
                     "FROM REVIEWS ";
        return jdbcOperations.query(sql, new ReviewRowMapper());
    }

    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) " +
                     "VALUES (:content, :typeIsPositive, :userId, :filmId) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("content", review.getContent());
        map.addValue("is_positive", review.isPositive());
        map.addValue("user_id", review.getUserId());
        map.addValue("film_id", review.getFilmId());
        jdbcOperations.update(sql, map);
        review.setReviewId(keyHolder.getKey().longValue());
        updateUseful(review);
        log.info("Отзыв " + review + " создан");
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE REVIEWS SET CONTENT = :content, IS_POSITIVE = :typeIsPositive, USER_ID = :userId, FILM_ID = :filmId " +
                "WHERE review_id = :reviewId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", review.getReviewId());
        map.addValue("content", review.getContent());
        map.addValue("is_positive", review.isPositive());
        map.addValue("user_id", review.getUserId());
        map.addValue("film_id", review.getFilmId());

        int count = jdbcOperations.update(sql, map);// нужно передавать туда айди отзыва или нет?
        checkExistence(count, review.getReviewId());
        log.info("Отзыв " + review + " обновлен");
        return review;
    }

    @Override
    public void deleteReviewById(long id) {
        String sql = "DELETE FROM REVIEWS " +
                "WHERE REVIEW_ID = :reviewId ";
        int count = jdbcOperations.update(sql, Map.of("review_id", id));
        checkExistence(count, id);
    }

    @Override
    public Review getReviewById(long id) {
        String sql = "SELECT * " +
                     "FROM REVIEWS " +
                     "WHERE REVIEW_ID = :reviewId ";
        List<Review> reviews = jdbcOperations.query(sql, Map.of("review_id", id), new ReviewRowMapper());
        if (reviews.size() != 1) {
            throw new ReviewNotExistObject("Отзыв с айди " + id + " не найден");
        }
        return reviews.get(0);
    }

    @Override
    public List<Review> getAllReviewByFilmId(long filmId, int count) {
        String sql = "SELECT * " +
                     "FROM REVIEWS " +
                     "WHERE FILM_ID = :filmId " +
                     "ORDER BY FILM_ID ";
        List<Review> reviews = jdbcOperations.query(sql, Map.of("film_id", filmId), new ReviewRowMapper());
        if (reviews.size() > count) {
            reviews = reviews.subList(0, count);
        }
        return reviews;
    }

    @Override
    public void addLike(long reviewId, long userId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 " +
                "WHERE USER_ID = :userId AND REVIEW_ID = :reviewId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("user_id", userId);
        jdbcOperations.update(sql, map);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 " +
                "WHERE USER_ID = :userId AND REVIEW_ID = :reviewId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("user_id", userId);
        jdbcOperations.update(sql, map);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 " +
                "WHERE USER_ID = :userId AND REVIEW_ID = :reviewId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("user_id", userId);
        jdbcOperations.update(sql, map);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 " +
                "WHERE USER_ID = :userId AND REVIEW_ID = :reviewId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("user_id", userId);
        jdbcOperations.update(sql, map);
    }

    public void updateUseful(Review review) {
        int useful = review.getUseful();
        if (review.isPositive()) {
            review.setUseful(useful++);
            log.info("Количество лайков отзыва " + review + " увеличено");
        }
    }

    public void checkExistence(int count, long id) {
        if (count == 0) {
            throw new ReviewNotExistObject("Отзыв с айди " + id + " не найден");
        }
    }
}
