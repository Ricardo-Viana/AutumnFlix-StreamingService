package com.autumnflix.streaming.api.exceptionHandler;

import lombok.Getter;

@Getter
public enum ApiErrorType {

    BUSINESS_ERROR("/business-error", "A business rule was violated"),
    ENTITY_BEING_USED("/entity-being-used", "Entity being used"),
    RESOURCE_NOT_FOUND("/resource-not-found", "Resource not found"),
    MESSAGE_NOT_READABLE("/message-not-readable", "Message not readable"),
    INVALID_PROPERTY("/invalid-property", "Body has a invalid property"),
    INVALID_DATA("/invalid-data", "Body has invalid data"),
    INVALID_URL_PARAMETER("invalid-url-parameter", "There is a invalid parameter in the url");

    private String uri;
    private String title;

    ApiErrorType(String path, String title) {
        this.uri = "https://autumnflix.com.br" + path;
        this.title = title;
    }
}
