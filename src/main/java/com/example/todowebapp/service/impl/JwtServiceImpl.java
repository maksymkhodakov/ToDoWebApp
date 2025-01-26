package com.example.todowebapp.service.impl;

import com.example.todowebapp.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${secret.key}")
    private String secretKey;

    @Override
    public String getEmailFromToken(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(final String token,
                              final Function<Claims, T> resolveClaims) {
        var claims = getAllInfoFromToken(token);
        return resolveClaims.apply(claims);
    }

    @Override
    public Claims getAllInfoFromToken(final String token) {
        final Key key = getKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        final byte[] array = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(array);
    }

    @Override
    public String generateToken(final UserDetails userDetails) {
        final Map<String, Object> map = new HashMap<>();
        map.put("roles", userDetails.getAuthorities());
        return generate(map, userDetails);
    }

    private String generate(final Map<String, Object> map,
                            final UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(map)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public boolean isValidToken(final String token) {
        final Claims claims = getAllInfoFromToken(token);
        final Date expirationDate = claims.getExpiration();
        return !expirationDate.before(new Date());
    }
}
