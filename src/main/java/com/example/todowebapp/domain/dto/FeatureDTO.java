package com.example.todowebapp.domain.dto;

import com.example.todowebapp.domain.enumerated.Privilege;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureDTO implements GrantedAuthority {
    private Privilege privilege;

    @Override
    public String getAuthority() {
        return privilege.name();
    }
}
