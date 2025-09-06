package io.github.grano22.carfleetapp.config;

import io.github.grano22.carfleetapp.kit.AdjustableClock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;

@TestConfiguration
public class AppConfig {
    private final AdjustableClock clock = AdjustableClock.systemUTC();

    @Bean
    @Primary
    public Clock clock() {
        return clock;
    }

    @Bean
    public AdjustableClock adjustableClock() {
        return clock;
    }
}
