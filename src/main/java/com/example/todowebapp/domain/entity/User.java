package com.example.todowebapp.domain.entity;

import com.example.todowebapp.domain.converters.FeatureConverter;
import com.example.todowebapp.domain.dto.FeatureDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, exclude = "todos")
@Entity
@Table(name = "users", schema = "public")
public class User extends TimestampEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    private String email;

    private String password;

    private String name;

    private String lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Todo> todos = new ArrayList<>();

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    @Column(name = "authorities", columnDefinition = "TEXT")
    @Convert(converter = FeatureConverter.class)
    private List<FeatureDTO> authorities;

    @Override
    public String getUsername() {
        return email;
    }
}
