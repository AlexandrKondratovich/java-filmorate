MERGE INTO GENRES (genre_id, name) VALUES ( 1, 'Комедия');
MERGE INTO GENRES (genre_id, name) VALUES ( 2, 'Драма' );
MERGE INTO GENRES (genre_id, name) VALUES ( 3, 'Мультфильм' );
MERGE INTO GENRES (genre_id, name) VALUES ( 4, 'Триллер' );
MERGE INTO GENRES (genre_id, name) VALUES ( 5, 'Документальный' );
MERGE INTO GENRES (genre_id, name) VALUES ( 6, 'Боевик' );

MERGE INTO MPA (mpa_id, name) VALUES ( 1, 'G');
MERGE INTO MPA (mpa_id, name) VALUES ( 2, 'PG');
MERGE INTO MPA (mpa_id, name) VALUES ( 3, 'PG-13');
MERGE INTO MPA (mpa_id, name) VALUES ( 4, 'R');
MERGE INTO MPA (mpa_id, name) VALUES ( 5, 'NC-17');