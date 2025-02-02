package com.example.todowebapp.service;

import com.example.todowebapp.domain.dto.TodoDTO;

import java.util.List;
import java.util.Set;

public interface TodoService {
    List<TodoDTO> getTodos();
    TodoDTO createTodo(TodoDTO todo);
    TodoDTO updateTodo(TodoDTO todo);
    void deleteTodos(Set<Long> ids);
}
