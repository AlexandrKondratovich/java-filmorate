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
public class ReviewDbRepository implements ReviewRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO REVIEWS (CONTENT, ISPOSITIVE, USER_ID, FILM_ID) " +
                "VALUES (:content, :isPositive, :userId, :filmId) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("content", review.getContent());
        map.addValue("isPositive", review.getIsPositive());
        map.addValue("userId", review.getUserId());
        map.addValue("filmId", review.getFilmId());
        jdbcOperations.update(sql, map, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        updateUseful(review);
        log.info("Отзыв " + review + " создан");
        return getReviewById(review.getReviewId());
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE REVIEWS SET CONTENT = :content, ISPOSITIVE = :isPositive " +
                "WHERE REVIEW_ID = :reviewId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("content", review.getContent());
        map.addValue("isPositive", review.getIsPositive());
        map.addValue("reviewId", review.getReviewId());

        int count = jdbcOperations.update(sql, map);
        checkExistence(count, review.getReviewId());
        log.info("Отзыв " + review + " обновлен");
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReviewById(long id) {
        String sql = "DELETE FROM REVIEWS " +
                "WHERE REVIEW_ID = :reviewId ";
        int count = jdbcOperations.update(sql, Map.of("reviewId", id));
        checkExistence(count, id);
    }

    @Override
    public Review getReviewById(long id) {
        String sql = "SELECT * " +
                "FROM REVIEWS " +
                "WHERE REVIEW_ID = :reviewId ";
        List<Review> reviews = jdbcOperations.query(sql, Map.of("reviewId", id), new ReviewRowMapper());
        if (reviews.size() != 1) {
            throw new ReviewNotExistObject("Отзыв с айди " + id + " не найден");
        }
        return reviews.get(0);
    }

    @Override
    public List<Review> getAllReviewByFilmId(Long filmId, int count) {
        String sql;
        log.info("поле filmId равно" + filmId);
        log.info("поле count равно" + count);
        if (filmId == 0) {
            sql = "SELECT * " +
                    "FROM REVIEWS " +
                    "ORDER BY USEFUL DESC ";
        } else if (count != 10) {
            sql = "SELECT * " +
                    "FROM REVIEWS " +
                    "ORDER BY USEFUL DESC " +
                    "LIMIT :count ";
        } else {
            sql = "SELECT * " +
                    "FROM REVIEWS " +
                    "WHERE FILM_ID = :filmId " +
                    "ORDER BY USEFUL DESC " +
                    "LIMIT :count ";
        }
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("filmId", filmId);
        map.addValue("count", count);
        List<Review> reviews = jdbcOperations.query(sql, map, new ReviewRowMapper());
        log.info("Количнсвто отзывов в списке " + reviews.size());
        return reviews;
    }

    @Override
    public void addLike(long reviewId, long userId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 " +
                "WHERE REVIEW_ID = :reviewId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("reviewId", reviewId);
        jdbcOperations.update(sql, map);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 " +
                "WHERE REVIEW_ID = :reviewId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("reviewId", reviewId);
        jdbcOperations.update(sql, map);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        if (getReviewById(reviewId).getUseful() == 0) {
            log.info("У отзыва рейтинг равен 0");
            return;
        }
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 " +
                "WHERE REVIEW_ID = :reviewId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("reviewId", reviewId);
        jdbcOperations.update(sql, map);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 " +
                "WHERE REVIEW_ID = :reviewId ";
        if (getReviewById(reviewId).getUseful() == 0) {
            log.info("У отзыва рейтинг равен 0");
            return;
        }
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("reviewId", reviewId);
        jdbcOperations.update(sql, map);
    }

    public void updateUseful(Review review) {
        int useful = review.getUseful();
        if (review.getIsPositive()) {
            review.setUseful(++useful);
            log.info("Количество лайков отзыва " + review + " увеличено");
        }
    }

    public void checkExistence(int count, long id) {
        if (count == 0) {
            throw new ReviewNotExistObject("Отзыв с айди " + id + " не найден");
        }
    }

}
