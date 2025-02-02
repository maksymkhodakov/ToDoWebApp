package com.example.todowebapp.service;

import com.example.todowebapp.domain.dto.TodoDTO;
import com.example.todowebapp.domain.entity.Todo;
import com.example.todowebapp.domain.entity.User;
import com.example.todowebapp.exceptions.ApiException;
import com.example.todowebapp.exceptions.ErrorCode;
import com.example.todowebapp.repository.TodoRepository;
import com.example.todowebapp.repository.UserRepository;
import com.example.todowebapp.service.impl.TodoServiceImpl;
import com.example.todowebapp.util.UserSecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(100L)
                .email("user@example.com")
                .name("Test")
                .lastName("User")
                .todos(new ArrayList<>())
                .build();
    }

    // ----------------- getTodos() -----------------

    @Test
    void testGetTodos_ReturnsMappedTodoDTOs() {
        // Arrange: Create some sample todos and stub the repository.
        Todo todo1 = Todo.builder()
                .id(1L)
                .description("Task 1")
                .dueDate(LocalDate.of(2023, 1, 1))
                .checkMark(false)
                .completionDate(null)
                .build();
        Todo todo2 = Todo.builder()
                .id(2L)
                .description("Task 2")
                .dueDate(LocalDate.of(2023, 2, 1))
                .checkMark(true)
                .completionDate(LocalDate.of(2023, 2, 2))
                .build();

        when(todoRepository.findAllByUserId(currentUser.getId()))
                .thenReturn(List.of(todo1, todo2));

        // Use static mock for UserSecurityUtil.getCurrentUser()
        try (MockedStatic<UserSecurityUtil> userUtilMock = mockStatic(UserSecurityUtil.class)) {
            userUtilMock.when(UserSecurityUtil::getCurrentUser).thenReturn(currentUser);

            // Act:
            List<TodoDTO> todos = todoService.getTodos();

            // Assert:
            assertNotNull(todos);
            assertEquals(2, todos.size());
            TodoDTO dto1 = todos.get(0);
            assertEquals(todo1.getId(), dto1.getId());
            assertEquals(todo1.getDescription(), dto1.getDescription());
            assertEquals(todo1.getDueDate(), dto1.getDueDate());
            assertEquals(todo1.isCheckMark(), dto1.isCheckMark());
            assertEquals(todo1.getCompletionDate(), dto1.getCompletionDate());
        }
    }

    // ----------------- createTodo() -----------------

    @Test
    void testCreateTodo_Success() {
        // Arrange: Create a TodoDTO to be saved.
        TodoDTO dtoToCreate = TodoDTO.builder()
                .description("New Task")
                .dueDate(LocalDate.of(2023, 3, 1))
                .checkMark(false)
                .completionDate(null)
                .build();

        Todo savedTodo = Todo.builder()
                .id(10L)
                .description(dtoToCreate.getDescription())
                .dueDate(dtoToCreate.getDueDate())
                .checkMark(dtoToCreate.isCheckMark())
                .completionDate(dtoToCreate.getCompletionDate())
                .build();

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        // Static mock the current user.
        try (MockedStatic<UserSecurityUtil> userUtilMock = mockStatic(UserSecurityUtil.class)) {
            userUtilMock.when(UserSecurityUtil::getCurrentUser).thenReturn(currentUser);

            // Act:
            TodoDTO returnedDTO = todoService.createTodo(dtoToCreate);

            // Assert:
            assertNotNull(returnedDTO);
            assertEquals(savedTodo.getId(), returnedDTO.getId());
            assertEquals(savedTodo.getDescription(), returnedDTO.getDescription());
            assertEquals(savedTodo.getDueDate(), returnedDTO.getDueDate());
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User updatedUser = userCaptor.getValue();
            assertTrue(updatedUser.getTodos().contains(savedTodo));
        }
    }

    // ----------------- updateTodo() -----------------

    @Test
    void testUpdateTodo_NullId_ThrowsException() {
        // Arrange: TodoDTO with null id.
        TodoDTO dto = TodoDTO.builder()
                .id(null)
                .description("Update Task")
                .dueDate(LocalDate.now())
                .checkMark(false)
                .completionDate(null)
                .build();

        // Act & Assert:
        ApiException exception = assertThrows(ApiException.class, () -> todoService.updateTodo(dto));
        assertEquals(ErrorCode.TODO_TASK_NOT_FOUND.getData(), exception.getMessage());
    }

    @Test
    void testUpdateTodo_TodoNotFound_ThrowsException() {
        // Arrange: TodoDTO with non-null id.
        TodoDTO dto = TodoDTO.builder()
                .id(50L)
                .description("Update Task")
                .dueDate(LocalDate.now())
                .checkMark(true)
                .completionDate(LocalDate.now())
                .build();

        when(todoRepository.findById(dto.getId())).thenReturn(Optional.empty());

        // Act & Assert:
        ApiException exception = assertThrows(ApiException.class, () -> todoService.updateTodo(dto));
        assertEquals(ErrorCode.TODO_TASK_NOT_FOUND.getData(), exception.getMessage());
    }

    @Test
    void testUpdateTodo_Success() {
        Todo existingTodo = Todo.builder()
                .id(20L)
                .description("Old Description")
                .dueDate(LocalDate.of(2023, 1, 1))
                .checkMark(false)
                .completionDate(null)
                .build();
        TodoDTO dtoToUpdate = TodoDTO.builder()
                .id(20L)
                .description("New Description")
                .dueDate(LocalDate.of(2023, 4, 1))
                .checkMark(true)
                .completionDate(LocalDate.of(2023, 4, 2))
                .build();

        when(todoRepository.findById(dtoToUpdate.getId())).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(existingTodo)).thenReturn(existingTodo);

        // Act:
        TodoDTO updatedDTO = todoService.updateTodo(dtoToUpdate);

        // Assert: Check that fields were updated.
        assertEquals(dtoToUpdate.getId(), updatedDTO.getId());
        assertEquals("New Description", updatedDTO.getDescription());
        assertEquals(LocalDate.of(2023, 4, 1), updatedDTO.getDueDate());
        assertTrue(updatedDTO.isCheckMark());
        assertEquals(LocalDate.of(2023, 4, 2), updatedDTO.getCompletionDate());
    }

    // ----------------- deleteTodos() -----------------

    @Test
    void testDeleteTodos_TodoNotFound_ThrowsException() {
        // Arrange:
        Set<Long> idsToDelete = Set.of(1L, 2L);
        // Simulate repository returning fewer todos than requested.
        when(todoRepository.findAllById(idsToDelete)).thenReturn(List.of(new Todo()));

        try (MockedStatic<UserSecurityUtil> userUtilMock = mockStatic(UserSecurityUtil.class)) {
            userUtilMock.when(UserSecurityUtil::getCurrentUser).thenReturn(currentUser);

            // Act & Assert:
            ApiException exception = assertThrows(ApiException.class, () -> todoService.deleteTodos(idsToDelete));
            assertEquals(ErrorCode.TODO_TASK_NOT_FOUND.getData(), exception.getMessage());
        }
    }

    @Test
    void testDeleteTodos_UserCannotDeleteAnotherUsersTodo_ThrowsException() {
        // Arrange:
        Todo userTodo = Todo.builder().id(10L).build();
        currentUser.setTodos(List.of(userTodo));

        // Attempt to delete ids including one that is not owned by currentUser.
        Set<Long> idsToDelete = Set.of(10L, 20L);
        // Repository returns two todos.
        Todo otherTodo = Todo.builder().id(20L).build();
        when(todoRepository.findAllById(idsToDelete)).thenReturn(List.of(userTodo, otherTodo));

        try (MockedStatic<UserSecurityUtil> userUtilMock = mockStatic(UserSecurityUtil.class)) {
            userUtilMock.when(UserSecurityUtil::getCurrentUser).thenReturn(currentUser);

            // Act & Assert:
            ApiException exception = assertThrows(ApiException.class, () -> todoService.deleteTodos(idsToDelete));
            assertEquals(ErrorCode.USER_CANNOT_DELETE_ANOTHER_USER_TODO.getData(), exception.getMessage());
        }
    }

    @Test
    void testDeleteTodos_Success() {
        // Arrange:
        // Current user owns todos with id 10 and 20.
        Todo todo10 = Todo.builder().id(10L).build();
        Todo todo20 = Todo.builder().id(20L).build();
        currentUser.setTodos(List.of(todo10, todo20));

        Set<Long> idsToDelete = Set.of(10L, 20L);
        when(todoRepository.findAllById(idsToDelete)).thenReturn(List.of(todo10, todo20));

        try (MockedStatic<UserSecurityUtil> userUtilMock = mockStatic(UserSecurityUtil.class)) {
            userUtilMock.when(UserSecurityUtil::getCurrentUser).thenReturn(currentUser);

            // Act:
            todoService.deleteTodos(idsToDelete);

            // Assert: Verify that the repository's batch delete method was called.
            verify(todoRepository, times(1)).deleteAllByIdInBatch(idsToDelete);
        }
    }
}
