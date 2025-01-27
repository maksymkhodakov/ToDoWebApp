package com.example.todowebapp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1370232080745550612L;

    private Long id;
    private Set<String> roles;
    private Set<String> features;
    private String firstName;
    private String lastName;
    private String email;
    private String username;

}
