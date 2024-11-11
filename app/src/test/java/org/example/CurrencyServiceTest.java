package org.example;

import org.example.exception.CurrencyNotFoundException;
import org.example.service.CurrencyCacheService;
import org.example.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.MockitoAnnotations.openMocks;

public class CurrencyServiceTest {


    @Test
    void setUp() {
        openMocks(this);
    }

    @Test
    void convertCurrencyTest_shouldConvertCurrency() {
        CurrencyCacheService cacheService = mock(CurrencyCacheService.class);
        CurrencyService currencyService = new CurrencyService(cacheService);
        when(cacheService.getCurrencyRate(eq("USD")))
                .thenReturn(BigDecimal.valueOf(100.0));
        when(cacheService.getCurrencyRate(eq("EUR")))
                .thenReturn(BigDecimal.valueOf(200.0));
        BigDecimal amount = BigDecimal.valueOf(100.0);
        BigDecimal convertedAmount = currencyService.convertCurrency(amount, "USD", "EUR");
        assertEquals(BigDecimal.valueOf(50.0), convertedAmount);

        verify(cacheService, times(1)).getCurrencyRate(eq("USD"));
        verify(cacheService, times(1)).getCurrencyRate(eq("EUR"));
    }

    @Test
    void convertCurrencyTest_shouldThrowIllegalArgumentException() {
        CurrencyCacheService cacheService = mock(CurrencyCacheService.class);
        CurrencyService currencyService = new CurrencyService(cacheService);
        when(cacheService.getCurrencyRate(eq("USD")))
                .thenReturn(BigDecimal.valueOf(100.0));
        when(cacheService.getCurrencyRate(eq("EUR")))
                .thenReturn(BigDecimal.valueOf(200.0));
        BigDecimal amount = BigDecimal.valueOf(-100.0);
        assertThrows(IllegalArgumentException.class, () -> currencyService.convertCurrency(amount, "USD", "EUR"));
    }

    @Test
    void convertCurrencyTest_shouldThrowCurrencyNotFoundException() {
        CurrencyCacheService cacheService = mock(CurrencyCacheService.class);
        CurrencyService currencyService = new CurrencyService(cacheService);
        when(cacheService.getCurrencyRate(eq("USD")))
                .thenReturn(BigDecimal.valueOf(100.0));
        when(cacheService.getCurrencyRate(eq("EUR")))
                .thenReturn(BigDecimal.valueOf(200.0));
        BigDecimal amount = BigDecimal.valueOf(100.0);
        assertThrows(CurrencyNotFoundException.class, () -> currencyService.convertCurrency(amount, "USD", "ASD"));
    }

}
