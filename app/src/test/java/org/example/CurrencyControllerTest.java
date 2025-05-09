package org.example;

import org.example.entity.request.ConvertRequest;
import org.example.exception.CurrencyNotFoundException;
import org.example.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    void convertCurrenciesTest_shouldConvertCurrency() throws Exception {
        ConvertRequest request = new ConvertRequest("USD", "EUR", 100.0);
        when(currencyService.convertCurrency(request.amount(), request.fromCurrency(), request.toCurrency())).thenReturn(50.0);
        mockMvc.perform(post("/currencies/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"fromCurrency\": \"USD\", \"toCurrency\": \"EUR\", \"convertedAmount\": 50.0 }"))
                .andExpect(status().isOk());
    }

    @Test
    void convertCurrenciesTest_shouldReturnBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid currency code")).when(currencyService).convertCurrency(any(), eq(null), any());
        mockMvc.perform(post("/currencies/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"fromCurrency\": null, \"toCurrency\": \"EUR\", \"convertedAmount\": 50.0 }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void convertCurrenciesTest_shouldReturnNotFound() throws Exception {
        doThrow(new CurrencyNotFoundException("Currency not found")).when(currencyService).convertCurrency(any(), any(), any());
        mockMvc.perform(post("/currencies/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"fromCurrency\": \"RUB\", \"toCurrency\": \"EUR\", \"convertedAmount\": 50.0 }"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRateTest_shouldReturnRate() throws Exception {
        when(currencyService.getCurrencyRate("USD")).thenReturn(100.0);
        mockMvc.perform(get("/currencies/rates/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    void getRateTest_shouldReturnNotFound() throws Exception {
        doThrow(new CurrencyNotFoundException("Currency not found")).when(currencyService).getCurrencyRate(any());
        mockMvc.perform(get("/currencies/rates/RUB"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRateTest_shouldReturnBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Code must not be null")).when(currencyService).getCurrencyRate(any());
        mockMvc.perform(get("/currencies/rates/null"))
                .andExpect(status().isBadRequest());
    }
}
