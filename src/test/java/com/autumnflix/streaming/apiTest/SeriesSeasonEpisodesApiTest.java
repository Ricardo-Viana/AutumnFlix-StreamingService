package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.api.assembler.episode.EpisodeInputDtoAssembler;
import com.autumnflix.streaming.api.exceptionHandler.ApiErrorType;
import com.autumnflix.streaming.api.model.episode.EpisodeInputDto;
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
import java.util.Set;

import static com.autumnflix.streaming.domain.model.EntertainmentWorkType.SERIES;
import static org.hamcrest.Matchers.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SeriesSeasonEpisodesApiTest {

    private static final Long VALID_SERIES_ID = 1L;
    private static final Long VALID_SEASON_WITH_EPISODE_ID = 1L;
    private static final Long VALID_SEASON_WITHOUT_EPISODE_ID = 2L;
    private static final Long VALID_EPISODE_ID_1 = 1L;
    private static final Long VALID_EPISODE_ID_2 = 2L;
    private static final Integer VALID_SEASON_WITH_EPISODE_NUMBER = 1;
    private static final Integer VALID_SEASON_WITHOUT_EPISODE_NUMBER = 2;
    private static final Integer VALID_EPISODE_NUMBER_1 = 1;
    private static final Integer VALID_EPISODE_NUMBER_2 = 2;

    private static final Long NON_EXISTING_ID = Long.MAX_VALUE;
    private static final Integer NON_EXISTING_NUMBER = Integer.MAX_VALUE;
    private static final String INVALID_ID = "INVALID";
    private static final String INVALID_NUMBER = "INVALID";
    private static final String INVALID_PROPERTY_FIELD = "invalidProperty";

    private static final String USER_MESSAGE_NOT_NULL = "não deve ser nulo";
    private static final String USER_MESSAGE_NOT_BLANK = "não deve estar em branco";
    private static final String USER_MESSAGE_POSITIVE = "must be greater than zero";

    private Series seriesExample;
    private Season seasonExampleWithEpisode;
    private Season seasonExampleWithoutEpisode;
    private Episode episodeExample1;
    private Episode episodeExample2;

    private Integer seasonEpisodeQuantity;
    private Integer episodeQuantity;

    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private SeasonRepository seasonRepository;
    @Autowired
    private EpisodeRepository episodeRepository;
    @Autowired
    private EpisodeInputDtoAssembler episodeInputDtoAssembler;

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/series/{seriesId}/seasons/{seasonNumber}/episodes";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingByValidSeasonWithEpisodeNumber_Expect_status200QuantityAndProperties() {

        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasSize(seasonEpisodeQuantity))
                .body("id", hasItems(episodeExample1.getId().intValue(), episodeExample2.getId().intValue()))
                .body("name", hasItems(episodeExample1.getName(), episodeExample2.getName()))
                .body("number", hasItems(episodeExample1.getNumber(), episodeExample2.getNumber()))
                .body("synopsis", hasItems(episodeExample1.getSynopsis(), episodeExample2.getSynopsis()))
                .body("duration", hasItems(episodeExample1.getDuration(), episodeExample2.getDuration()));
    }

    @Test
    void when_GettingByValidSeasonWithoutEpisodeNumber_Expect_status200QuantityAndProperties() {

        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_NUMBER)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasSize(0))
                .body("id", hasItems())
                .body("name", hasItems())
                .body("number", hasItems())
                .body("synopsis", hasItems())
                .body("duration", hasItems());
    }

    @Test
    void when_GettingByNonExistingSeriesId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("seriesId", NON_EXISTING_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_ID)));
    }

    @Test
    void when_GettingByNonExistingSeasonNumber_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", NON_EXISTING_NUMBER)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_NUMBER, VALID_SERIES_ID)));
    }

    @Test
    void when_GettingByInvalidSeriesId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("seriesId", INVALID_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(
                        getInvalidUrlParameterDetail("seriesId", INVALID_ID, VALID_SERIES_ID)
                ));
    }

    @Test
    void when_GettingByInvalidSeasonNumber_Expect_Status400TitleAndDetailTitleAndDetail() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", INVALID_NUMBER)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(
                        getInvalidUrlParameterDetail("seasonNumber", INVALID_NUMBER, VALID_EPISODE_NUMBER_1)
                ));
    }

    @Test
    void when_GettingByValidEpisodeNumber_Expect_Status200AndProperties() {

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                    .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{episodeNumber}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(episodeExample1.getId().intValue()))
                    .body("name", equalTo(episodeExample1.getName()))
                    .body("number", equalTo(episodeExample1.getNumber()))
                    .body("synopsis", equalTo(episodeExample1.getSynopsis()))
                    .body("duration", equalTo(episodeExample1.getDuration()));
    }

    @Test
    void when_GettingByNonExistingEpisodeNumber_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", NON_EXISTING_NUMBER)
                .accept(ContentType.JSON)
                .when()
                .get("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail(VALID_SEASON_WITH_EPISODE_NUMBER, NON_EXISTING_NUMBER, VALID_SERIES_ID)));
    }

    @Test
    void when_GettingByInvalidEpisodeNumber_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", INVALID_NUMBER)
                .accept(ContentType.JSON)
                .when()
                .get("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail("episodeNumber", INVALID_NUMBER, VALID_EPISODE_NUMBER_1)));
    }

    @Test
    void when_PostingValidEpisodeAtValidSeason_Expect_Status201AndProperties() {

        Episode validEpisode = createEpisode(null, 1, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(validEpisode);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(episodeInputDto))
                    .pathParam("seriesId", VALID_SERIES_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", equalTo(episodeQuantity + 1))
                    .body("name", equalTo(validEpisode.getName()))
                    .body("number", equalTo(validEpisode.getNumber()))
                    .body("synopsis", equalTo(validEpisode.getSynopsis()))
                    .body("duration", equalTo(validEpisode.getDuration()));
    }

    @Test
    void when_PostingValidEpisodeAtNonExistingSeriesId_Expect_Status404TitleAndDetail() {
        Episode validEpisode = createEpisode(null, 1, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(validEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", NON_EXISTING_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
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
    void when_PostingValidEpisodeAtNonExistingSeasonNumber_Expect_Status404TitleAndDetail() {
        Episode validEpisode = createEpisode(null, 1, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(validEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", NON_EXISTING_NUMBER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_NUMBER, VALID_SERIES_ID)));
    }

    @Test
    void when_PostingValidEpisodeAtInvalidSeriesId_Expect_Status400TitleAndDetail() {
        Episode validEpisode = createEpisode(null, 1, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(validEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", INVALID_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail("seriesId", INVALID_ID, VALID_SERIES_ID)));
    }

    @Test
    void when_PostingValidEpisodeAtInvalidSeasonNumber_Expect_Status400TitleAndDetail() {
        Episode validEpisode = createEpisode(null, 1, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(validEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", INVALID_NUMBER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail("seasonNumber", INVALID_NUMBER, VALID_EPISODE_NUMBER_1)));
    }

    @Test
    void when_PostingInvalidEpisodeWithoutName_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 1, null, "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(episodeInputDto))
                    .pathParam("seriesId", VALID_SERIES_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("name"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PostingInvalidEpisodeWithoutNumber_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, null, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(episodeInputDto))
                    .pathParam("seriesId", VALID_SERIES_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("number"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PostingInvalidEpisodeWithoutSynopsis_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 1, "VALID", null, 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("synopsis"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PostingInvalidEpisodeWithoutDuration_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 1, "VALID", "EXAMPLE", null);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(episodeInputDto))
                    .pathParam("seriesId", VALID_SERIES_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("duration"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PostingInvalidEpisodeWithNumber0_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 0, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(episodeInputDto))
                    .pathParam("seriesId", VALID_SERIES_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("number"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PostingInvalidEpisodeWithNumberNegative_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, -1, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("number"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PostingInvalidEpisodeWithDuration0_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 10, "VALID", "EXAMPLE", 0);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("duration"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PostingInvalidEpisodeWithDurationNegative_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 1, "VALID", "EXAMPLE", -45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("duration"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PostingInvalidEpisodeWithDuplicatedNumber_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, VALID_EPISODE_NUMBER_1, "VALID", "EXAMPLE",
                100);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(episodeInputDto))
                    .pathParam("seriesId", VALID_SERIES_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(ApiErrorType.BUSINESS_ERROR.getTitle()))
                    .body("detail", equalTo(getDuplicatedNumberEpisodeMsg(
                            VALID_SEASON_WITH_EPISODE_NUMBER, VALID_EPISODE_NUMBER_1, VALID_SERIES_ID)));
    }

    @Test
    void when_PuttingValidEpisodeAtValidEpisodeNumber_Expect_Status200AndProperties() {
        Episode updatedEpisode = createEpisode(null, 10, "Updated Name", "Updated Synopsis",
                50);
        EpisodeInputDto updatedEpisodeDto = episodeInputDtoAssembler.toInputDto(updatedEpisode);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(updatedEpisodeDto))
                    .pathParam("seriesId", VALID_SERIES_ID)
                    .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                    .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{episodeNumber}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(VALID_EPISODE_ID_1.intValue()))
                    .body("name", equalTo(updatedEpisode.getName()))
                    .body("number", equalTo(updatedEpisode.getNumber()))
                    .body("synopsis", equalTo(updatedEpisode.getSynopsis()))
                    .body("duration", equalTo(updatedEpisode.getDuration()));
    }

    @Test
    void when_PuttingValidEpisodeAtNonExistingSeriesId_Expect_Status404TitleAndDetail() {
        Episode updatedEpisode = createEpisode(null, 10, "Updated Name", "Updated Synopsis", 50);
        EpisodeInputDto updatedEpisodeDto = episodeInputDtoAssembler.toInputDto(updatedEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(updatedEpisodeDto))
                .pathParam("seriesId", NON_EXISTING_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_ID)));
    }

    @Test
    void when_PuttingValidEpisodeAtInvalidSeriesId_Expect_Status400TitleAndDetail() {
        Episode updatedEpisode = createEpisode(null, 10, "Updated Name", "Updated Synopsis", 50);
        EpisodeInputDto updatedEpisodeDto = episodeInputDtoAssembler.toInputDto(updatedEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(updatedEpisodeDto))
                .pathParam("seriesId", INVALID_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail("seriesId", INVALID_ID, VALID_SERIES_ID)));
    }

    @Test
    void when_PuttingValidEpisodeAtNonExistingSeasonNumber_Expect_Status404TitleAndDetail() {
        Episode updatedEpisode = createEpisode(null, 10, "Updated Name", "Updated Synopsis", 50);
        EpisodeInputDto updatedEpisodeDto = episodeInputDtoAssembler.toInputDto(updatedEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(updatedEpisodeDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", NON_EXISTING_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_NUMBER, VALID_SERIES_ID)));
    }

    @Test
    void when_PuttingValidEpisodeAtInvalidSeasonNumber_Expect_Status400TitleAndDetail() {
        Episode updatedEpisode = createEpisode(null, 10, "Updated Name", "Updated Synopsis", 50);
        EpisodeInputDto updatedEpisodeDto = episodeInputDtoAssembler.toInputDto(updatedEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(updatedEpisodeDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", INVALID_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail("seasonNumber", INVALID_NUMBER, VALID_EPISODE_NUMBER_1)));
    }

    @Test
    void when_PuttingValidEpisodeAtNonExistingEpisodeNumber_Expect_Status404TitleAndDetail() {
        Episode updatedEpisode = createEpisode(null, 10, "Updated Name", "Updated Synopsis", 50);
        EpisodeInputDto updatedEpisodeDto = episodeInputDtoAssembler.toInputDto(updatedEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(updatedEpisodeDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", NON_EXISTING_NUMBER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail(VALID_SEASON_WITH_EPISODE_NUMBER, NON_EXISTING_NUMBER, VALID_SERIES_ID)));
    }

    @Test
    void when_PuttingValidEpisodeAtInvalidEpisodeNumber_Expect_Status400TitleAndDetail() {
        Episode updatedEpisode = createEpisode(null, 10, "Updated Name", "Updated Synopsis", 50);
        EpisodeInputDto updatedEpisodeDto = episodeInputDtoAssembler.toInputDto(updatedEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(updatedEpisodeDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", INVALID_NUMBER)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail("episodeNumber", INVALID_NUMBER, VALID_EPISODE_NUMBER_1)));
    }

    @Test
    void when_PuttingInvalidEpisodeWithoutName_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 1, null, "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("name"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PuttingInvalidEpisodeWithoutNumber_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, null, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("number"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PuttingInvalidEpisodeWithoutSynopsis_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 1, "VALID", null, 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("synopsis"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PuttingInvalidEpisodeWithoutDuration_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 1, "VALID", "EXAMPLE", null);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("duration"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PuttingInvalidEpisodeWithNumber0_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 0, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("number"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PuttingInvalidEpisodeWithNumberNegative_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, -1, "VALID", "EXAMPLE", 45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("number"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PuttingInvalidEpisodeWithDuration0_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 10, "VALID", "EXAMPLE", 0);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("duration"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PuttingInvalidEpisodeWithDurationNegative_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, 1, "VALID", "EXAMPLE", -45);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITHOUT_EPISODE_ID)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_DATA.getTitle()))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects[0].name", equalTo("duration"))
                .body("objects[0].userMessage", equalTo(USER_MESSAGE_POSITIVE));
    }

    @Test
    void when_PuttingInvalidEpisodeWithDuplicatedNumber_Expect_Status400TitleDetailAndObjectName() {
        Episode invalidEpisode = createEpisode(null, VALID_EPISODE_NUMBER_2, "VALID", "EXAMPLE", 100);
        EpisodeInputDto episodeInputDto = episodeInputDtoAssembler.toInputDto(invalidEpisode);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(episodeInputDto))
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.BUSINESS_ERROR.getTitle()))
                .body("detail", equalTo(getDuplicatedNumberEpisodeMsg(
                        VALID_SEASON_WITH_EPISODE_NUMBER, VALID_EPISODE_NUMBER_2, VALID_SERIES_ID)));
    }

    @Test
    void when_DeletingByValidEpisodeNumber_Expect_Status204() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .when()
                    .delete("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void when_DeletingEpisodeWithNonExistingSeries_Expect_Status404() {
        RestAssured
                .given()
                .pathParam("seriesId", NON_EXISTING_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .when()
                .delete("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_ID)));
    }

    @Test
    void when_DeletingEpisodeWithNonExistingSeason_Expect_Status404() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", NON_EXISTING_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .when()
                .delete("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_NUMBER, VALID_SERIES_ID)));
    }

    @Test
    void when_DeletingEpisodeWithNonExistingEpisodeNumber_Expect_Status404() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", NON_EXISTING_NUMBER)
                .when()
                .delete("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(ApiErrorType.RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail(VALID_SEASON_WITH_EPISODE_NUMBER, NON_EXISTING_NUMBER, VALID_SERIES_ID)));
    }

    @Test
    void when_DeletingEpisodeWithInvalidSeriesId_Expect_Status400() {
        RestAssured
                .given()
                .pathParam("seriesId", INVALID_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .when()
                .delete("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(
                        getInvalidUrlParameterDetail("seriesId", INVALID_ID, VALID_SERIES_ID)));
    }

    @Test
    void when_DeletingEpisodeWithInvalidSeasonNumber_Expect_Status400() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", INVALID_NUMBER)
                .pathParam("episodeNumber", VALID_EPISODE_NUMBER_1)
                .when()
                .delete("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(
                        getInvalidUrlParameterDetail("seasonNumber", INVALID_NUMBER, VALID_SEASON_WITH_EPISODE_NUMBER)));
    }

    @Test
    void when_DeletingEpisodeWithInvalidEpisodeNumber_Expect_Status400() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID)
                .pathParam("seasonNumber", VALID_SEASON_WITH_EPISODE_NUMBER)
                .pathParam("episodeNumber", INVALID_NUMBER)
                .when()
                .delete("/{episodeNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(ApiErrorType.INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(
                        getInvalidUrlParameterDetail("episodeNumber", INVALID_NUMBER, VALID_EPISODE_NUMBER_2)));
    }


    private void setUpData() {
        setUpSeries();

        setUpSeasons();

        setUpEpisode();

        seasonEpisodeQuantity = episodeRepository.findBySeason(seasonExampleWithEpisode).size();
        episodeQuantity = episodeRepository.findAll().size();
    }

    private void setUpSeries() {
        seriesExample = createSeries(VALID_SERIES_ID, "Series example2", "sample2", 10,
                Year.of(1900), Rating.PG_13, Set.of());

        seriesRepository.save(seriesExample);
    }

    private void setUpSeasons() {

        seasonExampleWithEpisode = createSeason(VALID_SEASON_WITH_EPISODE_ID, VALID_SEASON_WITH_EPISODE_NUMBER,
                "EXAMPLE");
        seasonExampleWithoutEpisode = createSeason(VALID_SEASON_WITHOUT_EPISODE_ID, VALID_SEASON_WITHOUT_EPISODE_NUMBER,
                "EXAMPLE");

        attachSeason(seasonExampleWithEpisode, seriesExample);
        attachSeason(seasonExampleWithoutEpisode, seriesExample);
    }

    private void setUpEpisode() {
        episodeExample1 = createEpisode(VALID_EPISODE_ID_1, VALID_EPISODE_NUMBER_1, "EPISODE", "EXAMPLE",
                60);

        episodeExample2 = createEpisode(VALID_EPISODE_ID_2, VALID_EPISODE_NUMBER_2, "EPISODE", "EXAMPLE",
                        60);

        attachEpisode(episodeExample1, seasonExampleWithEpisode);
        attachEpisode(episodeExample2, seasonExampleWithEpisode);
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

    private String getResourceNotFoundDetail(String resource, Long id) {
        return "%s with id %d doesn't exist".formatted(resource, id);
    }

    private String getResourceNotFoundDetail(Integer seasonNumber, Long seriesId) {
        return "The season %d from the series of id %d doesn't exists".formatted(seasonNumber, seriesId);
    }

    private String getResourceNotFoundDetail(Integer seasonNumber, Integer episodeNumber, Long seriesId) {
        return "The episode %d in season %d from the series of id %d doesnt exists"
                .formatted(episodeNumber, seasonNumber, seriesId);
    }

    private String getInvalidUrlParameterDetail(String urlParameter, String value, Object expectedValueType) {
        return String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                urlParameter, value.getClass().getSimpleName(), expectedValueType.getClass().getSimpleName());
    }

    private String getInvalidDataErrorDetail() {
        return "One or more fields are not valid. Correct it.";
    }

    private String getInvalidPropertyDetail(String property) {
        return String.format("Property '%s' is not valid, remove it", property);
    }

    private String getDuplicatedNumberEpisodeMsg(Integer seasonNumber, Integer episodeNumber, Long seriesId) {
        return "The episode %d in season %d from the series of id %d already exists"
                .formatted(episodeNumber, seasonNumber, seriesId);
    }
}
