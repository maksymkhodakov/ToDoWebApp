package com.example.todowebapp.api;

import com.example.todowebapp.config.TestSecurityConfig;
import com.example.todowebapp.domain.dto.FeatureDTO;
import com.example.todowebapp.domain.dto.TodoDTO;
import com.example.todowebapp.domain.entity.User;
import com.example.todowebapp.domain.enumerated.Privilege;
import com.example.todowebapp.service.TodoService;
import com.example.todowebapp.service.impl.JwtServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@Import(TestSecurityConfig.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    // Use a spy bean for the real JwtServiceImpl
    @SpyBean
    private JwtServiceImpl jwtService;

    // --- Helper method to create a sample TodoDTO ---
    private TodoDTO sampleTodoDTO() {
        return TodoDTO.builder()
                .id(1L)
                .description("Test Todo")
                .dueDate(LocalDate.now().plusDays(1))
                .checkMark(false)
                .completionDate(null)
                .build();
    }

    /**
     * Creates a fully authenticated RequestPostProcessor using a domain User.
     */
    private RequestPostProcessor authenticatedUser(User domainUser) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(domainUser, "password", domainUser.getAuthorities());
        // Set the authentication in the SecurityContextHolder for the current thread.
        SecurityContextHolder.getContext().setAuthentication(auth);
        return request -> request;
    }

    /**
     * For GET, POST, and PUT endpoints, allowed roles include ROLE_BASIC_USER.
     */
    private RequestPostProcessor authorizedBasicUser() {
        User domainUser = new User();
        domainUser.setId(1L);
        domainUser.setEmail("basicUser@example.com");
        domainUser.setAuthorities(List.of(
                FeatureDTO.builder()
                        .authority(Privilege.ROLE_BASIC_USER.name())
                        .build())
        );
        return authenticatedUser(domainUser);
    }

    /**
     * Simulates an unauthorized user (with no valid role).
     */
    private RequestPostProcessor unauthorizedUser() {
        User domainUser = new User();
        domainUser.setId(3L);
        domainUser.setEmail("unauthorized@example.com");
        domainUser.setAuthorities(List.of(FeatureDTO.builder().authority("ROLE_NONE").build()));
        return authenticatedUser(domainUser);
    }

    // --- GET /api/todos Tests ---
    @Test
    void testGetTodos_Authorized() throws Exception {
        when(todoService.getTodos()).thenReturn(List.of(sampleTodoDTO()));

        mockMvc.perform(get("/api/todos")
                        .with(authorizedBasicUser()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Todo")));
    }

    @Test
    void testGetTodos_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateTodo_Unauthorized() throws Exception {
        TodoDTO inputTodo = sampleTodoDTO();
        mockMvc.perform(put("/api/todo/update")
                        .with(unauthorizedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTodo)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteTodos_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/todo/delete")
                        .with(authorizedBasicUser())
                        .param("ids", "1", "2"))
                .andExpect(status().isForbidden());
    }
}
