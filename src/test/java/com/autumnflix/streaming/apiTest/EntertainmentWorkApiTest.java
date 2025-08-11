package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.api.exceptionHandler.ApiErrorType;
import com.autumnflix.streaming.domain.model.*;
import com.autumnflix.streaming.domain.repository.EntertainmentWorkRepository;
import com.autumnflix.streaming.domain.repository.GenreRepository;
import com.autumnflix.streaming.util.DatabaseCleaner;
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
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EntertainmentWorkApiTest {

    private static final int NON_EXISTING_ENTERTAINMENT_WORK_ID = Integer.MAX_VALUE;
    private static final String INVALID_ENTERTAINMENT_WORK_ID = "a";
    private static final String ENTERTAINMENT_WORK_ID_SIMPLE_CLASS_NAME = Long.valueOf(1L).getClass().getSimpleName();

    private static final String RESOURCE_NOT_FOUND_TITLE = ApiErrorType.RESOURCE_NOT_FOUND.getTitle();
    private static final String INVALID_URL_PARAMETER_TITLE = ApiErrorType.INVALID_URL_PARAMETER.getTitle();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;


    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private EntertainmentWorkRepository entertainmentWorkRepository;

    private Genre terror;
    private Genre comedy;
    private Genre drama;
    private EntertainmentWork fnaf;
    private EntertainmentWork tedLasso;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/entertainment-works";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingAllEntertainmentWork_Expect_Status200QuantityAndProperties() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("", hasSize(2))
                    .body("name", hasItems(fnaf.getName(), tedLasso.getName()))
                    .body("synopsis", hasItems(fnaf.getSynopsis(), tedLasso.getSynopsis()))
                    .body("parentalRating", hasItems(
                            fnaf.getParentalRating().toString(), tedLasso.getParentalRating().toString()))
                    .body("type", hasItems(fnaf.getType().toString(), tedLasso.getType().toString()));
    }

    @Test
    void when_GettingByEntertainmentWorkId_Expect_Status200AndProperties() {
        RestAssured
                .given()
                    .pathParam("entertainmentWorkId", fnaf.getId())
                    .accept(ContentType.JSON)
                .when()
                    .get("/{entertainmentWorkId}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("name", equalTo(fnaf.getName()))
                    .body("synopsis", equalTo(fnaf.getSynopsis()))
                    .body("relevance", equalTo(fnaf.getRelevance()))
                    .body("parentalRating", equalTo(fnaf.getParentalRating().toString()))
                    .body("type", equalTo(fnaf.getType().toString()))
                    .body("genres", hasItems(
                            fnaf.getGenres().stream()
                                    .map(genre -> hasEntry("name", genre.getName()))
                                    .toArray(Matcher[]::new)
                    ));
    }

    @Test
    void when_GettingByNonExistingEntertainmentWorkId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("entertainmentWorkId", NON_EXISTING_ENTERTAINMENT_WORK_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{entertainmentWorkId}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND_TITLE))
                    .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_ENTERTAINMENT_WORK_ID)));
    }

    @Test
    void when_GettingByInvalidEntertainmentWorkId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("entertainmentWorkId", INVALID_ENTERTAINMENT_WORK_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{entertainmentWorkId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_URL_PARAMETER_TITLE))
                    .body("detail", equalTo(getInvalidUrlParameterDetail(
                            "entertainmentWorkId", INVALID_ENTERTAINMENT_WORK_ID, ENTERTAINMENT_WORK_ID_SIMPLE_CLASS_NAME))
                    );
    }

    private void setUpData() {
        terror = createGenre("Terror");
        comedy = createGenre("Comedy");
        drama = createGenre("Drama");
        genreRepository.saveAll(List.of(terror, comedy, drama));

        fnaf = createEntertainmentWork("FNAF", "Crazy bear", 8, Year.of(2023),
                Rating.PG_13, EntertainmentWorkType.MOVIE, Set.of(terror, drama));
        tedLasso = createEntertainmentWork("Ted Lasso", "Football coach",
                10, Year.of(2020), Rating.G, EntertainmentWorkType.SERIES, Set.of(comedy, drama));
        entertainmentWorkRepository.saveAll(List.of(fnaf, tedLasso));
    }

    private String getResourceNotFoundDetail(int id) {
        return "Entertainment Work with id %d doesn't exist".formatted(id);
    }

    private String getInvalidUrlParameterDetail(String urlParameter, String value, String expectedValueType) {
        return String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                urlParameter, value.getClass().getSimpleName(), expectedValueType);
    }

    private Genre createGenre(String name) {
        Genre genre = new Genre();
        genre.setName(name);

        return genre;
    }

    private EntertainmentWork createEntertainmentWork(String name, String synopsis, Integer relevance, Year releaseYear,
                                                      Rating parentalRating, EntertainmentWorkType type,
                                                      Set<Genre> genres) {

        EntertainmentWork entertainmentWork = new EntertainmentWork();
        entertainmentWork.setName(name);
        entertainmentWork.setSynopsis(synopsis);
        entertainmentWork.setRelevance(relevance);
        entertainmentWork.setReleaseYear(releaseYear);
        entertainmentWork.setParentalRating(parentalRating);
        entertainmentWork.setType(type);
        entertainmentWork.setGenres(genres);

        return entertainmentWork;
    }
}
