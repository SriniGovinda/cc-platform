package com.example.gateway.filter;

import com.example.common.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Validates JWT and forwards user context headers:
 *  - X-User-Id
 *  - X-User-Email
 *  - X-User-Role
 */
@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public JwtGatewayFilter(@Value("${security.jwt.secret}") String secret,
                            @Value("${security.jwt.issuer}") String issuer) {
        this.jwtService = new JwtService(secret, issuer);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Allow unauth routes
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = auth.substring("Bearer ".length());
        try {
            Claims claims = jwtService.parse(token);
            Long userId = ((Number) claims.get("userId")).longValue();
            String email = claims.getSubject();
            String role = String.valueOf(claims.get("role"));

            ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r.headers(h -> {
                        h.add("X-User-Id", String.valueOf(userId));
                        h.add("X-User-Email", email);
                        h.add("X-User-Role", role);
                    }))
                    .build();

            return chain.filter(mutated);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
