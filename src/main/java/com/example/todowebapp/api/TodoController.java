package com.example.todowebapp.api;

import com.example.todowebapp.domain.dto.TodoDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.todowebapp.service.TodoService;

import java.util.List;
import java.util.Set;


@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @PreAuthorize("hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_BASIC_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_STANDARD_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_PREMIUM_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_ADMIN)")
    @GetMapping("/todos")
    @Operation(description = "Retrieve related todo task(s)")
    public ResponseEntity<List<TodoDTO>> getTodos() {
        return ResponseEntity.ok(todoService.getTodos());
    }

    @PreAuthorize("hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_BASIC_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_STANDARD_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_PREMIUM_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_ADMIN)")
    @PostMapping("/todo/create")
    @Operation(description = "Create todo task")
    public ResponseEntity<TodoDTO> createTodo(@RequestBody @Valid final TodoDTO todo) {
        return ResponseEntity.ok(todoService.createTodo(todo));
    }

    @PreAuthorize("hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_BASIC_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_STANDARD_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_PREMIUM_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_ADMIN)")
    @PutMapping("/todo/update")
    @Operation(description = "Update todo task")
    public ResponseEntity<TodoDTO> updateTodo(@RequestBody @Valid final TodoDTO todo) {
        return ResponseEntity.ok(todoService.updateTodo(todo));
    }

    @PreAuthorize("hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_STANDARD_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_PREMIUM_USER) or " +
            "hasPermission(null, T(com.example.todowebapp.domain.enumerated.Privilege).ROLE_ADMIN)")
    @DeleteMapping("/todo/delete")
    @Operation(description = "Delete todo task(s)")
    public ResponseEntity<Void> deleteTodos(@RequestParam final Set<Long> ids) {
        todoService.deleteTodos(ids);
        return ResponseEntity.ok().build();
    }
}
