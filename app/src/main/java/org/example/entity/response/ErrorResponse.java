package org.example.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details about error")
public record ErrorResponse(@Schema(description = "Error code", example = "400") Integer code,
                            @Schema(description = "Error message", example = "Invalid currency code" ) String message) {
}
