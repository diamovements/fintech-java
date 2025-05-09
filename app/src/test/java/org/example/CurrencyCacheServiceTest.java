package org.example;

import org.example.exception.CurrencyNotFoundException;
import org.example.exception.CurrencyServiceUnavailableException;
import org.springframework.cache.Cache;
import org.example.service.CurrencyCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

public class CurrencyCacheServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CacheManager manager;

    @InjectMocks
    private CurrencyCacheService currencyCacheService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void parseXmlTest_shouldParse() {
        String xml = """
                <ValCurs Date="02.03.2002" name="Foreign Currency Market">
                         <Valute ID="R01010">
                         <NumCode>036</NumCode>
                         <CharCode>AUD</CharCode>
                         <Nominal>1</Nominal>
                         <Name>Австралийский доллар</Name>
                         <Value>16,00</Value>
                         <VunitRate>16,00</VunitRate>
                         </Valute>
                         <Valute ID="R01035">
                         <NumCode>826</NumCode>
                         <CharCode>GBP</CharCode>
                         <Nominal>1</Nominal>
                         <Name>Фунт стерлингов Соединенного королевства</Name>
                         <Value>43,8254</Value>
                         <VunitRate>43,8254</VunitRate>
                         </Valute>
                </ValCurs>""";
        Double rate = currencyCacheService.parseRateFromXML(xml, "AUD");

        assertEquals(16.00, rate);
    }

    @Test
    void parseXmlTest_shouldThrowException() {
        String xml = """
                <ValCurs Date="02.03.2002" name="Foreign Currency Market">
                         <Valute ID="R01010">
                         <NumCode>036</NumCode>
                         <CharCode>AUD</CharCode>
                         <Nominal>1</Nominal>
                         <Name>Австралийский доллар</Name>
                         <Value>16,00</Value>
                         <VunitRate>16,00</VunitRate>
                         </Valute>
                </ValCurs>""";
        assertThrows(RuntimeException.class, () -> currencyCacheService.parseRateFromXML(xml, "USD"));
    }

    @Test
    void getCurrencyRateTest_shouldReturnCurrency() {
        String xml = """
                <ValCurs Date="02.03.2002" name="Foreign Currency Market">
                         <Valute ID="R01010">
                         <NumCode>036</NumCode>
                         <CharCode>AUD</CharCode>
                         <Nominal>1</Nominal>
                         <Name>Австралийский доллар</Name>
                         <Value>16,00</Value>
                         <VunitRate>16,00</VunitRate>
                         </Valute>
                         <Valute ID="R01035">
                         <NumCode>826</NumCode>
                         <CharCode>GBP</CharCode>
                         <Nominal>1</Nominal>
                         <Name>Фунт стерлингов Соединенного королевства</Name>
                         <Value>43,8254</Value>
                         <VunitRate>43,8254</VunitRate>
                         </Valute>
                </ValCurs>""";
        String expectedUrl = "http://fake-url.com/api?";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(xml);
        Double rate = currencyCacheService.getCurrencyRate("AUD", restTemplate, expectedUrl);

        assertEquals(16.00, rate);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getCurrencyRateTest_shouldThrowRuntimeException() {
        String xml = """
                <ValCurs Date="02.03.2002" name="Foreign Currency Market">
                         <Valute ID="R01010">
                         <NumCode>036</NumCode>
                         <CharCode>AUD</CharCode>
                         <Nominal>1</Nominal>
                         <Name>Австралийский доллар</Name>
                         <Value>16,00</Value>
                         <VunitRate>16,00</VunitRate>
                         </Valute>
                         <Valute ID="R01035">
                         <NumCode>826</NumCode>
                         <CharCode>GBP</CharCode>
                         <Nominal>1</Nominal>
                         <Name>Фунт стерлингов Соединенного королевства</Name>
                         <Value>43,8254</Value>
                         <VunitRate>43,8254</VunitRate>
                         </Valute>
                </ValCurs>""";
        String expectedUrl = "http://fake-url.com/api?";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(xml);

        assertThrows(RuntimeException.class,() -> currencyCacheService.getCurrencyRate("RUB", restTemplate, expectedUrl));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getCurrencyRateFallbackTest_shouldReturnCurrency() {
        Cache cache = mock(Cache.class);
        when(manager.getCache("rates")).thenReturn(cache);
        when(cache.get("AUD", Double.class)).thenReturn(16.00);

        Double rate = currencyCacheService.getCurrencyRateFallback("AUD", new Throwable("Error"));

        assertEquals(16.00, rate);
        verify(manager, times(1)).getCache("rates");
        verify(cache, times(1)).get("AUD", Double.class);
    }

    @Test
    void getCurrencyRateFallbackTest_shouldThrowCurrencyNotFoundException() {
        Cache cache = mock(Cache.class);
        when(manager.getCache("rates")).thenReturn(cache);
        when(cache.get("RUB", Double.class)).thenReturn(null);

        assertThrows(CurrencyNotFoundException.class, () ->
                currencyCacheService.getCurrencyRateFallback("RUB", new Throwable("Error")));
    }

    @Test
    void getCurrencyRateTest_shouldThrowUnavailableException() {
        String apiUrl = "http://fake-url.com/api?";
        String code = "USD";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(HttpServerErrorException.ServiceUnavailable.create(HttpStatusCode.valueOf(503), "Service Unavailable", null, null, null));

        assertThrows(CurrencyServiceUnavailableException.class, () -> {
            currencyCacheService.getCurrencyRate(code, restTemplate, apiUrl);
        });
    }
}
