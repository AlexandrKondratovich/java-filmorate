package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class FilmComparator implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2){
        if (film1.id == null && film2.id != null){
            return -1;
        }
        if (film1.id != null && film2.id == null){
            return 1;
        }
        if (film1.id == null && film2.id== null){
            return 0;
        }
        return film1.id.compareTo(film2.id);
    }
}
