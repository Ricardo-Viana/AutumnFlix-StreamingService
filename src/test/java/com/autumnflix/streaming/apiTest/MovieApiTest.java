package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.api.assembler.movie.MovieInputDtoAssembler;
import com.autumnflix.streaming.api.exceptionHandler.ApiErrorType;
import com.autumnflix.streaming.api.model.movie.MovieInputDto;
import com.autumnflix.streaming.domain.model.*;
import com.autumnflix.streaming.domain.repository.EntertainmentWorkRepository;
import com.autumnflix.streaming.domain.repository.GenreRepository;
import com.autumnflix.streaming.domain.repository.MovieRepository;
import com.autumnflix.streaming.util.DatabaseCleaner;
import com.autumnflix.streaming.util.JsonReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.time.Year;
import java.util.Arrays;
import java.util.Set;

import static com.autumnflix.streaming.api.exceptionHandler.ApiErrorType.*;
import static com.autumnflix.streaming.domain.model.EntertainmentWorkType.MOVIE;
import static com.autumnflix.streaming.domain.model.EntertainmentWorkType.SERIES;
import static org.hamcrest.Matchers.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieApiTest {

    private static final Long NON_EXISTING_MOVIE_ID = Long.MAX_VALUE;
    private static final Long VALID_MOVIE_ID = 1L;

    private static final String INVALID_MOVIE_ID = "a";
    private static final String INVALID_PARENTAL_RATE = "PG_30";
    private static final String INVALID_PROPERTY_FIELD = "invalidProperty";

    private static final String USER_MESSAGE_NOT_NULL = "não deve ser nulo";
    private static final String USER_MESSAGE_NOT_BLANK = "não deve estar em branco";
    private static final String USER_MESSAGE_POSITIVE = "must be greater than zero";
    private static final String USER_MESSAGE_INVALID_RELEASE_YEAR = "The release year of the movie must be on or after 1895.";

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private EntertainmentWorkRepository entertainmentWorkRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieInputDtoAssembler movieInputDtoAssembler;

    private Genre terror;
    private Genre comedy;
    private Genre drama;
    private Genre scienceFiction;

    private EntertainmentWork series;

    private Movie movieWithGenres;
    private Movie movieWithoutGenres;

    private String incorrectMovieJsonWithNonExistingRating;
    private String incorrectMovieJsonWithNonExistingProperty;

    private int movieQuantity;

    public MovieApiTest() {
        this.incorrectMovieJsonWithNonExistingRating = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/movie/invalidMovieWithNonExistingParentalRating.JSON");

        this.incorrectMovieJsonWithNonExistingProperty = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/movie/invalidMovieWithNonExistingProperty.JSON");
    }

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port=port;
        RestAssured.basePath="/movies";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingAllMovies_Expect_Status200QuantityAndResumedProperties() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasSize(movieQuantity))
                .body("name", hasItems(movieWithGenres.getName(), movieWithoutGenres.getName()))
                .body("synopsis", hasItems(movieWithGenres.getSynopsis(), movieWithoutGenres.getSynopsis()))
                .body("parentalRating", hasItems(movieWithGenres.getParentalRating().toString(), movieWithoutGenres.getParentalRating().toString()));
    }

    @Test
    void when_GettingAMovieWithGenresByMovieId_Expect_Status200AndAllProperties() {
        RestAssured
                .given()
                    .pathParam("movieId", movieWithGenres.getId())
                    .accept(ContentType.JSON)
                .when()
                    .get("/{movieId}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("name", equalTo(movieWithGenres.getName()))
                    .body("synopsis", equalTo(movieWithGenres.getSynopsis()))
                    .body("relevance", equalTo(movieWithGenres.getRelevance()))
                    .body("parentalRating", equalTo(movieWithGenres.getParentalRating().toString()))
                    .body("genres", hasItems(
                            movieWithGenres.getGenres().stream()
                                    .map(genre -> hasEntry("name", genre.getName()))
                                    .toArray(Matcher[]::new)
                    ));
    }

    @Test
    void when_GettingAMovieWithoutGenresByMovieId_Expect_Status200AndAllProperties() {
        RestAssured
                .given()
                .pathParam("movieId", movieWithoutGenres.getId())
                .accept(ContentType.JSON)
                .when()
                .get("/{movieId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(movieWithoutGenres.getName()))
                .body("synopsis", equalTo(movieWithoutGenres.getSynopsis()))
                .body("relevance", equalTo(movieWithoutGenres.getRelevance()))
                .body("parentalRating", equalTo(movieWithoutGenres.getParentalRating().toString()))
                .body("genres", hasItems());
    }

    @Test
    void when_GettingByNonExistingMovieId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("movieId", NON_EXISTING_MOVIE_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{movieId}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail(Movie.class.getSimpleName(), NON_EXISTING_MOVIE_ID)));
    }

    @Test
    void when_GettingByInvalidId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("movieId", INVALID_MOVIE_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                    .body("detail", equalTo(
                            getInvalidUrlParameterDetail("movieId", INVALID_MOVIE_ID, VALID_MOVIE_ID)));
    }

    @Test
    void when_PostingValidMovie_Expect_Status201Properties() {
        Movie validMovie = createMovie("valid", "Valid movie", 10, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("name", equalTo(validMovie.getName()))
                    .body("synopsis", equalTo(validMovie.getSynopsis()))
                    .body("relevance", equalTo(validMovie.getRelevance()))
                    .body("parentalRating", equalTo(validMovie.getParentalRating().toString()))
                    .body("genres", hasItems());
    }

    @Test
    void when_PostingValidMovieWithReleaseYear1895_Expect_Status201Properties() {
        Movie validMovie = createMovie("valid", "Valid movie", 10, Year.of(1895),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(movieInputDTO))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(validMovie.getName()))
                .body("synopsis", equalTo(validMovie.getSynopsis()))
                .body("relevance", equalTo(validMovie.getRelevance()))
                .body("parentalRating", equalTo(validMovie.getParentalRating().toString()))
                .body("genres", hasItems());
    }

    @Test
    void when_PostingInvalidMovieWithoutName_Expect_Status400TitleDetailAndObjectName() {
        Movie validMovie = createMovie(null, "Valid movie", 10, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("name"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PostingInvalidMovieWithoutSynopsis_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("Valid", null, 10, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("synopsis"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PostingInvalidMovieWithoutRelevance_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("Valid", "Valid movie", null, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("relevance"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PostingInvalidMovieWithoutReleaseYear_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("Valid", "Valid movie", 10, null,
                Rating.G, Set.of(terror, comedy), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("releaseYear"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PostingInvalidMovieWithoutParentalRating_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("Valid", "Valid movie", 10, Year.of(2003),
                null, Set.of(terror, comedy), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("parentalRating"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PostingInvalidMovieWithoutDuration_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("Valid", "Valid movie", 10, Year.of(2003),
                Rating.PG_13, Set.of(terror, comedy), null);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("duration"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PostingInvalidMovieWithReleaseYearBefore1895_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("Valid", "Valid movie", 10, Year.of(1894),
                Rating.PG_13, Set.of(terror, comedy), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("releaseYear"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_INVALID_RELEASE_YEAR));
    }

    @Test
    void when_PostingInvalidMovieWithZeroDuration_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("Valid", "Valid movie", 10, Year.of(2003),
                Rating.PG_13, Set.of(terror, comedy), 0);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("duration"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PostingInvalidMovieWithNegativeDuration_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("Valid", "Valid movie", 10, Year.of(2003),
                Rating.PG_13, Set.of(terror, comedy), -1);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("duration"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PostingInvalidMovieWithNonExistingParentalRating_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .body(incorrectMovieJsonWithNonExistingRating)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(MESSAGE_NOT_READABLE.getTitle()))
                    .body("detail", equalTo(getMessageNotReadableDetail("parentalRating", INVALID_PARENTAL_RATE)));
    }

    @Test
    void when_PostingInvalidMovieWithNonExistingProperty_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .body(incorrectMovieJsonWithNonExistingProperty)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_PROPERTY.getTitle()))
                    .body("detail", equalTo(getInvalidPropertyDetail(INVALID_PROPERTY_FIELD)));
    }

    @Test
    void when_PuttingValidMovieAtValidId_Expect_Status200Properties() {
        Movie validMovie = createMovie("valid", "Valid movie", 10, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("name", equalTo(validMovie.getName()))
                    .body("synopsis", equalTo(validMovie.getSynopsis()))
                    .body("relevance", equalTo(validMovie.getRelevance()))
                    .body("parentalRating", equalTo(validMovie.getParentalRating().toString()))
                    .body("genres", hasItems());
    }

    @Test
    void when_PuttingValidReleaseYear1895MovieAtValidId_Expect_Status200Properties() {
        Movie validMovie = createMovie("valid", "Valid movie", 10, Year.of(1895),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);

        RestAssured
                .given()
                .pathParam("movieId", VALID_MOVIE_ID)
                .body(JsonReader.readObjectAsJson(movieInputDTO))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{movieId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(validMovie.getName()))
                .body("synopsis", equalTo(validMovie.getSynopsis()))
                .body("relevance", equalTo(validMovie.getRelevance()))
                .body("parentalRating", equalTo(validMovie.getParentalRating().toString()))
                .body("genres", hasItems());
    }

    @Test
    void when_PuttingValidMovieAtNonExistingMovieId_Expect_Status404Properties() {
        Movie validMovie = createMovie("valid", "Valid movie", 10, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);

        RestAssured
                .given()
                    .pathParam("movieId", NON_EXISTING_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail(Movie.class.getSimpleName(), NON_EXISTING_MOVIE_ID)));;
    }

    @Test
    void when_PuttingValidMovieAtInvalidMovieId_Expect_Status400TitleAndDetail() {
        Movie validMovie = createMovie("valid", "Valid movie", 10, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);


        RestAssured
                .given()
                    .pathParam("movieId", INVALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                    .body("detail", equalTo(
                            getInvalidUrlParameterDetail("movieId", INVALID_MOVIE_ID, VALID_MOVIE_ID)));
    }

    @Test
    void when_PuttingInvalidMovieWithoutNameAtValidId_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie(null, "Valid movie", 10, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("name"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PuttingInvalidMovieWithoutSynopsisAtValidId_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("valid", null, 10, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("synopsis"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PuttingInvalidMovieWithoutRelevanceAtValidId_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("valid", "Valid movie", null, Year.of(2003),
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("relevance"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PuttingInvalidMovieWithoutReleaseYearAtValidId_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("valid", "Valid movie", 10, null,
                Rating.G, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("releaseYear"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PuttingInvalidMovieWithoutRatingAtValidId_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("valid", "Valid movie", 10, Year.of(2003),
                null, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("parentalRating"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PuttingInvalidMovieWithoutDurationAtValidId_Expect_Status400TitleDetailAndObjectName() {
        Movie invalidMovie = createMovie("valid", "Valid movie", 10, Year.of(2003),
                Rating.PG_13, Set.of(), null);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(invalidMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("duration"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PuttingInvalidMovieWithReleaseYearBefore1895AtValidId_Expect_Status400TitleDetailAndObjectName() {
        Movie validMovie = createMovie("valid", "Valid movie", 10, Year.of(1894),
                Rating.PG_13, Set.of(), 120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("releaseYear"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_INVALID_RELEASE_YEAR));
    }

    @Test
    void when_PuttingInvalidMovieWithZeroDurationAtValidId_Expect_Status400TitleDetailAndObjectName() {
        Movie validMovie = createMovie("valid", "Valid movie", 10, Year.of(1900),
                Rating.PG_13, Set.of(), 0);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("duration"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PuttingInvalidMovieWithNegativeDurationAtValidId_Expect_Status400TitleDetailAndObjectName() {
        Movie validMovie = createMovie("valid", "Valid movie", 10, Year.of(1900),
                Rating.PG_13, Set.of(), -120);
        MovieInputDto movieInputDTO = movieInputDtoAssembler.toInputDto(validMovie);

        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(JsonReader.readObjectAsJson(movieInputDTO))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("duration"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PuttingInvalidMovieWithNonExistingParentalRatingAtValidMovieId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(incorrectMovieJsonWithNonExistingRating)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(MESSAGE_NOT_READABLE.getTitle()))
                    .body("detail", equalTo(getMessageNotReadableDetail("parentalRating", INVALID_PARENTAL_RATE)));
    }

    @Test
    void when_PuttingInvalidMovieWithNonExistingPropertyAtValidMovieId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                    .body(incorrectMovieJsonWithNonExistingProperty)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_PROPERTY.getTitle()))
                    .body("detail", equalTo(getInvalidPropertyDetail(INVALID_PROPERTY_FIELD)));
    }

    @Test
    void when_DeletingByValidMovieId_Expect_Status204() {
        RestAssured
                .given()
                    .pathParam("movieId", VALID_MOVIE_ID)
                .when()
                    .delete("/{movieId}")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void when_DeletingByNonExistingMovieId_Expect_Status404() {
        RestAssured
                .given()
                    .pathParam("movieId", NON_EXISTING_MOVIE_ID)
                .when()
                    .delete("/{movieId}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail("Movie", NON_EXISTING_MOVIE_ID)));
    }

    @Test
    void when_DeletingByInvalidMovieId_Expect_Status404() {
        RestAssured
                .given()
                    .pathParam("movieId", INVALID_MOVIE_ID)
                .when()
                    .delete("/{movieId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                    .body("detail", equalTo(
                            getInvalidUrlParameterDetail("movieId", INVALID_MOVIE_ID, VALID_MOVIE_ID)));
    }

    private void setUpData() {
        setUpGenres();

        setUpMovies();

        setUpSeries();

        movieQuantity = movieRepository.findAll().size();
    }

    private void setUpGenres() {
        terror = createGenre("Terror");
        comedy = createGenre("Comedy");
        drama = createGenre("Drama");
        scienceFiction = createGenre("Science Fiction");

        genreRepository.saveAll(Arrays.asList(terror, comedy, drama, scienceFiction));
    }

    private void setUpMovies() {

        movieWithGenres = createMovie("FNAF", "Crazy bear", 8, Year.of(2023),
                Rating.PG_13, Set.of(terror, drama), 120);
        movieWithoutGenres = createMovie("Avatar 2", "Crazy Blue people", 10, Year.of(2023),
                Rating.PG_13, Set.of(drama, scienceFiction), 150);

        movieRepository.saveAll(Arrays.asList(movieWithGenres, movieWithoutGenres));
    }

    private void setUpSeries() {
        series = createSeries("Ted Lasso", "Football coach",
                10, Year.of(2020), Rating.G, Set.of(comedy, drama));

        entertainmentWorkRepository.save(series);
    }

    private Genre createGenre(String name) {
        Genre genre = new Genre();

        genre.setName(name);

        return genre;
    }

    private Movie createMovie(String name, String synopsis, Integer relevance, Year releaseYear,
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

    private Series createSeries(String name, String synopsis, Integer relevance, Year releaseYear,
                              Rating parentalRating, Set<Genre> genres) {

        Series series = new Series();

        series.setName(name);
        series.setSynopsis(synopsis);
        series.setReleaseYear(releaseYear);
        series.setRelevance(relevance);
        series.setParentalRating(parentalRating);
        series.setGenres(genres);
        series.setType(SERIES);

        return series;
    }

    private String getResourceNotFoundDetail(String resource, Long id) {
        return "%s with id %d doesn't exist".formatted(resource, id);
    }

    private String getInvalidUrlParameterDetail(String urlParameter, String value, Object expectedValueType) {
        return String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                urlParameter, value.getClass().getSimpleName(), expectedValueType.getClass().getSimpleName());
    }

    private String getInvalidPropertyDetail(String property) {
        return String.format("Property '%s' is not valid, remove it", property);
    }

    private String getInvalidDataErrorDetail() {
        return "One or more fields are not valid. Correct it.";
    }

    private String getEntityBeingUsedDetail(String entity, Long id) {
        return "%s with id %d is being used and can't be removed".formatted(entity, id);
    }

    private String getMessageNotReadableDetail(String property, String value) {
        return "Property '%s' has a value of '%s' , replace it with a compatible value of type 'Rating'".formatted(property, value);
    }
}
