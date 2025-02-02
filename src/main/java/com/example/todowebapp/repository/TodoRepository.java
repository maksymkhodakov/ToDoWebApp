package com.example.todowebapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.todowebapp.domain.entity.Todo;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserId(Long id);
}
