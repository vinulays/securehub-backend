package com.securehub.api_gateway.filter;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Override
    public @NonNull GatewayFilter apply(@NonNull Object config) {

        return (exchange, chain) -> {
            HttpCookie cookie = exchange.getRequest().getCookies().getFirst("access_token");

            if(cookie == null){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

            String accessToken = cookie.getValue();

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }
}
