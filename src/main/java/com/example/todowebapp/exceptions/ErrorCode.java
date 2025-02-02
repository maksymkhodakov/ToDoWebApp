package com.example.todowebapp.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TODO_TASK_NOT_FOUND("Todo task not found"),
    USER_ALREADY_EXISTS("User already exists"),
    USER_NOT_FOUND("User not found"),
    USER_NOT_FOUND_IN_SECURITY_CONTEXT("User not found in security context"),
    USER_CANNOT_DELETE_ANOTHER_USER_TODO("User cannot delete another user todo");
    private final String data;
}
