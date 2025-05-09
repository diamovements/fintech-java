package org.example.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Details about currency conversion response")
public record ConvertResponse(@Schema(description = "From currency code", example = "USD", type = "string") String fromCurrency,
                              @Schema(description = "To currency code", example = "EUR", type = "string") String toCurrency,
                              @Schema(description = "Converted amount", example = "93.04", type = "double") BigDecimal convertedAmount) {
}
