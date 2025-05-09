package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.request.ConvertRequest;
import org.example.entity.response.ConvertResponse;
import org.example.entity.response.ErrorResponse;
import org.example.entity.response.RateResponse;
import org.example.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currencies")
@Slf4j
public class CurrencyController {

    private final CurrencyService currencyService;


    @Operation(summary = "Get currency rate by its code",
    description = "Fetching exchange rate for given code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched currency rate",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid currency code",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/rates/{code}")
    public ResponseEntity<RateResponse> getRate(@PathVariable("code") @NotNull(message = "Code must not be null") String code) {
        return ResponseEntity.ok(new RateResponse(code, currencyService.getCurrencyRate(code)));
    }

    @Operation(summary = "Convert currency by request", description = "Converting currency from one code to another.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully converted currency",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ConvertResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid currency code",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/convert")
    public ResponseEntity<ConvertResponse> convertCurrencies(@RequestBody @Valid ConvertRequest request) {
        String fromCurrency = request.fromCurrency();
        String toCurrency = request.toCurrency();
        BigDecimal amount = request.amount();
        BigDecimal convertedAmount = currencyService.convertCurrency(amount, fromCurrency, toCurrency);
        return ResponseEntity.ok(new ConvertResponse(fromCurrency, toCurrency, convertedAmount));
    }
}
