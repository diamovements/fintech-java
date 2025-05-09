package org.example.timing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimingAutoConfiguration {

    @Bean
    public static TimingPostProcessor timingPostProcessor() {
        return new TimingPostProcessor();
    }
}

