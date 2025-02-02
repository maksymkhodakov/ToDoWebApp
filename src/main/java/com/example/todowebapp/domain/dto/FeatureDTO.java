package com.example.todowebapp.domain.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureDTO implements GrantedAuthority {
    private String authority;
}
