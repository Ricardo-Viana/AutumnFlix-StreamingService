package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.domain.model.*;
import com.autumnflix.streaming.domain.repository.EntertainmentWorkRepository;
import com.autumnflix.streaming.domain.repository.GenreRepository;
import com.autumnflix.streaming.domain.repository.UserRepository;
import com.autumnflix.streaming.util.DatabaseCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static com.autumnflix.streaming.api.exceptionHandler.ApiErrorType.*;
import static com.autumnflix.streaming.api.exceptionHandler.ApiErrorType.BUSINESS_ERROR;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class UserFavoritesEntertainWorkApiTest {

    private static final int VALID_ID = 1;

    private static final int VALID_ID_2 = 2;

    private static final int NON_EXISTING_ID = Integer.MAX_VALUE;

    private static final String INVALID_ID = "a";

    private static final String RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE = RESOURCE_NOT_FOUND.getTitle();

    private static final String INVALID_DATA_ERROR_TYPE_TITLE = INVALID_DATA.getTitle();

    private static final String INVALID_URL_PARAMETER_ERROR_TYPE_TITLE = INVALID_URL_PARAMETER.getTitle();

    private static final String BUSSINESS_ERROR_TYPE_TITLE = BUSINESS_ERROR.getTitle();

    private static final String ENTITY_BEING_USED_ERROR_TYPE_TITLE = ENTITY_BEING_USED.getTitle();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private EntertainmentWorkRepository entertainmentWorkRepository;

    private int entertainWorkQuantity;

    private Genre terror;
    private Genre comedy;
    private Genre drama;
    private EntertainmentWork fnaf;
    private EntertainmentWork tedLasso;

    private User user;

    private User savedUser;

    private IdentificationDocument identificationDocument;

    private PaymentMethod paymentMethod;

    private Credit credit;

    private Plan plan;

    public UserFavoritesEntertainWorkApiTest(){

    }

    @BeforeEach
    void setUp(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/users/{userId}/favorite";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingAllByUserId_Expect_Status200QuantityAndProperties(){
        RestAssured
                .given()
                .pathParam("userId", VALID_ID)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(OK.value())
                .body("", hasSize(entertainWorkQuantity))
                .body("id", hasItems(VALID_ID))
                .body("name", hasItems(fnaf.getName()))
                .body("synopsis", hasItems(fnaf.getSynopsis()))
                .body("relevance", hasItems(fnaf.getRelevance()))
                .body("releaseYear", hasItems(fnaf.getReleaseYear().toString()))
                .body("parentalRating", hasItems(fnaf.getParentalRating().toString()))
                .body("type", hasItems(fnaf.getType().toString()))
                .body("genres[0].id", hasItems(fnaf.getGenres().stream().map(genre -> genre.getId().intValue()).toArray()))
                .body("genres[0].name", hasItems(fnaf.getGenres().stream().map(Genre::getName).toArray()));
    }

    @Test
    void when_GettingByNonExistingUserId_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", NON_EXISTING_ID)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getResourceNotFoundDetail("User", NON_EXISTING_ID)));
    }

    @Test
    void when_GettingByInvalidUserId_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", INVALID_ID)
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidUrlParameterDetail("userId")));
    }

    @Test
    void when_PuttingValidEntertainWork_Expect_Status204(){
        RestAssured
                .given()
                .pathParam("userId", VALID_ID)
                .pathParam("entertainmentWorkId", VALID_ID_2)
                .when()
                .put("/{entertainmentWorkId}")
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    void when_PuttingNonExistingEntertainWork_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_ID)
                .pathParam("entertainmentWorkId", NON_EXISTING_ID)
                .when()
                .put("/{entertainmentWorkId}")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getResourceNotFoundDetail("Entertainment Work", NON_EXISTING_ID)));
    }

    @Test
    void when_PuttingInvalidEntertainWork_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_ID)
                .pathParam("entertainmentWorkId", INVALID_ID)
                .when()
                .put("/{entertainmentWorkId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidUrlParameterDetail("entertainmentWorkId")));
    }


    @Test
    void when_PuttingDuplicatedFavoriteEntertainWork_Expect_Status409TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_ID)
                .pathParam("entertainmentWorkId", VALID_ID)
                .when()
                .put("/{entertainmentWorkId}")
                .then()
                .statusCode(CONFLICT.value())
                .body("title", equalTo(ENTITY_BEING_USED_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getDuplicatedFavoriteErrorDetail()));
    }

    @Test
    void when_DeletingWithValidEntertainWork_Expect_Status204(){
        RestAssured
                .given()
                .pathParam("userId", VALID_ID)
                .pathParam("entertainmentWorkId", VALID_ID)
                .when()
                .delete("/{entertainmentWorkId}")
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    void when_DeletingWithNonExistingEntertainWork_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_ID)
                .pathParam("entertainmentWorkId", NON_EXISTING_ID)
                .when()
                .delete("/{entertainmentWorkId}")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getResourceNotFoundDetail("Entertainment Work", NON_EXISTING_ID)));
    }

    @Test
    void when_DeletingWithInvalidEntertainWork_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_ID)
                .pathParam("entertainmentWorkId", INVALID_ID)
                .when()
                .delete("/{entertainmentWorkId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidUrlParameterDetail("entertainmentWorkId")));
    }

    private void setUpData(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        identificationDocument = new IdentificationDocument();
        identificationDocument.setType(IdentificationDocumentType.SSN);
        identificationDocument.setValue("001-01-0001");

        paymentMethod = new PaymentMethod();
        paymentMethod.setCardType(CardType.VISA);
        paymentMethod.setNumber("4000620000000007"); // Using just for testing purposes provided by Adyen
        paymentMethod.setExpiringDate(LocalDate.parse("10/01/3000", formatter));
        paymentMethod.setSecurityCode("000");
        paymentMethod.setOwnerName("John Smith");

        plan = new Plan();
        plan.setType(PlanType.MEDIUM);
        plan.setValue(new BigDecimal(5.00));
        plan.setNumCredits(3);

        credit = new Credit();
        credit.setQuantity(plan.getNumCredits());
        LocalDateTime ldt  = LocalDateTime.now();
        ZoneOffset offset = ZoneOffset.UTC;
        credit.setDate(ldt.atOffset(offset));

        savedUser = new User();
        savedUser.setFullName("John Smith");
        savedUser.setDob(LocalDate.parse("10/01/1990", formatter));
        savedUser.setEmail("johnsmith@gmail.com");
        savedUser.setIdentificationDocument(identificationDocument);
        savedUser.setPaymentMethod(paymentMethod);
        savedUser.setPassword("johnsmithpassword");
        savedUser.setRole(Role.USER);
        savedUser.setPlan(plan);
        savedUser.setCredit(credit);

        terror = createGenre("Terror");
        comedy = createGenre("Comedy");
        drama = createGenre("Drama");
        genreRepository.saveAll(List.of(terror, comedy, drama));


        fnaf = createEntertainmentWork("FNAF", "Crazy bear", 8, Year.of(2023),
                Rating.PG_13, EntertainmentWorkType.MOVIE, Set.of(terror, drama));
        tedLasso = createEntertainmentWork("Ted Lasso", "Football coach",
                10, Year.of(2020), Rating.G, EntertainmentWorkType.SERIES, Set.of(comedy, drama));
        entertainmentWorkRepository.saveAll(List.of(fnaf, tedLasso));

        savedUser.addFavoritesEntertainWorks(entertainmentWorkRepository.findById(1L).orElseThrow());

        userRepository.save(savedUser);

        entertainWorkQuantity = savedUser.getFavoriteEntertainWorks().size();
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


    private String getResourceNotFoundDetail(String entityName, int id) {
        return String.format("%s with id %d doesn't exist",entityName, id);
    }

    private String getInvalidUrlParameterDetail(String keyName) {
        return String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                keyName, INVALID_ID.getClass().getSimpleName(), "Long");
    }

    private String getDuplicatedFavoriteErrorDetail(){
        return "EntertainmentWork is already favorite";
    }

}
