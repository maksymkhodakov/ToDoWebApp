package com.example.todowebapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.todowebapp.domain.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
