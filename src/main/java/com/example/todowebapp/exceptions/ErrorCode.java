package com.example.todowebapp.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TODO_TASK_NOT_FOUND("Todo task not found"),
    USER_ALREADY_EXISTS("User already exists"),
    USER_NOT_FOUND("User not found");
    private final String data;
}
