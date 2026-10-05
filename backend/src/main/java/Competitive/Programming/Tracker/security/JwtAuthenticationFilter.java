package Competitive.Programming.Tracker.security;

import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            sendUnauthorized(response, "Missing JWT token");
            return;
        }

        try {
            String username = jwtService.extractUsername(token);

            if (username == null || username.isBlank()) {
                sendUnauthorized(response, "Invalid JWT token");
                return;
            }

            User user = userRepository.findByUsername(username)
                    .orElse(null);

            if (user == null) {
                sendUnauthorized(response, "Invalid JWT token");
                return;
            }

            if (!user.isEnabled()) {
                sendUnauthorized(response, "Account is disabled");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String role = user.getRole().name();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException e) {
            logger.debug(
                    "JWT rejected for {} {}: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getMessage());
            sendUnauthorized(response, "Invalid or expired JWT token");
        }
    }

    private void sendUnauthorized(
            HttpServletResponse response,
            String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String safeMessage = message.replace("\\", "\\\\")
                .replace("\"", "\\\"");

        response.getWriter().write(
                "{\"error\":\"Unauthorized\",\"message\":\""
                        + safeMessage
                        + "\"}"
        );
    }
}
