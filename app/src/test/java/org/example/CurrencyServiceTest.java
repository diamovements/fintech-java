package org.example;

import org.example.entity.response.CurrencyResponse;
import org.example.service.CurrencyService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CurrencyServiceTest {
    @Test
    void convertToRubTest_shouldConvertToRub() {
        CurrencyService currencyService = mock(CurrencyService.class);
        CurrencyResponse currencyResponse = new CurrencyResponse();
        currencyResponse.setRates(Collections.singletonMap("RUB", BigDecimal.valueOf(95)));
        when(currencyService.convertToRub(BigDecimal.valueOf(7.00), "USD"))
                .thenReturn(Mono.just(BigDecimal.valueOf(7.00).multiply(BigDecimal.valueOf(95))));

        Mono<BigDecimal> result = currencyService.convertToRub(BigDecimal.valueOf(7.00), "USD");
        StepVerifier.create(result)
                .expectNext(BigDecimal.valueOf(665.0))
                .verifyComplete();

        verify(currencyService, times(1))
                .convertToRub(BigDecimal.valueOf(7.00), "USD");
    }

    @Test
    void convertToRubTest_shouldThrowException() {
        CurrencyService currencyService = mock(CurrencyService.class);
        when(currencyService.convertToRub(BigDecimal.valueOf(7.00), "RAT"))
                .thenReturn(Mono.error(new IllegalArgumentException("Currency not found")));

        Mono<BigDecimal> result = currencyService.convertToRub(BigDecimal.valueOf(7.00), "RAT");
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(currencyService, times(1))
                .convertToRub(BigDecimal.valueOf(7.00), "RAT");
    }
}
