package com.shop.order.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.order.exception.BusinessException;
import com.shop.order.exception.ResourceNotFoundException;
import com.shop.order.exception.ServiceUnavailableException;
import feign.FeignException;

/** Translates Feign / Resilience4j failures into the service's own exceptions. */
final class FeignErrors {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private FeignErrors() {
    }

    static RuntimeException map(Throwable cause, String service) {
        if (cause instanceof FeignException fe) {
            if (fe.status() == 404) {
                return new ResourceNotFoundException(message(fe, service));
            }
            if (fe.status() >= 400 && fe.status() < 500) {
                return new BusinessException(message(fe, service));
            }
        }
        // connection refused, timeout, open circuit (CallNotPermittedException), 5xx ...
        return new ServiceUnavailableException(service + " is currently unavailable, please try again later");
    }

    private static String message(FeignException fe, String service) {
        try {
            JsonNode node = MAPPER.readTree(fe.contentUTF8());
            if (node.hasNonNull("message")) {
                return node.get("message").asText();
            }
        } catch (Exception ignored) {
            // fall through
        }
        return service + " rejected the request (HTTP " + fe.status() + ")";
    }
}
