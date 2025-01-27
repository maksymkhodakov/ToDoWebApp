package com.example.todowebapp.security.jwt;

import com.example.todowebapp.domain.dto.UserDTO;
import com.example.todowebapp.exceptions.ApiException;
import com.example.todowebapp.exceptions.ErrorCode;
import com.example.todowebapp.util.JacksonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {
    public static final String USER = "user";

    private final JWTProperties jwtProperties;
    private final SecretKey secretKey;


    public TokenProvider(JWTProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                this.jwtProperties
                        .getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createTokenFromUser(final UserDTO user) {
        final LocalDateTime validTimestamp = LocalDateTime.now().plusSeconds(jwtProperties.getTokenValidity());
        final Date validDate = Date.from(validTimestamp.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(user.getUsername())
                .claim(USER, JacksonUtil.serialize(user))
                .expiration(validDate)
                .signWith(secretKey)
                .compact();
    }

    public Jws<Claims> verifyAndParseToken(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.ERROR_PROCESSING_JWT);
        }
    }

    public boolean requireRefreshToken(final String token) {
        final LocalDateTime validTimestamp = LocalDateTime.now().plusSeconds(jwtProperties.getMinTokenValidity());
        final Date validDate = Date.from(validTimestamp.atZone(ZoneId.systemDefault()).toInstant());

        try {
            final Jws<Claims> claims = verifyAndParseToken(token);
            return validDate.after(claims.getPayload().getExpiration());
        } catch (Exception e) {
            return true;
        }
    }
}
