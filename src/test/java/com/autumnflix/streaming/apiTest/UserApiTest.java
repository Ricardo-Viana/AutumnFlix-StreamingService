package com.autumnflix.streaming.apiTest;

import com.autumnflix.streaming.domain.exception.PlanNotFoundException;
import com.autumnflix.streaming.domain.model.*;
import com.autumnflix.streaming.domain.repository.PlanRepository;
import com.autumnflix.streaming.domain.repository.UserRepository;
import com.autumnflix.streaming.util.DatabaseCleaner;
import com.autumnflix.streaming.util.JsonReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.autumnflix.streaming.api.exceptionHandler.ApiErrorType.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class UserApiTest {

    private static final int VALID_USER_ID = 1;

    private static final int VALID_USER_ID_2 = 2;

    private static final int NON_EXISTING_USER_ID = Integer.MAX_VALUE;

    private static final String INVALID_USER_ID = "a";

    private static final String VALID_USER_EMAIL = "johnsmith@gmail.com";

    private static final String NON_EXISTING_USER_EMAIL = "a@gmail.com";

    private static final String RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE = RESOURCE_NOT_FOUND.getTitle();

    private static final String INVALID_DATA_ERROR_TYPE_TITLE = INVALID_DATA.getTitle();

    private static final String INVALID_URL_PARAMETER_ERROR_TYPE_TITLE = INVALID_URL_PARAMETER.getTitle();

    private static final String ENTITY_BEING_USED_ERROR_TYPE_TITLE = ENTITY_BEING_USED.getTitle();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    private int userQuantity;


    // savedUser and User are divided because some inputs cant be equal (unique annotation on db, like email)
    private User user;

    private User savedUser;

    private User savedUser2; // Testing Duplicates

    private IdentificationDocument identificationDocument;

    private IdentificationDocument identificationDocument1;

    private IdentificationDocument identificationDocument2;

    private PaymentMethod paymentMethod;

    private Credit credit;

    private Plan plan;

    private Plan plan2;

    private final String correctUserJson;

    private final String correctUserJson2;

    private final String invalidUserJsonWithInvalidDOB;

    private final String invalidUserJsonWithInvalidEmail;

    private final String invalidUserJsonWithInvalidDocumentValue;

    private final String invalidUserJsonWithInvalidExpiringDate;

    private final String invalidUserJsonWithInvalidCreditCardNumber;

    private final String invalidUserJsonWithInvalidSecurityCode;

    private final String invalidUserWithDuplicatedEmail;

    private final String invalidUserWithDuplicatedIdentificationDocument;

    public UserApiTest(){
        this.correctUserJson = JsonReader
                .readJsonAsString("src/test/resources/JSON/valid/user/validUser.JSON");
        this.correctUserJson2 = JsonReader
                .readJsonAsString("src/test/resources/JSON/valid/user/validUser2.JSON");
        this.invalidUserJsonWithInvalidDOB = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/user/invalidUserWithInvalidDOB.JSON");
        this.invalidUserJsonWithInvalidEmail = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/user/invalidUserWithInvalidEmail.JSON");
        this.invalidUserJsonWithInvalidDocumentValue = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/user/invalidUserWithInvalidDocumentValue.JSON");
        this.invalidUserJsonWithInvalidExpiringDate = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/user/invalidUserWithInvalidExpiringDate.JSON");
        this.invalidUserJsonWithInvalidCreditCardNumber = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/user/invalidUserWithInvalidCreditCardNumber.JSON");
        this.invalidUserJsonWithInvalidSecurityCode = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/user/invalidUserWithInvalidSecurityCode.JSON");
        this.invalidUserWithDuplicatedEmail = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/user/invalidUserWithDuplicatedEmail.JSON");
        this.invalidUserWithDuplicatedIdentificationDocument = JsonReader
                .readJsonAsString("src/test/resources/JSON/invalid/user/invalidUserWithDuplicatedIdentificationDocument.JSON");
    }

    @BeforeEach
    void setUp(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/users";

        databaseCleaner.clearTables();
        setUpData();
    }

    @Test
    void when_GettingAllUsers_Expect_Status200QuantityAndProperties(){
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(OK.value())
                .body("", hasSize(userQuantity))
                .body("fullName", hasItems(savedUser.getFullName()))
                .body("dob", hasItems(savedUser.getDob().toString()))
                .body("email", hasItems(savedUser.getEmail()))
                .body("password", hasItems(savedUser.getPassword()))
                .body("role", hasItems(savedUser.getRole().toString()))
                .body("identificationDocument.type", hasItems(savedUser.getIdentificationDocument().getType().toString()))
                .body("identificationDocument.value",hasItems(savedUser.getIdentificationDocument().getValue()))
                .body("paymentMethod.cardType",hasItems(savedUser.getPaymentMethod().getCardType().toString()))
                .body("paymentMethod.expiringDate", hasItems(savedUser.getPaymentMethod().getExpiringDate().toString()))
                .body("paymentMethod.ownerName", hasItems(savedUser.getPaymentMethod().getOwnerName()))
                .body("plan.type", hasItems(savedUser.getPlan().getType().toString()))
                .body("plan.value", hasItems(savedUser.getPlan().getValue().floatValue()))
                .body("plan.numCredits",hasItems(savedUser.getPlan().getNumCredits().intValue()))
                .body("credit.quantity", hasItems(savedUser.getCredit().getQuantity().intValue()));
    }

    @Test
    void when_GettingByValidUserId_Expect_Status200AndProperties(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .accept(ContentType.JSON)
                .when()
                .get("/{userId}")
                .then()
                .statusCode(OK.value())
                .body("fullName", equalTo(savedUser.getFullName()))
                .body("dob", equalTo(savedUser.getDob().toString()))
                .body("email", equalTo(savedUser.getEmail()))
                .body("password", equalTo(savedUser.getPassword()))
                .body("role", equalTo(savedUser.getRole().toString()))
                .body("identificationDocument.type", equalTo(savedUser.getIdentificationDocument().getType().toString()))
                .body("identificationDocument.value",equalTo(savedUser.getIdentificationDocument().getValue()))
                .body("paymentMethod.cardType",equalTo(savedUser.getPaymentMethod().getCardType().toString()))
                .body("paymentMethod.expiringDate", equalTo(savedUser.getPaymentMethod().getExpiringDate().toString()))
                .body("paymentMethod.ownerName", equalTo(savedUser.getPaymentMethod().getOwnerName()))
                .body("plan.type", equalTo(savedUser.getPlan().getType().toString()))
                .body("plan.value", equalTo(savedUser.getPlan().getValue().floatValue()))
                .body("plan.numCredits",equalTo(savedUser.getPlan().getNumCredits().intValue()))
                .body("credit.quantity", equalTo(savedUser.getCredit().getQuantity().intValue()));
    }

    @Test
    void when_GettingByNonExistingUserId_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", NON_EXISTING_USER_ID)
                .accept(ContentType.JSON)
                .when()
                .get("/{userId}")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_USER_ID)));
    }

    @Test
    void when_GettingByInvalidUserId_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", INVALID_USER_ID)
                .accept(ContentType.JSON)
                .when()
                .get("/{userId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidUrlParameterDetail(INVALID_USER_ID)));
    }

    @Test
    void when_GettingByValidUserEmail_Expect_Status200AndProperties(){
        RestAssured
                .given()
                .pathParam("email", VALID_USER_EMAIL)
                .accept(ContentType.JSON)
                .when()
                .get("/email/{email}")
                .then()
                .statusCode(OK.value())
                .body("fullName", equalTo(savedUser.getFullName()))
                .body("dob", equalTo(savedUser.getDob().toString()))
                .body("email", equalTo(savedUser.getEmail()))
                .body("password", equalTo(savedUser.getPassword()))
                .body("role", equalTo(savedUser.getRole().toString()))
                .body("identificationDocument.type", equalTo(savedUser.getIdentificationDocument().getType().toString()))
                .body("identificationDocument.value",equalTo(savedUser.getIdentificationDocument().getValue()))
                .body("paymentMethod.cardType",equalTo(savedUser.getPaymentMethod().getCardType().toString()))
                .body("paymentMethod.expiringDate", equalTo(savedUser.getPaymentMethod().getExpiringDate().toString()))
                .body("paymentMethod.ownerName", equalTo(savedUser.getPaymentMethod().getOwnerName()))
                .body("plan.type", equalTo(savedUser.getPlan().getType().toString()))
                .body("plan.value", equalTo(savedUser.getPlan().getValue().floatValue()))
                .body("plan.numCredits",equalTo(savedUser.getPlan().getNumCredits().intValue()))
                .body("credit.quantity", equalTo(savedUser.getCredit().getQuantity().intValue()));
    }

    @Test
    void when_GettingByNonExistingUserEmail_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("email", NON_EXISTING_USER_EMAIL)
                .accept(ContentType.JSON)
                .when()
                .get("/email/{email}")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getEmailResourceNotFoundErrorTypeTitle(NON_EXISTING_USER_EMAIL)));
    }

    @Test
    void when_PostingValidUser_Expect_Status201AndProperties(){
        RestAssured
                .given()
                .body(correctUserJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(CREATED.value())
                .body("fullName", equalTo(user.getFullName()))
                .body("dob", equalTo(user.getDob().toString()))
                .body("email", equalTo(user.getEmail()))
                .body("password", equalTo(user.getPassword()))
                .body("role", equalTo(user.getRole().toString()))
                .body("identificationDocument.type", equalTo(user.getIdentificationDocument().getType().toString()))
                .body("identificationDocument.value",equalTo(user.getIdentificationDocument().getValue()))
                .body("paymentMethod.cardType",equalTo(user.getPaymentMethod().getCardType().toString()))
                .body("paymentMethod.expiringDate", equalTo(user.getPaymentMethod().getExpiringDate().toString()))
                .body("paymentMethod.ownerName", equalTo(user.getPaymentMethod().getOwnerName()))
                .body("plan.type", equalTo(user.getPlan().getType().toString()))
                .body("plan.value", equalTo(user.getPlan().getValue().floatValue()))
                .body("plan.numCredits",equalTo(user.getPlan().getNumCredits().intValue()))
                .body("credit.quantity", equalTo(user.getCredit().getQuantity().intValue()));
    }

    @Test
    void when_PostingInvalidUserWithInvalidDOB_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidUserJsonWithInvalidDOB)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getMinimumAgeErrorDetail()));
    }

    @Test
    void when_PostingInvalidUserWithInvalidEmail_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidUserJsonWithInvalidEmail)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getEmailFormatErrorDetail()));
    }


    @Test
    void when_PostingInvalidUserWithInvalidCreditCardNumber_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidUserJsonWithInvalidCreditCardNumber)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getCreditCardErrorDetail()));
    }

    @Test
    void when_PostingInvalidUserWithInvalidSecurityCode_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidUserJsonWithInvalidSecurityCode)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getCVVErrorDetail()));
    }


    @Test
    void when_PostingInvalidUserWithInvalidDocumentValue_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidUserJsonWithInvalidDocumentValue)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getDocumentValueErrorDetail()));
    }

    @Test
    void when_PostingInvalidUserWithInvalidExpiringDate_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidUserJsonWithInvalidExpiringDate)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getExpiringDateErrorDetail()));
    }

    @Test
    void when_PostingInvalidUserWithDuplicatedEmail_Expect_Status409TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidUserWithDuplicatedEmail)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(CONFLICT.value())
                .body("title", equalTo(ENTITY_BEING_USED_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getDuplicatedInformationErrorDetail()));
    }

    @Test
    void when_PostingInvalidUserWithDuplicatedIdentificationDocument_Expect_Status409TitleAndDetail(){
        RestAssured
                .given()
                .body(invalidUserWithDuplicatedIdentificationDocument)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(CONFLICT.value())
                .body("title", equalTo(ENTITY_BEING_USED_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getDuplicatedInformationErrorDetail()));
    }
    @Test
    void when_PuttingValidUser_Expect_Status200AndProperties(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .body(correctUserJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(OK.value())
                .body("id", equalTo(VALID_USER_ID))
                .body("fullName", equalTo(user.getFullName()))
                .body("dob", equalTo(user.getDob().toString()))
                .body("email", equalTo(user.getEmail()))
                .body("password", equalTo(user.getPassword()))
                .body("role", equalTo(user.getRole().toString()))
                .body("identificationDocument.type", equalTo(user.getIdentificationDocument().getType().toString()))
                .body("identificationDocument.value",equalTo(user.getIdentificationDocument().getValue()))
                .body("paymentMethod.cardType",equalTo(user.getPaymentMethod().getCardType().toString()))
                .body("paymentMethod.expiringDate", equalTo(user.getPaymentMethod().getExpiringDate().toString()))
                .body("paymentMethod.ownerName", equalTo(user.getPaymentMethod().getOwnerName()))
                .body("plan.type", equalTo(user.getPlan().getType().toString()))
                .body("plan.value", equalTo(user.getPlan().getValue().floatValue()))
                .body("plan.numCredits",equalTo(user.getPlan().getNumCredits().intValue()))
                .body("credit.quantity", equalTo(user.getCredit().getQuantity().intValue()));
    }

    @Test
    void when_PuttingValidUserWithDifferentPlanId_Expect_Status200AndProperties(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .body(correctUserJson2)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(OK.value())
                .body("id", equalTo(VALID_USER_ID))
                .body("fullName", equalTo(user.getFullName()))
                .body("dob", equalTo(user.getDob().toString()))
                .body("email", equalTo(user.getEmail()))
                .body("password", equalTo(user.getPassword()))
                .body("role", equalTo(user.getRole().toString()))
                .body("identificationDocument.type", equalTo(user.getIdentificationDocument().getType().toString()))
                .body("identificationDocument.value",equalTo(user.getIdentificationDocument().getValue()))
                .body("paymentMethod.cardType",equalTo(user.getPaymentMethod().getCardType().toString()))
                .body("paymentMethod.expiringDate", equalTo(user.getPaymentMethod().getExpiringDate().toString()))
                .body("paymentMethod.ownerName", equalTo(user.getPaymentMethod().getOwnerName()))
                .body("plan.type", equalTo(plan2.getType().toString()))
                .body("plan.value", equalTo(plan2.getValue().floatValue()))
                .body("plan.numCredits",equalTo(plan2.getNumCredits().intValue()))
                .body("credit.quantity", equalTo(plan2.getNumCredits().intValue()));
    }

    @Test
    void when_PuttingValidUserWithNonExistingUserId_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", NON_EXISTING_USER_ID)
                .body(correctUserJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_USER_ID)));
    }

    @Test
    void when_PuttingInvalidUserWithInvalidCreditCardNumber_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .body(invalidUserJsonWithInvalidCreditCardNumber)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getCreditCardErrorDetail()));
    }

    @Test
    void when_PuttingInvalidUserWithInvalidDOB_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .body(invalidUserJsonWithInvalidDOB)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getMinimumAgeErrorDetail()));
    }

    @Test
    void when_PuttingInvalidUserWithInvalidDocumentValue_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .body(invalidUserJsonWithInvalidDocumentValue)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getDocumentValueErrorDetail()));
    }

    @Test
    void when_PuttingInvalidUserWithInvalidEmail_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .body(invalidUserJsonWithInvalidEmail)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getEmailFormatErrorDetail()));
    }

    @Test
    void when_PuttingInvalidUserWithInvalidExpiringDate_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .body(invalidUserJsonWithInvalidExpiringDate)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getExpiringDateErrorDetail()));
    }

    @Test
    void when_PuttingInvalidUserWithInvalidSecurityCode_Expect_Status400TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .body(invalidUserJsonWithInvalidSecurityCode)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_DATA_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidDataErrorDetail()))
                .body("objects.userMessage", hasItems(getCVVErrorDetail()));
    }

    @Test
    void when_PuttingInvalidUserWithDuplicatedEmail_Expect_Status409TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID_2)
                .body(invalidUserWithDuplicatedEmail)
                .contentType(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(CONFLICT.value())
                .body("title", equalTo(ENTITY_BEING_USED_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getDuplicatedInformationErrorDetail()));
    }

    @Test
    void when_PuttingInvalidUserWithDuplicatedIdentificationDocument_Expect_Status409TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID_2)
                .body(invalidUserWithDuplicatedIdentificationDocument)
                .contentType(ContentType.JSON)
                .when()
                .put("/{userId}")
                .then()
                .statusCode(CONFLICT.value())
                .body("title", equalTo(ENTITY_BEING_USED_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getDuplicatedInformationErrorDetail()));
    }
    @Test
    void when_DeletingValidUserId_Expect_Status204(){
        RestAssured
                .given()
                .pathParam("userId", VALID_USER_ID)
                .when()
                .delete("/{userId}")
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    void when_DeletingNonExistingUserId_Expect_Status404TitleAndDetail(){
        RestAssured
                .given()
                .pathParam("userId", NON_EXISTING_USER_ID)
                .when()
                .delete("/{userId}")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("title", equalTo(RESOURCE_NOT_FOUND_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getResourceNotFoundDetail(NON_EXISTING_USER_ID)));
    }

    @Test
    void when_DeletingByInvalidUserId_Expect_Status404(){
        RestAssured
                .given()
                .pathParam("userId", INVALID_USER_ID)
                .when()
                .delete("/{userId}")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("title", equalTo(INVALID_URL_PARAMETER_ERROR_TYPE_TITLE))
                .body("detail", equalTo(getInvalidUrlParameterDetail("userId")));
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

        plan2 = new Plan();
        plan2.setType(PlanType.BASIC);
        plan2.setValue(new BigDecimal(3.00));
        plan2.setNumCredits(1);

        credit = new Credit();
        credit.setQuantity(plan.getNumCredits());

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

        userRepository.save(savedUser); // WORKING ON DATABASE TO MATCH WHAT IS SAVED

        user = new User();
        user.setFullName("John Smith");
        user.setDob(LocalDate.parse("10/01/1990", formatter));
        user.setEmail("johnsmith1@gmail.com");
        identificationDocument1 = new IdentificationDocument(); // just create to not conflict with identification Document on saved user
        identificationDocument1.setValue("002-02-0002");
        identificationDocument1.setType(IdentificationDocumentType.SSN);
        user.setIdentificationDocument(identificationDocument1);
        user.setPaymentMethod(paymentMethod);
        user.setPassword("johnsmithpassword");
        user.setRole(Role.USER);
        user.setPlan(plan);
        user.setCredit(credit);

        //Creating user for test of duplicates

        savedUser2 = new User();

        savedUser2.setFullName("Anna");
        savedUser2.setDob(LocalDate.parse("10/01/1990", formatter));
        savedUser2.setEmail("anna@gmail.com");
        identificationDocument2 = new IdentificationDocument();
        identificationDocument2.setValue("003-03-0003");
        identificationDocument2.setType(IdentificationDocumentType.SSN);
        savedUser2.setIdentificationDocument(identificationDocument2);
        savedUser2.setPaymentMethod(paymentMethod);
        savedUser2.setPassword("annapassword");
        savedUser2.setRole(Role.USER);
        savedUser2.setPlan(plan2);
        savedUser2.setCredit(credit);

        userRepository.save(savedUser2);
        userQuantity = userRepository.findAll().size();
    }

    private String getResourceNotFoundDetail(int id) {
        return String.format("User with id %d doesn't exist", id);
    }

    private String getEmailResourceNotFoundErrorTypeTitle(String email) { return String.format("User with email %s doesn't exist", email);}

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
                "userId", INVALID_USER_ID.getClass().getSimpleName(), "Long");
    }

    private String getMinimumAgeErrorDetail() {
        return "The minimum age to create an account is 18 years old";
    }


    private String getEmailFormatErrorDetail() {
        return "must be a well formatted email";
    }

    private String getCreditCardErrorDetail() {
        return "Credit card number must be valid";
    }

    private String getCVVErrorDetail() {
        return "Credit card cvv must be 3 digits for Visa,MasterCard and Discover or 4 digits for American Express";
    }

    private String getDocumentValueErrorDetail() {
        return "Document value must be valid for the type";
    }

    private String getExpiringDateErrorDetail() {
        return "Expiring date must be some date in the future";
    }

    private String getDuplicatedInformationErrorDetail(){
        return "User is already registered";
    }
}
