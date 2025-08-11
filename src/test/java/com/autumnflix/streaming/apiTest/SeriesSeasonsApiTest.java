package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.api.assembler.season.SeasonInputDtoAssembler;
import com.autumnflix.streaming.api.exceptionHandler.ApiErrorType;
import com.autumnflix.streaming.api.model.season.SeasonInputDto;
import com.autumnflix.streaming.domain.model.*;
import com.autumnflix.streaming.domain.repository.EpisodeRepository;
import com.autumnflix.streaming.domain.repository.SeasonRepository;
import com.autumnflix.streaming.domain.repository.SeriesRepository;
import com.autumnflix.streaming.util.DatabaseCleaner;
import com.autumnflix.streaming.util.JsonReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

import static com.autumnflix.streaming.api.exceptionHandler.ApiErrorType.*;
import static com.autumnflix.streaming.domain.model.EntertainmentWorkType.SERIES;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class SeriesSeasonsApiTest {

    private static final Long VALID_SERIES_WITHOUT_SEASON_ID = 1L;
    private static final Long VALID_SERIES_WITH_SEASON_ID = 2L;
    private static final Long VALID_SEASON_WITH_EPISODE_ID = 1L;
    private static final Long VALID_SEASON_WITHOUT_EPISODE_ID = 2L;
    private static final Integer VALID_SEASON_WITH_EPISODE_NUMBER = 1;
    private static final Integer VALID_SEASON_WITHOUT_EPISODE_NUMBER = 2;

    private static final Long NON_EXISTING_ID = Long.MAX_VALUE;
    private static final Integer NON_EXISTING_NUMBER = Integer.MAX_VALUE;
    private static final String INVALID_ID = "INVALID";
    private static final String INVALID_NUMBER = "INVALID";
    private static final String INVALID_PROPERTY_FIELD = "invalidProperty";

    private static final String USER_MESSAGE_NOT_NULL = "não deve ser nulo";
    private static final String USER_MESSAGE_NOT_BLANK = "não deve estar em branco";
    private static final String USER_MESSAGE_POSITIVE = "must be greater than zero";

    private Series seriesExampleWithoutSeasons;
    private Series seriesExampleWithSeason;
    private Season seasonExampleWithEpisode;
    private Season seasonExampleWithoutEpisode;
    private Episode episodeExample;

    private String invalidSeasonJsonWithNonExistingProperty;

    private Integer seasonQuantitySeriesExample;

    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private SeasonRepository seasonRepository;
    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private SeasonInputDtoAssembler seasonInputDtoAssembler;

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/series/{seriesId}/seasons";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingAllSeasonsByValidSeriesWithSeasonId_Expect_Status200QuantityAndProperties() {

        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasSize(seasonQuantitySeriesExample))
                .body("id", hasItems(seasonExampleWithEpisode.getId().intValue(), seasonExampleWithoutEpisode.getId().intValue()))
                .body("number", hasItems(seasonExampleWithEpisode.getNumber(), seasonExampleWithoutEpisode.getNumber()))
                .body("synopsis", hasItems(seasonExampleWithEpisode.getSynopsis(), seasonExampleWithoutEpisode.getSynopsis()));
    }

    @Test
    void when_GettingAllSeasonsByValidSeriesWithoutSeasonId_Expect_Status200QuantityAndProperties() {

        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_WITHOUT_SEASON_ID)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasSize(0));
    }

    @Test
    void when_GettingAllSeasonsByNonExistingSeriesId_Expect_Status404TitleAndDetail() {

        RestAssured
                .given()
                    .pathParam("seriesId", NON_EXISTING_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_ID)));
    }

    @Test
    void when_GettingAllSeasonsByInvalidSeriesId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("seriesId", INVALID_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                    .body("detail", equalTo(getInvalidUrlParameterDetail(
                            "seriesId", INVALID_ID, VALID_SERIES_WITHOUT_SEASON_ID.getClass().getSimpleName()))
                    );
    }

    @Test
    void when_GettingSeasonByValidSeasonNumber_Expect_Status200AndProperties() {

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{seasonNumber}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(seasonExampleWithEpisode.getId().intValue()))
                    .body("number", equalTo(seasonExampleWithEpisode.getNumber()))
                    .body("synopsis", equalTo(seasonExampleWithEpisode.getSynopsis()));
    }

    @Test
    void when_GettingAllSeasonsByNonExistingSeasonNumber_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .pathParam("seasonNumber", NON_EXISTING_NUMBER)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{seasonNumber}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail(
                            NON_EXISTING_NUMBER, VALID_SERIES_WITH_SEASON_ID))
                    );
    }

    @Test
    void when_GettingAllSeasonsInvalidSeasonNumber_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .pathParam("seasonNumber", INVALID_NUMBER)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{seasonNumber}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                    .body("detail", equalTo(getInvalidUrlParameterDetail(
                            "seasonNumber", INVALID_NUMBER, VALID_SEASON_WITH_EPISODE_NUMBER.getClass().getSimpleName()))
                    );
    }

    @Test
    void when_PostingValidSeasonAtValidSeriesWithSeasonId_Expect_Status201AndProperties() {
        Season validSeason = createSeason(null, 3, "VALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", equalTo(seasonQuantitySeriesExample + 1))
                    .body("number", equalTo(validSeason.getNumber()))
                    .body("synopsis", equalTo(validSeason.getSynopsis()));
    }

    @Test
    void when_PostingValidSeasonAtValidSeriesWithoutSeasonId_Expect_Status201AndProperties() {
        Season validSeason = createSeason(null, 1, "VALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITHOUT_SEASON_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", equalTo(seasonQuantitySeriesExample + 1))
                    .body("number", equalTo(validSeason.getNumber()))
                    .body("synopsis", equalTo(validSeason.getSynopsis()));
    }

    @Test
    void when_PostingInvalidSeasonWithNullNumberAtValidSeriesId_Expect_Status400TitleDetailAndObject() {
        Season validSeason = createSeason(null, null, "VALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("number"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PostingInvalidSeasonWithNullSynopsisAtValidSeriesId_Expect_Status400TitleDetailAndObject() {
        Season validSeason = createSeason(null, 2, null);
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
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
    void when_PostingInvalidSeasonWithNumber0AtValidSeriesId_Expect_Status400TitleDetailAndObject() {
        Season validSeason = createSeason(null, 0, "EXAMPLE");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("number"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PostingInvalidSeasonWithNegativeNumberAtValidSeriesId_Expect_Status400TitleDetailAndObject() {
        Season validSeason = createSeason(null, -1, "EXAMPLE");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("number"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PostingInvalidSeasonWithDuplicatedNumberAtValidSeriesId_Expect_Status400TitleDetailAndObject() {
        Season validSeason = createSeason(null, 1, "EXAMPLE");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(BUSINESS_ERROR.getTitle()))
                    .body("detail", equalTo(
                            getDuplicatedNumberSeasonMsg(validSeason.getNumber(), VALID_SERIES_WITH_SEASON_ID)));
    }

    @Test
    void when_PostingInvalidWithNonExistingPropertyAtValidSeriesId_Expect_Status400TitleDetailAndObject() {
        RestAssured
                .given()
                    .body(invalidSeasonJsonWithNonExistingProperty)
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
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
    void when_PostingValidSeasonAtInvalidSeriesId_Expect_Status404TitleAndDetail() {
        Season validSeason = createSeason(null, 2, "VALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", NON_EXISTING_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_ID)));
    }

    @Test
    void when_PuttingValidSeasonAtValidSeasonNumber_Expect_Status200TitleAndDetail() {
        Season validSeason = createSeason(null, 3, "VALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seasonNumber}")
                .then()
                    .body("id", equalTo(VALID_SEASON_WITH_EPISODE_ID.intValue()))
                    .body("number", equalTo(validSeason.getNumber()))
                    .body("synopsis", equalTo(validSeason.getSynopsis()));
    }

    @Test
    void when_PuttingValidSeasonAtNonExistingSeriesId_Expect_Status404TitleAndDetail() {
        Season validSeason = createSeason(null, 2, "VALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", NON_EXISTING_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seasonNumber}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_ID)));
    }

    @Test
    void when_PuttingValidSeasonAtInvalidSeriesId_Expect_Status400TitleDetailAndObject() {
        Season validSeason = createSeason(null, 2, "VALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(seasonInputDto))
                .pathParam("seriesId", INVALID_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{seasonNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail(
                        "seriesId", INVALID_ID, VALID_SERIES_WITHOUT_SEASON_ID.getClass().getSimpleName())));
    }

    @Test
    void when_PuttingValidSeasonAtNonExistingSeasonNumber_Expect_Status404TitleAndDetail() {
        Season validSeason = createSeason(null, 2, "VALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(validSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .pathParam("seasonNumber", NON_EXISTING_NUMBER)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seasonNumber}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail(
                            NON_EXISTING_NUMBER, VALID_SERIES_WITH_SEASON_ID)));
    }

    @Test
    void when_PuttingInvalidSeasonWithNullNumberAtValidSeasonNumber_Expect_Status400TitleDetailAndObject() {
        Season invalidSeason = createSeason(null, null, "INVALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(invalidSeason);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seasonInputDto))
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seasonNumber}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("number"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PuttingInvalidSeasonWithNullSynopsisAtValidSeasonNumber_Expect_Status400TitleDetailAndObject() {
        Season invalidSeason = createSeason(null, 2, null);
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(invalidSeason);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(seasonInputDto))
                .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{seasonNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("synopsis"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PuttingInvalidSeasonWithNumber0AtValidSeasonNumber_Expect_Status400TitleDetailAndObject() {
        // Create an invalid season with number 0
        Season invalidSeason = createSeason(null, 0, "INVALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(invalidSeason);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(seasonInputDto))
                .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{seasonNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("number"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PuttingInvalidSeasonWithNegativeNumberAtValidSeasonNumber_Expect_Status400TitleDetailAndObject() {
        // Create an invalid season with negative number
        Season invalidSeason = createSeason(null, -1, "INVALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(invalidSeason);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(seasonInputDto))
                .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{seasonNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("number"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PuttingInvalidSeasonWithDuplicatedNumberAtValidSeasonNumber_Expect_Status400TitleDetailAndObject() {
        Season invalidSeason = createSeason(null, 1, "INVALID SEASON");
        SeasonInputDto seasonInputDto = seasonInputDtoAssembler.toInputDto(invalidSeason);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(seasonInputDto))
                .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_NUMBER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{seasonNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(BUSINESS_ERROR.getTitle()))
                .body("detail", equalTo(
                        getDuplicatedNumberSeasonMsg(invalidSeason.getNumber(), VALID_SERIES_WITH_SEASON_ID)));
    }

    @Test
    void when_PuttingSeasonWithInvalidProperty_Expect_Status400TitleDetailAndObject() {
        RestAssured
                .given()
                    .body(invalidSeasonJsonWithNonExistingProperty)
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seasonNumber}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_PROPERTY.getTitle()))
                    .body("detail", equalTo(getInvalidPropertyDetail("invalidProperty")));
    }


    @Test
    void when_DeletingByValidSeasonNumber_Expect_Status204() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_NUMBER)
                .when()
                .delete("{seasonNumber}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void when_DeletingByValidSeasonWithEpisodeNumber_Expect_Status409() {
        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .when()
                    .delete("{seasonNumber}")
                .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                .body("title", equalTo(ENTITY_BEING_USED.getTitle()))
                .body("detail", equalTo(getEntityBeingUsedDetail("Season", VALID_SEASON_WITH_EPISODE_NUMBER)));
    }

    @Test
    void when_DeletingByNonExistingSeriesId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("seriesId", NON_EXISTING_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_NUMBER)
                .when()
                .delete("/{seasonNumber}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_ID)));
    }

    @Test
    void when_DeletingByNonExistingSeasonNumber_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                .pathParam("seasonNumber", NON_EXISTING_NUMBER)
                .when()
                .delete("/{seasonNumber}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_NUMBER, VALID_SERIES_WITH_SEASON_ID)));
    }

    @Test
    void when_DeletingByInvalidSeriesId_Expect_Status400TitleDetailAndObject() {
        RestAssured
                .given()
                .pathParam("seriesId", INVALID_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_NUMBER)
                .when()
                .delete("/{seasonNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail(
                        "seriesId", INVALID_ID, VALID_SERIES_WITHOUT_SEASON_ID.getClass().getSimpleName())));
    }

    @Test
    void when_DeletingByInvalidSeasonNumber_Expect_Status400TitleDetailAndObject() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_WITH_SEASON_ID)
                .pathParam("seasonNumber", INVALID_NUMBER)
                .when()
                .delete("/{seasonNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail(
                        "seasonNumber", INVALID_NUMBER, VALID_SEASON_WITH_EPISODE_NUMBER.getClass().getSimpleName())));
    }


    private void setUpData() {

        setUpSeries();

        setUpSeasons();

        setUpEpisode();

        setUpJsons();

        seasonQuantitySeriesExample = seasonRepository.findBySeries(seriesExampleWithSeason).size();
    }

    private void setUpSeries() {

        seriesExampleWithoutSeasons = createSeries(VALID_SERIES_WITHOUT_SEASON_ID, "Series example1", "sample1", 8,
                Year.of(2000), Rating.PG_13, Set.of());
        seriesExampleWithSeason = createSeries(VALID_SERIES_WITH_SEASON_ID, "Series example2", "sample2", 10,
                Year.of(1900), Rating.PG_13, Set.of());

        seriesRepository.saveAll(List.of(seriesExampleWithoutSeasons, seriesExampleWithSeason));
    }

    private void setUpSeasons() {

        seasonExampleWithEpisode = createSeason(VALID_SEASON_WITH_EPISODE_ID, VALID_SEASON_WITH_EPISODE_NUMBER,
                "EXAMPLE");
        seasonExampleWithoutEpisode = createSeason(VALID_SEASON_WITHOUT_EPISODE_ID, VALID_SEASON_WITHOUT_EPISODE_NUMBER,
                "EXAMPLE");

        attachSeason(seasonExampleWithEpisode, seriesExampleWithSeason);
        attachSeason(seasonExampleWithoutEpisode, seriesExampleWithSeason);
    }

    private void setUpEpisode() {
        episodeExample = createEpisode(1L, 1, "EPISODE", "EXAMPLE", 60);

        attachEpisode(episodeExample, seasonExampleWithEpisode);
    }

    private void setUpJsons() {
        invalidSeasonJsonWithNonExistingProperty = JsonReader
                        .readJsonAsString("src/test/resources/JSON/invalid/season/invalidSeasonWithNonExistingProperty.json");
    }

    private Series createSeries(Long id, String name, String synopsis, Integer relevance, Year releaseYear,
                                Rating parentalRating, Set<Genre> genres) {

        Series series = new Series();

        series.setId(id);
        series.setName(name);
        series.setSynopsis(synopsis);
        series.setReleaseYear(releaseYear);
        series.setRelevance(relevance);
        series.setParentalRating(parentalRating);
        series.setGenres(genres);
        series.setType(SERIES);

        return series;
    }

    private Season createSeason(Long id, Integer number, String synopsis) {

        Season season = new Season();

        season.setId(id);
        season.setNumber(number);
        season.setSynopsis(synopsis);

        return season;
    }

    private Episode createEpisode(Long id, Integer number, String name, String synopsis, Integer duration) {

        Episode episode = new Episode();

        episode.setId(id);
        episode.setNumber(number);
        episode.setName(name);
        episode.setSynopsis(synopsis);
        episode.setDuration(duration);

        return episode;
    }

    public void attachSeason(Season season, Series series) {
        season.setSeries(series);

        series.getSeasons().add(season);

        seasonRepository.save(season);
        seriesRepository.save(series);
    }

    private void attachEpisode(Episode episode, Season season) {
        episode.setSeason(season);

        season.getEpisodes().add(episode);

        episodeRepository.save(episode);
        seasonRepository.save(season);
    }

    private String getResourceNotFoundDetail(String resource, Long id) {
        return "%s with id %d doesn't exist".formatted(resource, id);
    }

    private String getResourceNotFoundDetail(Integer seasonNumber, Long seriesId) {
        return "The season %d from the series of id %d doesn't exists".formatted(seasonNumber, seriesId);
    }

    private String getInvalidUrlParameterDetail(String urlParameter, String value, String expectedValueType) {
        return String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                urlParameter, value.getClass().getSimpleName(), expectedValueType);
    }

    private String getInvalidDataErrorDetail() {
        return "One or more fields are not valid. Correct it.";
    }

    private String getInvalidPropertyDetail(String property) {
        return String.format("Property '%s' is not valid, remove it", property);
    }

    private String getDuplicatedNumberSeasonMsg(Integer seasonNumber, Long seriesId) {
        return "Season %d already exists for the series with id %d".formatted(seasonNumber, seriesId);
    }

    private String getEntityBeingUsedDetail(String entity, Integer number) {
        return "%s %d is being used and can't be removed".formatted(entity, number);
    }
}
