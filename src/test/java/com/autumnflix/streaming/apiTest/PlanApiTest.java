
package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.api.exceptionHandler.ApiErrorType;
import com.autumnflix.streaming.domain.exception.PlanNotFoundException;
import com.autumnflix.streaming.domain.model.Plan;
import com.autumnflix.streaming.domain.model.PlanType;
import com.autumnflix.streaming.domain.repository.PlanRepository;
import com.autumnflix.streaming.util.DatabaseCleaner;
import com.autumnflix.streaming.util.JsonReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.autumnflix.streaming.api.exceptionHandler.ApiErrorType.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class PlanApiTest {
    private static final int VALID_PLAN_ID = 1;

    private static final int NON_EXISTING_PLAN_ID = Integer.MAX_VALUE;

    private static final String INVALID_PLAN_ID = "a";

    private static final String MESSAGE_NOT_READABLE_ERROR_TYPE_TITLE = MESSAGE_NOT_READABLE.getTitle();
    private static final String RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE = RESOURCE_NOT_FOUND.getTitle();
    private static final String INVALID_DATA_ERROR_TYPE_TITLE = INVALID_DATA.getTitle();
    private static final String INVALID_URL_PARAMETER_ERROR_TYPE_TITLE = INVALID_URL_PARAMETER.getTitle();

    private static final String BUSINESS_ERROR_TYPE_TITLE = BUSINESS_ERROR.getTitle();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private PlanRepository planRepository;
    private int planQuantity;

    private Plan basic;

    private Plan medium;

    private Plan premium;

    private final String correctPlanJson;

    private final String correctPlanJsonWithoutDescription;

    private final String correctPlanJsonWithoutNumCredits;

    private final String invalidPlanJsonWithNonExistingType;

    private final String invalidPlanJsonWithInvalidNumCreditsValue;

    private final String invalidPlanJsonWithAttributeValueInvalid;

    private final String invalidPlanJsonWithNegativeNumCredits;

    private final String invalidPlanJsonWithNegativeValue;

    public PlanApiTest(){
        this.correctPlanJson = JsonReader
                .readJsonAsString("src/test/resources/JSON/valid/plan/validPlan.JSON");
        this.correctPlanJsonWithoutDescription = JsonReader
                .readJsonAsString("src/test/resources/JSON/valid/plan/validPlanWithoutDescription.JSON");
        this.correctPlanJsonWithoutNumCredits = JsonReader
                .readJsonAsString("src/test/resources/JSON/valid/plan/validPlanWithoutNumCredits.JSON");
        this.invalidPlanJsonWithNonExistingType = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/plan/invalidPlanWithNonExistingType.JSON");
        this.invalidPlanJsonWithInvalidNumCreditsValue = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/plan/invalidPlanWithInvalidNumCreditsValue.JSON");
        this.invalidPlanJsonWithAttributeValueInvalid = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/plan/invalidPlanWithAttributeValueInvalid.JSON");
        this.invalidPlanJsonWithNegativeNumCredits = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/plan/invalidPlanWithNegativeNumCredits.JSON");
        this.invalidPlanJsonWithNegativeValue = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/plan/InvalidPlanWithNegativeValue.JSON");
    }

    @BeforeEach
    void setUp(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/plans";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingAllPlans_Expect_Status200QuantityAndProperties(){
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get()
                .then()
                    .statusCode(OK.value())
                    .body("", hasSize(planQuantity))
                    .body("type", hasItems(basic.getType().toString(), medium.getType().toString(), premium.getType().toString()))
                    .body("numCredits", hasItems(basic.getNumCredits(), medium.getNumCredits(), premium.getNumCredits()))
                    .body("value", hasItems(basic.getValue().floatValue(), medium.getValue().floatValue(), premium.getValue().floatValue()));
    }

    @Test
    void when_GettingByValidPlanId_Expect_Status200AndProperties(){
        RestAssured
                .given()
                .pathParam("planId", basic.getId())
                .accept(ContentType.JSON)
                .when()
                .get("/{planId}")
                .then()
                .statusCode(OK.value())
                .body("type", equalTo(basic.getType().toString()))
                .body("numCredits", equalTo(basic.getNumCredits()))
                .body("value", equalTo(basic.getValue().floatValue()));
    }

    @Test
    void when_GettingByNonExistingPlanId_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("planId", NON_EXISTING_PLAN_ID)
                .accept(ContentType.JSON)
                .when()
                .get("/{planId}")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_PLAN_ID)));
    }

    @Test
    void when_GettingByInvalidPlanId_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("planId", INVALID_PLAN_ID)
                .accept(ContentType.JSON)
                .when()
                .get("/{planId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidUrlParameterDetail(INVALID_PLAN_ID)));
    }

    @Test
    void when_PostingValidPlan_Expect_Status201AndProperties(){
        RestAssured
                .given()
                .body(correctPlanJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(CREATED.value())
                .body("type", equalTo(basic.getType().toString()))
                .body("numCredits", equalTo(basic.getNumCredits()))
                .body("value", equalTo(basic.getValue().floatValue()))
                .body("description", equalTo(basic.getDescription()));
    }

    @Test
    void when_PostingValidPlanWithoutDescription_Expect_Status201AndProperties(){
        RestAssured
                .given()
                .body(correctPlanJsonWithoutDescription)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(CREATED.value())
                .body("type", equalTo(medium.getType().toString()))
                .body("numCredits", equalTo(medium.getNumCredits()))
                .body("value", equalTo(medium.getValue().floatValue()));
    }

    @Test
    void when_PostingValidPlanWithoutNumCredits_Expect_Status201AndProperties(){
        RestAssured
                .given()
                .body(correctPlanJsonWithoutNumCredits)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(CREATED.value())
                .body("type", equalTo(premium.getType().toString()))
                .body("value", equalTo(premium.getValue().floatValue()))
                .body("description", equalTo(premium.getDescription()));
    }
    @Test
    void when_PostingInvalidPlanWithNonExistingType_Expect_Status400TitleAndDetail() throws JSONException {
        RestAssured
                .given()
                .body(invalidPlanJsonWithNonExistingType)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(MESSAGE_NOT_READABLE_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getMessageNotReadableDetail("type",
                        invalidPlanJsonWithNonExistingType, PlanType.class.getSimpleName())));
    }

    @Test
    void when_PostingInvalidPlanWithInvalidNumCredits_Expect_Status400TitleAndDetail() throws JSONException {
        RestAssured
                .given()
                .body(invalidPlanJsonWithInvalidNumCreditsValue)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(MESSAGE_NOT_READABLE_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getMessageNotReadableDetail("numCredits",
                        invalidPlanJsonWithInvalidNumCreditsValue, Integer.class.getSimpleName())));
    }

    @Test
    void when_PostingInvalidPlanWithAttributeValueInvalid_Expect_Status400TitleAndDetail() throws JSONException {
        RestAssured
                .given()
                .body(invalidPlanJsonWithAttributeValueInvalid)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(MESSAGE_NOT_READABLE_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getMessageNotReadableDetail("value", invalidPlanJsonWithAttributeValueInvalid,
                        BigDecimal.class.getSimpleName())));
    }

    @Test
    void when_PostingInvalidPlanWithNegativeNumCredits_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidPlanJsonWithNegativeNumCredits)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getNegativeNumCreditsErrorDetail()));
    }

    @Test
    void when_PostingInvalidPlanWithNegativeValue_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidPlanJsonWithNegativeValue)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getNegativeValueErrorDetail()));
    }
    @Test
    void when_PuttingValidPlan_Expect_Status200AndProperties(){
        RestAssured
                .given()
                .pathParam("planId", VALID_PLAN_ID)
                .body(correctPlanJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{planId}")
                .then()
                .statusCode(OK.value())
                .body("id", equalTo(VALID_PLAN_ID))
                .body("type", equalTo(basic.getType().toString()))
                .body("numCredits", equalTo(basic.getNumCredits()))
                .body("value", equalTo(basic.getValue().floatValue()))
                .body("description", equalTo(basic.getDescription()));

    }

    @Test
    void when_PuttingValidPlanWithNonExistingPlanId_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("planId", NON_EXISTING_PLAN_ID)
                .body(correctPlanJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{planId}")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_PLAN_ID)));
    }

    @Test
    void when_PuttingInvalidPlanWithNonExistingType_Expect_Status400TitleAndDetail() throws JSONException {
        RestAssured
                .given()
                .pathParam("planId", VALID_PLAN_ID)
                .body(invalidPlanJsonWithNonExistingType)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{planId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(MESSAGE_NOT_READABLE.getTitle()))
                .body("detail", equalTo(getMessageNotReadableDetail("type", invalidPlanJsonWithNonExistingType,
                        PlanType.class.getSimpleName())));
    }

    @Test
    void when_PuttingInvalidPlanWithInvalidNumCredits_Expect_Status400TitleAndDetail() throws JSONException {
        RestAssured
                .given()
                .pathParam("planId", VALID_PLAN_ID)
                .body(invalidPlanJsonWithInvalidNumCreditsValue)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{planId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(MESSAGE_NOT_READABLE.getTitle()))
                .body("detail", equalTo(getMessageNotReadableDetail("numCredits", invalidPlanJsonWithInvalidNumCreditsValue,
                        Integer.class.getSimpleName())));
    }

    @Test
    void when_PuttingInvalidPlanWithAttributeValueInvalid_Expect_Status400TitleAndDetail() throws JSONException {
        RestAssured
                .given()
                .pathParam("planId", VALID_PLAN_ID)
                .body(invalidPlanJsonWithAttributeValueInvalid)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{planId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(MESSAGE_NOT_READABLE.getTitle()))
                .body("detail", equalTo(getMessageNotReadableDetail("value", invalidPlanJsonWithAttributeValueInvalid,
                        BigDecimal.class.getSimpleName())));
    }

    @Test
    void when_PuttingInvalidPlanWithNegativeNumCredits_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("planId", VALID_PLAN_ID)
                .body(invalidPlanJsonWithNegativeNumCredits)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{planId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getNegativeNumCreditsErrorDetail()));
    }

    @Test
    void when_PuttingInvalidPlanWithNegativeValue_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("planId", VALID_PLAN_ID)
                .body(invalidPlanJsonWithNegativeValue)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{planId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getNegativeValueErrorDetail()));
    }

    @Test
    void when_DeletingValidPlanId_Expect_Status204(){
        RestAssured
                .given()
                .pathParam("planId", VALID_PLAN_ID)
                .when()
                .delete("/{planId}")
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    void when_DeletingNonExistingPlanId_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("planId", NON_EXISTING_PLAN_ID)
                .when()
                .delete("/{planId}")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_PLAN_ID)));
    }

    private void setUpData(){
        basic = new Plan();
        basic.setType(PlanType.BASIC);
        basic.setValue(new BigDecimal(3));
        basic.setNumCredits(1);
        basic.setDescription("Basic plan, access to 1 movie/series per day");
        planRepository.save(basic);

        medium = new Plan();
        medium.setType(PlanType.MEDIUM);
        medium.setValue(new BigDecimal(5));
        medium.setNumCredits(3);
        planRepository.save(medium);

        premium = new Plan();
        premium.setType(PlanType.PREMIUM);
        premium.setValue(new BigDecimal(15));
        premium.setNumCredits(null);
        premium.setDescription("Premium plan, access to unlimited movies/series");
        planRepository.save(premium);

        planQuantity = planRepository.findAll().size();
    }
    private String getResourceNotFoundDetail(int id) {
        return String.format("Plan with id %d doesn't exist", id);
    }

    private String getMessageNotReadableDetail(String property, String JSON, String classString) throws JSONException {
        JSONObject jsonObject = new JSONObject(JSON);
        return String.format("Property '%s' has a value of '%s' , replace it with a compatible value of type '%s'",
                property, jsonObject.getString(property), classString);
    }

    private String getInvalidDataErrorDetail() {
        return "One or more fields are not valid. Correct it.";
    }

    private String getInvalidUrlParameterDetail(String urlParameter) {
        return String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                "planId", INVALID_PLAN_ID.getClass().getSimpleName(), "Long");
    }

    private String getNegativeNumCreditsErrorDetail(){
        return "Number of credits must be provided and be greater than zero if type is not PREMIUM";
    }

    private String getNegativeValueErrorDetail(){
        return "must be greater than zero";
    }
}
