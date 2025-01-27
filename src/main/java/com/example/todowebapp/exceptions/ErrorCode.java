package com.example.todowebapp.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TODO_TASK_NOT_FOUND("Todo task not found"),
    USER_ALREADY_EXISTS("User already exists"),
    USER_NOT_FOUND("User not found"),
    CANNOT_DESERIALIZE_JSON("Cannot deserialize json"),
    CANNOT_SERIALIZE_JSON("Cannot serialize json"),
    ERROR_PROCESSING_JWT("Error processing JWT"),
    UNAUTHORIZED("Unauthorized error");
    private final String data;
}
