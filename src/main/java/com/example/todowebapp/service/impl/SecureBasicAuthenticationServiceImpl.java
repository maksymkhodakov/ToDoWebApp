package com.example.todowebapp.service.impl;

import com.example.todowebapp.domain.dto.*;
import com.example.todowebapp.domain.entity.User;
import com.example.todowebapp.domain.enumerated.Privilege;
import com.example.todowebapp.exceptions.ApiException;
import com.example.todowebapp.exceptions.ErrorCode;
import com.example.todowebapp.repository.UserRepository;
import com.example.todowebapp.service.JwtService;
import com.example.todowebapp.service.SecureBasicAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecureBasicAuthenticationServiceImpl implements SecureBasicAuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserDTO getCurrentUser() {
        final var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            return UserDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .lastName(user.getLastName())
                    .enabled(user.isEnabled())
                    .accountNonExpired(user.isAccountNonExpired())
                    .accountNonLocked(user.isAccountNonLocked())
                    .credentialsNonExpired(user.isCredentialsNonExpired())
                    .authorities(user.getAuthorities())
                    .build();
        }
        return null;
    }

    @Override
    public void register(final RegisterData data) {
        final Optional<User> userInDb = userRepository.findByEmail(data.getEmail());
        if (userInDb.isPresent())  {
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS);
        }
        try {
            var user = User
                    .builder()
                    .email(data.getEmail())
                    .password(passwordEncoder.encode(data.getPassword()))
                    .authorities(getAuthorities(data))
                    .name(data.getFirstName())
                    .lastName(data.getLastName())
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();
            userRepository.saveAndFlush(user);
        } catch (Exception e) {
            log.info("Error occurred wile registering user");
            throw new ApiException(e.getMessage());
        }
    }

    public List<FeatureDTO> getAuthorities(final RegisterData data) {
        if (data.getAuthorities() == null) {
            return List.of(
                    FeatureDTO.builder()
                            .authority(Privilege.ROLE_BASIC_USER.name())
                            .build()
            );
        }
        return data.getAuthorities();
    }

    @Override
    public LoginResponseDTO login(final LoginData data) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword()));

        final User user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        final String token = jwtService.generateToken(user);

        return LoginResponseDTO.builder()
                .token(token)
                .build();
    }
}
