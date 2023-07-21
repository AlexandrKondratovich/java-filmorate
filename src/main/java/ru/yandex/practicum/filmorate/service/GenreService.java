package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;


import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    @Qualifier("genreDbRepository")
    final GenreRepository genreRepository;

    public Genre get(long genreId) {
        return genreRepository.getById(genreId);
    }

    public List<Genre> getAll() {
        return genreRepository.getGenresList();
    }

    public Genre add(Genre genre) {
        return genreRepository.add(genre);
    }

    public Genre update(Genre genre) {
        return genreRepository.update(genre);
    }

    public void delete(long genreid) {
        genreRepository.delete(genreid);
    }
}