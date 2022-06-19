package com.micropos.gateway.channel;

import com.micropos.dto.DeliveryRecordDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class DeliveryChannel {
    static final String DELIVERY_ORDER_ID = "/api/delivery/{orderId}";
    static final String DELIVERY_ORDERS = "/api/delivery";

    @Value("micropos.delivery")
    String deliveryUrl;

    @Bean
    public IntegrationFlow inChannel() {
        return IntegrationFlows.from(WebFlux.inboundGateway(DELIVERY_ORDER_ID)
                .requestMapping(it -> it.methods(HttpMethod.GET))
                .payloadExpression("#pathVariables.orderId"))
            .headerFilter("accept-encoding", false)
            .channel("deliveryInChannel")
            .get();
    }

    @Bean
    public IntegrationFlow outChannel() {
        return IntegrationFlows.from("deliveryInChannel")
            .handle(WebFlux.outboundGateway(it -> UriComponentsBuilder
                    .fromUriString(deliveryUrl + DELIVERY_ORDER_ID)
                    .buildAndExpand(it.getPayload())
                    .toUri())
                .httpMethod(HttpMethod.GET)
                .expectedResponseType(DeliveryRecordDto.class)
            )
            .get();
    }
}
