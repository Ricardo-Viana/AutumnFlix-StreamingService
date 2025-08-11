package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.domain.model.*;
import com.autumnflix.streaming.domain.repository.EntertainmentWorkRepository;
import com.autumnflix.streaming.domain.repository.GenreRepository;
import com.autumnflix.streaming.util.DatabaseCleaner;
import com.autumnflix.streaming.util.JsonReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.time.Year;
import java.util.List;
import java.util.Set;

import static com.autumnflix.streaming.api.exceptionHandler.ApiErrorType.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class GenreApiTest {

    private static final Long VALID_GENRE_ID_BEING_USED_BY_ENTERTAINMENT_WORK = 1L;
    private static final Long VALID_GENRE_ID_NOT_BEING_USED_BY_ENTERTAINMENT_WORK = 2L;
    private static final Long NON_EXISTING_GENRE_ID = Long.MAX_VALUE;
    private static final String INVALID_GENRE_ID = "a";

    private static final String INVALID_PROPERTY_FIELD = "invalidProperty";

    private static final String RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE = RESOURCE_NOT_FOUND.getTitle();
    private static final String INVALID_PROPERTY_ERROR_TYPE_TITLE = INVALID_PROPERTY.getTitle();
    private static final String INVALID_DATA_ERROR_TYPE_TITLE = INVALID_DATA.getTitle();
    private static final String INVALID_URL_PARAMETER_ERROR_TYPE_TITLE = INVALID_URL_PARAMETER.getTitle();
    private static final String ENTITY_BEING_USED_ERROR_TYPE_TITLE = ENTITY_BEING_USED.getTitle();

    @LocalServerPort
    private int port;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private EntertainmentWorkRepository entertainmentWorkRepository;

    private int genreQuantity;
    private Genre terror;
    private Genre action;
    private Genre validGenre;
    private EntertainmentWork terrorEntertainmentWork;

    private final String correctGenreJson;
    private final String incorrectGenreJsonWithNullName;
    private final String incorrectGenreJsonWithNonExistingProperty;

    public GenreApiTest() {
        this.correctGenreJson = JsonReader
                .readJsonAsString("src/test/resources/JSON/valid/genre/validGenre.JSON");

        this.incorrectGenreJsonWithNullName = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/genre/invalidGenreWithNullName.JSON");

        this.incorrectGenreJsonWithNonExistingProperty = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/genre/invalidGenreWithNonExistingProperty.JSON");
    }

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/genres";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingAllGenres_Expect_Status200QuantityAndNames() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get()
                .then()
                    .statusCode(OK.value())
                    .body("", hasSize(genreQuantity))
                    .body("name", hasItems(terror.getName(), action.getName()));
    }

    @Test
    void when_GettingByValidGenreId_Expect_Status200AndName() {
        RestAssured
                .given()
                    .pathParam("genreId", terror.getId())
                    .accept(ContentType.JSON)
                .when()
                    .get("/{genreId}")
                .then()
                    .statusCode(OK.value())
                    .body("name", equalTo(terror.getName()));
    }

    @Test
    void when_GettingByNonExistingGenreId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("genreId", NON_EXISTING_GENRE_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{genreId}")
                .then()
                    .statusCode(NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                    .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_GENRE_ID)));
    }

    @Test
    void when_GettingByInvalidGenreId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("genreId", INVALID_GENRE_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{genreId}")
                .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_URL_PARAMETER_ERROR_TYPE_TITLE))
                    .body("detail", equalTo(getInvalidUrlParameterDetail("genreId", INVALID_GENRE_ID)));
    }

    @Test
    void when_PostingValidGenre_Expect_Status201AndName() {
        RestAssured
                .given()
                    .body(correctGenreJson)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(CREATED.value())
                    .body("name", equalTo(validGenre.getName()));;
    }

    @Test
    void when_PostingInvalidGenreWithNullRequiredProperty_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .body(incorrectGenreJsonWithNullName)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                    .body("detail", equalTo(getInvalidDataErrorDetail()));
    }

    @Test
    void when_PostingInvalidGenreWithNonExistingProperty_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .body(incorrectGenreJsonWithNonExistingProperty)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_PROPERTY_ERROR_TYPE_TITLE))
                    .body("detail", equalTo(getInvalidPropertyDetail(INVALID_PROPERTY_FIELD)));
    }

    @Test
    void when_PuttingValidGenre_Expect_Status200AndProperties() {
        RestAssured
                .given()
                    .pathParam("genreId", VALID_GENRE_ID_BEING_USED_BY_ENTERTAINMENT_WORK)
                    .body(correctGenreJson)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{genreId}")
                .then()
                    .statusCode(OK.value())
                    .body("id", equalTo(VALID_GENRE_ID_BEING_USED_BY_ENTERTAINMENT_WORK.intValue()))
                    .body("name", equalTo(validGenre.getName()));;
    }

    @Test
    void when_PuttingValidGenreWithNonExistingGenreId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("genreId", NON_EXISTING_GENRE_ID)
                    .body(correctGenreJson)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{genreId}")
                .then()
                    .statusCode(NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                    .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_GENRE_ID)));
    }

    @Test
    void when_PuttingInvalidGenreWithNonExistingProperty_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("genreId", VALID_GENRE_ID_BEING_USED_BY_ENTERTAINMENT_WORK)
                    .body(incorrectGenreJsonWithNonExistingProperty)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{genreId}")
                .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_PROPERTY_ERROR_TYPE_TITLE))
                    .body("detail", equalTo(getInvalidPropertyDetail(INVALID_PROPERTY_FIELD)));
    }

    @Test
    void when_PuttingInvalidGenreWithNullRequiredProperties_Expect_Status400TitleAndDetails() {
        RestAssured
                .given()
                    .pathParam("genreId", VALID_GENRE_ID_BEING_USED_BY_ENTERTAINMENT_WORK)
                    .body(incorrectGenreJsonWithNullName)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{genreId}")
                .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                    .body("detail", equalTo(getInvalidDataErrorDetail()));
    }

    @Test
    void when_DeletingValidGenreId_Expect_Status204() {
        RestAssured
                .given()
                    .pathParam("genreId", VALID_GENRE_ID_NOT_BEING_USED_BY_ENTERTAINMENT_WORK)
                .when()
                    .delete("/{genreId}")
                .then()
                    .statusCode(NO_CONTENT.value());
    }

    @Test
    void when_DeletingNonExistingGenreId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("genreId", NON_EXISTING_GENRE_ID)
                .when()
                    .delete("/{genreId}")
                .then()
                    .statusCode(NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                    .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_GENRE_ID)));
    }

    @Test
    void when_DeletingGenreThatIsBeingUsed_Expect_Status409TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("genreId", VALID_GENRE_ID_BEING_USED_BY_ENTERTAINMENT_WORK)
                .when()
                    .delete("/{genreId}")
                .then()
                    .statusCode(CONFLICT.value())
                    .body("title", equalTo(ENTITY_BEING_USED_ERROR_TYPE_TITLE))
                    .body("detail", equalTo(getEntityBeingUsedDetail(terror.getClass().getSimpleName(), VALID_GENRE_ID_BEING_USED_BY_ENTERTAINMENT_WORK)));
    }

    private void setUpData() {
        terror = createGenre(VALID_GENRE_ID_BEING_USED_BY_ENTERTAINMENT_WORK, "Terror");
        action = createGenre(VALID_GENRE_ID_NOT_BEING_USED_BY_ENTERTAINMENT_WORK, "Action");

        genreRepository.saveAll(List.of(terror, action));

        terrorEntertainmentWork = createTerrorEntertainmentWork(
                1L, "FNAF", "Crazy bear", 8,
                Year.of(2023), Rating.PG_13, EntertainmentWorkType.MOVIE, Set.of(terror)
        );

        entertainmentWorkRepository.save(terrorEntertainmentWork);

        validGenre = new Genre();
        validGenre.setId(3L);
        validGenre.setName("Comedy");

        genreQuantity = genreRepository.findAll().size();
    }

    private String getResourceNotFoundDetail(Long id) {
        return String.format("Genre with id %d doesn't exist", id);
    }

    private String getInvalidPropertyDetail(String field) {
        return String.format("Property '%s' is not valid, remove it", field);
    }

    private String getInvalidDataErrorDetail() {
        return "One or more fields are not valid. Correct it.";
    }

    private String getInvalidUrlParameterDetail(String urlParameter, String value) {
        return String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                urlParameter, value.getClass().getSimpleName(), "Long");
    }

    private String getEntityBeingUsedDetail(String entity, Long id) {
        return "%s with id %d is being used and can't be removed".formatted(entity, id);
    }

    private Genre createGenre(Long id, String name) {
        Genre genre = new Genre();

        genre.setId(id);
        genre.setName(name);

        return genre;
    }

    private EntertainmentWork createTerrorEntertainmentWork(Long id, String name, String synopsis, Integer relevance, Year releaseYear,
                                                            Rating parentalRating, EntertainmentWorkType type,
                                                            Set<Genre> genres) {

        EntertainmentWork entertainmentWork = new EntertainmentWork();
        entertainmentWork.setId(id);
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
