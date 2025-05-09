package org.example.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.CurrencyNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
@Setter
public class CurrencyService {

    private final RestTemplate restTemplate;
    private final CurrencyCacheService cacheService;
    private final Set<Currency> currencies = Currency.getAvailableCurrencies();


    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0 || fromCurrency == null || toCurrency == null) {
            log.error("Parameters are incorrect of missing");
            throw new IllegalArgumentException("Parameters are incorrect of missing");
        }
        log.info("Validating currency: {}", fromCurrency);
        validateCurrency(fromCurrency);
        log.info("Validating currency: {}", toCurrency);
        validateCurrency(toCurrency);
        BigDecimal fromRate = cacheService.getCurrencyRate(fromCurrency);
        BigDecimal toRate = cacheService.getCurrencyRate(toCurrency);
        if (toRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new CurrencyNotFoundException("Currency not found");
        }
        return (fromRate.divide(toRate, RoundingMode.HALF_UP)).multiply(amount);    }

    public BigDecimal getCurrencyRate(String code) {
        log.info("Validating currency: {}", code);
        validateCurrency(code);
        log.info("Fetching currency rate from cacheService for code: {}", code);
        return cacheService.getCurrencyRate(code);
    }

    public void validateCurrency(String code) {
        try {
            Currency currency = Currency.getInstance(code);
            if (!currencies.contains(currency)) {
                log.error("Currency not found: {}", code);
                throw new CurrencyNotFoundException("Currency not found");
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid currency code: {}", code);
            throw new IllegalArgumentException("Invalid currency code");
        }
    }
}
