package com.example.gateway.filter;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.example.gateway.filter.FilterOrderType.POST;

@Component
public class CorrelationTrackingPostFilter implements GlobalFilter, Ordered {
    public static final String CORRELATION_ID = "correlation-id";
    private static final Logger log = LoggerFactory.getLogger(CorrelationTrackingPostFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        HttpHeaders header = exchange.getRequest().getHeaders();
        String correlationId = getCorrelationId(header);
        log.info("Injecting correlation into response..."+correlationId);
        exchange.getResponse().getHeaders().add(CORRELATION_ID, correlationId);

        return chain.filter(exchange);
    }

    private String getCorrelationId(HttpHeaders header){
        if (header.containsKey(CORRELATION_ID)) {
            return header.getFirst(CORRELATION_ID);
        }
        return "";
    }

    @Override
    public int getOrder(){
        return POST.getOrder();
    }
}
