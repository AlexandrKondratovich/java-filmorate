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

INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES ('email@email.com', 'user', 'test', '2000-03-22');
INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES ('user2mail@email.com', 'ser2Login', 'name2', '1980-4-26');

INSERT INTO FILMS (NAME, RELEASE_DATE, DESCRIPTION, MPA_ID, DURATION)
VALUES ('name1', '2004-04-19', 'description1', 3, 120);
INSERT INTO FILMS (NAME, RELEASE_DATE, DESCRIPTION, MPA_ID, DURATION)
VALUES ('name2', '2018-11-29', 'description2', 4, 145);

INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID)
VALUES (1, 2 );
INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID)
VALUES (1, 1 );
INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID)
VALUES (1, 3 );