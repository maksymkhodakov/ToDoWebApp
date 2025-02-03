package com.example.todowebapp.api;

import com.example.todowebapp.domain.dto.LoginData;
import com.example.todowebapp.domain.dto.LoginResponseDTO;
import com.example.todowebapp.domain.dto.RegisterData;
import com.example.todowebapp.domain.dto.UserDTO;
import com.example.todowebapp.service.SecureBasicAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityControllerTest {

    @Mock
    private SecureBasicAuthenticationService secureBasicAuthenticationService;

    @InjectMocks
    private SecurityController securityController;

    private UserDTO sampleUserDTO;
    private RegisterData sampleRegisterData;
    private LoginData sampleLoginData;
    private LoginResponseDTO sampleLoginResponse;

    @BeforeEach
    void setUp() {
        // Create sample data for tests.
        sampleUserDTO = UserDTO.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test")
                .lastName("User")
                .build();

        sampleRegisterData = new RegisterData();
        sampleRegisterData.setEmail("newuser@example.com");
        sampleRegisterData.setPassword("password");
        sampleRegisterData.setFirstName("New");
        sampleRegisterData.setLastName("User");

        sampleLoginData = new LoginData();
        sampleLoginData.setEmail("test@example.com");
        sampleLoginData.setPassword("password");

        sampleLoginResponse = LoginResponseDTO.builder()
                .token("dummyToken")
                .build();
    }

    // --- Test GET /api/me ---
    @Test
    void testCurrentUser() {
        // Arrange: Stub the service to return our sample user DTO.
        when(secureBasicAuthenticationService.getCurrentUser()).thenReturn(sampleUserDTO);

        // Act: Call the controller method.
        ResponseEntity<UserDTO> response = securityController.currentUser();

        // Assert:
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleUserDTO, response.getBody());
        verify(secureBasicAuthenticationService, times(1)).getCurrentUser();
    }

    // --- Test POST /api/register ---
    @Test
    void testRegistration() {
        // Arrange: For registration, the service returns void. We simulate that it does nothing.
        doNothing().when(secureBasicAuthenticationService).register(sampleRegisterData);

        // Act: Call the registration method.
        ResponseEntity<Void> response = securityController.registration(sampleRegisterData);

        // Assert:
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(secureBasicAuthenticationService, times(1)).register(sampleRegisterData);
    }

    // --- Test POST /api/login ---
    @Test
    void testLogin() {
        // Arrange: Stub the login method to return a sample login response.
        when(secureBasicAuthenticationService.login(sampleLoginData)).thenReturn(sampleLoginResponse);

        // Act: Call the login method.
        ResponseEntity<LoginResponseDTO> response = securityController.login(sampleLoginData);

        // Assert:
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleLoginResponse, response.getBody());
        verify(secureBasicAuthenticationService, times(1)).login(sampleLoginData);
    }
}
