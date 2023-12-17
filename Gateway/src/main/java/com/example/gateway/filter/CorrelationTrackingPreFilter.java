package com.example.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.example.gateway.filter.FilterOrderType.PRE;
import static java.lang.String.format;

@Component
public class CorrelationTrackingPreFilter implements GlobalFilter, Ordered {
    public static final String CORRELATION_ID = "correlation-id";
    private static final Logger log = LoggerFactory.getLogger(CorrelationTrackingPostFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Tracking filter invoked...");

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        if (hasCorrelationId(headers)) {
            log.info(String.format("Tracked request with correlation id %s", headers.getFirst(CORRELATION_ID)));
            headers.set(CORRELATION_ID, "");
            log.info("Cleared correlation id");
        } else {
            String correlationId = generateCorrelationId();
            request = exchange.getRequest()
                    .mutate()
                    .header(CORRELATION_ID, correlationId)
                    .build();
            exchange = exchange.mutate().request(request).build();
            return chain.filter(exchange);
        }

        return chain.filter(exchange);
    }

    private boolean hasCorrelationId(HttpHeaders headers) {
        return headers.containsKey(CORRELATION_ID);
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
