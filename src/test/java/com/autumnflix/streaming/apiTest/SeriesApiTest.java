package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.api.assembler.series.SeriesInputDtoAssembler;
import com.autumnflix.streaming.api.exceptionHandler.ApiErrorType;
import com.autumnflix.streaming.api.model.series.SeriesInputDto;
import com.autumnflix.streaming.domain.model.Genre;
import com.autumnflix.streaming.domain.model.Rating;
import com.autumnflix.streaming.domain.model.Season;
import com.autumnflix.streaming.domain.model.Series;
import com.autumnflix.streaming.domain.repository.GenreRepository;
import com.autumnflix.streaming.domain.repository.SeasonRepository;
import com.autumnflix.streaming.domain.repository.SeriesRepository;
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
import java.util.List;
import java.util.Set;

import static com.autumnflix.streaming.api.exceptionHandler.ApiErrorType.*;
import static com.autumnflix.streaming.domain.model.EntertainmentWorkType.SERIES;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class SeriesApiTest {

    private static final Long VALID_SERIES_ID_WITHOUT_GENRE_AND_SEASON = 1L;
    private static final Long VALID_SERIES_ID_WITH_GENRE_AND_SEASON = 2L;

    private static final Long NON_EXISTING_SERIES_ID = Long.MAX_VALUE;
    private static final String INVALID_SERIES_ID = "INVALID INVALID";
    private static final String INVALID_PARENTAL_RATING = "PG_30";
    private static final String INVALID_PROPERTY_FIELD = "invalidProperty";

    private static final String USER_MESSAGE_NOT_NULL = "não deve ser nulo";
    private static final String USER_MESSAGE_NOT_BLANK = "não deve estar em branco";
    private static final String USER_MESSAGE_INVALID_RELEASE_YEAR =
            "The release year of the movie must be on or after 1895.";

    private Genre genre;

    private Series seriesWithoutGenresAndSeason;
    private Series seriesWithGenresAndSeason;

    private String invalidSeriesJsonWithNonExistingRating;
    private String invalidSeriesJsonWithNonExistingProperty;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private SeriesInputDtoAssembler seriesInputDtoAssembler;

    private int seriesQuantity;

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;



    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/series";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingAllSeries_Expect_Status200QuantityAndResumedProperties() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get()
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("", hasSize(seriesQuantity))
                    .body("id", hasItems(
                            seriesWithoutGenresAndSeason.getId().intValue(), seriesWithGenresAndSeason.getId().intValue()))
                    .body("name", hasItems(seriesWithoutGenresAndSeason.getName(), seriesWithGenresAndSeason.getName()))
                    .body("synopsis", hasItems(seriesWithoutGenresAndSeason.getSynopsis(), seriesWithGenresAndSeason.getSynopsis()))
                    .body("parentalRating", hasItems(seriesWithoutGenresAndSeason.getParentalRating().toString(),
                            seriesWithGenresAndSeason.getParentalRating().toString()))
                    .body("numOfSeasons", hasItems(seriesWithoutGenresAndSeason.numOfSeasons(), seriesWithGenresAndSeason.numOfSeasons()));
    }

    @Test
    void when_GettingSeriesWithGenresAndSeasonBySeriesId_Expect_Status200AndAllProperties() {
        RestAssured
                .given()
                    .pathParam("seriesId", seriesWithGenresAndSeason.getId())
                    .accept(ContentType.JSON)
                .when()
                    .get("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(seriesWithGenresAndSeason.getId().intValue()))
                    .body("name", equalTo(seriesWithGenresAndSeason.getName()))
                    .body("synopsis", equalTo(seriesWithGenresAndSeason.getSynopsis()))
                    .body("relevance", equalTo(seriesWithGenresAndSeason.getRelevance()))
                    .body("releaseYear", equalTo(seriesWithGenresAndSeason.getReleaseYear().toString()))
                    .body("parentalRating", equalTo(seriesWithGenresAndSeason.getParentalRating().toString()))
                    .body("genres", hasItems(
                            seriesWithGenresAndSeason.getGenres().stream()
                                    .map(item -> hasEntry("name", item.getName()))
                                    .toArray(Matcher[]::new)
                    ))
                    .body("numOfSeasons", equalTo(seriesWithGenresAndSeason.numOfSeasons()));
    }

    @Test
    void when_GettingSeriesWithoutGenresAndSeasonBySeriesId_Expect_Status200AndAllProperties() {
        RestAssured
                .given()
                .pathParam("seriesId", seriesWithoutGenresAndSeason.getId())
                .accept(ContentType.JSON)
                .when()
                .get("/{seriesId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(seriesWithoutGenresAndSeason.getId().intValue()))
                .body("name", equalTo(seriesWithoutGenresAndSeason.getName()))
                .body("synopsis", equalTo(seriesWithoutGenresAndSeason.getSynopsis()))
                .body("relevance", equalTo(seriesWithoutGenresAndSeason.getRelevance()))
                .body("releaseYear", equalTo(seriesWithoutGenresAndSeason.getReleaseYear().toString()))
                .body("parentalRating", equalTo(seriesWithoutGenresAndSeason.getParentalRating().toString()))
                .body("genres", hasItems(
                        seriesWithoutGenresAndSeason.getGenres().stream()
                                .map(item -> hasEntry("name", item.getName()))
                                .toArray(Matcher[]::new)
                ))
                .body("numOfSeasons", equalTo(seriesWithoutGenresAndSeason.numOfSeasons()));
    }

    @Test
    void when_GettingByNonExistingSeriesId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("seriesId", NON_EXISTING_SERIES_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_SERIES_ID)));
    }

    @Test
    void when_GettingByInvalidSeriesId_Expect_Status404TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("seriesId", INVALID_SERIES_ID)
                    .accept(ContentType.JSON)
                .when()
                    .get("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                .   body("detail", equalTo(getInvalidUrlParameterDetail(
                        "seriesId", INVALID_SERIES_ID, Long.class.getSimpleName()))
                );
    }

    @Test
    void when_PostingValidSeries_Expect_Status201Properties() {
        Series validSeries = createSeries(null,"valid", "valid series", 10, Year.of(2003),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seriesInputDto))
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", equalTo(seriesQuantity + 1))
                    .body("name", equalTo(validSeries.getName()))
                    .body("synopsis", equalTo(validSeries.getSynopsis()))
                    .body("relevance", equalTo(validSeries.getRelevance()))
                    .body("releaseYear", equalTo(validSeries.getReleaseYear().toString()))
                    .body("parentalRating", equalTo(validSeries.getParentalRating().toString()))
                    .body("genres", hasItems())
                    .body("numOfSeasons", equalTo(validSeries.numOfSeasons()));
    }

    @Test
    void when_PostingValidSeriesWithReleaseYear1895_Expect_Status201Properties() {
        Series validSeries = createSeries(null,"valid", "valid series", 10, Year.of(1895),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                .body(JsonReader.readObjectAsJson(seriesInputDto))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", equalTo(seriesQuantity + 1))
                .body("name", equalTo(validSeries.getName()))
                .body("synopsis", equalTo(validSeries.getSynopsis()))
                .body("relevance", equalTo(validSeries.getRelevance()))
                .body("releaseYear", equalTo(validSeries.getReleaseYear().toString()))
                .body("parentalRating", equalTo(validSeries.getParentalRating().toString()))
                .body("genres", hasItems())
                .body("numOfSeasons", equalTo(validSeries.numOfSeasons()));
    }

    @Test
    void when_PostingInvalidSeriesWithoutName_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null, null, "valid series", 10, Year.of(2000),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seriesInputDto))
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
    void when_PostingInvalidSeriesWithoutSynopsis_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null,"valid", null, 10, Year.of(2000),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seriesInputDto))
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
    void when_PostingInvalidSeriesWithoutRelevance_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null,"valid", "valid series", null, Year.of(2000),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seriesInputDto))
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
    void when_PostingInvalidSeriesWithoutReleaseYear_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null,"valid", "valid series", 10, null,
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seriesInputDto))
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
    void when_PostingInvalidSeriesWithoutRating_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null,"valid", "valid series", 10, Year.of(2000),
                null, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seriesInputDto))
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
    void when_PostingInvalidSeriesWithReleaseYearBefore1895_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null,"valid", "valid series", 10, Year.of(1894),
                Rating.R, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .body(JsonReader.readObjectAsJson(seriesInputDto))
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
    void when_PostingInvalidSeriesWithNonExistingRating_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .body(invalidSeriesJsonWithNonExistingRating)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .post()
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(MESSAGE_NOT_READABLE.getTitle()))
                    .body("detail", equalTo(getMessageNotReadableDetail(
                            "parentalRating", INVALID_PARENTAL_RATING, "Rating")));
    }

    @Test
    void when_PostingInvalidSeriesWithNonExistingProperty_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .body(invalidSeriesJsonWithNonExistingProperty)
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
    void when_PuttingValidSeriesAtValidSeriesIdWithoutGenreAndSeason_Expect_Status200AndProperties() {
        Series existingSeries = seriesRepository.findById(VALID_SERIES_ID_WITHOUT_GENRE_AND_SEASON).get();

        Series validSeries = createSeries(null, "valid", "valid series", 10, Year.of(2003),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITHOUT_GENRE_AND_SEASON)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .body("id", equalTo(VALID_SERIES_ID_WITHOUT_GENRE_AND_SEASON.intValue()))
                    .body("name", equalTo(validSeries.getName()))
                    .body("synopsis", equalTo(validSeries.getSynopsis()))
                    .body("relevance", equalTo(validSeries.getRelevance()))
                    .body("releaseYear", equalTo(validSeries.getReleaseYear().toString()))
                    .body("parentalRating", equalTo(validSeries.getParentalRating().toString()))
                    .body("genres", hasItems(
                            existingSeries.getGenres().stream()
                                    .map(item -> hasEntry("name", item.getName()))
                                    .toArray(Matcher[]::new)
                    ))
                    .body("numOfSeasons", equalTo(existingSeries.numOfSeasons()));
    }

    @Test
    void when_PuttingValidSeriesAtValidSeriesIdWithGenreAndSeason_Expect_Status200AndProperties() {
        Series existingSeries = seriesRepository.findById(VALID_SERIES_ID_WITH_GENRE_AND_SEASON).get();

        Series validSeries = createSeries(null, "valid", "valid series", 10, Year.of(2003),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .body("id", equalTo(VALID_SERIES_ID_WITH_GENRE_AND_SEASON.intValue()))
                    .body("name", equalTo(validSeries.getName()))
                    .body("synopsis", equalTo(validSeries.getSynopsis()))
                    .body("relevance", equalTo(validSeries.getRelevance()))
                    .body("releaseYear", equalTo(validSeries.getReleaseYear().toString()))
                    .body("parentalRating", equalTo(validSeries.getParentalRating().toString()))
                    .body("genres", hasItems(
                            existingSeries.getGenres().stream()
                                    .map(item -> hasEntry("name", item.getName()))
                                    .toArray(Matcher[]::new)
                    ))
                    .body("numOfSeasons", equalTo(existingSeries.numOfSeasons()));
    }

    @Test
    void when_PuttingValidSeriesWithReleaseYear1895AtValidSeriesId_Expect_Status200AndProperties() {
        Series existingSeries = seriesRepository.findById(VALID_SERIES_ID_WITH_GENRE_AND_SEASON).get();

        Series validSeries = createSeries(null, "valid", "valid series", 10, Year.of(1895),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                .body(seriesInputDto)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{seriesId}")
                .then()
                .body("id", equalTo(VALID_SERIES_ID_WITH_GENRE_AND_SEASON.intValue()))
                .body("name", equalTo(validSeries.getName()))
                .body("synopsis", equalTo(validSeries.getSynopsis()))
                .body("relevance", equalTo(validSeries.getRelevance()))
                .body("releaseYear", equalTo(validSeries.getReleaseYear().toString()))
                .body("parentalRating", equalTo(validSeries.getParentalRating().toString()))
                .body("genres", hasItems(
                        existingSeries.getGenres().stream()
                                .map(item -> hasEntry("name", item.getName()))
                                .toArray(Matcher[]::new)
                ))
                .body("numOfSeasons", equalTo(existingSeries.numOfSeasons()));
    }

    @Test
    void when_PuttingValidSeriesAtNonExistingSeriesId_Expect_Status200AndProperties() {
        Series validSeries = createSeries(null, "valid", "valid series", 10, Year.of(2003),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", NON_EXISTING_SERIES_ID)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                    .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_SERIES_ID)));
    }

    @Test
    void when_PuttingValidSeriesAtInvalidSeriesId_Expect_Status200AndProperties() {
        Series validSeries = createSeries(null, "valid", "valid series", 10, Year.of(2003),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", INVALID_SERIES_ID)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                    .body("detail", equalTo(getInvalidUrlParameterDetail(
                            "seriesId", INVALID_SERIES_ID, seriesWithGenresAndSeason.getId().getClass().getSimpleName()))
                    );
    }

    @Test
    void when_PuttingInvalidSeriesWithoutNameAtValidSeriesId_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null, null, "valid series", 10, Year.of(2000),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("name"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PuttingInvalidSeriesWithoutSynopsisAtValidSeriesId_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null, "valid", null, 10, Year.of(2000),
                Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("synopsis"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_BLANK));
    }

    @Test
    void when_PuttingInvalidSeriesWithoutRelevanceAtValidSeriesId_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null, "valid", "valid series", null,
                Year.of(2000), Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("relevance"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PuttingInvalidSeriesWithoutReleaseYearAtValidSeriesId_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null, "valid", "valid series", 10,
                null, Rating.G, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("releaseYear"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PuttingInvalidSeriesWithoutRatingAtValidSeriesId_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null, "valid", "valid series", 10,
                Year.of(2000), null, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("parentalRating"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_NOT_NULL));
    }

    @Test
    void when_PuttingInvalidSeriesWithReleaseYearBefore1895AtValidSeriesId_Expect_Status400TitleDetailAndObjectName() {
        Series validSeries = createSeries(null, "valid", "valid series", 10,
                Year.of(1894), Rating.PG_13, Set.of());
        SeriesInputDto seriesInputDto = seriesInputDtoAssembler.toInputDto(validSeries);

        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                    .body(seriesInputDto)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_DATA.getTitle()))
                    .body("detail", equalTo(getInvalidDataErrorDetail()))
                    .body("objects[0].name", equalTo("releaseYear"))
                    .body("objects[0].userMessage", equalTo(USER_MESSAGE_INVALID_RELEASE_YEAR));
    }

    @Test
    void when_PuttingInvalidSeriesWithNonExistingRatingAtValidSeriesId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                    .body(invalidSeriesJsonWithNonExistingRating)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(MESSAGE_NOT_READABLE.getTitle()))
                    .body("detail", equalTo(getMessageNotReadableDetail(
                            "parentalRating", INVALID_PARENTAL_RATING, "Rating")));
    }

    @Test
    void when_PuttingInvalidSeriesWithNonExistingPropertyAtValidSeriesId_Expect_Status400TitleAndDetail() {
        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                    .body(invalidSeriesJsonWithNonExistingProperty)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                .when()
                    .put("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("title", equalTo(INVALID_PROPERTY.getTitle()))
                    .body("detail", equalTo(getInvalidPropertyDetail(INVALID_PROPERTY_FIELD)));
    }

    @Test
    void when_DeletingByValidSeriesId_Expect_Status204() {
        RestAssured
                .given()
                    .pathParam("seriesId", VALID_SERIES_ID_WITHOUT_GENRE_AND_SEASON)
                .when()
                    .delete("/{seriesId}")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void when_DeletingByValidSeriesWithSeasonsId_Expect_Status409() {
        RestAssured
                .given()
                .pathParam("seriesId", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)
                .when()
                .delete("/{seriesId}")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("title", equalTo(ENTITY_BEING_USED.getTitle()))
                .body("detail", equalTo(getEntityBeingUsedDetail(
                        "Series", VALID_SERIES_ID_WITH_GENRE_AND_SEASON)));
    }

    @Test
    void when_DeletingByNonExistingSeriesId_Expect_Status404() {
        RestAssured
                .given()
                .pathParam("seriesId", NON_EXISTING_SERIES_ID)
                .when()
                .delete("/{seriesId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND.getTitle()))
                .body("detail", equalTo(getResourceNotFoundDetail("Series", NON_EXISTING_SERIES_ID)));
    }

    @Test
    void when_DeletingByInvalidSeriesId_Expect_Status404() {
        RestAssured
                .given()
                .pathParam("seriesId", INVALID_SERIES_ID)
                .when()
                .delete("/{seriesId}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER.getTitle()))
                .body("detail", equalTo(getInvalidUrlParameterDetail(
                        "seriesId",
                        INVALID_SERIES_ID,
                        VALID_SERIES_ID_WITHOUT_GENRE_AND_SEASON.getClass().getSimpleName()))
                );
    }
    private void setUpData() {
        setUpGenre();

        setUpSeries();

        setUpJsons();

        seriesQuantity = seriesRepository.findAll().size();
    }

    private void setUpGenre() {
        genre = createGenre("Terror");

        genreRepository.save(genre);
    }

    private void setUpSeries() {
        seriesWithoutGenresAndSeason = createSeries(VALID_SERIES_ID_WITHOUT_GENRE_AND_SEASON, "seriesWithoutGenres", "NO GENRES", 0,
                Year.of(2000), Rating.NC_17, Set.of());

        seriesWithGenresAndSeason = createSeries(VALID_SERIES_ID_WITH_GENRE_AND_SEASON, "seriesWithGenres", "With GENRES", 10,
                Year.of(2010), Rating.PG, Set.of(genre));

        seriesRepository.saveAll(List.of(seriesWithoutGenresAndSeason, seriesWithGenresAndSeason));

        attachSeason(List.of(seriesWithGenresAndSeason));
    }

    private void setUpJsons() {
        invalidSeriesJsonWithNonExistingRating =
                JsonReader.readJsonAsString(
                        "src/test/resources/JSON/invalid/series/invalidSeriesWithNonExistingParentalRating.JSON"
                );

        invalidSeriesJsonWithNonExistingProperty =
                JsonReader.readJsonAsString(
                        "src/test/resources/JSON/invalid/series/invalidSeriesWithNonExistingProperty.JSON"
                );
    }


    private Genre createGenre(String name) {
        Genre genre = new Genre();

        genre.setName(name);

        return genre;
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

    private void attachSeason(List<Series> series) {
        series.forEach(item -> {
            Season season = new Season();

            season.setNumber(1);
            season.setSynopsis("SEASON TEST");
            season.setSeries(item);

            item.setSeasons(Set.of(season));

            seasonRepository.save(season);
        });
    }

    private String getResourceNotFoundDetail(String resource, Long id) {
        return "%s with id %d doesn't exist".formatted(resource, id);
    }

    private String getInvalidUrlParameterDetail(String urlParameter, String value, String expectedValueType) {
        return String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                urlParameter, value.getClass().getSimpleName(), expectedValueType);
    }

    private String getInvalidDataErrorDetail() {
        return "One or more fields are not valid. Correct it.";
    }

    private String getMessageNotReadableDetail(String property, String value, String expectedValueType) {
        return "Property '%s' has a value of '%s' , replace it with a compatible value of type '%s'"
                .formatted(property, value, expectedValueType);
    }

    private String getInvalidPropertyDetail(String property) {
        return String.format("Property '%s' is not valid, remove it", property);
    }

    private String getEntityBeingUsedDetail(String entity, Long id) {
        return "%s with id %d is being used and can't be removed".formatted(entity, id);
    }
}
