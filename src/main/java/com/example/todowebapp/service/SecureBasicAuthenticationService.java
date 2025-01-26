package com.example.todowebapp.service;

import com.example.todowebapp.domain.data.LoginData;
import com.example.todowebapp.domain.data.RegisterData;

public interface SecureBasicAuthenticationService {
    boolean register(RegisterData data);
    String login(LoginData data);
}
