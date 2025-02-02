package com.example.todowebapp.api;

import com.example.todowebapp.domain.dto.LoginData;
import com.example.todowebapp.domain.dto.LoginResponseDTO;
import com.example.todowebapp.domain.dto.RegisterData;
import com.example.todowebapp.domain.dto.UserDTO;
import com.example.todowebapp.service.SecureBasicAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SecurityController {
    private final SecureBasicAuthenticationService secureBasicAuthenticationService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> currentUser(){
        return ResponseEntity.ok(secureBasicAuthenticationService.getCurrentUser());
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registration(@RequestBody @Valid final RegisterData data){
        secureBasicAuthenticationService.register(data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid final LoginData data){
        return ResponseEntity.ok(secureBasicAuthenticationService.login(data));
    }
}
