package com.example.todowebapp.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TODO_TASK_NOT_FOUND("Todo task not found");
    private final String data;
}
