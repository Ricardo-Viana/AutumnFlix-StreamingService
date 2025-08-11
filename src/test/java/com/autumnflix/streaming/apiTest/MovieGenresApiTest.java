package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.api.exceptionHandler.ApiErrorType;
import com.autumnflix.streaming.domain.model.Genre;
import com.autumnflix.streaming.domain.model.Movie;
import com.autumnflix.streaming.domain.model.Rating;
import com.autumnflix.streaming.domain.repository.GenreRepository;
import com.autumnflix.streaming.domain.repository.MovieRepository;
import com.autumnflix.streaming.util.DatabaseCleaner;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.autumnflix.streaming.domain.model.EntertainmentWorkType.MOVIE;
import static org.hamcrest.Matchers.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieGenresApiTest {

    private static final Long VALID_GENRE_ID_EXAMPLE_1 = 1L;
    private static final Long VALID_GENRE_ID_EXAMPLE_2 = 2L;
    private static final Long VALID_MOVIE_ID_WITH_GENRES_1 = 1L;
    private static final Long VALID_MOVIE_ID_WITHOUT_GENRES_2 = 2L;
    private static final Long NON_EXISTING_ID = Long.MAX_VALUE;
    private static final String INVALID_ID = "INVALID";


    private Genre genreExample1;
    private Genre genreExample2;

    private Movie movieWithGenres;
    private Movie movieWithoutGenres;


    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/movies/{movieId}/genres";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingAllGenresByMovieWithGenresId_Expect_Status200QuantityAndProperties() {
        RestAssured
                .given()
                .pathParam("movieId", VALID_MOVIE_ID_WITH_GENRES_1)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasSize(movieWithGenres.getGenres().size()))
                .body("id", hasItems(genreExample1.getId().intValue(), genreExample2.getId().intValue()))
                .body("name", hasItems(genreExample1.getName(), genreExample2.getName()));
    }

    @Test
    void when_GettingAllGenresByMovieWithoutGenresId_Expect_Status200QuantityAndProperties() {
        RestAssured
                .given()
                .pathParam("movieId", VALID_MOVIE_ID_WITHOUT_GENRES_2)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasSize(movieWithoutGenres.getGenres().size()))
                .body("id", hasItems())
                .body("name", hasItems());
    }

    @Test
    void when_GettingAllGenresByNonExistingMovieId_Expect_Status404() {
        RestAssured
                .given()
                    .pathParam("movieId", NON_EXISTING_ID)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail("Movie", NON_EXISTING_ID)));
    }

    @Test
    void when_GettingAllGenresByInvalidMovieId_Expect_Status404() {
        RestAssured
                .given()
                    .pathParam("movieId", INVALID_ID)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(
                        getInvalidUrlParameterDetail("movieId", INVALID_ID, VALID_GENRE_ID_EXAMPLE_1)));
    }

    @Test
    void when_AssociatingByValidGenreIdAndValidMovie_Expect_Status204() {
        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID_WITHOUT_GENRES_2)
                    .pathParam("genreId", VALID_GENRE_ID_EXAMPLE_1)
                .when()
                    .put("/{genreId}")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void when_AssociatingByNonExistingGenreAndFValidMovie_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID_WITHOUT_GENRES_2)
                    .pathParam("genreId", NON_EXISTING_ID)
                .when()
                    .put("/{genreId}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail("Genre", NON_EXISTING_ID)));
    }

    @Test
    void when_AssociatingByValidGenreAndNonExistingMovieId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("movieId", NON_EXISTING_ID)
                    .pathParam("genreId", VALID_GENRE_ID_EXAMPLE_2)
                .when()
                    .put("/{genreId}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail("Movie", NON_EXISTING_ID)));
    }

    @Test
    void when_AssociatingByInvalidGenreIdAndValidMovie_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID_WITH_GENRES_1)
                    .pathParam("genreId", INVALID_ID)
                .when()
                    .put("/{genreId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                    .body("detail", equalTo(
                            getInvalidUrlParameterDetail("genreId", INVALID_ID, VALID_GENRE_ID_EXAMPLE_2)
                    ));
    }

    @Test
    void when_AssociatingByValidGenreAndInvalidMovie_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("movieId", INVALID_ID)
                    .pathParam("genreId", VALID_GENRE_ID_EXAMPLE_1)
                .when()
                    .put("/{genreId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                    .body("detail", equalTo(
                            getInvalidUrlParameterDetail("movieId", INVALID_ID, VALID_GENRE_ID_EXAMPLE_2)
                    ));
    }

    @Test
    void when_DisassociatingByGenreAndMovieWithGenresId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("movieId", VALID_MOVIE_ID_WITH_GENRES_1)
                .pathParam("genreId", VALID_GENRE_ID_EXAMPLE_1)
                .when()
                .delete("/{genreId}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void when_DisassociatingByGenreIdAndNonExistingMovieId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("movieId", NON_EXISTING_ID)
                .pathParam("genreId", VALID_GENRE_ID_EXAMPLE_1)
                .when()
                .delete("/{genreId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail("Movie", NON_EXISTING_ID)));
    }

    @Test
    void when_DisassociatingByNonExistingGenreIdAndMovieId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("movieId", VALID_MOVIE_ID_WITH_GENRES_1)
                .pathParam("genreId", NON_EXISTING_ID)
                .when()
                .delete("/{genreId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail("Genre", NON_EXISTING_ID)));
    }

    @Test
    void when_DisassociatingByInvalidGenreIdAndMovieId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("movieId", VALID_MOVIE_ID_WITH_GENRES_1)
                .pathParam("genreId", INVALID_ID)
                .when()
                .delete("/{genreId}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(
                        getInvalidUrlParameterDetail("genreId", INVALID_ID, VALID_GENRE_ID_EXAMPLE_2)
                ));
    }

    @Test
    void when_DisassociatingByGenreIdAndInvalidMovieId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("movieId", INVALID_ID)
                .pathParam("genreId", VALID_GENRE_ID_EXAMPLE_1)
                .when()
                .delete("/{genreId}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(
                        getInvalidUrlParameterDetail("movieId", INVALID_ID, VALID_MOVIE_ID_WITH_GENRES_1)
                ));
    }


    private void setUpData() {
        setUpGenres();

        setUpMovies();
    }

    private void setUpGenres() {
        genreExample1 = createGenre(VALID_GENRE_ID_EXAMPLE_1, "Terror");
        genreExample2 = createGenre(VALID_GENRE_ID_EXAMPLE_2, "Comedy");

        genreRepository.saveAll(List.of(genreExample1, genreExample2));
    }

    private void setUpMovies() {
        movieWithGenres = createMovie(VALID_MOVIE_ID_WITH_GENRES_1, "FNAF", "Crazy bear", 8,
                Year.of(2023), Rating.PG, Set.of(genreExample1, genreExample2), 120);
        movieWithoutGenres = createMovie(VALID_MOVIE_ID_WITHOUT_GENRES_2, "Avatar 2", "Crazy Blue people",
                10, Year.of(2023), Rating.PG_13, Set.of(), 150);

        movieRepository.saveAll(Arrays.asList(movieWithGenres, movieWithoutGenres));
    }

    private Genre createGenre(Long id, String name) {
        Genre genre = new Genre();

        genre.setId(id);
        genre.setName(name);

        return genre;
    }

    private Movie createMovie(Long id, String name, String synopsis, Integer relevance, Year releaseYear,
                              Rating parentalRating, Set<Genre> genres, Integer duration) {

        Movie movie = new Movie();

        movie.setName(name);
        movie.setSynopsis(synopsis);
        movie.setReleaseYear(releaseYear);
        movie.setRelevance(relevance);
        movie.setParentalRating(parentalRating);
        movie.setGenres(genres);
        movie.setDuration(duration);
        movie.setType(MOVIE);

        return movie;
    }

    private String getResourceNotFoundDetail(String resource, Long id) {
        return "%s with id %d doesn't exist".formatted(resource, id);
    }

    private String getInvalidUrlParameterDetail(String urlParameter, String value, Object expectedValueType) {
        return String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                urlParameter, value.getClass().getSimpleName(), expectedValueType.getClass().getSimpleName());
    }
}
