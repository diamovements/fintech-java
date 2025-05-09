package org.example.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.ValCurs;
import org.example.entity.Valute;
import org.example.exception.CurrencyNotFoundException;
import org.example.exception.CurrencyServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyCacheService {

    private final CacheManager manager;
    private final RestTemplate restTemplate;
    @Value("${api.currency-url}")
    private String apiUrl;
    @Cacheable(value = "rates", key = "#p0")
    @CircuitBreaker(name = "currencyService", fallbackMethod = "getCurrencyRateFallback")
    public BigDecimal getCurrencyRate(String code) {
        try {
            String url = String.format("%sdate_req=%s", apiUrl, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            String xmlResponse = restTemplate.getForObject(url, String.class);
            return parseRateFromXML(xmlResponse, code);
        } catch (RestClientException e) {
            log.error("Server error {}: {}", code, e.getMessage());
            throw new CurrencyServiceUnavailableException("Service is unavailable");
        }
    }

    private BigDecimal getCurrencyRateFallback(String code, Throwable throwable) {
        log.error("Error occurred while getting currency: {}, {}", code, throwable.getMessage());

        BigDecimal rateTry = Objects.requireNonNull(manager.getCache("rates")).get(code, BigDecimal.class);
        if (rateTry != null) {
            log.info("Currency rate from cache: {}", rateTry);
            return rateTry;
        }
        throw new CurrencyNotFoundException("Currency not found");
    }

    public BigDecimal parseRateFromXML(String xmlResponse, String code) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            ValCurs valCurs = xmlMapper.readValue(xmlResponse, ValCurs.class);
            log.info("ValCurs: {}", valCurs.valuteList());
            for (Valute valute : valCurs.valuteList()) {
                if (valute.charCode().equals(code)) {
                    log.info("Valutes: {}", valute.charCode());
                    return new BigDecimal(valute.value().replace(",", "."));                }
            }
            log.error("Currency not found: {}", code);
            throw new CurrencyNotFoundException("Currency not found");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML response", e);
        }
    }
}
