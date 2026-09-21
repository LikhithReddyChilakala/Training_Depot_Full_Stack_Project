package com.trainingdepot.gear.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    /**
     * Just the hashing utility - no Spring Security filter chain is added,
     * so the app's own session-based login/interceptors remain the only
     * authentication mechanism, exactly as the reference project had it.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
