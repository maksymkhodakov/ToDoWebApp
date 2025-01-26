package com.example.todowebapp.service.impl;

import com.example.todowebapp.domain.entity.Todo;
import com.example.todowebapp.exceptions.ApiException;
import com.example.todowebapp.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.todowebapp.repository.TodoRepository;
import com.example.todowebapp.service.TodoService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {
    private final TodoRepository todoRepository;

    @Override
    @Transactional
    public void deleteTodoTasks(final Set<Long> ids) {
        final List<Todo> todos = todoRepository.findAllById(ids);

        if (todos.size() != ids.size()) {
            throw new ApiException(ErrorCode.TODO_TASK_NOT_FOUND);
        }

        todoRepository.deleteAllByIdInBatch(ids);
    }
}
