package org.example.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Details about currency conversion request")
public record ConvertRequest(
        @Schema(description = "From currency code", example = "USD", type = "string")
        @NotNull(message = "fromCurrency is required")
        String fromCurrency,
        @Schema(description = "To currency code", example = "EUR", type = "string")
        @NotNull(message = "toCurrency is required")
        String toCurrency,
        @Schema(description = "Amount to convert", example = "10.4", type = "double")
        @Positive(message = "amount must be positive")
        @NotNull(message = "amount is required")
        BigDecimal amount) { }
