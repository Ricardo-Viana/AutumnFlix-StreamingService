package com.autumnflix.streaming.api.exceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.autumnflix.streaming.domain.exception.*;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String GENERIC_ERROR_MESSAGE_FINAL_USER = "An unexpected system error has occurred. " +
            "Try again and if the problem persists, contact your system administrator";

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof UnrecognizedPropertyException) {
            return handleUnrecognizedPropertyException((UnrecognizedPropertyException) rootCause,
                    headers, status, request);
        } else if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) rootCause, headers, status, request);
        }

        ApiErrorType errorType = ApiErrorType.MESSAGE_NOT_READABLE;
        String detail = "The body is not valid. Check for sintaxe errors.";
        ApiError error = createApiErrorBuilder(status, errorType, detail)
                .userMessage(GENERIC_ERROR_MESSAGE_FINAL_USER)
                .build();

        return handleExceptionInternal(ex, error, headers, status, request);
    }

    private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex,
                                                                HttpHeaders headers, HttpStatusCode status,
                                                                WebRequest request) {
        String property = ex.getPath()
                .stream()
                .map(prop -> prop.getFieldName())
                .collect(Collectors.joining("."));

        ApiErrorType errorType = ApiErrorType.MESSAGE_NOT_READABLE;
        String detail = String.format("Property '%s' has a value of '%s' , replace it with a compatible value of type '%s'",
                property, ex.getValue(), ex.getTargetType().getSimpleName());

        ApiError error = createApiErrorBuilder(status, errorType, detail).build();

        return handleExceptionInternal(ex, error, headers, status, request);
    }

    private ResponseEntity<Object> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex,
                                                                       HttpHeaders headers, HttpStatusCode status,
                                                                       WebRequest request) {

        HttpStatusCode statusCode = HttpStatusCode.valueOf(status.value());
        ApiErrorType errorType = ApiErrorType.INVALID_PROPERTY;
        String detail = String.format("Property '%s' is not valid, remove it", ex.getPropertyName());

        ApiError error = createApiErrorBuilder(statusCode, errorType, detail).build();

        return handleExceptionInternal(ex, error, headers, statusCode, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        ApiErrorType errorType = ApiErrorType.INVALID_DATA;
        String detail = "One or more fields are not valid. Correct it.";

        List<ApiError.Object> objects = ex.getAllErrors().stream()
                .map(error -> {
                   String name = error.getObjectName();
                   String userMessage = error.getDefaultMessage();

                   if(Arrays.asList(error.getCodes()).contains("Positive")){
                        userMessage = "must be greater than zero";
                   }

                   if(Arrays.asList(error.getCodes()).contains("Email")){
                       userMessage = "must be a well formatted email";
                   }

                   if(error instanceof FieldError){
                    name = ((FieldError) error).getField();
                   }
                   return ApiError.Object.builder()
                           .name(name)
                           .userMessage(userMessage)
                           .build();
                }).toList();

        ApiError error = createApiErrorBuilder(status, errorType, detail)
                .objects(objects)
                .build();

        return handleExceptionInternal(ex, error, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatusCode status, WebRequest request) {

        ApiErrorType errorType = ApiErrorType.RESOURCE_NOT_FOUND;
        String detail = String.format("The resource '%s' that you tried to access is not valid.", ex.getRequestURL());

        ApiError error = createApiErrorBuilder(status, errorType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, error, headers, status, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                             WebRequest request) {

        HttpStatusCode status = HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value());
        ApiErrorType errorType = ApiErrorType.INVALID_URL_PARAMETER;
        String detail = String.format("The url parameter '%s' received a invalid value of the type '%s'. " +
                        "Correct it and enter with a value '%s' type.",
                ex.getPropertyName(), ex.getValue().getClass().getSimpleName(), ex.getRequiredType().getSimpleName());

        ApiError error = createApiErrorBuilder(status, errorType, detail)
                .userMessage(GENERIC_ERROR_MESSAGE_FINAL_USER)
                .build();

        return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {

        HttpStatusCode statusCode = HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value());
        ApiErrorType errorType = ApiErrorType.BUSINESS_ERROR;
        String detail = ex.getMessage();

        ApiError error = createApiErrorBuilder(statusCode, errorType, detail)
                .userMessage(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, error, new HttpHeaders(), statusCode, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {

        HttpStatusCode statusCode = HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value());
        ApiErrorType errorType = ApiErrorType.RESOURCE_NOT_FOUND;
        String detail = ex.getMessage();

        ApiError error = createApiErrorBuilder(statusCode, errorType, detail)
                .userMessage(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, error, new HttpHeaders(), statusCode, request);
    }

    @ExceptionHandler(EntityBeingUsedException.class)
    public ResponseEntity<Object> handleEntityBeingUsedException(EntityBeingUsedException ex, WebRequest request) {

        HttpStatusCode statusCode = HttpStatusCode.valueOf(HttpStatus.CONFLICT.value());
        ApiErrorType errorType = ApiErrorType.ENTITY_BEING_USED;
        String detail = ex.getMessage();

        ApiError error = createApiErrorBuilder(statusCode, errorType, detail)
                .userMessage(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, error, new HttpHeaders(), statusCode, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {

        if (body == null) {
            body = ApiError.builder()
                    .title(HttpStatus.valueOf(statusCode.value()).getReasonPhrase())
                    .status(statusCode.value())
                    .userMessage(GENERIC_ERROR_MESSAGE_FINAL_USER)
                    .timeStamp(OffsetDateTime.now())
                    .build();
        } else if (body instanceof String) {
            body = ApiError.builder()
                    .title((String) body)
                    .status(statusCode.value())
                    .userMessage(GENERIC_ERROR_MESSAGE_FINAL_USER)
                    .timeStamp(OffsetDateTime.now())
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private ApiError.ApiErrorBuilder createApiErrorBuilder(HttpStatusCode statusCode, ApiErrorType errorType,
                                                           String detail) {
        return ApiError.builder()
                .status(statusCode.value())
                .type(errorType.getUri())
                .title(errorType.getTitle())
                .detail(detail)
                .timeStamp(OffsetDateTime.now());
    }
}
