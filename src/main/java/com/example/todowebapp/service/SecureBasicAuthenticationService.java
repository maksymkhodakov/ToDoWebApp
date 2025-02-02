package com.example.todowebapp.service;


import com.example.todowebapp.domain.dto.LoginData;
import com.example.todowebapp.domain.dto.LoginResponseDTO;
import com.example.todowebapp.domain.dto.RegisterData;
import com.example.todowebapp.domain.dto.UserDTO;

public interface SecureBasicAuthenticationService {
    UserDTO getCurrentUser();
    void register(RegisterData data);
    LoginResponseDTO login(LoginData data);
}
