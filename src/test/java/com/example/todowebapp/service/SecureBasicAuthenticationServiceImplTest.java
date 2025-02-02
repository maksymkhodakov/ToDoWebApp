package com.example.todowebapp.service;

import com.example.todowebapp.domain.dto.FeatureDTO;
import com.example.todowebapp.domain.dto.LoginData;
import com.example.todowebapp.domain.dto.LoginResponseDTO;
import com.example.todowebapp.domain.dto.RegisterData;
import com.example.todowebapp.domain.dto.UserDTO;
import com.example.todowebapp.domain.entity.User;
import com.example.todowebapp.domain.enumerated.Privilege;
import com.example.todowebapp.exceptions.ApiException;
import com.example.todowebapp.exceptions.ErrorCode;
import com.example.todowebapp.repository.UserRepository;
import com.example.todowebapp.service.impl.SecureBasicAuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecureBasicAuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private SecureBasicAuthenticationServiceImpl service;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    // --- Tests for getCurrentUser() ---

    @Test
    void testGetCurrentUser_NoAuthentication() {
        // When no authentication is set in the context, expect null.
        SecurityContextHolder.clearContext();
        UserDTO currentUser = service.getCurrentUser();
        assertNull(currentUser, "Expected null when no user is authenticated");
    }

    @Test
    void testGetCurrentUser_WithValidAuthentication() {
        // Arrange: create a sample User and put it in the SecurityContext.
        User sampleUser = createSampleUser();
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(sampleUser, null);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Act:
        UserDTO userDTO = service.getCurrentUser();

        // Assert:
        assertNotNull(userDTO, "UserDTO should not be null when authenticated");
        assertEquals(sampleUser.getId(), userDTO.getId());
        assertEquals(sampleUser.getEmail(), userDTO.getEmail());
        assertEquals(sampleUser.getName(), userDTO.getName());
        assertEquals(sampleUser.getLastName(), userDTO.getLastName());
        assertEquals(sampleUser.isEnabled(), userDTO.isEnabled());
        // Additional assertions for authorities can be added here.
    }

    @Test
    void testGetCurrentUser_WithWrongPrincipalType() {
        // Arrange: Set a non-User principal (e.g., a String) in the authentication token.
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("NotAUserPrincipal", null);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Act:
        UserDTO userDTO = service.getCurrentUser();

        // Assert: Since the principal isn't a User, expect null.
        assertNull(userDTO, "Expected null when the principal is not of type User");
    }

    // --- Tests for register() ---

    @Test
    void testRegister_Success() {
        // Arrange:
        RegisterData data = createSampleRegisterData();
        when(userRepository.findByEmail(data.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(data.getPassword())).thenReturn("encodedPassword");

        // Act:
        service.register(data);

        // Assert:
        // Verify that saveAndFlush was called exactly once.
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).saveAndFlush(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(data.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        // When authorities are null in data, the service sets a default authority.
        assertNotNull(savedUser.getAuthorities());
        assertFalse(savedUser.getAuthorities().isEmpty());
        assertEquals(Privilege.ROLE_BASIC_USER.name(), savedUser.getAuthorities().get(0).getAuthority());
    }

    @Test
    void testRegister_WithProvidedAuthorities() {
        // Arrange: Create RegisterData with a custom authority list.
        RegisterData data = createSampleRegisterData();
        data.setAuthorities(List.of(
                FeatureDTO.builder().authority(Privilege.ROLE_ADMIN.name()).build(),
                FeatureDTO.builder().authority(Privilege.ROLE_PREMIUM_USER.name()).build()
        ));
        when(userRepository.findByEmail(data.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(data.getPassword())).thenReturn("encodedPassword");

        // Act:
        service.register(data);

        // Assert:
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).saveAndFlush(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(data.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        // Verify that the provided authorities are used.
        List<FeatureDTO> authorities = savedUser.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(f -> f.getAuthority().equals(Privilege.ROLE_ADMIN.name())));
        assertTrue(authorities.stream().anyMatch(f -> f.getAuthority().equals(Privilege.ROLE_PREMIUM_USER.name())));
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Arrange:
        RegisterData data = createSampleRegisterData();
        when(userRepository.findByEmail(data.getEmail())).thenReturn(Optional.of(new User()));

        // Act & Assert:
        ApiException exception = assertThrows(ApiException.class, () -> service.register(data));
        assertEquals(ErrorCode.USER_ALREADY_EXISTS.getData(), exception.getMessage());
    }

    @Test
    void testLogin_Success() {
        // Arrange:
        LoginData loginData = createSampleLoginData();
        // Instead of doNothing(), stub authenticate() to return a valid token.
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);

        User sampleUser = createSampleUser();
        when(userRepository.findByEmail(loginData.getEmail())).thenReturn(Optional.of(sampleUser));
        when(jwtService.generateToken(sampleUser)).thenReturn("dummyToken");

        // Act:
        LoginResponseDTO response = service.login(loginData);

        // Assert:
        assertNotNull(response);
        assertEquals("dummyToken", response.getToken());

        // Verify that authenticationManager.authenticate was called with the correct token.
        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager, times(1)).authenticate(tokenCaptor.capture());
        UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
        assertEquals(loginData.getEmail(), capturedToken.getPrincipal());
        assertEquals(loginData.getPassword(), capturedToken.getCredentials());
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange:
        LoginData loginData = createSampleLoginData();
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);
        when(userRepository.findByEmail(loginData.getEmail())).thenReturn(Optional.empty());

        // Act & Assert:
        ApiException exception = assertThrows(ApiException.class, () -> service.login(loginData));
        assertEquals(ErrorCode.USER_NOT_FOUND.getData(), exception.getMessage());
    }

    @Test
    void testLogin_FailedAuthentication() {
        // Arrange:
        LoginData loginData = createSampleLoginData();
        // Simulate authentication failure (e.g., bad credentials)
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        // The repository and jwtService should never be called in this case.
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> service.login(loginData));
        assertTrue(exception.getMessage().contains("Bad credentials"),
                "Exception message should indicate bad credentials");
        verify(userRepository, never()).findByEmail(any());
        verify(jwtService, never()).generateToken(any());
    }


    // --- Helper methods to create sample data ---
    private User createSampleUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test")
                .lastName("User")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .authorities(List.of(
                        FeatureDTO.builder().authority(Privilege.ROLE_BASIC_USER.name()).build()
                ))
                .build();
    }

    private RegisterData createSampleRegisterData() {
        RegisterData data = new RegisterData();
        data.setEmail("test@example.com");
        data.setPassword("password");
        data.setFirstName("Test");
        data.setLastName("User");
        // Setting authorities to null to force the default assignment.
        data.setAuthorities(null);
        return data;
    }

    private LoginData createSampleLoginData() {
        LoginData data = new LoginData();
        data.setEmail("test@example.com");
        data.setPassword("password");
        return data;
    }
}
