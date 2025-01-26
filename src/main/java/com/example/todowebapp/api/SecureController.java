package com.example.todowebapp.api;

import com.example.todowebapp.domain.data.LoginData;
import com.example.todowebapp.domain.data.RegisterData;
import com.example.todowebapp.service.SecureBasicAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/secure")
public class SecureController {
    private final SecureBasicAuthenticationService secureBasicAuthenticationService;

    @PostMapping("/register")
    public ResponseEntity<Boolean> registration(@RequestBody @Valid RegisterData data){
        return ResponseEntity.ok(secureBasicAuthenticationService.register(data));
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginData data){
        return ResponseEntity.ok(secureBasicAuthenticationService.login(data));
    }
}
