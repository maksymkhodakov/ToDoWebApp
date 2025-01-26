package com.example.todowebapp.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.todowebapp.service.TodoService;

import java.util.Set;


@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @PostMapping("/todo/delete")
    @Operation(description = "Delete todo task")
    public ResponseEntity<Void> deleteTodoTasks(@RequestParam final Set<Long> ids) {
        todoService.deleteTodoTasks(ids);
        return ResponseEntity.ok().build();
    }
}
