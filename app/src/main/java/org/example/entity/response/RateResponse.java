package org.example.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Details about currency rate response")
public record RateResponse(@Schema(description = "Currency code", example = "USD") String currency,
                           @Schema(description = "Currency rate", example = "93.04") BigDecimal amount) {
}
