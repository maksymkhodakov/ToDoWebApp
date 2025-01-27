package com.example.todowebapp.security;

import com.example.todowebapp.domain.dto.UserDTO;
import com.example.todowebapp.exceptions.ApiException;
import com.example.todowebapp.exceptions.ErrorCode;
import com.example.todowebapp.security.jwt.TokenProvider;
import com.example.todowebapp.util.JacksonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;

import static com.example.todowebapp.security.jwt.TokenProvider.USER;

@Slf4j
@Component
public class JWTSecurityFilter implements Filter {
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";

    @Value("${security.routes}")
    private String[] routes;

    private final AntPathMatcher antPathMatcher;
    private final TokenProvider tokenProvider;

    public JWTSecurityFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
        this.antPathMatcher = new AntPathMatcher();
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        if (Arrays.stream(routes).anyMatch(path -> antPathMatcher.match(path, httpRequest.getRequestURI()))) {
            final UserDTO userDTO = new UserDTO();
            final UserAuthentication userAuthentication = new UserAuthentication(userDTO);
            SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        final String tokenHeader = httpRequest.getHeader(AUTH_HEADER);
        final UserDTO userDTO = verifyTokenAndMap(tokenHeader);

        if (userDTO == null) {
            log.info("Rejected access: {}, no header defined", httpRequest.getRequestURI());
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(JacksonUtil.serialize(new ApiException(ErrorCode.UNAUTHORIZED)));
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(userDTO));
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private UserDTO verifyTokenAndMap(final String tokenHeader) {
        if (!StringUtils.hasText(tokenHeader)) {
            return null;
        }

        try {
            final String bearer = tokenHeader.replace(BEARER, "");
            final Jws<Claims> claims = tokenProvider.verifyAndParseToken(bearer);
            return JacksonUtil.deserialize(claims.getPayload().get(USER, String.class), UserDTO.class);
        } catch (Exception e) {
            return null;
        }
    }
}
