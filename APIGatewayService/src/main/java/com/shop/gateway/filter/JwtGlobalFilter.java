package com.shop.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Single entry point security: every route except login/register must carry a valid JWT.
 * Fine-grained RBAC is enforced again inside each downstream service.
 */
@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private static final Set<String> OPEN_PATHS = Set.of("/api/auth/login", "/api/auth/register");

    private final SecretKey key;

    public JwtGlobalFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (OPEN_PATHS.contains(path)) {
            return chain.filter(exchange);
        }
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or malformed Authorization header");
        }
        try {
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(header.substring(7)).getPayload();
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(h -> {
                        h.set("X-User-Id", String.valueOf(claims.get("userId")));
                        h.set("X-User-Role", String.valueOf(claims.get("role")));
                    })
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        } catch (JwtException | IllegalArgumentException ex) {
            return unauthorized(exchange, "Invalid or expired token");
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = """
                {"status":401,"error":"Unauthorized","message":"%s"}""".formatted(message)
                .getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
