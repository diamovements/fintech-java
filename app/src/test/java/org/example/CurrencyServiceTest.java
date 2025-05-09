package org.example;

import org.example.exception.CurrencyNotFoundException;
import org.example.service.CurrencyCacheService;
import org.example.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

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

    private final String apiUrl = "http://fake-url.com/api?";

    @Test
    void setUp() {
        openMocks(this);
    }

    @Test
    void convertCurrencyTest_shouldConvertCurrency() {
        CurrencyCacheService cacheService = mock(CurrencyCacheService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        SupportedCurrencies supportedCurrencies = mock(SupportedCurrencies.class);
        CurrencyService currencyService = new CurrencyService(restTemplate, cacheService, supportedCurrencies);
        currencyService.setApiUrl(apiUrl);
        when(cacheService.getCurrencyRate(eq("USD"), eq(restTemplate), eq(apiUrl)))
                .thenReturn(100.0);
        when(cacheService.getCurrencyRate(eq("EUR"), eq(restTemplate), eq(apiUrl)))
                .thenReturn(200.0);
        Double amount = 100.0;
        Double convertedAmount = currencyService.convertCurrency(amount, "USD", "EUR");
        assertEquals(50.0, convertedAmount);

        verify(cacheService, times(1)).getCurrencyRate(eq("USD"), any(RestTemplate.class), eq(apiUrl));
        verify(cacheService, times(1)).getCurrencyRate(eq("EUR"), any(RestTemplate.class), eq(apiUrl));
    }

    @Test
    void convertCurrencyTest_shouldThrowIllegalArgumentException() {
        CurrencyCacheService cacheService = mock(CurrencyCacheService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        SupportedCurrencies supportedCurrencies = mock(SupportedCurrencies.class);
        CurrencyService currencyService = new CurrencyService(restTemplate, cacheService, supportedCurrencies);
        currencyService.setApiUrl(apiUrl);
        when(cacheService.getCurrencyRate(eq("USD"), eq(restTemplate), eq(apiUrl)))
                .thenReturn(100.0);
        when(cacheService.getCurrencyRate(eq("EUR"), eq(restTemplate), eq(apiUrl)))
                .thenReturn(200.0);
        Double amount = -100.0;
        assertThrows(IllegalArgumentException.class, () -> currencyService.convertCurrency(amount, "USD", "EUR"));
    }

    @Test
    void convertCurrencyTest_shouldThrowCurrencyNotFoundException() {
        CurrencyCacheService cacheService = mock(CurrencyCacheService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        SupportedCurrencies supportedCurrencies = mock(SupportedCurrencies.class);
        CurrencyService currencyService = new CurrencyService(restTemplate, cacheService, supportedCurrencies);
        currencyService.setApiUrl(apiUrl);
        when(cacheService.getCurrencyRate(eq("USD"), eq(restTemplate), eq(apiUrl)))
                .thenReturn(100.0);
        when(cacheService.getCurrencyRate(eq("EUR"), eq(restTemplate), eq(apiUrl)))
                .thenReturn(200.0);
        Double amount = 100.0;
        assertThrows(CurrencyNotFoundException.class, () -> currencyService.convertCurrency(amount, "USD", "ASD"));
    }

}
