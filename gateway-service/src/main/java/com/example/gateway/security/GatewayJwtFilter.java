package com.example.gateway.security;

import com.example.common.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayJwtFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public GatewayJwtFilter(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer}") String issuer
    ) {
        this.jwtService = new JwtService(secret, issuer);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().value();

        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = auth.substring(7);

            // ✅ CORRECT method
            Claims claims = jwtService.parse(token);

            String userId = String.valueOf(claims.get("userId"));
            String username = claims.getSubject(); // email / username
            String role = String.valueOf(claims.get("role"));

            ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r.headers(h -> {
                        h.add("X-User-Id", userId);
                        h.add("X-User-Name", username);
                        h.add("X-User-Role", role);
                    }))
                    .build();

            return chain.filter(mutated);

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublic(String path) {
        return matcher.match("/auth/**", path)
                || matcher.match("/actuator/**", path)
                || matcher.match("/swagger-ui/**", path)
                || matcher.match("/v3/api-docs/**", path);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
