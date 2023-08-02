CREATE TABLE IF NOT EXISTS GENRES (
                         genre_id INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(255)
);
ALTER TABLE GENRES ALTER COLUMN genre_id RESTART WITH 7;

CREATE TABLE IF NOT EXISTS MPA (
                         mpa_id INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(10),
                         CONSTRAINT mpa_pk PRIMARY KEY (mpa_id)

);
ALTER TABLE MPA ALTER COLUMN mpa_id RESTART WITH 6;

create table if not exists DIRECTORS
(
    DIRECTOR_ID INTEGER auto_increment,
    NAME        CHARACTER VARYING not null,
    constraint "DIRECTORS_pk"
        primary key (DIRECTOR_ID)
);

CREATE TABLE IF NOT EXISTS FILMS (
                        film_id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(120) DEFAULT '<no name>',
                        release_date DATE,
                        description VARCHAR(200) DEFAULT '<empty>',
                        mpa_id INT,
                        duration INT,
                        CONSTRAINT fk_film_mpa
                            FOREIGN KEY (mpa_id)
                                REFERENCES MPA(mpa_id)
);

CREATE TABLE IF NOT EXISTS USERS (
                        user_id INT PRIMARY KEY AUTO_INCREMENT,
                        email VARCHAR(100) NOT NULL,
                        login VARCHAR(30) NOT NULL ,
                        name varchar(30),
                        birthday DATE
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP_REQUESTS (
                                      user_from INT NOT NULL,
                                      user_to INT NOT NULL,
                                      CONSTRAINT fk_friendship_user
                                          FOREIGN KEY (user_from)
                                              REFERENCES USERS(user_id),
                                      CONSTRAINT fk_friendship_friend
                                          FOREIGN KEY (user_to)
                                              REFERENCES USERS(user_id)

);

CREATE TABLE IF NOT EXISTS FILMS_GENRES (
                                      film_id INT NOT NULL ,
                                      genre_id INT NOT NULL,
                                      CONSTRAINT fk_film_genre
                                          FOREIGN KEY (film_id)
                                              REFERENCES FILMS(film_id),
                                      CONSTRAINT fk_id_genre
                                          FOREIGN KEY (genre_id)
                                              REFERENCES GENRES(genre_id)
);

create table if not exists FILM_DIRECTORS
(
    FILM_ID     INTEGER not null,
    DIRECTOR_ID INTEGER not null,
    constraint FILM_DIRECTORS_DIRECTORS_DIRECTOR_ID_FK
        foreign key (DIRECTOR_ID) references DIRECTORS,
    constraint "FILM_DIRECTORS_FILMS_FILM_ID_fk"
        foreign key (FILM_ID) references FILMS
);

CREATE TABLE IF NOT EXISTS LIKES (
                         film_id INT NOT NULL,
                         CONSTRAINT fk_like_film
                             FOREIGN KEY (film_id)
                                 REFERENCES FILMS(film_id),

                         user_id INT NOT NULL,
                         CONSTRAINT fk_like_user
                             FOREIGN KEY (user_id)
                                 REFERENCES USERS(user_id)

);

