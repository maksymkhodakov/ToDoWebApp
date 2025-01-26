package com.example.todowebapp.service.impl;

import com.example.todowebapp.domain.data.LoginData;
import com.example.todowebapp.domain.data.RegisterData;
import com.example.todowebapp.domain.entity.User;
import com.example.todowebapp.exceptions.ApiException;
import com.example.todowebapp.exceptions.ErrorCode;
import com.example.todowebapp.repository.UserRepository;
import com.example.todowebapp.service.JwtService;
import com.example.todowebapp.service.SecureBasicAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Class for authentication with Bearer token, OAuth2 security would be slightly different
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecureBasicAuthenticationServiceImpl implements SecureBasicAuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public boolean register(final RegisterData data) {
        final Optional<User> userInDb = userRepository.loadByUsername(data.getEmail());
        if (userInDb.isPresent())  {
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS);
        }
        try {
            var user = User
                    .builder()
                    .email(data.getEmail())
                    .password(passwordEncoder.encode(data.getPassword()))
                    .lastName(data.getLastName())
                    .build();
            userRepository.saveAndFlush(user);
        } catch (Exception e) {
            log.info("Error occurred wile registering user");
            return false;
        }
        return true;
    }

    @Override
    public String login(final LoginData data) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword()));
        final User user = userRepository.loadByUsername(data.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return jwtService.generateToken(user);
    }
}
